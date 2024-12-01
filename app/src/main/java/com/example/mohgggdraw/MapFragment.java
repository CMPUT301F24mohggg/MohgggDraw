package com.example.mohgggdraw;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MapFragment extends Fragment {

    private ImageView mapImage;
    private Event event; // Event object to fetch data dynamically
    private final float mapWidth = 1527f; // Map image width in pixels
    private final float mapHeight = 768f; // Map image height in pixels

    public void setEvent(Event event) {
        this.event = event; // Set the event object dynamically
        Log.d("MapFragment", "Event set: " + event.getEventId());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mapImage = view.findViewById(R.id.map);

        if (event != null) {
            fetchEventAndDrawPins(event.getEventId());
        } else {
            Log.e("MapFragment", "Event object is null. Cannot fetch event details.");
        }
    }

    private void fetchEventAndDrawPins(String eventId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Events")
                .document(eventId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        // Fetch the waiting list from the event document
                        List<String> waitingList = (List<String>) task.getResult().get("EventWaitinglist");
                        Log.d("MapFragment", "Fetched waiting list: " + waitingList);

                        if (waitingList != null && !waitingList.isEmpty()) {
                            fetchUserGeolocations(waitingList); // Fetch all user locations
                        } else {
                            Log.e("MapFragment", "Waiting list is empty or null for event: " + eventId);
                        }
                    } else {
                        Log.e("FirestoreError", "Failed to fetch event data", task.getException());
                    }
                });
    }

    private void fetchUserGeolocations(List<String> userIds) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        List<PointF> pinLocations = new ArrayList<>();
        final int[] processedCount = {0}; // Counter to track processed users

        for (String userId : userIds) {
            db.collection("user")
                    .document(userId)
                    .get()
                    .addOnCompleteListener(task -> {
                        processedCount[0]++; // Increment counter

                        if (task.isSuccessful() && task.getResult() != null) {
                            DocumentSnapshot userDoc = task.getResult();
                            String location = userDoc.getString("location");
                            Log.d("MapFragment", "User ID: " + userId + ", Location: " + location);

                            if (location != null && !location.isEmpty()) {
                                String[] latLng = location.split(",");
                                try {
                                    double latitude = Double.parseDouble(latLng[0].trim());
                                    double longitude = Double.parseDouble(latLng[1].trim());
                                    PointF point = latLngToPoint(latitude, longitude);

                                    // Adjust overlapping points
                                    if (pinLocations.contains(point)) {
                                        point = adjustOverlappingPoint(point, pinLocations);
                                    }

                                    pinLocations.add(point);
                                } catch (NumberFormatException e) {
                                    Log.e("MapFragment", "Invalid location format for user ID: " + userId, e);
                                }
                            }
                        } else {
                            Log.e("FirestoreError", "Failed to fetch user data for ID: " + userId, task.getException());
                        }

                        // Once all users are processed, update the map
                        if (processedCount[0] == userIds.size()) {
                            mapImage.post(() -> {
                                Bitmap updatedMap = drawPinsOnMap(pinLocations);
                                if (updatedMap != null) {
                                    mapImage.setImageBitmap(updatedMap);
                                } else {
                                    Log.e("MapFragment", "Failed to update map with pins.");
                                }
                            });
                        }
                    });
        }
    }

    private PointF adjustOverlappingPoint(PointF originalPoint, List<PointF> existingPoints) {
        float offset = 10f; // Offset in pixels
        PointF newPoint = new PointF(originalPoint.x, originalPoint.y);

        while (existingPoints.contains(newPoint)) {
            // Incrementally adjust the point's position
            newPoint.x += offset;
            newPoint.y += offset;
        }

        return newPoint;
    }

    private Bitmap drawPinsOnMap(List<PointF> pinLocations) {
        BitmapDrawable drawable = (BitmapDrawable) mapImage.getDrawable();
        if (drawable == null || drawable.getBitmap() == null) {
            Log.e("MapFragment", "Map image drawable is null or invalid.");
            return null;
        }

        Bitmap originalBitmap = drawable.getBitmap();
        Bitmap updatedBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);

        Canvas canvas = new Canvas(updatedBitmap);
        Paint paint = new Paint();
        paint.setTextSize(48);
        paint.setColor(0xFFFF0000); // Red color for pins
        paint.setAntiAlias(true);

        for (PointF point : pinLocations) {
            // Slight adjustment for better visual alignment
            point.x += 10;
            point.y -= 10;
            Log.d("MapFragment", "Drawing pin at x: " + point.x + ", y: " + point.y);
            canvas.drawText("📍", point.x, point.y, paint); // Draw pin on the map
        }

        return updatedBitmap;
    }

    private PointF latLngToPoint(double latitude, double longitude) {
        double lonRange = 360.0; // Longitude range [-180, 180]
        double latRange = 180.0; // Latitude range [-90, 90]

        float x = (float) ((longitude + 180.0) / lonRange * mapWidth);
        float y = (float) ((90.0 - latitude) / latRange * mapHeight);

        x += 40; // Offset for better accuracy
        y -= 30;

        Log.d("MapFragment", "Converted lat/lng: (" + latitude + ", " + longitude + ") to x/y: (" + x + ", " + y + ")");
        return new PointF(x, y);
    }
}
