package com.example.mohgggdraw;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class BasicInformationFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView eventPosterImageView;
    private EditText titleInput, locationInput, detailInput;
    private Uri imageUri;
    private SharedViewModel sharedViewModel;
    private StorageReference storageReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_basic_information, container, false);

        eventPosterImageView = view.findViewById(R.id.organizer_event_poster);
        titleInput = view.findViewById(R.id.event_title);
        locationInput = view.findViewById(R.id.event_location);
        detailInput = view.findViewById(R.id.event_detail);

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        storageReference = FirebaseStorage.getInstance().getReference("event_images");

        eventPosterImageView.setOnClickListener(v -> selectImage());

        TextWatcher formWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateNextButtonState();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        titleInput.addTextChangedListener(formWatcher);
        locationInput.addTextChangedListener(formWatcher);
        detailInput.addTextChangedListener(formWatcher);

        return view;
    }

    public boolean isFormValid() {
        return !titleInput.getText().toString().isEmpty()
                && !locationInput.getText().toString().isEmpty()
                && !detailInput.getText().toString().isEmpty()
                && imageUri != null;
    }

    private void updateNextButtonState() {
        boolean isValid = isFormValid();
        CreateFragment parentFragment = (CreateFragment) getParentFragment();
        if (parentFragment != null) {
            parentFragment.setNextButtonEnabled(isValid);
        }
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    /**
     * Handles the result of the image selection activity.
     *
     * @param requestCode The request code passed to startActivityForResult.
     * @param resultCode The result code returned by the image selection activity.
     * @param data The data returned by the activity.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Glide.with(this).load(imageUri).into(eventPosterImageView);
            sharedViewModel.setImageUrl(imageUri.toString());
            updateNextButtonState();
        }
    }

    /**
     * Saves the data in the SharedViewModel.
     */
    public void saveData() {
        sharedViewModel.setEventTitle(titleInput.getText().toString());
        sharedViewModel.setEventLocation(locationInput.getText().toString());
        sharedViewModel.setEventDetail(detailInput.getText().toString());
    }
}
