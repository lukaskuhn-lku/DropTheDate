package de.rocketfox.dropthedate;


import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareButton;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import org.w3c.dom.Text;

import java.util.Random;

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

    private InterstitialAd interstitialAd;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            score = getArguments().getInt(ARG_PARAM1);
        }

        interstitialAd = new InterstitialAd(this.getContext());
        interstitialAd.setAdUnitId("ca-app-pub-4907387875634462/7477373236");

        requestNewInterstitial();

        interstitialAd.setAdListener(new AdListener(){
            public void onAdLoaded(){

                int min = 1;
                int max = 100;

                Random r = new Random();
                int random = r.nextInt(max - min + 1) + min;

                 if(random > 80)
                    interstitialAd.show();
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_game_over, container, false);
    }

    private void requestNewInterstitial() {
       try {
           AdRequest adRequest = new AdRequest.Builder()
                   .build();

           interstitialAd.loadAd(adRequest);
       }catch(Exception e){
           e.printStackTrace();
       }
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstance){
        super.onViewCreated(v, savedInstance);

        Typeface coolvetica = Typeface.createFromAsset(getActivity().getAssets(), "fonts/coolvetica.ttf");

        int min = 1;
        int max = 4;

        Random r = new Random();
        int random = r.nextInt(max - min + 1) + min;



        TextView txtScore = (TextView) v.findViewById(R.id.txtScore);
        txtScore.setTypeface(coolvetica);
        txtScore.setText("You scored");

        TextView txtScoreNumber = (TextView) v.findViewById(R.id.txtScoreNumber);
        txtScoreNumber.setTypeface(coolvetica);
        txtScoreNumber.setText(String.valueOf(score));

        TextView txtAverage = (TextView) v.findViewById(R.id.txtaverage);
        txtAverage.setTypeface(coolvetica);
        int averageCounter = 1;
        int averageSum = score;

        if(Paper.book().read("averageCounter") == null) {
            Paper.book().write("averageCounter", 1);
        }else {
            averageCounter = Paper.book().read("averageCounter");
            averageCounter++;
            Paper.book().write("averageCounter", averageCounter);
        }

        if (Paper.book().read("averageSum") == null) {
            Paper.book().write("averageSum", score);
        }else{
            averageSum = Paper.book().read("averageSum");
            averageSum += score;
            Paper.book().write("averageSum", averageSum);
        }

        int average = averageSum/averageCounter;

        try {
            String[] gifsLoose = new String[4];
            gifsLoose[0] = "https://media.giphy.com/media/ADr35Z4TvATIc/giphy.gif";
            gifsLoose[1] = "https://media.giphy.com/media/hVmCCt5ikEUQ8/giphy.gif";
            gifsLoose[2] = "https://media.giphy.com/media/hVmCCt5ikEUQ8/giphy.gif";
            gifsLoose[3] = "https://media.giphy.com/media/14aUO0Mf7dWDXW/giphy.gif";

            String[] gifsWin = new String[4];
            gifsWin[0] = "https://media.giphy.com/media/7rj2ZgttvgomY/giphy.gif";
            gifsWin[1] = "https://media.giphy.com/media/qcTMPQ7LxWNBC/giphy.gif";
            gifsWin[2] = "https://media.giphy.com/media/a3zqvrH40Cdhu/giphy.gif";
            gifsWin[3] = "https://media.giphy.com/media/asXCujsv7ddpm/giphy.gif";

            ImageView background = (ImageView) v.findViewById(R.id.gifBackgroundGameOver);
            GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(background);
            if (score > average) {
                Glide.with(this).load(gifsWin[random - 1]).into(imageViewTarget);
            } else {
                Glide.with(this).load(gifsLoose[random - 1]).into(imageViewTarget);
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        txtAverage.setText("Your average is " + average);

        if(Paper.book().read("highscore") == null) {
            Paper.book().write("highscore", 0);
        }

        if ((int) Paper.book().read("highscore") < score) {
                Paper.book().write("highscore", score);
        }

        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse("http://dropthedate.com"))
                .setContentTitle("I scored " + score + " points in DropTheDate! Can you compete?")
                .setContentDescription("DropTheDate, your new addicting trivia app.")
                .setImageUrl(Uri.parse("https://pixel.nymag.com/imgs/daily/intelligencer/2016/04/30/30-barack-obama-whcd-2016.w710.h473.2x.jpg"))
                .build();

        ShareButton shareBtn = (ShareButton) v.findViewById(R.id.fb_share);
        shareBtn.setShareContent(content);

        ImageView replayButton = (ImageView) v.findViewById(R.id.imgReplayButton);
        replayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.button_click));
                Fragment fragment = GameFragment.newInstance();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
                ft.replace(R.id.content_start, fragment).commit();
            }
        });

        Button btnBack = (Button) v.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.button_click));
                Fragment fragment = StartFragment.newInstance();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
                ft.replace(R.id.content_start, fragment).commit();
            }
        });

    }

}
