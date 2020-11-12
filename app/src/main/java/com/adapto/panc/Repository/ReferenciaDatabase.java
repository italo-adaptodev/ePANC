package com.adapto.panc.Repository;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ReferenciaDatabase {

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseFirestore databaseFirestore = FirebaseFirestore.getInstance();


    private FirebaseStorage  firebaseStorage = FirebaseStorage.getInstance();

    public ReferenciaDatabase() {
    }

    public DatabaseReference getDatabaseReference(String child){
        return  database.getReference().child(child);
    }


    public FirebaseFirestore getDatabaseFirestore() {
        return databaseFirestore;
    }

    public StorageReference getFirebaseStorage() {
        return firebaseStorage.getReference();
    }

}
