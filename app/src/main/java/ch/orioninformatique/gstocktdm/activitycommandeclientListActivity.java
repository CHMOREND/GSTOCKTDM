/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package ch.orioninformatique.gstocktdm;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class activitycommandeclientListActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();
    private ListView lv;
    private Activity activity = this;
    private String url;
    private  ProgressDialog pDialog;
    private String jsonStr;
    private SearchView searchView;
    ArrayList<HashMap<String, String>> commandeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_activitycommandeclient_list);


        commandeList = new ArrayList<>();
        lv = (ListView) findViewById(R.id.listViewcl);
        searchView = (SearchView) findViewById(R.id.searchViewcl);

        ListAdapter adapter = new SimpleAdapter(
                activitycommandeclientListActivity.this,commandeList,
                R.layout.activitycommandeclient_list_content,new String[]{"datebulletin","nomclient","numbulletin","montantbulletin"},
                new int[]{R.id.datebulletin,R.id.nomclient,R.id.numbulletin,R.id.montantbulletin} );


        lv.setAdapter(adapter);

        DatabaseHelper dbp = new DatabaseHelper(activity);
        Parametres parametres = new Parametres(0, "", 0);
        parametres = dbp.getParametre(1);  // lecture des paramètres de connexion
        if (parametres == null) {
            Intent parametreAcitivty = new Intent(getApplicationContext(), activity_Parametre.class);
            startActivity(parametreAcitivty);
            finish();
        } else {
            url = "http://" + parametres.getAdresse() + ':' + parametres.getPort() + "/commandecl";
            new Getcommandes().execute();


        }


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ((SimpleAdapter) adapter).getFilter().filter(newText);
                return false;
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String datebulletin = ((TextView) view.findViewById(R.id.datebulletin)).getText().toString();
                String nomclient = ((TextView) view.findViewById(R.id.nomclient)).getText().toString();
                String numbulletin = ((TextView) view.findViewById(R.id.numbulletin)).getText().toString();
                String montantbulletin = ((TextView) view.findViewById(R.id.montantbulletin)).getText().toString();

                Intent inventaireAcitivty = new Intent(getApplicationContext(), activityDetailCommandeClient.class);
                startActivity(inventaireAcitivty);
                finish();



            }
        });



    }

    private class Getcommandes extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // show loading dialog
            pDialog = new ProgressDialog(activity);
            pDialog.setMessage("Lecture des commandes clients ...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... Voids) {
            HttpHandler sh = new HttpHandler();
            jsonStr = sh.makeServiceCall(url);
            Log.e(TAG, "Réponse de url : " + jsonStr);
            if (jsonStr != null) {
                try {
                    if (jsonStr.contains("ORION INFORMATIQUE SA")) {

                    } else {

                        JSONObject jsonObjet = new JSONObject(jsonStr);
                        JSONArray article = jsonObjet.getJSONArray("commandes");
                        for (int i = 0; i < article.length(); i++) {
                            JSONObject a = article.getJSONObject(i);
                            String numclient = a.getString("numclient");
                            String nomclient = a.getString("nomclient");
                            String ville = a.getString("ville");
                            String numbulletin = a.getString("numbull");
                            String datelivraison = a.getString("datelivraison");
                            String montanttotal = a.getString("montanttotal");

                            HashMap<String, String> artic = new HashMap<>();
                            artic.put("datebulletin", datelivraison);
                            artic.put("nomclient", numclient+" " +nomclient+" "+ ville);
                            artic.put("numbulletin", numbulletin);
                            artic.put("montantbulletin", montanttotal);

                            commandeList.add(artic);


                        }

                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "JSON erreur paramètres : " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(activity, "JSON erreur paramètres : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else {
                Log.e(TAG, " pas de réponse du serveur : ");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {


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


                    for (int i = 0; i < commandeList.size(); i++) {

                    }


        }

    }

    public void onBackPressed() {
        Intent inventaireAcitivty = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(inventaireAcitivty);
        finish();
    }

}