package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class activity_Parametre extends AppCompatActivity {

    private Button btsauvegarde;
    EditText adresse;
    EditText port;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parametre);
        btsauvegarde =  (Button) this.findViewById(R.id.btParametreSauvegarde);
        this.adresse = findViewById(R.id.editTextAdresse);
        this.port = findViewById(R.id.editTextPort);
        DatabaseHelper db = new DatabaseHelper(this);
        Parametres parametres = new Parametres(0, "", 0);
        parametres = db.getParametre(1);  // lecture des paramètres
        if (parametres == null) {
            adresse.setText("192.168.1.2");
            port.setText("8081");
        } else {
            adresse.setText(parametres.adresse);
            port.setText(""+parametres.port);
        }

        btsauvegarde.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DatabaseHelper db = new DatabaseHelper(getApplicationContext());
                Parametres parametre = new Parametres(0, "", 0);
                parametre = db.getParametre(1);  // lecture des paramètres
                if (parametre == null) {
                    Parametres parametre2 = new Parametres(0, "", 0);
                    parametre2.setAdresse(adresse.getText().toString());
                    parametre2.setPort(Integer.parseInt(port.getText().toString()));
                    db.addParametre(parametre2);
                } else {
                    parametre.setAdresse(adresse.getText().toString());
                    parametre.setPort(Integer.parseInt(port.getText().toString()));
                    db.updateParametre(parametre);

                }

                Intent inventaireAcitivty = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(inventaireAcitivty);
                finish();
            }
        });

    }



}
