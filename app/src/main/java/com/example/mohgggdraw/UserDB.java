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

    public void getUserMapFromID(String id, TextView userName, ImageView image){

        myDoc = waitlistRef.document((String.valueOf(id)));
        Task<DocumentSnapshot> query = myDoc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                data = documentSnapshot.getData();
                if(data!=  null) {
                    userName.setText((String) data.get("name"));
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
    public void addEventToUserList(String eventID,String userID){
        DocumentReference mydoc = waitlistRef.document(userID);
        mydoc.update("waitList", FieldValue.arrayUnion(eventID));


    }

    public void removeEventToUserList(String eventID,String userID){
        DocumentReference mydoc = waitlistRef.document(userID);
        mydoc.update("waitList", FieldValue.arrayRemove(eventID));


    }

    public void queryAllListFromUser(String id, EventListView frag){
        myDoc = waitlistRef.document((String.valueOf(id)));
        Task<DocumentSnapshot> query = myDoc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Map data = documentSnapshot.getData();
                if(data!= null) {
                    ArrayList<String> waitList = (ArrayList) data.get("waitList");
                    ArrayList<String> createList = (ArrayList) data.get("createdList");
                    ArrayList<String> entrantList = (ArrayList) data.get("entrantList");
                    new WaitinglistDB().setAllUserLists(frag, waitList,createList,entrantList);
                }


            }
        });


    }

}
