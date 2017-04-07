package de.rocketfox.dropthedate;


import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import java.util.Random;

import io.paperdb.Paper;

public class StartFragment extends Fragment {
    Vibrator vibrator;
    public StartFragment() {
        // Required empty public constructor
    }

    public static StartFragment newInstance() {
        StartFragment fragment = new StartFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_start, container, false);
    }

    private String[] gifs;

    @Override
    public void onViewCreated(View v, Bundle savedInstance){
        super.onViewCreated(v, savedInstance);

        gifs = new String[3];
        gifs[0] = "https://media.giphy.com/media/3o6ZsVuQw2StpwojGE/giphy.gif";
        gifs[1] = "https://media.giphy.com/media/HthlLE44GCZtm/giphy.gif";
        gifs[2] = "https://media.giphy.com/media/YKBhLXKxjxHhK/giphy.gif";

        int min = 1;
        int max = 3;

        Random r = new Random();
        int random = r.nextInt(max - min + 1) + min;

        ImageView background = (ImageView) v.findViewById(R.id.gifBackgroundStart);
        GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(background);
        Glide.with(this).load(gifs[random-1]).into(imageViewTarget);

        Button btnSettings = (Button) v.findViewById(R.id.btnSettings);
        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.image_click));
                Paper.book().delete("Database");
            }
        });

        Button btnInfo = (Button) v.findViewById(R.id.btnInfo);
        btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrator.vibrate(100);
                v.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.image_click));
            }
        });

        ImageView btnPlay = (ImageView) v.findViewById(R.id.imgReplayButton);
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                vibrator.vibrate(100);
                v.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.button_click));
                Fragment fragment = GameFragment.newInstance();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
                ft.replace(R.id.content_start, fragment).addToBackStack("Start").commit();
            }
        });

    }

}
