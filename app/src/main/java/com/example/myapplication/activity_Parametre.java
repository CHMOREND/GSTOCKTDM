package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class activity_Parametre extends AppCompatActivity {

    private Button sauvegarde;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parametre);
        sauvegarde =  (Button) this.findViewById(R.id.btParametreSauvegarde);


        sauvegarde.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent inventaireAcitivty = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(inventaireAcitivty);
                finish();
            }
        });

    }



}
