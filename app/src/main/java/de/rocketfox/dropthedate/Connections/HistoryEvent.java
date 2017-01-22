package de.rocketfox.dropthedate.Connections;

import android.graphics.Bitmap;

import com.google.firebase.database.IgnoreExtraProperties;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.rocketfox.dropthedate.Connections.DownloadImageTask;

/**
 * Created by Lukas on 03.01.2017.
 */
@IgnoreExtraProperties
public class HistoryEvent {
    public String category;
    public String date;
    public String image;
    public String nameDE;
    public String nameEN;
    public Bitmap preloadCache;

    public HistoryEvent(){
        //Required for DataSnapshot.getValue
    }

    public HistoryEvent(String category, String date, String image, String nameDE, String nameEN){
        this.category = category;
        this.date = date;
        this.image = image;
        this.nameDE = nameDE;
        this.nameEN = nameEN;
    }


}
