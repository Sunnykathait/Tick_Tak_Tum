package com.example.ticktaktum;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;

public class waitAcitivity extends AppCompatActivity {

    FirebaseFirestore firebaseFirestore;
    String roomID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait_acitivity);

        firebaseFirestore = FirebaseFirestore.getInstance();

        Intent intent = getIntent();

        roomID = intent.getStringExtra("roomID");

        DocumentReference documentReference1 = firebaseFirestore.collection("GAME").document(roomID);

        documentReference1.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null){
                    Log.d("ERROR", error.getMessage());
                    return;
                }

                if(value != null && value.exists()) {
                    String user1_again , user2_again;
                    user1_again = value.getString("user1_again");
                    user2_again = value.getString("user2_again");

                    if(user1_again.equals("yes") && user2_again.equals("yes")){
                        Intent intent1 = new Intent(getApplicationContext() , GameActivity.class);
                        intent1.putExtra("roomID",roomID);
                        startActivity(intent1);
                        finish();
                    }else if(user1_again.equals("yes") && user2_again.equals("no")){
                        deleteDocument();
                    }else if(user1_again.equals("no") && user2_again.equals("yes")){
                        deleteDocument();
                    }else if(user1_again.equals("no") && user2_again.equals("no")){
                        deleteDocument();
                    }

                }
            }
        });
    }

    public  void deleteDocument(){
        Intent intent = new Intent(getApplicationContext() , FIrstAcitivity.class);
        startActivity(intent);
        updateSharedPreference();
        finish();

        DocumentReference documentReference = firebaseFirestore.collection("GAME").document(roomID);
        documentReference.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext() , "Document not deleted",Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void updateSharedPreference(){
        SharedPreferences sharedPreferences = getSharedPreferences("Game", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("inGame",0);

        editor.apply();
    }

}