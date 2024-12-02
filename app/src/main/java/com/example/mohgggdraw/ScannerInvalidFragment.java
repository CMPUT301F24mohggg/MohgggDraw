package com.example.mohgggdraw;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * Fragment that displays an invalid scanner state and provides a button to return to the scanner.
 * This fragment allows the user to attempt scanning again after an invalid scan.
 */
public class ScannerInvalidFragment extends Fragment {
    private Button scanButton;

    /**
     * Called to do initial creation of the fragment.
     *
     * @param savedInstanceState The previously saved state of the fragment, if any.
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Inflates the layout for this fragment.
     *
     * @param inflater LayoutInflater to inflate the views in this fragment.
     * @param container The parent view to attach this fragment to.
     * @param savedInstanceState The previously saved state of the fragment, if any.
     * @return The root view of the fragment.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_scanner_invalid, container, false);
    }

    /**
     * Sets up the fragment's UI elements after the view is created.
     *
     * @param view The root view of the fragment.
     * @param savedInstanceState The previously saved state of the fragment, if any.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        scanButton = view.findViewById(R.id.scan_button);

        scanButton.setOnClickListener(v -> {
            ((ScannerFragment) requireParentFragment()).swapToFragment(0);
        });
    }
}