package com.example.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private Button  btinventaire;
    private Button  btsortiemarchandise;
    private Button  btentreemarchandise;
    public  String url;
    private ListView lv;
    private  ProgressDialog pDialog;
    private String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        this.btinventaire = (Button) findViewById(R.id.BtInventaire);
        this.btentreemarchandise = (Button) findViewById(R.id.BtEntreeMarchandise);
        this.btsortiemarchandise = (Button) findViewById(R.id.BtSoriteMarchandise);

        DatabaseHelper db = new DatabaseHelper(this);

        Parametres parametres = new Parametres(0, "", 0);
        parametres = db.getParametre(1);  // lecture des paramètres
        if (parametres == null){

            Intent parametreAcitivty = new Intent(getApplicationContext(),activity_Parametre.class);
            startActivity(parametreAcitivty);
            finish();
        } else
        {
            url = "http://"+parametres.getAdresse()+':'+ parametres.getPort()+'/';

//            url = "http://192.168.10.58:8081/article?ean=8058333424644";

//           url = "https://api.androidhive.info/contacts/";
            // ici contrôle de la connexion avec le serveur REST
            new GetArticle().execute();


        }

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
    private class GetArticle extends AsyncTask<Void,Void,Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // show loading dialog
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Lecture ...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... Voids) {
            HttpHandler sh = new HttpHandler();
            String jsonStr = sh.makeServiceCall(url);
            Log.e(TAG, "Réponse de url : " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObjet = new JSONObject(jsonStr);
                } catch (final JSONException e) {
                    Log.e(TAG, "JSON erreur paramètres : " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "JSON erreur paramètres : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else {
                Log.e(TAG, " pas de réponse du serveur : ");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Pas de réponse du serveur.", Toast.LENGTH_SHORT).show();

                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }
            // mise à jour de json
            //ListAdapter adapter
        }
    }
}
