package de.rocketfox.dropthedate;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.crash.FirebaseCrash;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import de.rocketfox.dropthedate.Connections.DownloadImageTask;
import de.rocketfox.dropthedate.Connections.HistoryEvent;
import io.paperdb.Paper;

public class GameFragment extends Fragment {

    private int Score = 0;
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
            initializeGame(v);
        } catch (Exception ex) {
            FirebaseCrash.report(new Exception("Error in GameActivity: " + ex.getMessage()));
            ex.printStackTrace();
        }
    }


    HistoryEvent topEvent;
    HistoryEvent bottomEvent;

    HistoryEvent preloadEventTop;
    HistoryEvent preloadEventBottom;

    ImageView topImage;
    ImageView bottomImage;
    ImageView deciderImage;

    TextView txtEventTop;
    TextView txtEventBottom;
    TextView txtEventTopDate;
    TextView txtEventBottomDate;
    TextView txtHighscore;
    TextView txtScore;

    Button btnBack;

    RelativeLayout topLayout;
    RelativeLayout bottomLayout;


    private void initializeGame(View v) {
        initUIElements(v);
        initTurn();
    }

    private void initTurn() {
        gameState = GameState.Running;
        loadEvents();
        loadEventImagesAsync();
        updateUI();
        preloadNewEventsAsync();
    }

    private void preloadNewEventsAsync() {
       new Thread(new Runnable() {
           @Override
           public void run() {
               preloadEventsFunc();
           }
       }).start();
    }

    private void preloadEventsFunc(){
        preloadEventTop = null;
        preloadEventBottom = null;

        while(preloadEventTop == null && preloadEventBottom == null) {
            preloadEventTop = eventRandomizer.getRandomEvent();
            preloadEventBottom = eventRandomizer.getRandomEvent();
        }

        try {
            //Glide.with(this).load(preloadEventTop.image).diskCacheStrategy(DiskCacheStrategy.SOURCE).preload();
            //Glide.with(this).load(preloadEventBottom.image).diskCacheStrategy(DiskCacheStrategy.SOURCE).preload();
            preloadEventTop.preloadCache = new DownloadImageTask().execute(preloadEventTop.image).get(5, TimeUnit.SECONDS);
            preloadEventBottom.preloadCache = new DownloadImageTask().execute(preloadEventBottom.image).get(5, TimeUnit.SECONDS);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void updateUI() {
        txtEventTop.setText(topEvent.nameEN);
        txtEventBottom.setText(bottomEvent.nameEN);

        txtEventBottomDate.setText("");
        txtEventTopDate.setText("");

        //deciderImage.clearAnimation();


        setHighscore();
    }

    private void setHighscore() {
        if(Paper.book().read("highscore") != null)
            txtHighscore.setText("Highscore: " + Paper.book().read("highscore"));
        else
            txtHighscore.setText("Highscore: 0");
    }

    private enum Cards{
        TOP,
        BOTTOM
    }

    private void initUIElements(View v) {
        Typeface coolvetica = Typeface.createFromAsset(getActivity().getAssets(), "fonts/coolvetica.ttf");

        topImage = (ImageView) v.findViewById(R.id.imgTopEvent);
        bottomImage = (ImageView) v.findViewById(R.id.imgBottomEvent);

        deciderImage = (ImageView) v.findViewById(R.id.imgDecider);

        txtEventTop = (TextView) v.findViewById(R.id.txtEventTop);
        txtEventBottom = (TextView) v.findViewById(R.id.txtEventBottom);

        txtEventTopDate = (TextView) v.findViewById(R.id.txtDateTop);
        txtEventBottomDate = (TextView) v.findViewById(R.id.txtDateBottom);

        txtEventTopDate.setVisibility(View.GONE);
        txtEventBottomDate.setVisibility(View.GONE);

        txtEventTopDate.setVisibility(View.GONE);
        txtEventBottomDate.setVisibility(View.GONE);

        txtHighscore = (TextView) v.findViewById(R.id.txtHighScore);
        txtScore = (TextView) v.findViewById(R.id.txtScore);

        txtEventTop.setTypeface(coolvetica);
        txtEventBottom.setTypeface(coolvetica);
        txtEventTopDate.setTypeface(coolvetica);
        txtEventBottomDate.setTypeface(coolvetica);
        txtHighscore.setTypeface(coolvetica);
        txtScore.setTypeface(coolvetica);

        topLayout = (RelativeLayout) v.findViewById(R.id.rlTop);
        bottomLayout = (RelativeLayout) v.findViewById(R.id.rlBottom);

        topLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(gameState != GameState.Paused)
                cardClick(Cards.TOP);
            }
        });
        bottomLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(gameState != GameState.Paused)
                    cardClick(Cards.BOTTOM);
            }
        });


        btnBack = (Button) v.findViewById(R.id.btnBack);
        btnBack.setTypeface(coolvetica);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.button_click));
                getActivity().onBackPressed();
                Log.d("Button", "onClick: back");
            }
        });
    }

    private void cardClick(Cards decider) {
        gameState = GameState.Paused;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date topDate = null;
        Date bottomDate = null;
        try{
            topDate = format.parse(topEvent.date);
            bottomDate = format.parse(bottomEvent.date);
        }catch(Exception e){
            e.printStackTrace();
        }

        showDates(decider, topDate, bottomDate); //calls Decider() afterwards
    }

    private void Decider(Cards decider, Date topDate, Date bottomDate) {
        Log.e("GAME", topDate.toString() + ">" + bottomDate.toString() + "=" + topDate.after(bottomDate));
        if(decider == Cards.TOP){
            if(topDate.after(bottomDate)){
                showWin();
            }else{
                showLoose();
            }
        }else{
            if(bottomDate.after(topDate)){
                showWin();

            }else{
                showLoose();
            }
        }
    }

    private int fadeInDuration = 400;
    private void showDates(final Cards decider,final Date topDate, final Date bottomDate) {
        try {
            String[] date = topEvent.date.toString().split("-");
            txtEventTopDate.setText(date[2] + "." + date[1] + "." + date[0]);

            AlphaAnimation fadeInTop = new AlphaAnimation(0.0f, 1.0f);

            fadeInTop.setDuration(fadeInDuration);
            fadeInTop.setFillAfter(true);
            final AlphaAnimation fadeInBottom = new AlphaAnimation(0.0f, 1.0f);
            fadeInBottom.setDuration(fadeInDuration);
            fadeInBottom.setFillAfter(true);
            fadeInTop.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    txtEventBottomDate.setVisibility(View.VISIBLE);
                     String[] date = bottomEvent.date.toString().split("-");
                    txtEventBottomDate.setText(date[2] + "." + date[1] + "." + date[0]);
                    txtEventBottomDate.startAnimation(fadeInBottom);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            fadeInBottom.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    Decider(decider, topDate, bottomDate);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });

            txtEventTopDate.setVisibility(View.VISIBLE);
            txtEventTopDate.startAnimation(fadeInTop);



        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void endGame() {
        vibrator.vibrate(500);
        Fragment fragment = GameOverFragment.newInstance(Score);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
        ft.replace(R.id.content_start, fragment).commit();
    }

    private void showLoose() {
        gameState = GameState.Lost;
        ScaleAnimation scale = new ScaleAnimation(0,1,0,1,Animation.RELATIVE_TO_SELF,0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scale.setDuration(300);
        scale.setFillAfter(false);
        //final MediaPlayer mp = MediaPlayer.create(this, R.raw.soho);

        deciderImage.setImageResource(R.drawable.error);
        deciderImage.setVisibility(View.VISIBLE);
        deciderImage.startAnimation(scale);
        scale.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                txtScore.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                txtScore.setVisibility(View.VISIBLE);
                //deciderImage.setVisibility(View.INVISIBLE);
                endGame();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private enum GameState{
        Paused,
        Running,
        Lost
    }

    private GameState gameState = GameState.Running;

    private void showWin() {
        Score++;
        txtScore.setText(String.valueOf(Score));

        ScaleAnimation scale = new ScaleAnimation(0,1,0,1,Animation.RELATIVE_TO_SELF,0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scale.setDuration(300);
        scale.setFillAfter(false);
        deciderImage.setImageResource(R.drawable.checked);
        deciderImage.setVisibility(View.VISIBLE);
        deciderImage.startAnimation(scale);
        scale.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                txtScore.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                gameState = GameState.Paused;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        deciderImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(gameState == GameState.Paused && gameState != GameState.Lost) {
                    Animation plopFade = AnimationUtils.loadAnimation(getContext(), R.anim.image_fade_plop);
                    deciderImage.startAnimation(plopFade);
                    plopFade.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            txtScore.setVisibility(View.VISIBLE);
                            deciderImage.setVisibility(View.INVISIBLE);
                            initTurn();
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });
                }

            }
        });
    }

    private void loadEventImagesAsync() {
       try {
           if (topEvent.preloadCache == null) {
               //Glide.with(this).load(topEvent.image).crossFade().into(topImage);
               new DownloadImageTask(topImage).execute(topEvent.image);
           } else {
               //Glide.with(this).load(topEvent.preloadCache).crossFade().into(topImage);
               topImage.setImageBitmap(topEvent.preloadCache);
           }

           if (bottomEvent.preloadCache == null) {
               //Glide.with(this).load(bottomEvent.image).crossFade().into(bottomImage);
               new DownloadImageTask(bottomImage).execute(bottomEvent.image);
           } else {
               //Glide.with(this).load(bottomEvent.preloadCache).crossFade().into(bottomImage);
               bottomImage.setImageBitmap(bottomEvent.preloadCache);
            }
       }catch(Exception e){
           e.printStackTrace();
           Log.e("ERROR", e.getMessage());
       }
    }

    private void loadEvents() {
        if(preloadEventTop != null && preloadEventBottom != null){
            topEvent = preloadEventTop;
            bottomEvent = preloadEventBottom;
        }

        while(topEvent == null && bottomEvent == null) {
            topEvent = eventRandomizer.getRandomEvent();
            bottomEvent = eventRandomizer.getRandomEvent();
        }
    }


}
