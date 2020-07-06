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

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class activityDetailCommandeClient extends AppCompatActivity {
    private FloatingActionButton enregistre;
    private TextView r;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_commande_client);
        enregistre =   (FloatingActionButton) findViewById(R.id.fabcommandedetail);
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