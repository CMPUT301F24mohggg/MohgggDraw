package com.example.mohgggdraw;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class BrowseImagesFragment extends Fragment {

    private GridView gridView;
    private FirebaseStorage storage;
    private ArrayList<ImageItem> imageItems;
    private Set<ImageItem> selectedImages;
    private ImageAdapter adapter;
    private View fabDelete;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_browse_images, container, false);

        // Initialize views and Firebase
        gridView = view.findViewById(R.id.gridView);
        fabDelete = view.findViewById(R.id.fab_delete);
        storage = FirebaseStorage.getInstance();
        imageItems = new ArrayList<>();
        selectedImages = new HashSet<>();

        // Set up the adapter and attach to the GridView
        adapter = new ImageAdapter();
        gridView.setAdapter(adapter);

        // Fetch images from Firebase Storage
        fetchImages();

        // Set up the delete action
        fabDelete.setOnClickListener(v -> showDeleteConfirmationDialog());

        return view;
    }

    private void fetchImages() {
        StorageReference storageRef = storage.getReference().child("event_images");

        // List all items in the "event_images" folder
        storageRef.listAll().addOnSuccessListener((ListResult listResult) -> {
            imageItems.clear(); // Clear the list to avoid duplicates
            for (StorageReference item : listResult.getItems()) {
                item.getDownloadUrl().addOnSuccessListener(uri -> {
                    // Add image to the list and notify the adapter
                    imageItems.add(new ImageItem(uri.toString(), item));
                    adapter.notifyDataSetChanged();
                }).addOnFailureListener(e -> Log.e("BrowseImagesFragment", "Failed to fetch image URL", e));
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), "Error fetching images.", Toast.LENGTH_SHORT).show();
            Log.e("BrowseImagesFragment", "Failed to list images", e);
        });
    }

    private void showDeleteConfirmationDialog() {
        if (selectedImages.isEmpty()) {
            Toast.makeText(getContext(), "No images selected for deletion.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show a confirmation dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Delete Selected Images?");
        builder.setMessage("Are you sure you want to delete the selected images?");
        builder.setPositiveButton("Yes", (dialog, which) -> deleteSelectedImages());
        builder.setNegativeButton("Nevermind", null);
        builder.show();
    }

    private void deleteSelectedImages() {
        for (ImageItem image : selectedImages) {
            image.getStorageReference().delete().addOnSuccessListener(aVoid -> {
                imageItems.remove(image); // Remove the image from the list
                adapter.notifyDataSetChanged(); // Refresh the GridView
                Toast.makeText(getContext(), "Image deleted successfully.", Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(e -> {
                Log.e("BrowseImagesFragment", "Failed to delete image", e);
                Toast.makeText(getContext(), "Failed to delete some images.", Toast.LENGTH_SHORT).show();
            });
        }
        selectedImages.clear(); // Clear the selection after deletion
    }

    // Inner class to represent each image item
    private class ImageItem {
        private final String url;
        private final StorageReference storageReference;

        public ImageItem(String url, StorageReference storageReference) {
            this.url = url;
            this.storageReference = storageReference;
        }

        public String getUrl() {
            return url;
        }

        public StorageReference getStorageReference() {
            return storageReference;
        }
    }

    // Adapter to display images in the GridView
    private class ImageAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return imageItems.size();
        }

        @Override
        public Object getItem(int position) {
            return imageItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.grid_item_with_checkbox, parent, false);
            }

            ImageView imageView = convertView.findViewById(R.id.imageView);
            CheckBox checkBox = convertView.findViewById(R.id.checkbox);
            ImageItem imageItem = imageItems.get(position);

            // Load the image into the ImageView using Glide
            Glide.with(getContext()).load(imageItem.getUrl()).into(imageView);

            // Set up the checkbox
            checkBox.setChecked(selectedImages.contains(imageItem));
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    selectedImages.add(imageItem);
                } else {
                    selectedImages.remove(imageItem);
                }
            });

            return convertView;
        }
    }
}
