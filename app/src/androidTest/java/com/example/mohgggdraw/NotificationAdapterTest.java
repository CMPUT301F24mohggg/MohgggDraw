package com.example.mohgggdraw;

import static org.junit.Assert.assertEquals;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class NotificationAdapterTest {

    private List<NotificationModel> notificationList;
    private NotificationAdapter adapter;
    private boolean acceptButtonClicked = false;
    private boolean declineButtonClicked = false;

    private String mockDeviceId = "c7dc1ae4a545d5a2";

    @Before
    public void setUp() {
        // Reset button click flags
        acceptButtonClicked = false;
        declineButtonClicked = false;

        // Initialize a sample notification list
        notificationList = new ArrayList<>();
        notificationList.add(new NotificationModel("1", "Test Notification", "This is a test", "selected", "event123"));

        // Initialize the adapter with custom listeners that set flags when clicked
        adapter = new NotificationAdapter(notificationList, notification -> declineButtonClicked = true,
                notification -> acceptButtonClicked = true, mockDeviceId);
    }


    @Test
    public void testOnBindViewHolder_HidesButtonsForNonSelectedStatus() {
        // Add a non-selected notification
        NotificationModel nonSelectedNotification = new NotificationModel("2", "Non-selected Notification", "Message", "declined", "event456");
        notificationList.add(nonSelectedNotification);

        NotificationAdapter.NotificationViewHolder viewHolder = createViewHolder();

        adapter.onBindViewHolder(viewHolder, 1);

        // Verify that buttons are hidden for a non-"selected" status
        assertEquals(View.GONE, viewHolder.acceptButton.getVisibility());
        assertEquals(View.GONE, viewHolder.declineButton.getVisibility());
    }

    // Helper method to create a ViewHolder with real views
    private NotificationAdapter.NotificationViewHolder createViewHolder() {
        LayoutInflater inflater = LayoutInflater.from(ApplicationProvider.getApplicationContext());
        View itemView = inflater.inflate(R.layout.item_notification, null, false);

        return new NotificationAdapter.NotificationViewHolder(itemView);
    }
}
