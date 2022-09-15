package com.example.ticktaktum;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class FIrstAcitivity extends AppCompatActivity {

    CardView cardView1 , cardView2;
    String roomID_string;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_acitivity);

        cardView1 = findViewById(R.id.createGame_card);
        cardView2 = findViewById(R.id.joinGame_card);

        SharedPreferences sharedPreferences = getSharedPreferences("Game", Context.MODE_PRIVATE);
        int ingame = sharedPreferences.getInt("inGame",0);

        if(ingame == 1){

            SharedPreferences sharedPreferences2 = getSharedPreferences("RoomID",Context.MODE_PRIVATE);
            roomID_string = sharedPreferences2.getString("room","");

            Intent intent = new Intent(getApplicationContext() , GameActivity.class);
            intent.putExtra("roomID",roomID_string);
            startActivity(intent);
            finish();
        }


        cardView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext() , MainActivity.class);
                intent.putExtra("cardNumber",1);
                startActivity(intent);

            }
        });

        cardView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext() , MainActivity.class);
                intent.putExtra("cardNumber",2);
                startActivity(intent);
            }
        });

    }
}