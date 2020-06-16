
package ch.orioninformatique.gstocktdm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.symbol.emdk.EMDKManager;
import com.symbol.emdk.scanandpair.ScanAndPairManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class activityRetourClient extends AppCompatActivity {
    private Button retour;
    private String url;
    private String url2;
    private EMDKManager emdkManager;
    private ScanAndPairManager scanAndPairManager;
    private ProgressDialog pDialog;
    private String TAG = activityRetourClient.class.getSimpleName();
    private String decodedData;
    private String numArticle;
    private String qtstock;
    private String ean;
    private Boolean ok = true;
    private activityRetourClient activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

        setContentView(R.layout.activity_retour_client);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarretourclient);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Retour d'un article");
        final Activity activity = this;
        pDialog = new ProgressDialog(activityRetourClient.this);
        IntentFilter filter = new IntentFilter();
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        filter.addAction(getResources().getString(R.string.activity_intent_filter_action));
        registerReceiver(myBroadcastReceiver, filter);

        DatabaseHelper dbp = new DatabaseHelper(activity);
        Parametres parametres = new Parametres(0, "", 0);
        parametres = dbp.getParametre(1);  // lecture des paramètres de connexion
        if (parametres == null) {

        } else {
            url2 = "http://" + parametres.getAdresse() + ':' + parametres.getPort();
        }

        this.retour = findViewById(R.id.btRetourClient);
        retour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inventaireAcitivty = new Intent(getApplicationContext(),activityEntreeMarchandise.class);
                startActivity(inventaireAcitivty);
                finish();

            }
        });
    }
    private BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Bundle b = intent.getExtras();

            //  This is useful for debugging to verify the format of received intents from DataWedge
            //for (String key : b.keySet())
            //{
            //    Log.v(LOG_TAG, key);
            //}

            if (action.equals(getResources().getString(R.string.activity_intent_filter_action))) {
                //  Received a barcode scan
                try {
                    displayScanResult(intent, "via Broadcast");
                } catch (Exception e) {
                    //  Catch if the UI does not exist when we receive the broadcast
                }
            }
        }
    };

    //
    // The section below assumes that a UI exists in which to place the data. A production
    // application would be driving much of the behavior following a scan.
    //
    private void displayScanResult(Intent initiatingIntent, String howDataReceived) throws InterruptedException {
        String decodedSource = initiatingIntent.getStringExtra(getResources().getString(R.string.datawedge_intent_key_source));
        decodedData = initiatingIntent.getStringExtra(getResources().getString(R.string.datawedge_intent_key_data));
        String decodedLabelType = initiatingIntent.getStringExtra(getResources().getString(R.string.datawedge_intent_key_label_type));
        if (decodedData.length() == 12) {
            decodedData = "0" + decodedData;
        }
        //CodeEan.setText(decodedData);
        // recherche du code EAN13 dans pmeSof
        DatabaseHelper dbp = new DatabaseHelper(this);

        Parametres parametres = new Parametres(0, "", 0);
        parametres = dbp.getParametre(1);  // lecture des paramètres de connexion
        if (parametres == null) {

        } else {
            url = "http://" + parametres.getAdresse() + ':' + parametres.getPort() + "/article?ean=" + decodedData;
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            } else
            {

            }
            new activityRetourClient.GetArticle().execute();
        }
    }

    private class GetArticle extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // show loading dialog
            //          pDialog = new ProgressDialog(activity_inventaire.this);
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
                    if (jsonStr.contains("false")) {
                        // le code ean n'a pas été retrouvé
                        ok = false;
                    } else {
                        JSONObject jsonObjet = new JSONObject(jsonStr);
                        JSONArray article = jsonObjet.getJSONArray("article");
                        for (int i = 0; i < article.length(); i++) {
                            JSONObject a = article.getJSONObject(i);
                            String id = a.getString("id");
                            numArticle = a.getString("numero");
                            String designation = a.getString("designation");
                            ean = a.getString("ean");
                            qtstock = a.getString("qtstock");
                            ok = true;
                        }

                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "JSON erreur paramètres : " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(activityRetourClient.this, "JSON erreur paramètres : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            ok = false;
                        }
                    });
                }
            } else {
                Log.e(TAG, " pas de réponse du serveur : ");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activityRetourClient.this, "Pas de réponse du serveur.", Toast.LENGTH_SHORT).show();
                        ok = false;
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
            if (ok) {

                    Integer qt = Integer.parseInt(qtstock.toString());
                    qt++;
                    url = url2 +"/savearticle?id=" + ean +"&qt=" + qt.toString();
                    new   activityRetourClient.SauveInventaire().execute();
                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

            } else
            {
                Toast.makeText(activityRetourClient.this, "Pas trouvé l'article avec le code EAN : " + decodedData, Toast.LENGTH_SHORT).show();
            }


            }
        }
    }
    private class SauveInventaire extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // show loading dialog
            pDialog = new ProgressDialog(activityRetourClient.this);
            pDialog.setMessage("Enregistre ...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... Voids) {
            HttpHandler sh = new HttpHandler();
            String jsonStr = sh.makeServiceCall(url);
            Log.e(TAG, "Réponse de url : " + jsonStr);
            if (jsonStr != null) {


            } else {
                Log.e(TAG, " pas de réponse du serveur : ");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activityRetourClient.this, "Pas de réponse du serveur.", Toast.LENGTH_SHORT).show();

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
            Toast.makeText(activityRetourClient.this, "L'article " + numArticle + " à été enregsitré en retour !", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onBackPressed() {
        Intent inventaireAcitivty = new Intent(getApplicationContext(),activityEntreeMarchandise.class);
        startActivity(inventaireAcitivty);
        finish();
    }
}
