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
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class activity_recherche_article extends AppCompatActivity {

    private Button retour;
    private String url;
    private Activity activity = this;
    private  ProgressDialog pDialog;
    private String TAG = MainActivity.class.getSimpleName();
    private ListView lv;
    private String codeEan = "";
    ArrayList<HashMap<String, String>> articleList;
    private SearchView searchView;
    ArrayAdapter<HashMap<String,String>> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_recherche_article);
        searchView = (SearchView) findViewById(R.id.searchView);
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra("codeEan")) {
                codeEan = intent.getStringExtra("codeEan");
            }
        }
        this.retour = findViewById(R.id.btrechercharticle);

        DatabaseHelper dbp = new DatabaseHelper(activity);
        Parametres parametres = new Parametres(0, "", 0);
        articleList = new ArrayList<>();
        lv = (ListView) findViewById(R.id.listView);

        parametres = dbp.getParametre(1);  // lecture des paramètres de connexion
        if (parametres == null) {
            Intent parametreAcitivty = new Intent(getApplicationContext(), activity_Parametre.class);
            startActivity(parametreAcitivty);
            finish();
        } else {
            url = "http://" + parametres.getAdresse() + ':' + parametres.getPort() + "/articles";
            new GetArticles().execute();

        }
//        adapter = new ArrayAdapter<HashMap<String, String>>(activity,android.R.layout.simple_list_item_1, (List<HashMap<String, String>>) lv);
        lv.setAdapter(adapter);

        retour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inventaireAcitivty = new Intent(getApplicationContext(), activity_inventaire.class);
                startActivity(inventaireAcitivty);
                finish();

            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String id2 = ((TextView) view.findViewById(R.id.id)).getText().toString();
                String numero = ((TextView) view.findViewById(R.id.numero)).getText().toString();
                String designation = ((TextView) view.findViewById(R.id.designation)).getText().toString();
                AlertDialog.Builder mypopup = new AlertDialog.Builder(activity);

                mypopup.setTitle("Liaison ?");
                mypopup.setMessage("Voulez-vous vraiment lié ce code EAN : " + codeEan + " à cet article : " + numero + " " + designation);
                mypopup.setPositiveButton("OUI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // liaison du code enan à l'article et retour à l'inventaire


                        Intent inventaireAcitivty = new Intent(getApplicationContext(), activity_inventaire.class);
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
    private class GetArticles extends AsyncTask<Void,Void,Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // show loading dialog
            pDialog = new ProgressDialog(activity_recherche_article.this);
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

                            articleList.add(artic);


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
                        Toast.makeText(activity, "Pas de réponse du serveur.", Toast.LENGTH_SHORT).show();
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
            ListAdapter adapter = new SimpleAdapter(
                    activity_recherche_article.this,articleList,
                    R.layout.list_item_article,new String[]{"id","numero","designation"},
                    new int[]{R.id.id,R.id.numero,R.id.designation} );
            lv.setAdapter(adapter);


        }
    }
}
