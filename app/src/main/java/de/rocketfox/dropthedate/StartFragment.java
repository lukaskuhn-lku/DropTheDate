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

    @Override
    public void onViewCreated(View v, Bundle savedInstance){
        super.onViewCreated(v, savedInstance);

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
                v.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.image_click));
                vibrator.vibrate(100);
                Fragment fragment = GameFragment.newInstance();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                ft.replace(R.id.content_start, fragment).addToBackStack("Start").commit();
            }
        });

    }

}
