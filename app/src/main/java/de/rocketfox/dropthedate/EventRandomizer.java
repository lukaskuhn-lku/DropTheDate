package de.rocketfox.dropthedate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import de.rocketfox.dropthedate.Connections.HistoryEvent;

/**
 * Created by Lukas on 17.01.2017.
 */

public class EventRandomizer {
    private ArrayList<HistoryEvent> events;

    public EventRandomizer(ArrayList<HistoryEvent> eventsArray){
        events = eventsArray;
    }

    public HistoryEvent getRandomEvent(){
        int max = events.size()-1;
        int min = 0;

        Random random = new Random();
        int i1 = random.nextInt(max - min + 1) + min;

        return events.get(i1);
    }

    public HistoryEvent getRandomEventCloseToCurrentEvent(HistoryEvent currentEvent, int MaxDistanceDays){
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        Date currentEventDate = null;
        try {
            currentEventDate = f.parse(currentEvent.date);

            ArrayList<HistoryEvent> eventsSorted = new ArrayList<>();
            for (HistoryEvent ev : events) {
                Date evDate = f.parse(ev.date);
                long diff = currentEventDate.getTime() - evDate.getTime();
                long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);


                if (days <= MaxDistanceDays) {
                    eventsSorted.add(ev);
                }

            }
            int max = eventsSorted.size()-1;
            int min = 0;

            Random random = new Random();
            int i1 = random.nextInt(max - min + 1) + min;

            return eventsSorted.get(i1);
        }catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }




}
