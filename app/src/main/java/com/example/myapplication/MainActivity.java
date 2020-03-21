package com.example.myapplication;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button  btinventaire;
    private Button  btsortiemarchandise;
    private Button  btentreemarchandise;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        this.btinventaire = (Button) findViewById(R.id.BtInventaire);
        this.btentreemarchandise = (Button) findViewById(R.id.BtEntreeMarchandise);
        this.btsortiemarchandise = (Button) findViewById(R.id.BtSoriteMarchandise);



        btinventaire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inventaireAcitivty = new Intent(getApplicationContext(),activity_inventaire.class);
                startActivity(inventaireAcitivty);
                finish();
            }
        });
        btentreemarchandise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inventaireAcitivty = new Intent(getApplicationContext(),activityEntreeMarchandise.class);
                startActivity(inventaireAcitivty);
                finish();
            }
        });
        btsortiemarchandise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inventaireAcitivty = new Intent(getApplicationContext(),activity_sortie_marchandise.class);
                startActivity(inventaireAcitivty);
                finish();
            }
        });
    }
}
