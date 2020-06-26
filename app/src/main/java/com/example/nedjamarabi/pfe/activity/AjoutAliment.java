package com.example.nedjamarabi.pfe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nedjamarabi.pfe.Managers.AlimentsFavorisManager;
import com.example.nedjamarabi.pfe.Managers.BilanManager;
import com.example.nedjamarabi.pfe.R;
import com.example.nedjamarabi.pfe.Suivi.Aliment;
import com.example.nedjamarabi.pfe.Suivi.Consommation;
import com.example.nedjamarabi.pfe.Suivi.Repas;
import com.example.nedjamarabi.pfe.Utils.DateUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.ParseException;
import java.util.Date;

public class AjoutAliment extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseUser user;
    private TextView nomAlimentTextView, alimentCaloriesTextView, alimentProteinesTextView, alimentLipidesTextView, alimentGlucidesTextView, alimentCaloriesForConsumedTextView, alimentProteinesForConsumedTextView, alimentLipidesForConsumedTextView, alimentGlucidesForConsumedTextView, consumedQuantityTextView;
    private EditText consumedQuantityEditText;
    private Spinner consumedUnitSpinner;
    private Button ajouterAlimentButton;
    private Aliment searchedAliment;
    private Date selectedDate;
    private Repas repas;
    private String[] unites;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajout_aliment);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            user = auth.getCurrentUser();
            nomAlimentTextView = findViewById(R.id.nomAlimentTextView);
            alimentCaloriesTextView = findViewById(R.id.alimentCaloriesTextView);
            alimentProteinesTextView = findViewById(R.id.alimentProteinesTextView);
            alimentLipidesTextView = findViewById(R.id.alimentLipidesTextView);
            alimentGlucidesTextView = findViewById(R.id.alimentGlucidesTextView);
            alimentCaloriesForConsumedTextView = findViewById(R.id.alimentCaloriesForConsumedTextView);
            alimentProteinesForConsumedTextView = findViewById(R.id.alimentProteinesForConsumedTextView);
            alimentLipidesForConsumedTextView = findViewById(R.id.alimentLipidesForConsumedTextView);
            alimentGlucidesForConsumedTextView = findViewById(R.id.alimentGlucidesForConsumedTextView);
            consumedQuantityTextView = findViewById(R.id.consumedQuantityTextView);
            consumedQuantityEditText = findViewById(R.id.consumedQuantityEditText);
            consumedUnitSpinner = findViewById(R.id.consumedUnitSpinner);
            ajouterAlimentButton = findViewById(R.id.ajouterAlimentButton);
            final Intent i = getIntent();
            repas = Repas.get(i.getIntExtra("repas", 0));
            searchedAliment = i.getParcelableExtra("aliment");
            try {
                selectedDate = DateUtils.parse(i.getStringExtra("date"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            switch (searchedAliment.getTypeAliment()) {
                case LIQUIDE:
                    unites = new String[]{"g", "L", "cL", "fl oz", "cuillère à soupe", "tasse", "bol"};
                    break;
                case SOLIDE:
                    unites = new String[]{"g", "oz", "lb", "tasse (moulu)", "bol (moulu)"};
                    break;
            }
            ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, unites);
            consumedUnitSpinner.setAdapter(unitAdapter);
            consumedUnitSpinner.setSelection(0);
            nomAlimentTextView.setText(searchedAliment.getNom());
            alimentCaloriesTextView.setText(String.valueOf((int) searchedAliment.getNbCalories()));
            alimentProteinesTextView.setText(String.valueOf((int) searchedAliment.getQuantiteProteines()));
            alimentLipidesTextView.setText(String.valueOf((int) searchedAliment.getQuantiteLipides()));
            alimentGlucidesTextView.setText(String.valueOf((int) searchedAliment.getQuantiteGlucides()));
            consumedQuantityEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }
                
                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    consumedQuantityTextView.setText(charSequence);
                    calculateConsommation();
                }
                
                @Override
                public void afterTextChanged(Editable editable) {
                }
            });
            consumedUnitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    calculateConsommation();
                }
                
                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });
            ajouterAlimentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Float consumedQuantity = calculateQuantity();
                    if (consumedQuantity > 0) {
                        BilanManager bm = new BilanManager();
                        AlimentsFavorisManager am = new AlimentsFavorisManager();
                        bm.open();
                        am.open();
                        System.out.println(user.getUid() + " § " + selectedDate + " § " + new Consommation(searchedAliment.getIdAliment(), repas, consumedQuantity));
                        bm.insert(user.getUid(), selectedDate, new Consommation(searchedAliment.getIdAliment(), repas, consumedQuantity));
                        am.insert(user.getUid(), searchedAliment.getIdAliment());
                        onBackPressed();
                        finish();
                    } else {
                        Toast.makeText(AjoutAliment.this, "Veuillez entrer une quantité valide", Toast.LENGTH_LONG).show();
                    }
                }
            });
            
        } else {
        }
        
    }
    
    public void calculateConsommation() {
        Float consumedQuantity = calculateQuantity();
        alimentCaloriesForConsumedTextView.setText(String.valueOf((int) (searchedAliment.getNbCalories() * consumedQuantity / 100)));
        alimentProteinesForConsumedTextView.setText(String.valueOf((int) (searchedAliment.getQuantiteProteines() * consumedQuantity / 100)));
        alimentLipidesForConsumedTextView.setText(String.valueOf((int) (searchedAliment.getQuantiteLipides() * consumedQuantity / 100)));
        alimentGlucidesForConsumedTextView.setText(String.valueOf((int) (searchedAliment.getQuantiteGlucides() * consumedQuantity / 100)));
        
    }
    
    public float calculateQuantity() {
        try {
            Float consumedQuantity = Float.parseFloat(consumedQuantityEditText.getText().toString());
            Float coeff = 1f;
            switch (consumedUnitSpinner.getSelectedItem().toString()) {
                case "L":
                    coeff = 1000f;
                    break;
                case "cL":
                    coeff = 100f;
                    break;
                case "fl oz":
                    coeff = 29.5735296875f;
                    break;
                case "cuillère à soupe":
                    coeff = 15f;
                    break;
                case "tasse":
                    coeff = 250f;
                    break;
                case "bol":
                    coeff = 350f;
                    break;
                case "oz":
                    coeff = 28.3495f;
                    break;
                case "lb":
                    coeff = 453.59237f;
                    break;
                case "tasse (moulu)":
                    coeff = 120f;
                    break;
                case "bol (moulu)":
                    coeff = 220f;
                    break;
                default:
                    break;
            }
            if (consumedQuantity <= 0) {
                throw new NumberFormatException();
            }
            return consumedQuantity * coeff;
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent intent = new Intent(AjoutAliment.this, RechercheAliment.class);
            intent.putExtra("date", DateUtils.stringify(selectedDate));
            intent.putExtra("repas", getIntent().getStringExtra("repas"));
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
}
