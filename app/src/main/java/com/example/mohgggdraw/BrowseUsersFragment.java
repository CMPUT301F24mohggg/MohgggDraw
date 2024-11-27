package com.example.mohgggdraw;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class BrowseUsersFragment extends Fragment {
    private FirebaseFirestore db;
    private ListView userListView;
    private ArrayList<String> userNames;
    private ArrayList<String> filteredUserNames; // To hold filtered results
    private ArrayAdapter<String> adapter;
    private EditText searchBar;

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

        // Initialize views
        userListView = view.findViewById(R.id.user_list_view);
        searchBar = view.findViewById(R.id.search_bar);

        // Initialize Firestore and ListView
        db = FirebaseFirestore.getInstance();
        userNames = new ArrayList<>();
        filteredUserNames = new ArrayList<>();

        // Create ArrayAdapter to populate the ListView
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, filteredUserNames);
        userListView.setAdapter(adapter);

        // Fetch user names from Firestore
        fetchUserNames();

        // Set up search listener
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            // Refilters the user list after text changed
            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                filterUsers(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
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
                        // Initially show all users in the ListView
                        filteredUserNames.addAll(userNames);
                        // Notify the adapter to update the ListView
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getActivity(), "Error getting documents: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void filterUsers(String query) {
        /***
         * Filters the user list with string query
         */

        // Clear the filtered list
        filteredUserNames.clear();

        // If the query is empty, show all users
        if (query.isEmpty()) {
            filteredUserNames.addAll(userNames);
        } else {
            // Filter the user names based on the query
            for (String userName : userNames) {
                if (userName.toLowerCase().contains(query.toLowerCase())) {
                    filteredUserNames.add(userName);
                }
            }
        }

        // Notify the adapter to update the ListView with the filtered data
        adapter.notifyDataSetChanged();
    }
}