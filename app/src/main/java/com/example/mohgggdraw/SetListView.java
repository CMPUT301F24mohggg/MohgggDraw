package com.example.mohgggdraw;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.ArrayList;


/**
 *interface for the fragments that have listviews for userLists
 * */
public interface SetListView {



    public Context retContext();

    public void updateButton();

    public void updateList(ArrayAdapter adapter);

    public void updateSelectedList(String entrant);

    public void setEvent(Event event);
}
