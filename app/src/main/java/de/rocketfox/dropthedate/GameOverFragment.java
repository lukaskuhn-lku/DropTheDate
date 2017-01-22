package de.rocketfox.dropthedate;


import android.content.Context;
import android.graphics.Typeface;
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
import android.widget.TextView;

import io.paperdb.Paper;

public class GameOverFragment extends Fragment {

    private static final String ARG_PARAM1 = "Score";
    private int score;
    private Vibrator vibrator;

    public GameOverFragment() {
    }

    public static GameOverFragment newInstance(int score) {
        GameOverFragment fragment = new GameOverFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, score);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            score = getArguments().getInt(ARG_PARAM1);
        }

        vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_game_over, container, false);
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstance){
        super.onViewCreated(v, savedInstance);

        Typeface coolvetica = Typeface.createFromAsset(getActivity().getAssets(), "fonts/coolvetica.ttf");

        TextView txtScore = (TextView) v.findViewById(R.id.txtScore);
        txtScore.setTypeface(coolvetica);
        txtScore.setText("Your score: " + score);

        if(Paper.book().read("highscore") == null) {
            Paper.book().write("highscore", 0);
        }

        if ((int) Paper.book().read("highscore") < score) {
                Paper.book().write("highscore", score);
        }


        ImageView replayButton = (ImageView) v.findViewById(R.id.imgReplayButton);
        replayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //v.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.image_click));
                vibrator.vibrate(100);
                Fragment fragment = GameFragment.newInstance();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                //ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                ft.replace(R.id.content_start, fragment).commit();
            }
        });

        Button btnBack = (Button) v.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

    }

}
