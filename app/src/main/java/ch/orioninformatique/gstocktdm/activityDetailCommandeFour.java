/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package ch.orioninformatique.gstocktdm;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.symbol.emdk.EMDKManager;
import com.symbol.emdk.scanandpair.ScanAndPairManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class activityDetailCommandeFour extends AppCompatActivity {
    private FloatingActionButton enregistre;
    private TextView r;
    private Activity activity = this;
    private ListView lv;
    private ProgressDialog pDialog;
    ArrayList<HashMap<String, String>> detailList;
    private EMDKManager emdkManager;
    private ScanAndPairManager scanAndPairManager;
    private String decodedData;
    private String numCommande;
    private DatabaseHelper db;
    private String TAG = MainActivity.class.getSimpleName();
    private String url="";
    private String result="";
    private Boolean ok=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_commande_four);
        enregistre =   (FloatingActionButton) findViewById(R.id.fabcommandedetailfour);
        detailList = new ArrayList<>();
        lv = (ListView) findViewById(R.id.listViewDetailFourn);
        db = new DatabaseHelper(activity);
        ListAdapter adapter = new SimpleAdapter(
                activityDetailCommandeFour.this,detailList,
                R.layout.list_item_commandes,new String[]{"numarticle","qtcommande","qtlivre","solde","designation","ean"},
                new int[]{R.id.numeroarticlecommande,R.id.qtcommande,R.id.qtlivre,R.id.solde,R.id.designationarticlecommande,R.id.eanarticlecommande} );
        lv.setAdapter(adapter);
        IntentFilter filter = new IntentFilter();
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        filter.addAction(getResources().getString(R.string.activity_intent_filter_action));
        registerReceiver(myBroadcastReceiver, filter);
        if (savedInstanceState == null) {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                if (bundle.getString("datebulletin") != null) {
                    r =   (TextView) findViewById(R.id.textViewdatefour);
                    r.setText(bundle.getString("datebulletin"));
                }
                if (bundle.getString("nomclient") != null) {
                    r =   (TextView) findViewById(R.id.textViewClientfour);
                    String rawString = bundle.getString("nomclient");
                    byte[] bytes = rawString.getBytes(StandardCharsets.UTF_8);
                    String utf8EncodedString = new String(bytes, StandardCharsets.UTF_8);
                    r.setText(utf8EncodedString);
                }
                if (bundle.getString("numbulletin") != null) {
                    r =   (TextView) findViewById(R.id.textViewNumerofour);
                    r.setText(bundle.getString("numbulletin"));
                    numCommande = bundle.getString("numbulletin");
                }
                if (bundle.getString("montantbulletin") != null) {
                    r =   (TextView) findViewById(R.id.textViewMontantfour);
                    r.setText(bundle.getString("montantbulletin"));
                }
            }
            List<Commandes> commandeList = db.getCommandesfourndetail(bundle.getString("numbulletin"));



            for (int i = 0;i < commandeList.size();i++) {
                HashMap<String, String> artic = new HashMap<>();
                artic.put("numarticle", commandeList.get(i).getNumero());
                artic.put("designation", commandeList.get(i).getDesignation());
                String ean = commandeList.get(i).getEan();
                if (Objects.equals(ean, "")){
                    artic.put("ean", "Pas de EAN");

                } else {
                    artic.put("ean", ean);
                }
                Integer qt = commandeList.get(i).qt;
                artic.put("qtcommande", qt.toString() );
                Integer livre = commandeList.get(i).livre;
                Integer dejalivre = commandeList.get(i).dejalivre;
                Integer totallivraison = livre + dejalivre;
                artic.put("qtlivre", totallivraison.toString());
                Integer solde = qt -totallivraison;
                artic.put("solde", solde.toString());
                detailList.add(artic);
                artic.clear();
            }
            lv.invalidateViews();
        }
        enregistre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mypopup = new AlertDialog.Builder(activity);
                mypopup.setTitle("Enregistement ?");
                mypopup.setMessage("Voulez-vous terminer la commande "+numCommande+" ?");
                mypopup.setPositiveButton("OUI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        List<Commandes> commandeList = db.getCommandesfourndetail(numCommande);
                        Integer livre = 0;
                        for (int i = 0;i < commandeList.size();i++) {
                            if (livre == 0){
                                livre =  commandeList.get(i).getLivre();
                            }
                        }
                        if (livre > 0){
                            // ici l'envoi au serveur de la commande et effacement de la commande dans le scanner
                            Parametres parametres = new Parametres(0, "", 0);
                            parametres = db.getParametre(1);  // lecture des paramètres de connexion
                            if (parametres == null) {
                                Intent parametreAcitivty = new Intent(getApplicationContext(), activity_Parametre.class);
                                startActivity(parametreAcitivty);
                                finish();
                            } else {
                                url = "http://" + parametres.getAdresse() + ':' + parametres.getPort();
                            }
                            url = url+"/enregistrecommandefourn";
                            JSONArray jsonArray = new JSONArray();
                            JSONObject jsonObject = new JSONObject();
                            for (int i = 0;i < commandeList.size();i++) {
                                try {
                                    Integer solde = commandeList.get(i).getQt() - commandeList.get(i).getLivre();
                                    jsonArray.put(new JSONObject().put("numcommande", commandeList.get(i).getNumcommande())
                                            .put("ligne", commandeList.get(i).getNumligne())
                                            .put("livre", commandeList.get(i).getLivre())
                                            .put("solde", solde));
                                    jsonObject.put("commande",jsonArray);
                                } catch (Exception e) {

                                };
                            }
                            result = jsonObject.toString();
                            new  activityDetailCommandeFour.SetCommande().execute();


                        } else{
                            ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
                            toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 300);
                            Toast.makeText(activity, "La commande n° " + numCommande + " n'a pas d'article traité !!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                mypopup.setNegativeButton("NON", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                mypopup.show();
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myBroadcastReceiver);
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
    private void displayScanResult(Intent initiatingIntent, String howDataReceived) throws InterruptedException {
        //String decodedSource = initiatingIntent.getStringExtra(getResources().getString(R.string.datawedge_intent_key_source));
        decodedData = initiatingIntent.getStringExtra(getResources().getString(R.string.datawedge_intent_key_data));
        //String decodedLabelType = initiatingIntent.getStringExtra(getResources().getString(R.string.datawedge_intent_key_label_type));
        if (decodedData.length() == 12) {
            decodedData = "0" + decodedData;
        }
        // recherche du code EAN dans la commande et enregistre la sortie
        if (db.enregistreCommandesfourndetail(numCommande,decodedData)) {
            Toast.makeText(activity, "l'article a été enregistré dans la commande", Toast.LENGTH_SHORT).show();
            for (int i = 0; i < detailList.size(); i++){
                String ean =  detailList.get(i).get("ean").toString();
                Integer solde = Integer.valueOf(detailList.get(i).get("solde"));
                Integer livre = Integer.valueOf(detailList.get(i).get("qtlivre"));
                if ((ean.equals(decodedData)) && solde > 0){
                    solde = solde-1;
                    HashMap<String, String> artic = new HashMap<>();
                    artic = detailList.get(i);
                    artic.put("solde",solde.toString());
                    livre = livre +1;
                    artic.put("qtlivre",livre.toString());
                    detailList.set(i,artic);

                    break;
                }
            }

            lv.invalidateViews();
        } else {
            ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
            toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 300);

            Toast.makeText(activity, "l'article avec le code EAN "+decodedData+ " n'a pas été trouvé dans la commande ou il es déjà enregistré", Toast.LENGTH_SHORT).show();
        }

    }
    private class SetCommande extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // show loading dialog
            pDialog = new ProgressDialog(activity);
            pDialog.setMessage("Enregistre la commande ...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... Voids) {
            HttpHandler sh = new HttpHandler();
            url = url+"?commandes="+result;
            String jsonStr = sh.makeServiceCall(url);
            Log.e(TAG, "Réponse de url : " + jsonStr);
            if (jsonStr != null) {
                if (jsonStr.contains("True")) {
                    ok = true;
                } else {
                    ok = false;
                }

            } else {
                Log.e(TAG, " pas de réponse du serveur : ");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity, "Pas de réponse du serveur.", Toast.LENGTH_SHORT).show();

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
            if (ok){
                // efface la commande
                db.deleteCommandeFourn(numCommande);

                Toast.makeText(activity, "La commande " + numCommande +" a bien été enregistré !!!", Toast.LENGTH_SHORT).show();

                Intent inventaireAcitivty = new Intent(getApplicationContext(), activitycommandeFournList.class);
                startActivity(inventaireAcitivty);
                finish();
            } else {
                ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
                toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 300);
                Toast.makeText(activity, "La commande " + numCommande +" n'a pas été enregistré !!!", Toast.LENGTH_SHORT).show();

            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent inventaireAcitivty = new Intent(getApplicationContext(), activitycommandeFournList.class);
        startActivity(inventaireAcitivty);
        finish();

    }
}