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

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 *Map fragment to view general area where joined
 * */
public class MapFragment extends Fragment {

    private ImageView mapImage;

    // Actual map dimensions in pixels (match the drawable image dimensions)
    private final float mapWidth = 1527f; // Width of the map image
    private final float mapHeight = 768f; // Height of the map image

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mapImage = view.findViewById(R.id.map);

        // Query Firestore and add pins to the map image
        fetchUsersAndDrawPins();
    }

    /**
     * Fetch user data from Firestore and add pins to the map.
     */
    private void fetchUsersAndDrawPins() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("user")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<Map<String, Object>> userDataList = new ArrayList<>();
                        task.getResult().forEach(document -> userDataList.add(document.getData()));

                        mapImage.post(() -> {
                            Bitmap updatedMap = addPinsToMap(userDataList);
                            mapImage.setImageBitmap(updatedMap);
                        });
                    } else {
                        Log.e("FirestoreError", "Failed to fetch users", task.getException());
                    }
                });
    }

    /**
     * Adds pins to the map image and returns a new bitmap.
     */
    private Bitmap addPinsToMap(List<Map<String, Object>> userDataList) {
        // Load the map image as a Bitmap
        Bitmap originalBitmap = ((BitmapDrawable) mapImage.getDrawable()).getBitmap();
        Bitmap updatedBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);

        // Create a Canvas to draw on the Bitmap
        Canvas canvas = new Canvas(updatedBitmap);
        Paint paint = new Paint();
        paint.setTextSize(48); // Adjust size as needed
        paint.setColor(0xFFFF0000); // Red color
        paint.setAntiAlias(true);

        // Iterate through user data and draw pins
        for (Map<String, Object> userData : userDataList) {
            String location = (String) userData.get("location");

            if (location != null && !location.isEmpty()) {
                String[] latLng = location.split(",");
                try {
                    double latitude = Double.parseDouble(latLng[0].trim());
                    double longitude = Double.parseDouble(latLng[1].trim());

                    // Convert latitude/longitude to map points
                    PointF point = latLngToPoint(latitude, longitude);

                    // Adjust the point to move it slightly to the right and up
                    point.x += 10; // Move right by 10 pixels
                    point.y -= 10; // Move up by 10 pixels

                    // Draw the pin
                    canvas.drawText("📍", point.x, point.y, paint);
                } catch (NumberFormatException e) {
                    Log.e("MapFragment", "Invalid location format", e);
                }
            }
        }

        return updatedBitmap;
    }

    /**
     * Converts latitude and longitude to pixel coordinates on the map image.
     */
    private PointF latLngToPoint(double latitude, double longitude) {
        // Latitude/longitude range
        double lonRange = 360.0; // Longitude range [-180, 180]
        double latRange = 180.0; // Latitude range [-90, 90]

        // Convert lat/lng to x/y coordinates
        float x = (float) ((longitude + 180.0) / lonRange * mapWidth);
        float y = (float) ((90.0 - latitude) / latRange * mapHeight);

        // Apply manual offsets for Edmonton's location for more acciurate map

        x += 40; // Move right by 40 pixels
        y -= 30; // Move up by 30 pixels

        return new PointF(x, y);
    }
}