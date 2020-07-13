/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package ch.orioninformatique.gstocktdm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ButtonBarLayout;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.hardware.camera2.params.BlackLevelPattern;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.symbol.emdk.EMDKManager;
import com.symbol.emdk.scanandpair.ScanAndPairManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class activityDetailCommandeClient extends AppCompatActivity {
    private FloatingActionButton enregistre;
    private TextView r;
    private ListView lv;
    private Activity activity = this;
    ArrayList<HashMap<String, String>> detailList;
    private EMDKManager emdkManager;
    private ScanAndPairManager scanAndPairManager;
    private String decodedData;
    private String numCommande;
    private ListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_commande_client);
        enregistre =   (FloatingActionButton) findViewById(R.id.fabcommandedetail);
        detailList = new ArrayList<>();
        lv = (ListView) findViewById(R.id.listViewDetailClient);
        IntentFilter filter = new IntentFilter();
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        filter.addAction(getResources().getString(R.string.activity_intent_filter_action));
        registerReceiver(myBroadcastReceiver, filter);

        adapter = new SimpleAdapter(
                activityDetailCommandeClient.this,detailList,
                R.layout.list_item_commandes,new String[]{"numarticle","qtcommande","qtlivre","solde","designation","ean"},
                new int[]{R.id.numeroarticlecommande,R.id.qtcommande,R.id.qtlivre,R.id.solde,R.id.designationarticlecommande,R.id.eanarticlecommande} );
        lv.setAdapter(adapter);

        if (savedInstanceState == null) {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                if (bundle.getString("datebulletin") != null) {
                    r =   (TextView) findViewById(R.id.textViewdate);
                    r.setText(bundle.getString("datebulletin"));
                }
                if (bundle.getString("nomclient") != null) {
                    r =   (TextView) findViewById(R.id.textViewClient);
                    try {
                        String out = new String(bundle.getString("nomclient").getBytes("UTF-8"), "ISO-8859-1");
                        r.setText(out);
                    } catch (java.io.UnsupportedEncodingException e){

                    };

                    r.setText(bundle.getString("nomclient"));
                }
                if (bundle.getString("numbulletin") != null) {
                    r =   (TextView) findViewById(R.id.textViewNumero);
                    r.setText(bundle.getString("numbulletin"));
                    numCommande = bundle.getString("numbulletin");
                }
                if (bundle.getString("montantbulletin") != null) {
                    r =   (TextView) findViewById(R.id.textViewMontant);
                    r.setText(bundle.getString("montantbulletin"));
                }
            }
            DatabaseHelper db = new DatabaseHelper(activity);
            List<Commandes> commandeList = db.getCommandesclientdetail(bundle.getString("numbulletin"));
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
                artic.put("qtlivre", livre.toString());
                Integer solde = qt -livre;

                artic.put("solde", solde.toString());
                detailList.add(artic);
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
                        // ici l'envoi au serveur de la commande et effacement de la commande dans le scanner
                        Intent inventaireAcitivty = new Intent(getApplicationContext(), activitycommandeclientListActivity.class);
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
        String decodedSource = initiatingIntent.getStringExtra(getResources().getString(R.string.datawedge_intent_key_source));
        decodedData = initiatingIntent.getStringExtra(getResources().getString(R.string.datawedge_intent_key_data));
        String decodedLabelType = initiatingIntent.getStringExtra(getResources().getString(R.string.datawedge_intent_key_label_type));
        if (decodedData.length() == 12) {
            decodedData = "0" + decodedData;
        }
        // recherche du code EAN dans la commande et enregistre la sortie
        DatabaseHelper db = new DatabaseHelper(activity);
        if ( db.enregistreCommandesclientdetail(numCommande,decodedData)) {
             Toast.makeText(activity, "l'article a été enregistré dans la commande", Toast.LENGTH_SHORT).show();
            detailList.clear();
            List<Commandes> commandeList = db.getCommandesclientdetail(numCommande);

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
                artic.put("qtlivre", livre.toString());
                Integer solde = qt -livre;
                artic.put("solde", solde.toString());
                detailList.add(artic);
            }

            lv.invalidateViews();
        } else {
            ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
            toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 300);

            Toast.makeText(activity, "l'article avec le code EAN "+decodedData+ " n'a pas été trouvé dans la commande ou il es déjà enregistré", Toast.LENGTH_SHORT).show();

        }

    }
    @Override
    public void onBackPressed() {
        Intent inventaireAcitivty = new Intent(getApplicationContext(), activitycommandeclientListActivity.class);
        startActivity(inventaireAcitivty);
        finish();

    }
}