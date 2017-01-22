package de.rocketfox.dropthedate;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.crash.FirebaseCrash;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.rocketfox.dropthedate.Connections.DownloadImageTask;
import de.rocketfox.dropthedate.Connections.HistoryEvent;
import io.paperdb.Paper;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GameFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GameFragment extends Fragment {

    private int Score;
    private Vibrator vibrator;

    public GameFragment() {
    }
    private EventRandomizer eventRandomizer;

    public static GameFragment newInstance() {
        GameFragment fragment = new GameFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
        ArrayList<HistoryEvent> events = Paper.book().read("Database");
       eventRandomizer = new EventRandomizer(events);
        Score = 0;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_game, container, false);
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstance) {
        super.onViewCreated(v, savedInstance);
        try {
            initialUiUpdate(v);
        } catch (Exception ex) {
            FirebaseCrash.report(new Exception("Error in GameActivity: " + ex.getMessage()));
            ex.printStackTrace();
        }
    }

    ImageView imgTopEvent;
    ImageView imgBottomEvent;
    TextView txtTopEvent;
    TextView txtBottomEvent;
    TextView txtScore;
    HistoryEvent eventTop;
    HistoryEvent eventBottom;
    Bitmap bmpTop = null;
    Bitmap bmpBottom = null;
    HistoryEvent preloadEvent;
    private void initialUiUpdate(View v) {
        TextView txtDateTop = (TextView) v.findViewById(R.id.txtDateTop);
        TextView txtDateBottom = (TextView) v.findViewById(R.id.txtDateBottom);

        txtDateTop.setVisibility(View.GONE);
        txtDateBottom.setVisibility(View.GONE);


        txtScore = (TextView) v.findViewById(R.id.txtScore);
        TextView txtHighscore = (TextView) v.findViewById(R.id.txtHighScore);
        if(Paper.book().read("highscore") != null)
            txtHighscore.setText("Highscore: " + Paper.book().read("highscore"));
        else
            txtHighscore.setText("Highscore: 0");

        Typeface coolvetica = Typeface.createFromAsset(getActivity().getAssets(), "fonts/coolvetica.ttf");
        txtHighscore.setTypeface(coolvetica);
        txtScore.setTypeface(coolvetica);


        eventTop = eventRandomizer.getRandomEvent();
        eventBottom = eventRandomizer.getRandomEventCloseToCurrentEvent(eventTop, 365);

        while(eventTop == null){
            eventTop = eventRandomizer.getRandomEvent();
        }

        while(eventBottom == null){
            eventBottom = eventRandomizer.getRandomEventCloseToCurrentEvent(eventTop, 365);
        }

        while(eventBottom == eventTop) {
            eventBottom = eventRandomizer.getRandomEventCloseToCurrentEvent(eventTop, 365);
        }

        imgTopEvent = (ImageView) v.findViewById(R.id.imgTopEvent);
        imgBottomEvent = (ImageView) v.findViewById(R.id.imgBottomEvent);
        txtTopEvent = (TextView) v.findViewById(R.id.txtEventTop);
        txtBottomEvent = (TextView) v.findViewById(R.id.txtEventBottom);

        try {
            new DownloadImageTask(imgTopEvent).execute(eventTop.image);
            new DownloadImageTask(imgBottomEvent).execute(eventBottom.image);
        }catch(Exception ex){
            ex.printStackTrace();
        }

        txtTopEvent.setText(eventTop.nameDE);
        txtBottomEvent.setText(eventBottom.nameDE);


        Button btnBack = (Button) v.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        RelativeLayout rlBottom = (RelativeLayout) v.findViewById(R.id.rlBottom);
        RelativeLayout rlTop = (RelativeLayout) v.findViewById(R.id.rlTop);

        rlBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickOnCard(eventBottom, eventTop, false);
            }
        });

        rlTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickOnCard(eventTop, eventBottom, true);
            }
        });

        txtTopEvent.setTypeface(coolvetica);
        txtBottomEvent.setTypeface(coolvetica);

        Preload_Async();
    }

    private void Preload_Async() {
        new Thread(new Runnable() {
            public void run(){
                PreloadEvent();
            }
        }).start();
    }

    private void PreloadEvent() {
        try {
            preloadEvent = eventRandomizer.getRandomEventCloseToCurrentEvent(eventTop, 365 * 2);
            preloadEvent.preloadCache = new DownloadImageTask().execute(preloadEvent.image).get();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void clickOnCard(HistoryEvent clickedEvent, HistoryEvent notClickedEvent, boolean topClick){
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        try{
            Date clickedEventDate = f.parse(clickedEvent.date);
            Date notClickedEventDate = f.parse(notClickedEvent.date);

            if(clickedEventDate.after(notClickedEventDate)){
                //RIGHT
                Score++;
                txtScore.setText(String.valueOf(Score));
                getNewCard(!topClick, clickedEvent);
            }else{
                //WRONG
                vibrator.vibrate(100);
                Fragment fragment = GameOverFragment.newInstance(Score);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                //ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                ft.replace(R.id.content_start, fragment).commit();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void getNewCard(boolean topCard, HistoryEvent stayingEvent) {
        boolean preloadSuccess = true;

        while(preloadEvent == null){
            while(preloadEvent == stayingEvent) {
                preloadEvent = eventRandomizer.getRandomEventCloseToCurrentEvent(stayingEvent, 365 * 2);
                preloadSuccess = false;
            }
        }

        while(preloadEvent == stayingEvent) {
            preloadEvent = eventRandomizer.getRandomEventCloseToCurrentEvent(stayingEvent, 365 * 2);
            preloadSuccess = false;
        }

        HistoryEvent event = preloadEvent;

        if(topCard){
            eventTop = event;
            if(preloadSuccess)
                 imgTopEvent.setImageBitmap(event.preloadCache);
            else
                new DownloadImageTask(imgTopEvent).execute(event.image);
            txtTopEvent.setText(event.nameDE);
        }else{
            eventBottom = event;
            if(preloadSuccess)
                 imgBottomEvent.setImageBitmap(event.preloadCache);
            else
                new DownloadImageTask(imgBottomEvent).execute(event.image);
            txtBottomEvent.setText(event.nameDE);
        }

        Preload_Async();
    }

}
