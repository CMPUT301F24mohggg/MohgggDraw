package com.example.mohgggdraw;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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

        // Add pins to the map image
        mapImage.post(() -> {
            Bitmap updatedMap = addPinsToMap();
            mapImage.setImageBitmap(updatedMap);
        });
    }

    /**
     * Adds pins to the map image and returns a new bitmap.
     */
    private Bitmap addPinsToMap() {
        // Load the map image as a Bitmap
        Bitmap originalBitmap = ((BitmapDrawable) mapImage.getDrawable()).getBitmap();
        Bitmap updatedBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);

        // Create a Canvas to draw on the Bitmap
        Canvas canvas = new Canvas(updatedBitmap);
        Paint paint = new Paint();
        paint.setColor(0xFFFF0000); // Red color for pins
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);

        // Example locations (latitude, longitude)
        drawPin(canvas, 53.5232, -113.5263); // Edmonton
        drawPin(canvas, 51.0447, -114.0719); // Calgary
        drawPin(canvas, 43.651070, -79.347015); // Toronto

        return updatedBitmap;
    }

    /**
     * Draws a pin on the map at the given latitude and longitude.
     */
    private void drawPin(Canvas canvas, double latitude, double longitude) {
        PointF point = latLngToPoint(latitude, longitude);
        Paint textPaint = new Paint();
        textPaint.setTextSize(48); // Adjust size as needed
        textPaint.setColor(0xFFFF0000); // Red color
        textPaint.setAntiAlias(true);

        // Draw the emoji at the point
        canvas.drawText("📍", point.x, point.y, textPaint);
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

        return new PointF(x, y);
    }
}