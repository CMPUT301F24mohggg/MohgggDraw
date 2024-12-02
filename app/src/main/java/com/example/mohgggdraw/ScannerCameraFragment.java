package com.example.mohgggdraw;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

/***
 * This fragment starts an Intent for QR scanner. It:
 * - Uses the camera to scan a QR code
 * - Displays the event details if the QR code is valid
 ***/
public class ScannerCameraFragment extends Fragment {
    private ScannerViewModel scannerViewModel;
    private String qrHash;
    private WaitinglistDB waitinglistDB = new WaitinglistDB();
    private CollectionReference eventRef = waitinglistDB.getWaitlistRef();

    /**
     * Inflates the layout for this fragment.
     *
     * @param inflater LayoutInflater to inflate the views in this fragment.
     * @param container The parent view to attach this fragment to.
     * @param savedInstanceState The previously saved state of the fragment, if any.
     * @return The root view of the fragment.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        scannerViewModel = new ViewModelProvider(requireActivity()).get(ScannerViewModel.class);
        return inflater.inflate(R.layout.fragment_scanner_camera, container, false);
    }

    /**
     * Resumes the fragment and starts the QR scanner if required.
     */
    @Override
    public void onResume() {
        super.onResume();
        if (scannerViewModel.getScanStatus() == 1) {
            scannerViewModel.setScanStatus(0);
            startScanner();
        }
    }

    /**
     * Launches the scanner activity and handles the result.
     */
    ActivityResultLauncher<Intent> ScannerActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                /**
                 * Handles the result from the scanner activity.
                 *
                 * @param result The result from the scanner activity.
                 */
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent resultIntent = result.getData();
                        qrHash = resultIntent.getStringExtra("qrTextKey");

                        // Validate and retrieve event by QR hash
                        validQrHash(qrHash);
                    }
                }
            });

    /**
     * Validates the QR hash and retrieves the event details.
     *
     * @param qrHash The hash extracted from the scanned QR code.
     */
    private void validQrHash(String qrHash) {
        // Validate the QR hash format: numeric characters, optionally starting with a "-"
        if (qrHash == null || !qrHash.matches("^-?[0-9]+$")) {
            setToPage(1); // Invalid QR code, go to error page
            return;
        }

        qrHash = qrHash.trim(); // Ensure there are no leading/trailing spaces

        // Query Firestore to find the document with the matching QR hash
        Query query = eventRef.whereEqualTo("QRhash", qrHash);
        query.get().addOnCompleteListener(new OnCompleteListener<com.google.firebase.firestore.QuerySnapshot>() {
            /**
             * Handles the result of the Firestore query for the QR hash.
             *
             * @param task The task representing the Firestore query result.
             */
            @Override
            public void onComplete(@NonNull Task<com.google.firebase.firestore.QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (!task.getResult().isEmpty()) {
                        // Retrieve the first matching DocumentSnapshot
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);

                        // Check if the document has data
                        if (document.exists()) {
                            // Convert the document into an Event object
                            Event event = returnEvent(document);

                            // Pass the event to the ViewModel
                            scannerViewModel.setSharedEvent(event);

                            // Navigate to the event details page
                            setToPage(2);
                        } else {
                            // Document exists in the result set but has no data
                            Log.d(TAG, "Document exists but has no data");
                            setToPage(1); // Navigate to the error page
                        }
                    } else {
                        // No matching documents found
                        setToPage(1); // Navigate to the error page
                    }
                } else {
                    // Log query failure
                    Log.d(TAG, "Query failed with ", task.getException());
                }
            }
        });
    }

    /**
     * Navigates to the specified page.
     *
     * @param position The page position to navigate to.
     */
    private void setToPage(int position) {
        ((ScannerFragment) requireParentFragment()).swapToFragment(position);
    }

    /**
     * Starts the QR scanner activity.
     */
    private void startScanner() {
        // Start QR scanner Activity
        Intent intent = new Intent(getActivity(), ScannerActivity.class);
        ScannerActivityResultLauncher.launch(intent);
    }

    /**
     * Converts a Firestore DocumentSnapshot into an Event object.
     *
     * @param document The Firestore document to convert.
     * @return The Event object created from the document.
     */
    private Event returnEvent(DocumentSnapshot document) {
        Event event = waitinglistDB.docSnapshotToEvent(document);
        return event;
    }
}