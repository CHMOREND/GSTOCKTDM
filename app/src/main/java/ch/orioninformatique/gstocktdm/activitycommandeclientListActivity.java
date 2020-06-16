/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package ch.orioninformatique.gstocktdm;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ch.orioninformatique.gstocktdm.dummy.DummyContent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * An activity representing a list of activity_commande_client_detail. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link activitycommandeclientDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class activitycommandeclientListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private String TAG = MainActivity.class.getSimpleName();
    private ListView lv;
    private Activity activity = this;
    private String url;
    private  ProgressDialog pDialog;
    ArrayList<HashMap<String, String>> commandeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        setContentView(R.layout.activity_activitycommandeclient_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.title_activitycommandeclient_list);

        commandeList = new ArrayList<>();
        lv = (ListView) findViewById(R.id.activitycommandeclient_container);

        ListAdapter adapter = new SimpleAdapter(
                activitycommandeclientListActivity.this,commandeList,
                R.layout.activitycommandeclient_list_content,new String[]{"datelivraison","nomclient","numbulletin","montantBulletin"},
                new int[]{R.id.datebulletin,R.id.client,R.id.numBulletin,R.id.montantBulletin} );

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

    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
       recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(this, DummyContent.ITEM, mTwoPane,commandeList));
    }

    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final activitycommandeclientListActivity mParentActivity;
        private final List<DummyContent.DummyItem> mValues;
        private final ArrayList<HashMap<String, String>> mcommandeListe;
        private final boolean mTwoPane;
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DummyContent.DummyItem item = (DummyContent.DummyItem) view.getTag();
                if (mTwoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putString(activitycommandeclientDetailFragment.ARG_ITEM_ID, item.numeroClient);
                    activitycommandeclientDetailFragment fragment = new activitycommandeclientDetailFragment();
                    fragment.setArguments(arguments);
                    mParentActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.activitycommandeclient_detail_container, fragment)
                            .commit();
                } else {
                    Context context = view.getContext();
                    Intent intent = new Intent(context, activitycommandeclientDetailActivity.class);
                    intent.putExtra(activitycommandeclientDetailFragment.ARG_ITEM_ID, item.numeroClient);

                    context.startActivity(intent);
                }
            }
        };

        SimpleItemRecyclerViewAdapter(activitycommandeclientListActivity parent,
                                      List<DummyContent.DummyItem> items,
                                      boolean twoPane,
                                      ArrayList<HashMap<String, String>> commandeList) {
            mcommandeListe = commandeList;
            mValues = items;
            mParentActivity = parent;
            mTwoPane = twoPane;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.activitycommandeclient_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mDatebulletin.setText(mValues.get(position).numeroClient);
            holder.mNomClient.setText(mValues.get(position).nomclient);

            holder.itemView.setTag(mValues.get(position));
            holder.itemView.setOnClickListener(mOnClickListener);
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView mDatebulletin;
            final TextView mNomClient;
            final TextView mNumBUlletin;

            ViewHolder(View view) {
                super(view);
                mDatebulletin = (TextView) view.findViewById(R.id.datebulletin);
                mNomClient = (TextView) view.findViewById(R.id.client);
                mNumBUlletin = (TextView) view.findViewById(R.id.numBulletin);
            }
        }
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
            String jsonStr = sh.makeServiceCall(url);
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
                            artic.put("numclient", numclient);
                            artic.put("nomclient", nomclient);
                            artic.put("ville", ville);
                            artic.put("numbulletin", numbulletin);
                            artic.put("datelivraison", datelivraison);
                            artic.put("montanttotal", montanttotal);

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
            if (commandeList.size() > 0) {

                //setupRecyclerView((RecyclerView) recyclerView);

                /**

                for (int i = 0; i < commandeList.size(); i++) {
                    HashMap<String, String> a = commandeList.get(i);
                    String datelivraison = a.get("datelivraison");
                    String nom = a.get("numclient")+" "+a.get("nomclient")+" "+a.get("ville");
                    String numbulletin = a.get("numbulletin");
                    HashMap<String,String> artic = new HashMap<>();
                    artic.put("Datebulletin",datelivraison);
                    artic.put("NomClient",nom);
                    artic.put("NumBUlletin",numbulletin);

                }
                */
            }

        }
    }
    public void onBackPressed() {
        Intent inventaireAcitivty = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(inventaireAcitivty);
        finish();
    }
}