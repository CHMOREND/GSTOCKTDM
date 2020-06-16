package ch.orioninformatique.gstocktdm;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.symbol.emdk.EMDKManager;
import com.symbol.emdk.EMDKResults;
import com.zebra.savanna.Models.BarcodeData;
import com.zebra.savanna.Models.Errors.Error;
import java.net.HttpRetryException;
import org.json.JSONException;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;

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

                Intent inventaireAcitivty = new Intent(getApplicationContext(),activitycommandeclientListActivity.class);
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
                    if (jsonStr.contains("ORION INFORMATIQUE SA")){

                    } else {

                        JSONObject jsonObjet = new JSONObject(jsonStr);
                        JSONArray article = jsonObjet.getJSONArray("articles");
                        for (int i = 0; i < article.length();i++){
                            JSONObject a = article.getJSONObject(i);
                            String id = a.getString("id");
                            String numero = a.getString("numero");
                            String designation = a.getString("designation");
                            String ean  = a.getString("ean");
                            String qtstock  = a.getString("qtstock");

                            HashMap<String,String> artic = new HashMap<>();
                            artic.put("id",id);
                            artic.put("numero",numero);
                            artic.put("designation",designation);
                            artic.put("ean",ean);
                            artic.put("qtstock",qtstock);

                            // articleList.add(artic);

                        }

                    }
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
                        Intent paramAcitivty = new Intent(getApplicationContext(),activity_Parametre.class);
                        startActivity(paramAcitivty);
                        finish();
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
    public void onBackPressed() {
        finishAndRemoveTask();
    }
}
