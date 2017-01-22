package de.rocketfox.dropthedate;


import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.ArrayList;

import de.rocketfox.dropthedate.Connections.DBconnection;
import de.rocketfox.dropthedate.Connections.HistoryEvent;
import io.paperdb.Paper;

public class LoadingFragment extends Fragment implements DBconnection.databaseListener {

    private Typeface RalewayLight;
    private TextView txtLoading;

    public LoadingFragment() {

    }

    public static LoadingFragment newInstance() {
        LoadingFragment fragment = new LoadingFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RalewayLight = Typeface.createFromAsset(getActivity().getAssets(), "fonts/raleway_light.ttf");

        Paper.init(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_loading, container, false);
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstance){
        super.onViewCreated(v, savedInstance);
        txtLoading = (TextView) v.findViewById(R.id.txtLoadingHeader);
        txtLoading.setTypeface(RalewayLight);

        if(Paper.book().read("Database") == null) {
            new DBconnection(this).getDatabase();
        }else{
            changeToStart();
        }
    }

    @Override
    public void databaseFinishedLoading(ArrayList<HistoryEvent> event) {
        Paper.book().write("Database", event);
        changeToStart();

    }

    private void changeToStart() {
        txtLoading.setText("Done!");

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        Fragment startFragment = StartFragment.newInstance();

        fragmentManager.beginTransaction().replace(R.id.content_start, startFragment).commit();
    }
}
