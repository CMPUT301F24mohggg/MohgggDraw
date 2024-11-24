package com.example.mohgggdraw;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.util.Log;

import org.junit.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class DrawFromWaitlistTest {

    Event event;

    @Before
    public void setUp() {
        event = new Event("olKgM5GAgkLRUqo97eVS", "testname", "testname", "https://firebasestorage.googleapis.com/v0/b/mohgggdraw.appspot.com/o/event_images%2F1730963184849.jpg?alt=media&token=8c93f3c0-2e18-494a-95ec-a95b864ccdbd", "testname", "testname", "testname", "testname", "testname", true);
        ArrayList<String> waitinglist = new ArrayList<>();
        for(int i=0;i<10;i++){
            waitinglist.add(String.valueOf(i));
        }
        event.setWaitingList(waitinglist);
        event.setMaxCapacity(4);

    }

    @org.junit.Test
    @Test
    public void testRandomizer(){
        RandomWaitlistSelector randomizer = new RandomWaitlistSelector(event);
        randomizer.setSeed((long)1.00);
        ArrayList<String> result = randomizer.pickFromWaitlist();
        assertEquals(result.get(0),"5");
        assertEquals(event.getWaitingList().get(0),"2");

    }
    @org.junit.Test
    @Test
    public void testNoCapacity(){
        event.setMaxCapacity(-1);
        RandomWaitlistSelector randomWaitlistSelector = new RandomWaitlistSelector(event);
        randomWaitlistSelector.pickFromWaitlist();
        assertEquals(event.getWaitingList().size(),0);
    }

}
