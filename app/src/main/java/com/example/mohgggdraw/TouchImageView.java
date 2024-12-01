package com.example.mohgggdraw;

import android.content.Context;
import android.graphics.Matrix;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

public class TouchImageView extends AppCompatImageView {

    private Matrix matrix = new Matrix();
    private float scale = 1f;

    public TouchImageView(Context context) {
        super(context);
        setup();
    }

    public TouchImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup();
    }

    public TouchImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setup();
    }

    private void setup() {
        setScaleType(ScaleType.MATRIX);
        setImageMatrix(matrix);
    }

    /**
     * Zooms into a specific point on the map.
     *
     * @param x The x-coordinate of the point to zoom to.
     * @param y The y-coordinate of the point to zoom to.
     * @param scaleFactor The scale factor to apply.
     */
    public void zoomToPoint(float x, float y, float scaleFactor) {
        // Reset the matrix
        matrix.reset();

        // Translate the map to center the point
        float translateX = getWidth() / 2f - x * scaleFactor;
        float translateY = getHeight() / 2f - y * scaleFactor;
        matrix.postTranslate(translateX, translateY);

        // Apply zoom
        matrix.postScale(scaleFactor, scaleFactor, getWidth() / 2f, getHeight() / 2f);

        // Set the matrix
        setImageMatrix(matrix);
    }
}