package com.example.mohgggdraw;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class BrowseFacilitiesFragment extends Fragment {

    private ListView facilityListView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> facilityNames;
    private ArrayList<String> facilityIds;
    private FirebaseFirestore db;
    private View fabDelete;

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

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Delete Facilities?");
        builder.setMessage("Are you sure you want to delete the selected facilities?");
        builder.setPositiveButton("Yes", (dialog, which) -> deleteSelectedFacilities(selectedIds));
        builder.setNegativeButton("Nevermind", null);
        builder.show();
    }

    private void deleteSelectedFacilities(ArrayList<String> selectedIds) {
        for (String id : selectedIds) {
            db.collection("user").document(id).delete().addOnSuccessListener(aVoid -> {
                Toast.makeText(getActivity(), "Deleted successfully.", Toast.LENGTH_SHORT).show();
                fetchFacilities();
            }).addOnFailureListener(e -> Toast.makeText(getActivity(), "Failed to delete.", Toast.LENGTH_SHORT).show());
        }
    }
}
