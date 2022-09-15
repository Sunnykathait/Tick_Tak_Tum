package com.example.ticktaktum;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    TextView btn_createID , btn_createGame;
    TextView textView;

    String roomID_string , userName_join;

    EditText room_id_edttxt , user_name;

    int flag = 0;

    ArrayList<String> moves = new ArrayList<String>();

    FirebaseFirestore firebaseFirestore;

    int num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseFirestore = FirebaseFirestore.getInstance();

        textView = findViewById(R.id.roomid_txt);
        btn_createID = findViewById(R.id.button_createRoom);
        btn_createGame = findViewById(R.id.btn_createGame);

        room_id_edttxt = findViewById(R.id.join_roomID_edt);
        user_name = findViewById(R.id.userName);

        Intent intent = getIntent();

        num = intent.getExtras().getInt("cardNumber");

        if(num == 1){
            room_id_edttxt.setVisibility(View.GONE);
            btn_createGame.setText("Create Game");
        }
        if(num == 2){
            btn_createID.setVisibility(View.GONE);
            textView.setVisibility(View.GONE);
            btn_createGame.setText("Join Game");
        }

        btn_createID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textView.setText(getAlphaNumericString(6));
            }
        });

        btn_createGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(num == 1){
                    if(textView.getText().toString().length() == 0){
                        Toast.makeText(getApplicationContext() , "Room Id required to create a game(1)",Toast.LENGTH_SHORT).show();
                        return;
                    }

                    roomID_string = textView.getText().toString();

                    String userName_create = user_name.getText().toString();

                    if(userName_create.length() == 0){
                        userName_create = "Player 1";
                    }

                    UserInfo userInfo = new UserInfo(userName_create,"",false,moves,"X","none","none","none");
                    CollectionReference collectionReference = firebaseFirestore.collection("GAME");
                    collectionReference.document(textView.getText().toString())
                            .set(userInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(getApplicationContext() , "DONE",Toast.LENGTH_SHORT).show();

                                    SharedPreferences.Editor editor = getSharedPreferences("RoomID",Context.MODE_PRIVATE).edit();
                                    editor.putString("room",roomID_string);
                                    editor.apply();

                                    SharedPreferences.Editor editor1 = getSharedPreferences("Game",Context.MODE_PRIVATE).edit();
                                    editor1.putInt("whichPlayer",10);
                                    editor1.apply();

                                    Intent intent = new Intent(getApplicationContext() , GameActivity.class);
                                    intent.putExtra("roomID",roomID_string);
                                    startActivity(intent);
                                    finish();

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext() , "NOT DONE",Toast.LENGTH_SHORT).show();
                                }
                            });

                }

                if(num == 2){
                    if(room_id_edttxt.getText().toString().length() == 0){
                        Toast.makeText(getApplicationContext() , "Room Id required to create a game(2)",Toast.LENGTH_SHORT).show();
                        return;
                    }

                    roomID_string = room_id_edttxt.getText().toString();


                    userName_join = user_name.getText().toString();

                    if(userName_join.trim().length() == 0){
                        userName_join = "Player 2";
                    }

                    CollectionReference collectionReference = firebaseFirestore.collection("GAME");

                    DocumentReference documentReference = firebaseFirestore.collection("GAME").document(room_id_edttxt.getText().toString());

                    documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            Boolean taken = documentSnapshot.getBoolean("taken");

                            if(taken.equals(true)){
                                Toast.makeText(getApplicationContext() , "Room acquired already",Toast.LENGTH_SHORT).show();
                                return;
                            }
                            collectionReference.document(room_id_edttxt.getText().toString())
                                    .update("username2",userName_join);

                            collectionReference.document(room_id_edttxt.getText().toString())
                                    .update("taken",true);

                            SharedPreferences.Editor editor = getSharedPreferences("RoomID",Context.MODE_PRIVATE).edit();
                            editor.putString("room",roomID_string);
                            editor.apply();

                            SharedPreferences.Editor editor1 = getSharedPreferences("Game",Context.MODE_PRIVATE).edit();
                            editor1.putInt("whichPlayer",20);
                            editor1.apply();

                            Intent intent = new Intent(getApplicationContext() , GameActivity.class);
                            intent.putExtra("roomID",roomID_string);
                            startActivity(intent);
                            finish();
                        }
                    });
                }

                btn_createGame.setVisibility(View.GONE);
            }
        });

    }

    static String getAlphaNumericString(int n)
    {

        // chose a Character random from this String
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";

        // create StringBuffer size of AlphaNumericString
        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {

            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index
                    = (int)(AlphaNumericString.length()
                    * Math.random());

            // add Character one by one in end of sb
            sb.append(AlphaNumericString
                    .charAt(index));
        }

        return sb.toString();
    }
}