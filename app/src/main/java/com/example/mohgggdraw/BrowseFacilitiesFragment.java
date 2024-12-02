package com.example.mohgggdraw;

import android.app.AlertDialog;
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
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

/**
 * BrowseFacilitiesFragment manages the display, selection, and deletion of facilities in the app.
 * <p>
 * This fragment retrieves facilities from the Firestore database where the user type is 1 (organizer)
 * and displays them in a multi-selectable list view. Users can select multiple facilities and delete them
 * with a confirmation dialog.
 */
public class BrowseFacilitiesFragment extends Fragment {

    private ListView facilityListView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> facilityNames;
    private ArrayList<String> facilityIds;
    private FirebaseFirestore db;
    private View fabDelete;

    /**
     * Creates and initializes the view hierarchy for the fragment.
     *
     * @param inflater           The LayoutInflater object to inflate views in the fragment.
     * @param container          The parent view that this fragment's UI will be attached to.
     * @param savedInstanceState If non-null, contains data from the previous saved state.
     * @return The created view for the fragment.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_tab, container, false);

        facilityListView = view.findViewById(R.id.user_list_view);
        fabDelete = view.findViewById(R.id.fab_delete);

        db = FirebaseFirestore.getInstance();
        facilityNames = new ArrayList<>();
        facilityIds = new ArrayList<>();

        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_multiple_choice, facilityNames);
        facilityListView.setAdapter(adapter);
        facilityListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        fetchFacilities();

        fabDelete.setOnClickListener(v -> showDeleteConfirmationDialog());

        return view;
    }

    /**
     * Fetches facilities from Firestore where the userType is 1 (organizer) and populates the list view.
     */
    private void fetchFacilities() {
        db.collection("user").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                facilityNames.clear();
                facilityIds.clear();

                for (QueryDocumentSnapshot document : task.getResult()) {
                    String name = document.getString("name");
                    String id = document.getId();
                    Long userType = document.getLong("userType");

                    if (name != null && userType != null && userType == 1) {
                        facilityNames.add(name);
                        facilityIds.add(id);
                    }
                }
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(getActivity(), "Failed to fetch facilities.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Displays a custom confirmation dialog for deleting the selected facilities.
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
        ArrayList<String> selectedIds = new ArrayList<>();
        for (int i = 0; i < facilityListView.getCount(); i++) {
            if (facilityListView.isItemChecked(i)) {
                selectedIds.add(facilityIds.get(i));
            }
        }

        if (selectedIds.isEmpty()) {
            Toast.makeText(getActivity(), "No facilities selected for deletion.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Inflate the custom layout for the dialog
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_confirmation, null);

        // Set up the dialog views
        TextView title = dialogView.findViewById(R.id.dialog_title);
        TextView message = dialogView.findViewById(R.id.dialog_message);
        TextView btnYes = dialogView.findViewById(R.id.btn_yes);
        TextView btnNevermind = dialogView.findViewById(R.id.btn_nevermind);

        title.setText("Delete Selected?");
        message.setText("Are you sure you want to delete the selected facilities?");

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .setCancelable(true)
                .create();

        btnYes.setOnClickListener(v -> {
            deleteSelectedFacilities(selectedIds);
            dialog.dismiss();
        });

        btnNevermind.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    /**
     * Deletes the selected facilities from Firestore and updates the list view.
     *
     * @param selectedIds A list of Firestore document IDs for the selected facilities.
     */
    private void deleteSelectedFacilities(ArrayList<String> selectedIds) {
        for (String id : selectedIds) {
            db.collection("user").document(id).delete().addOnSuccessListener(aVoid -> {
                Toast.makeText(getActivity(), "Deleted successfully.", Toast.LENGTH_SHORT).show();
                fetchFacilities();
            }).addOnFailureListener(e -> Toast.makeText(getActivity(), "Failed to delete.", Toast.LENGTH_SHORT).show());
        }
    }
}
