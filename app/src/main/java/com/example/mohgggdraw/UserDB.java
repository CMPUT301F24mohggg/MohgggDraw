package com.example.mohgggdraw;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;


/**
 *Access point to the user collection in the firebase
 * */
public class UserDB {
    private FirebaseFirestore db;
    private CollectionReference waitlistRef;
    private String name;
    private DocumentReference myDoc;
    private Map data;

    public UserDB() {
        db = FirebaseFirestore.getInstance();
        waitlistRef= db.collection("user");
    }


    /**
     *Gets a user map from the user id string and sets it in the list
     *
     * @param id
     * @param userName
     * @param image
     * */
    public void getUserMapFromID(String id, TextView userName, ImageView image){
        //query id in userdoc
        myDoc = waitlistRef.document((String.valueOf(id)));
        Task<DocumentSnapshot> query = myDoc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                data = documentSnapshot.getData();
                if(data!=  null) {
                    userName.setText((String) data.get("name"));
                    //creating and setting image
                    if (data.get("profileImageUrl") != null) {
                        try {
                            File imageFile = File.createTempFile(
                                    "u" + (String) data.get("deviceID"), // Handle null titles
                                    ".png"
                            );
                            StorageReference myImage = new WaitinglistDB().getImage((String) data.get("profileImageUrl"));
                            myImage.getFile(imageFile)
                                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                            Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                                            String path = imageFile.getAbsolutePath();
                                            image.setImageBitmap(bitmap);
                                        }
                                    });
                        } catch (IOException e) {
                            e.printStackTrace();
                            // Optionally set a placeholder image on error
                            image.setImageResource(R.drawable.eventpage_banner_placeholder);
                        }
                    } else {
                        // Set a placeholder image if no URL is provided
                        image.setImageResource(R.drawable.eventpage_banner_placeholder);
                    }
                }


            }
        });
    }

    /**
     * creates a list of eventIDs from the userID and what eventlist name is
     * then creates the event list to be pushed to event listview frags
     *
     * */
    public void queryList(String listName, EventListView frag,String id){
        myDoc = waitlistRef.document((String.valueOf(id)));
        Task<DocumentSnapshot> query = myDoc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                Map data = documentSnapshot.getData();
                if(data!= null) {
                    ArrayList<String> list = (ArrayList) data.get(listName);
                    new WaitinglistDB().createEventListFromStringList(list, frag);
                }
            }
        });





    }
    /**
     * adds event to user waitlist
     * */
    public void addEventToUserList(String eventID,String userID){
        DocumentReference mydoc = waitlistRef.document(userID);
        mydoc.update("waitList", FieldValue.arrayUnion(eventID));


    }
    /**
     *removes event from user waitlist
     * */
    public void removeEventToUserList(String eventID,String userID){
        DocumentReference mydoc = waitlistRef.document(userID);
        mydoc.update("waitList", FieldValue.arrayRemove(eventID));


    }

}
