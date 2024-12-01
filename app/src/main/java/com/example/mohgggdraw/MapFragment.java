package com.example.mohgggdraw;

import android.graphics.PointF;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

public class MapFragment extends Fragment {

    private static final int LOCATION_PERMISSION_REQUEST = 100;

    private com.example.mohgggdraw.TouchImageView mapImage;
    private FrameLayout markerContainer;
    private LocationManager locationManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        return inflater.inflate(R.layout.fragment_map, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mapImage = view.findViewById(R.id.map);
        markerContainer = view.findViewById(R.id.marker_container);

        // Example marker placement
        addMarker(53.5232, -113.5263, "Edmonton");
        addMarker(51.0447, -114.0719, "Calgary");
        addMarker(43.651070, -79.347015, "Toronto");


    }

    /**
     * Adds a marker to the map and zooms into it.
     */
    private void addMarker(double latitude, double longitude, String title) {
        // Convert lat/lng to x/y coordinates
        PointF point = latLngToPoint(latitude, longitude);

        // Create a marker view
        TextView marker = new TextView(getContext());
        marker.setText("📍");
        marker.setTextSize(24);
        marker.setX(point.x);
        marker.setY(point.y);
        marker.setOnClickListener(v -> Toast.makeText(getContext(), title, Toast.LENGTH_SHORT).show());

        // Add the marker to the container
        markerContainer.addView(marker);

        // Zoom into the marker
        mapImage.zoomToPoint(point.x, point.y, 2f); // Adjust scaleFactor as needed (e.g., 2x zoom)
    }

    /**
     * Converts latitude and longitude to pixel coordinates on the map image.
     */
    private PointF latLngToPoint(double latitude, double longitude) {
        // Map dimensions (adjust these to match your map image size)
        float mapWidth = 1920;  // Replace with your map's width
        float mapHeight = 960; // Replace with your map's height

        // Latitude/longitude range
        double lonRange = 360.0; // Longitude range [-180, 180]
        double latRange = 180.0; // Latitude range [-90, 90]

        // Convert lat/lng to x/y
        float x = (float) ((longitude + 180.0) / lonRange * mapWidth);
        float y = (float) ((90.0 - latitude) / latRange * mapHeight);

        return new PointF(x, y);
    }
}