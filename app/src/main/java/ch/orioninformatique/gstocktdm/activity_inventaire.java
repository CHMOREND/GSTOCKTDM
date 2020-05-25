package ch.orioninformatique.gstocktdm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.VectorEnabledTintResources;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.app.Activity;
import android.widget.Toast;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.symbol.emdk.EMDKManager;
import com.symbol.emdk.EMDKResults;
import com.symbol.emdk.VersionManager;
import com.symbol.emdk.scanandpair.ScanAndPairManager;
import com.zebra.savanna.Symbology;
import com.zebra.savanna.UPCLookup;
import com.zebra.savanna.Models.Errors.Error;
import com.zebra.savanna.SavannaAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ch.orioninformatique.gstocktdm.BarcodeScannerEngine;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.*;

public class activity_inventaire extends AppCompatActivity {
    private int qt = 0;
    private Button btplus;
    private Button btmoins;
    private Button btajourinventaire;
    private TextView viewqt;
    private Button retour;
    private Button scanner;
    private activity_inventaire activity;
    private TextView Numero;
    private TextView Designation;
    public TextView CodeEan;
    private TextView QtStock;
    private TextView DataScann;
    private String url;
    private EMDKManager emdkManager;
    private ScanAndPairManager scanAndPairManager;
    private ProgressDialog pDialog;
    private String TAG = MainActivity.class.getSimpleName();
    private String eanCode = "";
    private Integer idStock = 0;
    private Boolean allerRechercheStock = false;
    private String decodedData;
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myBroadcastReceiver);
    }


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_inventaire);
        DatabaseHelper db = new DatabaseHelper(activity);
        this.Numero = findViewById(R.id.textViewNumeroD);
        this.Designation = findViewById(R.id.textViewDesignation);
        this.CodeEan = findViewById(R.id.textViewCodeEan);
        this.QtStock = findViewById(R.id.textViewQtStock);
        this.scanner = findViewById(R.id.scan_button);
        this.activity = this;
        this.btajourinventaire = findViewById(R.id.btmetajourinventaire);
        this.btplus = findViewById(R.id.buttonPlus);
        this.btmoins = findViewById(R.id.btMoins);
        this.viewqt = (TextView) findViewById(R.id.textViewQt);
        this.retour = findViewById(R.id.btinventaireretour);
        Numero.setText("");
        Designation.setText("");
        CodeEan.setText("");
        QtStock.setText("");
        final Activity activity = this;
        pDialog = new ProgressDialog(activity_inventaire.this);
        IntentFilter filter = new IntentFilter();
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        filter.addAction(getResources().getString(R.string.activity_intent_filter_action));
        registerReceiver(myBroadcastReceiver, filter);


        scanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = new IntentIntegrator(activity);

                /**
                 integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                 integrator.setPrompt("Scan");
                 integrator.setCameraId(0);
                 integrator.setBeepEnabled(false);
                 integrator.setBarcodeImageEnabled(false);
                 **/
                integrator.initiateScan();

            }
        });

        btplus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (CodeEan.getText().length() == 0) {
                    Toast.makeText(getApplicationContext(), "Vous devez d'abord scanner un article !", Toast.LENGTH_SHORT).show();
                } else {
                    qt =  Integer.parseInt( viewqt.getText().toString());
                    qt++;
                    viewqt.setText("" + qt);
                    DatabaseHelper db = new DatabaseHelper(activity);
                    Inventaire inventaire = new Inventaire(0, "", "", "", 1, 0);
                    inventaire = db.getInventaire(CodeEan.getText().toString());
                    inventaire.setQt(qt);
                    db.updateInventaire(inventaire);

                }
            }
        });
        btmoins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qt =  Integer.parseInt( viewqt.getText().toString());
                qt--;
                if (qt < 0) {
                    qt = 0;
                }
                viewqt.setText("" + qt);
                if (CodeEan.getText().length() != 0) {
                    DatabaseHelper db = new DatabaseHelper(activity);
                    Inventaire inventaire = new Inventaire(0, "", "", "", 1, 0);
                    inventaire = db.getInventaire(CodeEan.getText().toString());
                    inventaire.setQt(qt);
                    db.updateInventaire(inventaire);
                }

            }
        });
        btajourinventaire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // enregsitre l'inventaire si il existe
                DatabaseHelper db = new DatabaseHelper(activity);
                Integer nbrInventaire = db.GetInventaireCount();
                String url2 = "";
                if (nbrInventaire > 0) {
                    qt = 0;
                    viewqt.setText("" + qt);

                    DatabaseHelper dbp = new DatabaseHelper(activity);
                    Parametres parametres = new Parametres(0, "", 0);
                    parametres = dbp.getParametre(1);  // lecture des paramètres de connexion
                    if (parametres == null) {
                        Intent parametreAcitivty = new Intent(getApplicationContext(), activity_Parametre.class);
                        startActivity(parametreAcitivty);
                        finish();
                    } else {
                        url2 = "http://" + parametres.getAdresse() + ':' + parametres.getPort();
                    }

                    // ici enregistre les inventaires dans pmeSoft
                    List<Inventaire> inventaires = db.getAllInventaire();

                    for (int i=0;i < inventaires.size();i++){
                        String ean = inventaires.get(i).ean;
                        Integer qt = inventaires.get(i).qt;
                        url = url2+"/savearticle?id=" + ean +"&qt=" + qt.toString();
                        new   activity_inventaire.SauveInventaire().execute();
                        try {
                            Thread.sleep(250);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    // ici efface les inventaire
                    db.deleteInventaire();
                    Intent inventaireAcitivty = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(inventaireAcitivty);
                    finish();
                }
            }
        });
        retour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseHelper db = new DatabaseHelper(activity);
                Integer nbrInventaire = db.GetInventaireCount();
                if (nbrInventaire > 0) {
                    AlertDialog.Builder mypopup = new AlertDialog.Builder(activity);
                    mypopup.setTitle("Abandonner ?");
                    mypopup.setMessage("Vous n'avez pas enregistrer l'inventaire, voulez-vous vraiment sortir");
                    mypopup.setPositiveButton("OUI", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent inventaireAcitivty = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(inventaireAcitivty);
                            finish();
                        }
                    });
                    mypopup.setNegativeButton("NON", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    mypopup.show();

                } else {
                    Intent inventaireAcitivty = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(inventaireAcitivty);
                    finish();


                }
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
        CodeEan.setText(decodedData);
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
            new activity_inventaire.GetArticle().execute();
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
                        QtStock.setText("0");
                        CodeEan.setText("");
                        Numero.setText("");
                        Designation.setText("");
                        idStock = 0;
                    } else {
                        JSONObject jsonObjet = new JSONObject(jsonStr);
                        JSONArray article = jsonObjet.getJSONArray("article");
                        for (int i = 0; i < article.length(); i++) {
                            JSONObject a = article.getJSONObject(i);
                            String id = a.getString("id");
                            idStock = a.getInt("id");
                            String numero = a.getString("numero");
                            String designation = a.getString("designation");
                            String ean = a.getString("ean");
                            String qtstock = a.getString("qtstock");
                            idStock = a.getInt("id");
                            QtStock.setText(qtstock);
                            CodeEan.setText(ean);
                            Numero.setText(numero);
                            Designation.setText(designation);
                        }

                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "JSON erreur paramètres : " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(activity_inventaire.this, "JSON erreur paramètres : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else {
                Log.e(TAG, " pas de réponse du serveur : ");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity_inventaire.this, "Pas de réponse du serveur.", Toast.LENGTH_SHORT).show();
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
            if (idStock == 0) {
                // message pas trouvé l'article
                AlertDialog.Builder mypopup = new AlertDialog.Builder(activity);
                viewqt.setText("0");
                QtStock.setText("");
                mypopup.setTitle("Choisissez une réponse ?");
                mypopup.setMessage("Je n'ai pas trouvé l'article dans le stock avec le code EAN suivant : "+decodedData+" voulez-vous lier ce code à un article");
                eanCode = decodedData;
                mypopup.setPositiveButton("OUI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent rechercheArticleAcitivty = new Intent(getApplicationContext(), activity_recherche_article.class);
                        rechercheArticleAcitivty.putExtra("codeEan",eanCode);
                        startActivity(rechercheArticleAcitivty);
                        finish();
                    }
                });
                mypopup.setNegativeButton("NON", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                mypopup.show();

            } else
            {

                DatabaseHelper db = new DatabaseHelper(activity);
                Inventaire inventaire = new Inventaire(0, "", "", "", 1, 0);
                inventaire = db.getInventaire(decodedData);
                if (inventaire == null) {
                    // création de l'inventaire
                    Inventaire inventaire1 = new Inventaire("", "", "", 1, 0);
                    inventaire1.setQt(1);
                    inventaire1.setQtstock(Integer.parseInt(QtStock.getText().toString()));
                    inventaire1.setEan(decodedData);
                    inventaire1.setNumero(Numero.getText().toString());
                    inventaire1.setDesignation(Designation.getText().toString());
                    db.addInventaire(inventaire1);

                    viewqt.setText(Integer.toString(inventaire1.getQt()));
                    QtStock.setText(Integer.toString(inventaire1.getQtstock()));
                    CodeEan.setText(inventaire1.getEan());
                    Numero.setText(inventaire1.getNumero());
                    Designation.setText(inventaire1.getDesignation());

                } else {
                    qt = inventaire.getQt();
                    qt++;
                    inventaire.setQt(qt);
                    db.updateInventaire(inventaire);
                    viewqt.setText(Integer.toString(inventaire.getQt()));
                    QtStock.setText(Integer.toString(inventaire.getQtstock()));
                    CodeEan.setText(inventaire.getEan());
                    Numero.setText(inventaire.getNumero());
                    Designation.setText(inventaire.getDesignation());
                }
                if (inventaire != null) {
                    viewqt.setText(Integer.toString(inventaire.getQt()));
                    QtStock.setText(Integer.toString(inventaire.getQtstock()));
                    CodeEan.setText(inventaire.getEan());
                    Numero.setText(inventaire.getNumero());
                    Designation.setText(inventaire.getDesignation());

                }
            }
        }
    }

    private class SauveInventaire extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // show loading dialog
            pDialog = new ProgressDialog(activity_inventaire.this);
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
                        Toast.makeText(activity_inventaire.this, "Pas de réponse du serveur.", Toast.LENGTH_SHORT).show();
                        Intent paramAcitivty = new Intent(getApplicationContext(), activity_Parametre.class);
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

};