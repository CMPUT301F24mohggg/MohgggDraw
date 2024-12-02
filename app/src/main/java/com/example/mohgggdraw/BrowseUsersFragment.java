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

public class BrowseUsersFragment extends Fragment {

    private ListView userListView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> userNames;
    private ArrayList<String> userIds;
    private FirebaseFirestore db;
    private View fabDelete;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_tab, container, false);

        userListView = view.findViewById(R.id.user_list_view);
        fabDelete = view.findViewById(R.id.fab_delete);

        db = FirebaseFirestore.getInstance();
        userNames = new ArrayList<>();
        userIds = new ArrayList<>();

        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_multiple_choice, userNames);
        userListView.setAdapter(adapter);
        userListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        fetchUsers();

        fabDelete.setOnClickListener(v -> showDeleteConfirmationDialog());

        return view;
    }

    private void fetchUsers() {
        db.collection("user").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                userNames.clear();
                userIds.clear();

                for (QueryDocumentSnapshot document : task.getResult()) {
                    String name = document.getString("name");
                    String id = document.getId();
                    Long userType = document.getLong("userType");

                    if (name != null && userType != null && userType == 0) {
                        userNames.add(name);
                        userIds.add(id);
                    }
                }
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(getActivity(), "Failed to fetch users.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDeleteConfirmationDialog() {
        ArrayList<String> selectedIds = new ArrayList<>();
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
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_confirmation, null);

        // Set up the dialog views
        TextView title = dialogView.findViewById(R.id.dialog_title);
        TextView message = dialogView.findViewById(R.id.dialog_message);
        TextView btnYes = dialogView.findViewById(R.id.btn_yes);
        TextView btnNevermind = dialogView.findViewById(R.id.btn_nevermind);

        title.setText("Delete Selected?");
        message.setText("Are you sure you want to delete the selected users?");

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .setCancelable(true)
                .create();

        btnYes.setOnClickListener(v -> {
            deleteSelectedUsers(selectedIds);
            dialog.dismiss();
        });

        btnNevermind.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void deleteSelectedUsers(ArrayList<String> selectedIds) {
        for (String id : selectedIds) {
            db.collection("user").document(id).delete().addOnSuccessListener(aVoid -> {
                Toast.makeText(getActivity(), "Deleted successfully.", Toast.LENGTH_SHORT).show();
                fetchUsers();
            }).addOnFailureListener(e -> Toast.makeText(getActivity(), "Failed to delete.", Toast.LENGTH_SHORT).show());
        }
    }
}
