package com.example.mohgggdraw;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

public class ScannerInvalidFragment extends Fragment {

    private ScannerViewModel scannerViewModel;
    private Button scanButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        scannerViewModel = new ViewModelProvider(requireActivity()).get(ScannerViewModel.class);
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
