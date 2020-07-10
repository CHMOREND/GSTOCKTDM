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
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class activityDetailCommandeClient extends AppCompatActivity {
    private FloatingActionButton enregistre;
    private TextView r;
    private ListView lv;
    private Activity activity = this;
    ArrayList<HashMap<String, String>> detailList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_commande_client);
        enregistre =   (FloatingActionButton) findViewById(R.id.fabcommandedetail);
        detailList = new ArrayList<>();
        lv = (ListView) findViewById(R.id.listViewDetailClient);
        ListAdapter adapter = new SimpleAdapter(
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
                if (ean == ""){
                    artic.put("ean", "Pas de code EAN");

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

        }

        enregistre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent inventaireAcitivty = new Intent(getApplicationContext(), activitycommandeclientListActivity.class);
        startActivity(inventaireAcitivty);
        finish();

    }
}