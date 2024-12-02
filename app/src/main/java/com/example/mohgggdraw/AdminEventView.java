package com.example.mohgggdraw;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class AdminEventView extends Fragment {

    private static final String TAG = "AdminEventView";

    private ArrayList<Event> dataList; // Event list
    private ListView eventList; // ListView
    private EventAdapter eventAdapter; // Adapter for displaying events
    private FirebaseFirestore firestore; // Firestore instance
    private CollectionReference eventsRef; // Reference to the "Events" collection

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.admin_event_view_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize ListView and Adapter
        eventList = view.findViewById(R.id.admin_event_list);
        dataList = new ArrayList<>();
        eventAdapter = new EventAdapter(requireContext(), dataList);
        eventList.setAdapter(eventAdapter);

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();
        eventsRef = firestore.collection("Events");

        // Fetch all events in real-time
        //fetchAllEventsInRealTime();
        new WaitinglistDB().setAllEventsView(this);

        // Handle click on events to navigate to AdminEventDetails
        eventList.setOnItemClickListener((parent, itemView, position, id) -> {
            Event selectedEvent = dataList.get(position);
            navigateToEventDetails(selectedEvent);
        });
    }

    /**
     * Fetches all events in real-time from the Firestore database
     * and updates the ListView whenever data changes.
     */
    private void fetchAllEventsInRealTime() {
        eventsRef.addSnapshotListener((querySnapshot, e) -> {
            if (e != null) {
                Log.e(TAG, "Error listening to event changes: " + e.getMessage());
                return;
            }

            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                dataList.clear(); // Clear existing data
                for (DocumentSnapshot doc : querySnapshot) {
                    Event event = createEventFromDocument(doc);
                    if (event != null) {
                        dataList.add(event);
                    }
                }
                eventAdapter.notifyDataSetChanged(); // Notify adapter of data changes
                Log.d(TAG, "Real-time update received. Total events: " + dataList.size());
            } else {
                Log.d(TAG, "No events found in the database.");
            }
        });
    }

    /**
     * Converts a Firestore document into an Event object.
     *
     * @param doc The Firestore document representing an event.
     * @return The Event object created from the document.
     */
    private Event createEventFromDocument(DocumentSnapshot doc) {
        try {
            Event event = new Event();
            event.setEventId(doc.getId());
            event.setTitle(doc.getString("eventTitle") != null ? doc.getString("eventTitle") : "Unknown Event");
            event.setLocation(doc.getString("eventLocation") != null ? doc.getString("eventLocation") : "No Location Provided");

            // Handle poster URL gracefully
            String posterUrl = doc.getString("imageUrl");
            if (posterUrl == null || posterUrl.isEmpty()) {
                posterUrl = "placeholder"; // Indicate that the placeholder should be used
            }
            event.setPosterUrl(posterUrl);

            event.setRegistrationDetails(doc.getString("eventDetail") != null ? doc.getString("eventDetail") : "No Details Available");

            if (doc.getTimestamp("startTime") != null) {
                event.setStartTime(doc.getTimestamp("startTime").toDate());
            }

            if (doc.contains("geoLocationEnabled")) {
                event.setGeolocation(doc.getBoolean("geoLocationEnabled"));
            }

            if (doc.contains("EventWaitinglist")) {
                ArrayList<String> waitingList = (ArrayList<String>) doc.get("EventWaitinglist");
                event.setWaitingList(waitingList != null ? waitingList : new ArrayList<>());
            }

            return event;
        } catch (Exception e) {
            Log.e(TAG, "Error creating Event from document: " + e.getMessage());
            return null;
        }
    }

    /**
     * Navigates to the AdminEventDetails fragment with the selected event data.
     *
     * @param selectedEvent The event selected by the user.
     */
    private void navigateToEventDetails(Event selectedEvent) {
        AdminEventDetails detailsFragment = new AdminEventDetails();

        // Pass the selected event data to the details fragment using a Bundle
        Bundle bundle = new Bundle();
        bundle.putString("eventId", selectedEvent.getEventId());
        bundle.putString("eventTitle", selectedEvent.getTitle());
        bundle.putString("eventLocation", selectedEvent.getLocation());
        bundle.putString("eventPosterUrl", selectedEvent.getPosterUrl());
        bundle.putString("eventDetails", selectedEvent.getRegistrationDetails());
        if (selectedEvent.getStartTime() != null) {
            bundle.putLong("eventStartTime", selectedEvent.getStartTime().getTime());
        }
        detailsFragment.setArguments(bundle);

        // Navigate to the AdminEventDetailsFragment
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, detailsFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void setDataList(ArrayList<Event> dataList){
        this.dataList = dataList;
        dataChange();

    }
    public void dataChange() {
        eventAdapter = new EventAdapter(this.getContext(), dataList);
        eventList.setAdapter(eventAdapter);
    }
}
