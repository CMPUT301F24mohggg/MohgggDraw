// BasicInformationFragment.java
package com.example.mohgggdraw;

import android.app.Activity;
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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class BasicInformationFragment extends Fragment {

    private ImageView organizerEventPoster;
    private StorageReference storageReference;
    private SharedViewModel sharedViewModel;
    private EditText eventTitleEditText, eventLocationEditText, eventDetailEditText;

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri selectedImage = result.getData().getData();
                    if (selectedImage != null) {
                        organizerEventPoster.setImageURI(selectedImage);  // Display selected image
                        uploadImageToStorage(selectedImage);  // Upload image to Firebase
                    }
                } else {
                    Toast.makeText(getContext(), "Image selection canceled or failed", Toast.LENGTH_SHORT).show();
                }
            }
    );

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_basic_information, container, false);

        organizerEventPoster = view.findViewById(R.id.organizer_event_poster);
        eventTitleEditText = view.findViewById(R.id.event_title);
        eventLocationEditText = view.findViewById(R.id.event_location);
        eventDetailEditText = view.findViewById(R.id.event_detail);

        // Set click listener to open image picker
        organizerEventPoster.setOnClickListener(v -> openImagePicker());

        // Update SharedViewModel with text fields
        eventTitleEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                sharedViewModel.setEventTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        eventLocationEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                sharedViewModel.setEventLocation(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        eventDetailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                sharedViewModel.setEventDetail(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return view;
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private void uploadImageToStorage(Uri selectedImage) {
        String imageName = "images/" + UUID.randomUUID().toString();
        StorageReference imageRef = storageReference.child(imageName);

        UploadTask uploadTask = imageRef.putFile(selectedImage);
        uploadTask.addOnSuccessListener(taskSnapshot ->
                imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    sharedViewModel.setImageUrl(uri.toString());  // Update ViewModel with URL
                    Toast.makeText(getContext(), "Image uploaded successfully!", Toast.LENGTH_SHORT).show();
                })
        ).addOnFailureListener(e ->
                Toast.makeText(getContext(), "Image upload failed.", Toast.LENGTH_SHORT).show()
        );
    }

    public void saveData() {
    }
}
