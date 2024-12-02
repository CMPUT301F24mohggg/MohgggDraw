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

/***
 This fragment handles the initial event creation details. It allows users to:
 - Upload an event poster image
 - Enter the event title, location, and details
 - Save this information to the SharedViewModel
 - Upload the selected image to Firebase Storage
 ***/
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

        // Initialize UI components
        eventPosterImageView = view.findViewById(R.id.organizer_event_poster);
        titleInput = view.findViewById(R.id.event_title);
        locationInput = view.findViewById(R.id.event_location);
        detailInput = view.findViewById(R.id.event_detail); // Ensure this ID matches your layout

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        storageReference = FirebaseStorage.getInstance().getReference("event_images");

        // Set click listener on ImageView to select image
        eventPosterImageView.setOnClickListener(v -> selectImage());

        // Automatically save data to ViewModel when text changes
        titleInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                sharedViewModel.setEventTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        locationInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                sharedViewModel.setEventLocation(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Add TextWatcher for detailInput to save event details
        detailInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                sharedViewModel.setEventDetail(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        return view;
    }

    /**
     * Opens an image picker to allow the user to select an image for the event poster and is displayed
     */
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
            uploadImageToFirebase();
        }
    }

    /**
     * Uploads the selected image to Firebase Storage and saves the image URL to the ViewModel.
     */
    private void uploadImageToFirebase() {
        if (imageUri != null) {
            StorageReference fileRef = storageReference.child(System.currentTimeMillis() + ".jpg");
            fileRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        sharedViewModel.setImageUrl(imageUrl); // Save URL in SharedViewModel
                        Toast.makeText(getContext(), "Image Uploaded Successfully", Toast.LENGTH_SHORT).show();
                    }))
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Image Upload Failed", Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(getContext(), "No image selected", Toast.LENGTH_SHORT).show();
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

    /**
     * Resets the data in the SharedViewModel.
     */
    public void resetData() {
        sharedViewModel.setEventTitle(null);
        sharedViewModel.setEventLocation(null);
        sharedViewModel.setEventDetail(null);
    }
}
