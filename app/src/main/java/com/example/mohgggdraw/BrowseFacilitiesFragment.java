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


public class BrowseFacilitiesFragment extends Fragment {
    private FirebaseFirestore db;
    private ListView userListView;
    private ArrayList<String> userNames;
    private ArrayAdapter<String> adapter;
//    private EditText searchBar; // Commented out search bar

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
//        searchBar = view.findViewById(R.id.search_bar); // Commented out initialization of search bar

        // Initialize Firestore and ListView
        db = FirebaseFirestore.getInstance();
        userNames = new ArrayList<>();

        // Create ArrayAdapter to populate the ListView
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, userNames);
        userListView.setAdapter(adapter);

        // Fetch user names from Firestore
        fetchUserNames();

        // Commented-out search listener
        /*
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                filterUsers(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
        */
    }

    private void fetchUserNames() {
        // Get a reference to the "user" collection in Firestore
        db.collection("user")
                .get()  // Fetch all documents
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Loop through all documents in the "user" collection
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Get the name and userType
                            String userName = document.getString("name");
                            Long userType = document.getLong("userType");

                            // Only add users with userType 1 (Organizers)
                            if (userName != null && userType != null && userType == 1) {
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

    // Commented-out filtering functionality
    /*
    private void filterUsers(String query) {
        // Filters the user list with string query
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
    */
}