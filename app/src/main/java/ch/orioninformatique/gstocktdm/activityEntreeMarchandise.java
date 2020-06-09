package ch.orioninformatique.gstocktdm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class activityEntreeMarchandise extends AppCompatActivity {
    private Button retour;
    private Button retourClient;
    private Button commandesFourniiseurs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.activity_entree_marchandise);
        this.retour = findViewById(R.id.btEntreeMarchandiseReretour);
        this.retourClient = findViewById(R.id.btRetourClient);
        this.commandesFourniiseurs = findViewById(R.id.btCommandeFournisseur);
        retour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inventaireAcitivty = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(inventaireAcitivty);
                finish();

            }
        });
        retourClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inventaireAcitivty = new Intent(getApplicationContext(), activityRetourClient.class);
                startActivity(inventaireAcitivty);
                finish();

            }
        });
    }
}
