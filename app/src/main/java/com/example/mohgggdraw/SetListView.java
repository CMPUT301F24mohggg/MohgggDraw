package com.example.mohgggdraw;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

public interface SetListView {



    public Context retContext();

    public void updateButton();

    public void updateList(ArrayAdapter adapter);

    public void updateSelectedList(String entrant);
}
