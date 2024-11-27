package com.example.mohgggdraw;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class BrowseUsersFragment extends Fragment {
    private FirebaseFirestore db;
    private ListView userListView;
    private ArrayList<String> userNames;
    private ArrayAdapter<String> adapter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_tab, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userListView = view.findViewById(R.id.user_list_view);

        // Initialize Firestore and ListView
        db = FirebaseFirestore.getInstance();
        userListView = view.findViewById(R.id.user_list_view);
        userNames = new ArrayList<>();

        // Create ArrayAdapter to populate the ListView
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, userNames);
        userListView.setAdapter(adapter);

        fetchUserNames();
    }

    private void fetchUserNames() {
        // Get a reference to the "user" collection in Firestore
        db.collection("user")
                .get()  // Fetch all documents
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Loop through all documents in the "user" collection
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Get the name field from each document
                            String userName = document.getString("name");

                            if (userName != null) {
                                // Add the name to the list
                                userNames.add(userName);
                            }
                        }
                        // Notify the adapter to update the ListView
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getActivity(), "Error getting documents: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
