package com.example.ticktaktum;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;

public class GameActivity extends AppCompatActivity {

    TextView user1 , user2;
    FirebaseFirestore firebaseFirestore;

    String turn = "X";

    String whoWon;

    String roomID;

    private static final int TIME_INTERVAL = 2000;
    private long mBackPressed;

    int whichPlayer;

    String playerTurn;

    ArrayList<String> show_moves;

    TextView cardView1 , cardView2 , cardView3 , cardView4 , cardView5 , cardView6 , cardView7
            , cardView8 , cardView9;

    String player1_name , player2_name;

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        user1 = findViewById(R.id.userName_player1);
        user2 = findViewById(R.id.userName_player2);

        Intent intent = getIntent();
        roomID = intent.getStringExtra("roomID");

        SharedPreferences.Editor editor1 = getSharedPreferences("Game",Context.MODE_PRIVATE).edit();
        editor1.putInt("inGame",1);
        editor1.apply();

        SharedPreferences preferences = getSharedPreferences("Game", Context.MODE_PRIVATE);
        whichPlayer = preferences.getInt("whichPlayer",0);

        firebaseFirestore = FirebaseFirestore.getInstance();

        DocumentReference documentReference = firebaseFirestore.collection("GAME").document(roomID);

        cardView1 = findViewById(R.id.card1);
        cardView2 = findViewById(R.id.card2);
        cardView3 = findViewById(R.id.card3);
        cardView4 = findViewById(R.id.card4);
        cardView5 = findViewById(R.id.card5);
        cardView6 = findViewById(R.id.card6);
        cardView7 = findViewById(R.id.card7);
        cardView8 = findViewById(R.id.card8);
        cardView9 = findViewById(R.id.card9);

        cardView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initialize_check(roomID,"1" , cardView1);
            }
        });

        cardView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initialize_check(roomID,"2",cardView2);
            }
        });

        cardView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initialize_check(roomID,"3",cardView3);
            }
        });

        cardView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initialize_check(roomID,"4",cardView4);
            }
        });

        cardView5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initialize_check(roomID,"5" , cardView5);
            }
        });

        cardView6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initialize_check(roomID,"6", cardView6);
            }
        });

        cardView7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initialize_check(roomID,"7" , cardView7);
            }
        });

        cardView8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initialize_check(roomID,"8", cardView8);
            }
        });

        cardView9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initialize_check(roomID,"9" , cardView9);
            }
        });

        // getting players name
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot documentSnapshot = task.getResult();
                player1_name = documentSnapshot.getString("username1");
                player2_name = documentSnapshot.getString("username2");

                user1.setText(player1_name);
                user2.setText(player2_name);

            }
        });

        DocumentReference documentReference1 = firebaseFirestore.collection("GAME").document(roomID);

        // checking if any player left the game
        documentReference1.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(documentSnapshot.exists()){
                        return;
                    }else{
                        Toast.makeText(getApplicationContext() , "Other player left the match",Toast.LENGTH_SHORT).show();
                        SharedPreferences sharedPreferences = getSharedPreferences("Game", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt("inGame",0);

                        editor.apply();

                        Intent intent = new Intent(getApplicationContext() , FIrstAcitivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        });


        // checking for updates
        documentReference1.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null){
                    Log.d("ERROR", error.getMessage());
                    return;
                }

                if(value != null && value.exists()) {
                    int turn = 0;
                    player2_name = value.getString("username2");
                    show_moves = (ArrayList<String>) value.get("moves");
                    user2.setText(player2_name);
                    updateCards(show_moves);
                    String checkWinner = value.getString("won");
                    if(!checkWinner.equals("none")){
                        showReplayCard();
                    }
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

    // checking if both player arrived , to check if the playing on his turn
    public void initialize_check(String roomID , String number , TextView textView){
        DocumentReference documentReference = firebaseFirestore.collection("GAME").document(roomID);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot documentSnapshot = task.getResult();
                playerTurn = documentSnapshot.getString("turn");
                String isValidToPlay = documentSnapshot.getString("username2");

                if(isValidToPlay.equals("")){
                    Toast.makeText(getApplicationContext() , "Second player not arrived yet..... ",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(show_moves.contains(number)){
                    Toast.makeText(getApplicationContext() , "Move already taken",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(whichPlayer == 10 && playerTurn.equals("X")){
                    documentReference.update("turn","O");
                    documentReference.update("moves",FieldValue.arrayUnion(number));
                    textView.setText("X");
                }
                else if(whichPlayer == 20 && playerTurn.equals("O")){
                    documentReference.update("moves",FieldValue.arrayUnion(number));
                    documentReference.update("turn","X");
                    textView.setText("O");
                }
                else {
                    Toast.makeText(getApplicationContext(),"Not your turn broooo..",Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }

    public void updateCards(ArrayList<String> arrayList){
        Resources resources = getResources();
        if(arrayList.size() >= 5){
            if(checkForWin()){
                showReplayCard();
                return;
            }
        }
        for(int i = 0 ; i < arrayList.size() ; i++){
            if(i % 2 == 0){
                String id_name = "card"+ arrayList.get(i);
                int id = resources.getIdentifier(id_name,"id",getPackageName());
                TextView textView = findViewById(id);
                textView.setText("X");
            }
            if(i % 2 != 0){
                String id_name = "card"+ arrayList.get(i);
                int id = resources.getIdentifier(id_name,"id",getPackageName());
                TextView textView = findViewById(id);
                textView.setText("O");
            }
        }
    }

    public boolean checkForWin() {
        int flag = 0;
        if (!cardView1.getText().toString().isEmpty() && cardView1.getText().toString().equals(cardView2.getText().toString())  && cardView1.getText().toString().equals(cardView3.getText().toString())) {
            Toast.makeText(getApplicationContext(), cardView1.getText().toString() + "Won by condition 1", Toast.LENGTH_SHORT).show();
            flag = 1;
            whoWon = cardView1.getText().toString();
        } else if (!cardView4.getText().toString().isEmpty() && cardView4.getText().toString().equals(cardView5.getText().toString()) && cardView4.getText().toString().equals(cardView6.getText().toString())) {
            Toast.makeText(getApplicationContext(), cardView4.getText().toString() + "Won by condition 2", Toast.LENGTH_SHORT).show();
            flag = 1;
            whoWon = cardView4.getText().toString();

        } else if (!cardView7.getText().toString().isEmpty() && cardView7.getText().toString().equals(cardView8.getText().toString()) && cardView7.getText().toString().equals(cardView9.getText().toString())) {
            Toast.makeText(getApplicationContext(), cardView7.getText().toString() + "Won by condition 3", Toast.LENGTH_SHORT).show();
            flag = 1;
            whoWon = cardView7.getText().toString();

        } else if (!cardView1.getText().toString().isEmpty() && cardView1.getText().toString().equals(cardView4.getText().toString()) && cardView1.getText().toString().equals(cardView7.getText().toString())) {
            Toast.makeText(getApplicationContext(), cardView1.getText().toString() + "Won by condition 4", Toast.LENGTH_SHORT).show();
            flag = 1;
            whoWon = cardView1.getText().toString();

        }
        else if (!cardView2.getText().toString().isEmpty() && cardView2.getText().toString().equals(cardView5.getText().toString()) && cardView2.getText().toString().equals(cardView8.getText().toString())) {
            Toast.makeText(getApplicationContext(), cardView2.getText().toString() + "Won by condition 5", Toast.LENGTH_SHORT).show();
            flag = 1;
            whoWon = cardView2.getText().toString();

        }
        else if (!cardView3.getText().toString().isEmpty() && cardView3.getText().toString().equals(cardView6.getText().toString()) && cardView3.getText().toString().equals(cardView9.getText().toString())) {
            Toast.makeText(getApplicationContext(), cardView1.getText().toString() + "Won by condition 6", Toast.LENGTH_SHORT).show();
            flag = 1;
            whoWon = cardView3.getText().toString();

        }
        else if (!cardView1.getText().toString().isEmpty() && cardView1.getText().toString().equals(cardView5.getText().toString()) && cardView1.getText().toString().equals(cardView9.getText().toString())) {
            Toast.makeText(getApplicationContext(), cardView1.getText().toString() + " Won by condition 7", Toast.LENGTH_SHORT).show();
            flag = 1;
            whoWon = cardView1.getText().toString();

        }
        else if (!cardView3.getText().toString().isEmpty() && cardView3.getText().toString().equals(cardView5.getText().toString()) && cardView5.getText().toString().equals(cardView7.getText().toString())) {
            Toast.makeText(getApplicationContext(), cardView1.getText().toString() + "Won by condition 8", Toast.LENGTH_SHORT).show();
            flag = 1;
            whoWon = cardView3.getText().toString();
        }

        if(flag == 1){
            DocumentReference documentReference = firebaseFirestore.collection("GAME").document(roomID);
            documentReference.update("won",whoWon);
            return true;
        }

        return false;

    }

    @Override
    public void onBackPressed() {
        if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {

            SharedPreferences sharedPreferences = getSharedPreferences("Game", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("inGame",0);

            editor.apply();

            DocumentReference documentReference = firebaseFirestore.collection("GAME").document(roomID);

            documentReference.delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(getApplicationContext() , "Document deleted",Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext() , "Document not deleted",Toast.LENGTH_SHORT).show();
                        }
                    });

            Intent intent = new Intent(getApplicationContext() , FIrstAcitivity.class);
            startActivity(intent);
            finish();

            return;

        } else {
            Toast.makeText(getBaseContext(), "Click two times to close an activity",    Toast.LENGTH_SHORT).show();
        }
        mBackPressed = System.currentTimeMillis();
    }

    public void showReplayCard(){
        LayoutInflater factory = LayoutInflater.from(getApplicationContext());
        final  View view = factory.inflate(R.layout.custom_dailog_box,null);
        final  AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setView(view);

        TextView textView = (TextView) view.findViewById(R.id.playerWho_won);


        if(whoWon.equals("X")){
            textView.setText(player1_name);
        }else {
            textView.setText(player2_name);
        }

        view.findViewById(R.id.playAgain).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> new_moves = new ArrayList<String>();
                DocumentReference collectionReference = firebaseFirestore.collection("GAME").document(roomID);
                collectionReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        String playAgain_user1 = documentSnapshot.getString("user1_again");
                        String playAgain_user2 = documentSnapshot.getString("user2_again");

                        if(whichPlayer == 10){
                            if(playAgain_user1.equals("none")){
                                collectionReference.update("user1_again","yes");
                                Intent intent = new Intent(getApplicationContext() , waitAcitivity.class);
                                collectionReference.update("moves",new_moves);
                                intent.putExtra("roomID",roomID);
                                dialog.dismiss();
                                startActivity(intent);
                                finish();
                            }
                        }
                        else if(whichPlayer == 20){
                            if(playAgain_user2.equals("none")){
                                collectionReference.update("user2_again","yes");
                                Intent intent = new Intent(getApplicationContext() , waitAcitivity.class);
                                intent.putExtra("roomID",roomID);
                                collectionReference.update("moves",new_moves);
                                dialog.dismiss();
                                startActivity(intent);
                                finish();
                            }
                        }
                    }
                });
            }
        });

        view.findViewById(R.id.cancel_play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DocumentReference collectionReference = firebaseFirestore.collection("GAME").document(roomID);
                collectionReference.update("playAgain","no");
                dialog.dismiss();

                deleteDocument();

            }
        });

        dialog.show();

    }

    public  void deleteDocument(){
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

        Intent intent = new Intent(getApplicationContext() , FIrstAcitivity.class);
        startActivity(intent);
        updateSharedPreference();
        finish();
    }

    public void updateSharedPreference(){
        SharedPreferences sharedPreferences = getSharedPreferences("Game", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("inGame",0);

        editor.apply();
    }

}