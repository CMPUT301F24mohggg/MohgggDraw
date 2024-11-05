// ParticipationSettingsFragment.java
package com.example.mohgggdraw;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

public class ParticipationSettingsFragment extends Fragment {
    private SharedViewModel sharedViewModel;
    private EditText maxPoolingSampleEditText, maxEntrantsEditText;
    private Switch enableGeolocationSwitch;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_participation_settings, container, false);

        maxPoolingSampleEditText = view.findViewById(R.id.maximum_pooling_sample);
        maxEntrantsEditText = view.findViewById(R.id.max_entrants_optional);
        enableGeolocationSwitch = view.findViewById(R.id.switch_enable_geolocation);

        maxPoolingSampleEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                sharedViewModel.setMaxPoolingSample(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        maxEntrantsEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                sharedViewModel.setMaxEntrants(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        enableGeolocationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> sharedViewModel.setEnableGeolocation(isChecked));

        return view;
    }

    public void saveData() {
    }
}