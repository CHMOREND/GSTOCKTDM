package ch.orioninformatique.gstocktdm;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.app.Activity;
import android.widget.Toast;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

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
    private TextView CodeEan;
    private TextView QtStock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
                    Toast.makeText(getApplicationContext(),"Vous devez d'abord scanner un article !",Toast.LENGTH_SHORT).show();
                } else {
                    qt++;
                    viewqt.setText("" + qt);

                    DatabaseHelper db = new DatabaseHelper(activity);
                    Inventaire  inventaire = new Inventaire(0,"","","",1,0);
                    inventaire = db.getInventaire(CodeEan.getText().toString());
                    inventaire.setQt(qt);
                    db.updateInventaire(inventaire);

                }
            }
        });
        btmoins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qt--;
                if (qt < 0) {qt = 0;}
                viewqt.setText(""+qt);
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
                if (nbrInventaire > 0){
                    qt = 0;
                    viewqt.setText(""+qt);
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
                if (nbrInventaire > 0){
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
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult scanningResult =
                IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (scanningResult != null) {
            String scanContent = scanningResult.getContents();
            String scanFormat = scanningResult.getFormatName();
            if (scanContent.length() == 12) {
                // ajout d'un 0 si le code est inférieure à 13 caractères
                scanContent = "0"+scanningResult.getContents();
            }
            else {
                scanContent = scanningResult.getContents();
            }
            DatabaseHelper db = new DatabaseHelper(activity);
            Inventaire  inventaire = new Inventaire(0,"","","",1,0);
            inventaire = db.getInventaire(scanContent);
            if (inventaire == null){
                // création de l'inventaire
                Inventaire inventaire1 = new Inventaire("","","",1,0);
                inventaire1.setQt(1);
                inventaire1.setQtstock(0);
                inventaire1.setEan(scanContent);
                inventaire1.setNumero(inventaire1.getNumero());
                inventaire1.setDesignation(inventaire1.getDesignation());
                db.addInventaire(inventaire1);
                viewqt.setText( Integer.toString(inventaire1.getQt()));
                QtStock.setText(Integer.toString(inventaire1.getQtstock()));
                CodeEan.setText(inventaire1.getEan());
                Numero.setText(inventaire1.getNumero());
                Designation.setText(inventaire1.getDesignation());

            } else {
                qt =  inventaire.getQt();
                qt++;
                inventaire.setQt(qt);
                db.updateInventaire(inventaire);
            }
            if (inventaire != null) {
                viewqt.setText( Integer.toString(inventaire.getQt()));
                QtStock.setText(Integer.toString(inventaire.getQtstock()));
                CodeEan.setText(inventaire.getEan());
                Numero.setText(inventaire.getNumero());
                Designation.setText(inventaire.getDesignation());

            }


        } else {
            Toast.makeText(getApplicationContext(),
                "Aucunne données de scan reçu !", Toast.LENGTH_SHORT).show();

        }
    }



};