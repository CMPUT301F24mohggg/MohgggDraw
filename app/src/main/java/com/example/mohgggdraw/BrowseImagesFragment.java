package com.example.mohgggdraw;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class BrowseImagesFragment extends Fragment {

    private GridView gridView;
    private FirebaseStorage storage;
    private ImageAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_browse_images, container, false);

        gridView = view.findViewById(R.id.gridView);
        storage = FirebaseStorage.getInstance();

        // Initialize an empty adapter
        adapter = new ImageAdapter(getActivity(), new ArrayList<>());
        gridView.setAdapter(adapter);

        // Fetch images from Firebase Storage
        fetchImagesFromStorage();

        return view;
    }

    private void fetchImagesFromStorage() {
        // Reference to the "event_images" folder in Firebase Storage
        StorageReference storageRef = storage.getReference().child("event_images");

        storageRef.listAll().addOnSuccessListener(listResult -> {
            ArrayList<String> imageUrls = new ArrayList<>();

            for (StorageReference item : listResult.getItems()) {
                // Generate a download URL for each image
                item.getDownloadUrl().addOnSuccessListener(uri -> {
                    imageUrls.add(uri.toString());

                    // Update the adapter with the valid URLs
                    adapter.updateImages(imageUrls);
                }).addOnFailureListener(e -> {
                    Log.e("BrowseImagesFragment", "Failed to get download URL", e);
                });
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), "Error fetching images", Toast.LENGTH_SHORT).show();
            Log.e("BrowseImagesFragment", "Failed to list images", e);
        });
    }
}