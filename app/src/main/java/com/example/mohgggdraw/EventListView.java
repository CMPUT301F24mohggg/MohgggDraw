package com.example.mohgggdraw;

import java.util.ArrayList;
/**
 *Interface for the fragments that deal with setting lists in home fragment
 * */
public interface EventListView {
    public void dataChange();
    public void setList(ArrayList list);
    public void setDevice(String id);
    public void setFragment(HomeFragment frag);
    public void setEventList(ArrayList<Event> events);
}
