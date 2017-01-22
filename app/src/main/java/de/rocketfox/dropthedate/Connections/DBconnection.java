package de.rocketfox.dropthedate.Connections;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DBconnection {

    private ArrayList<HistoryEvent> tmpEventList;

    private String TAG = "DBconnection";
    private databaseListener mDelegate;
    private String categoryName;

    public DBconnection(databaseListener dbListener){
        this.mDelegate = dbListener;
    }

    public boolean getDatabase(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("main/");

        tmpEventList = new ArrayList<HistoryEvent>();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    HistoryEvent tmpEvent = data.getValue(HistoryEvent.class);
                    tmpEventList.add(tmpEvent);
                }
                mDelegate.databaseFinishedLoading(tmpEventList);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, databaseError.getMessage());
                System.err.println("Listener was cancelled");
            }
        });

        return true;
    }

    public interface databaseListener{
        void databaseFinishedLoading(ArrayList<HistoryEvent> event);
    }

}
