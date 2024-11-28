package com.example.mohgggdraw;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.HashSet;

public class BrowseImagesFragment extends Fragment {

    private GridView gridView;
    private FirebaseFirestore db;
    private ImageAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_browse_images, container, false);

        gridView = view.findViewById(R.id.gridView);
        db = FirebaseFirestore.getInstance();

        // Initialize an empty adapter
        adapter = new ImageAdapter(getActivity(), new ArrayList<>());
        gridView.setAdapter(adapter);

        // Fetch images from Firestore
        fetchImagesFromFirestore();

        return view;
    }

    private void fetchImagesFromFirestore() {
        db.collection("Events")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        HashSet<String> uniqueUrls = new HashSet<>();
                        ArrayList<String> imageUrls = new ArrayList<>();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String imageUrl = document.getString("imageUrl");
                            // Skip null and duplicate URLs
                            if (imageUrl != null && uniqueUrls.add(imageUrl)) {
                                imageUrls.add(imageUrl);
                            }
                        }

                        // Update the adapter with the valid URLs
                        adapter.updateImages(imageUrls);
                    } else {
                        Toast.makeText(getContext(), "Error fetching data", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}