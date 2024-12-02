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
import android.widget.TextView;
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

/**
 * BrowseImagesFragment manages the display and deletion of images stored in Firebase Storage.
 * <p>
 * Users can view images in a grid layout, select multiple images using checkboxes, and delete the selected images
 * with confirmation through a custom dialog.
 */
public class BrowseImagesFragment extends Fragment {

    private GridView gridView;
    private FirebaseStorage storage;
    private ArrayList<ImageItem> imageItems;
    private Set<ImageItem> selectedImages;
    private ImageAdapter adapter;
    private View fabDelete;

    /**
     * Creates and initializes the view hierarchy for the fragment.
     *
     * @param inflater           The LayoutInflater object to inflate views in the fragment.
     * @param container          The parent view that this fragment's UI will be attached to.
     * @param savedInstanceState If non-null, contains data from the previous saved state.
     * @return The created view for the fragment.
     */
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

        // Set up the adapter and attach it to the GridView
        adapter = new ImageAdapter();
        gridView.setAdapter(adapter);

        // Fetch images from Firebase Storage
        fetchImages();

        // Set up the delete action
        fabDelete.setOnClickListener(v -> showDeleteConfirmationDialog());

        return view;
    }

    /**
     * Fetches images from the "event_images" folder in Firebase Storage.
     * <p>
     * The images are added to a list and displayed in the GridView.
     */
    private void fetchImages() {
        StorageReference storageRef = storage.getReference().child("event_images");

        storageRef.listAll().addOnSuccessListener((ListResult listResult) -> {
            imageItems.clear(); // Clear the list to avoid duplicates
            for (StorageReference item : listResult.getItems()) {
                item.getDownloadUrl().addOnSuccessListener(uri -> {
                    imageItems.add(new ImageItem(uri.toString(), item));
                    adapter.notifyDataSetChanged(); // Notify the adapter after adding each image
                }).addOnFailureListener(e -> Log.e("BrowseImagesFragment", "Failed to fetch image URL", e));
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), "Error fetching images.", Toast.LENGTH_SHORT).show();
            Log.e("BrowseImagesFragment", "Failed to list images", e);
        });
    }

    /**
     * Displays a custom confirmation dialog for deleting the selected images.
     * <p>
     * The dialog includes:
     * <ul>
     * <li>A title indicating the action ("Delete Selected?")</li>
     * <li>A message asking for confirmation</li>
     * <li>A "Yes" button to proceed with deletion</li>
     * <li>A "Nevermind" button to cancel the action</li>
     * </ul>
     */
    private void showDeleteConfirmationDialog() {
        if (selectedImages.isEmpty()) {
            Toast.makeText(getContext(), "No images selected for deletion.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Inflate the custom layout
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_confirmation, null);

        // Find views in the custom layout
        TextView title = dialogView.findViewById(R.id.dialog_title);
        TextView message = dialogView.findViewById(R.id.dialog_message);
        TextView btnYes = dialogView.findViewById(R.id.btn_yes);
        TextView btnNevermind = dialogView.findViewById(R.id.btn_nevermind);

        // Customize the dialog
        title.setText("Delete Selected?");
        message.setText("Are you sure you want to delete the selected images?");

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .setCancelable(true)
                .create();

        btnYes.setOnClickListener(v -> {
            deleteSelectedImages();
            dialog.dismiss();
        });

        btnNevermind.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    /**
     * Deletes the selected images from Firebase Storage and updates the GridView.
     */
    private void deleteSelectedImages() {
        ArrayList<ImageItem> imagesToDelete = new ArrayList<>(selectedImages); // Copy to avoid modification issues

        for (ImageItem image : imagesToDelete) {
            image.getStorageReference().delete().addOnSuccessListener(aVoid -> {
                imageItems.remove(image); // Remove the image from the list
                selectedImages.remove(image);
                adapter.notifyDataSetChanged(); // Refresh the GridView
                Toast.makeText(getContext(), "Image deleted successfully.", Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(e -> {
                Log.e("BrowseImagesFragment", "Failed to delete image", e);
                Toast.makeText(getContext(), "Failed to delete some images.", Toast.LENGTH_SHORT).show();
            });
        }
    }

    /**
     * Inner class representing an image item.
     * <p>
     * Each image item contains:
     * <ul>
     * <li>A URL for displaying the image</li>
     * <li>A reference to the Firebase Storage location</li>
     * </ul>
     */
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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ImageItem)) return false;
            ImageItem imageItem = (ImageItem) o;
            return url.equals(imageItem.url);
        }

        @Override
        public int hashCode() {
            return url.hashCode();
        }
    }

    /**
     * Adapter class for displaying images in the GridView.
     */
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
            checkBox.setOnCheckedChangeListener(null); // Remove existing listener to avoid interference
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
