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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_scanner_invalid, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        scanButton = view.findViewById(R.id.scan_button);

        scanButton.setOnClickListener(v -> {
            ((ScannerFragment) requireParentFragment()).swapToFragment(0);
        });    }
}
