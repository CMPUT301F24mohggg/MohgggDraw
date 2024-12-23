package com.example.mohgggdraw;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

/**
 * BrowseUsersFragment manages the UI and functionality for browsing and managing user profiles of type "entrant".
 * Features:
 * - Displays a list of users with userType 0 (entrants) in a ListView.
 * - Allows multiple users to be selected and deleted.
 * - Uses Firestore for fetching and deleting user data.
 */
public class BrowseUsersFragment extends Fragment {

    private ListView userListView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> userNames;
    private ArrayList<String> userIds;
    private FirebaseFirestore db;
    private View fabDelete;

    /**
     * Inflates the fragment layout and initializes the ListView and delete button.
     *
     * @param inflater           The LayoutInflater object used to inflate views.
     * @param container          The parent view that this fragment's UI will be attached to.
     * @param savedInstanceState If non-null, contains the previous saved state of the fragment.
     * @return The created view for this fragment.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_tab, container, false);

        // Initialize UI components
        userListView = view.findViewById(R.id.user_list_view);
        fabDelete = view.findViewById(R.id.fab_delete);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize lists and adapter
        userNames = new ArrayList<>();
        userIds = new ArrayList<>();
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_multiple_choice, userNames);
        userListView.setAdapter(adapter);
        userListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        // Fetch user data
        fetchUsers();

        // Set up delete button click listener
        fabDelete.setOnClickListener(v -> showDeleteConfirmationDialog());

        return view;
    }

    /**
     * Fetches users with userType 0 (entrants) from Firestore and populates the ListView.
     */
    private void fetchUsers() {
        db.collection("user").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                userNames.clear();
                userIds.clear();

                for (QueryDocumentSnapshot document : task.getResult()) {
                    String name = document.getString("name");
                    String id = document.getId();
                    Long userType = document.getLong("userType");

                    // Add users of type "entrant" to the list
                    if (name != null && userType != null && userType == 0) {
                        userNames.add(name);
                        userIds.add(id);
                    }
                }
                adapter.notifyDataSetChanged(); // Refresh the ListView
            } else {
                Toast.makeText(getActivity(), "Failed to fetch users.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Displays a custom confirmation dialog to confirm the deletion of selected users.
     */
    private void showDeleteConfirmationDialog() {
        ArrayList<String> selectedIds = new ArrayList<>();

        // Identify the selected users
        for (int i = 0; i < userListView.getCount(); i++) {
            if (userListView.isItemChecked(i)) {
                selectedIds.add(userIds.get(i));
            }
        }

        if (selectedIds.isEmpty()) {
            Toast.makeText(getActivity(), "No users selected for deletion.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Inflate the custom layout for the dialog
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.custom_confirmation_dialog, null);

        // Build the AlertDialog
        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setCancelable(false)
                .create();

        // Ensure rounded background and proper dimensions
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        // Set up dialog views
        TextView titleTextView = dialogView.findViewById(R.id.title_text_view);
        TextView messageTextView = dialogView.findViewById(R.id.message_text_view);
        TextView cancelTextView = dialogView.findViewById(R.id.cancel_text_view);
        View confirmButton = dialogView.findViewById(R.id.confirm_button);

        // Customize dialog text
        titleTextView.setText("Delete Selected?");
        messageTextView.setText("Are you sure you want to delete the selected users?");

        // Set up dialog button listeners
        confirmButton.setOnClickListener(v -> {
            deleteSelectedUsers(selectedIds);
            dialog.dismiss();
        });
        cancelTextView.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    /**
     * Deletes the selected users from Firestore.
     *
     * @param selectedIds The IDs of the users to be deleted.
     */
    private void deleteSelectedUsers(ArrayList<String> selectedIds) {
        for (String id : selectedIds) {
            db.collection("user").document(id).delete().addOnSuccessListener(aVoid -> {
                Toast.makeText(getActivity(), "Deleted successfully.", Toast.LENGTH_SHORT).show();
                fetchUsers(); // Refresh the ListView after deletion
            }).addOnFailureListener(e -> Toast.makeText(getActivity(), "Failed to delete.", Toast.LENGTH_SHORT).show());
        }
    }
}
