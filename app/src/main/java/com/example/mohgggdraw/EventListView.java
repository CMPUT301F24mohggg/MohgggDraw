package com.example.mohgggdraw;

import java.util.ArrayList;

public interface EventListView {
    public void dataChange();
    public void setList(ArrayList list);
    public void setDevice(String id);
    public void setFragment(HomeFragment frag);
    public void setEventList(ArrayList<Event> events);
}
