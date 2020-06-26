package com.example.nedjamarabi.pfe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.nedjamarabi.pfe.Managers.AlimentManager;
import com.example.nedjamarabi.pfe.Managers.AlimentsFavorisManager;
import com.example.nedjamarabi.pfe.R;
import com.example.nedjamarabi.pfe.Suivi.Aliment;
import com.example.nedjamarabi.pfe.Suivi.AlimentsFavoris;
import com.example.nedjamarabi.pfe.Utils.LimitArrayAdapter;
import com.example.nedjamarabi.pfe.Utils.OccurenceRechercheAliment;
import com.example.nedjamarabi.pfe.Utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;


public class RechercheAliment extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseDatabase database;
    private DatabaseReference root;
    private ImageButton rechercheCodeBarreImageButton;
    private EditText rechercheCodeBarreEditText;
    private AutoCompleteTextView rechercheNomAutoCompleteTextView;
    private Button confirmerRechercheButton;
    private ListView alimentsFavorisListView;
    private ArrayAdapter<String> alimentsFavorisAdapter, rechercheNomAdapter;
    private int repasOrdinal;
    private Aliment aliment;
    private ArrayList<Aliment> alimentsPlusRecherches;
    private HashMap<String, String> alimentsTrouves;
    private ArrayList<String> nomsAlimentsPlusRecherches, nomsAlimentsTrouves;
    private AlimentManager am;
    private AlimentsFavoris alimentsFavoris;
    private AlimentsFavorisManager afm;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recherche_aliment);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            user = auth.getCurrentUser();
            database = Utils.getDatabase();
            root = database.getReference();
            rechercheCodeBarreImageButton = findViewById(R.id.rechercheCodeBarreImageButton);
            rechercheCodeBarreEditText = findViewById(R.id.rechercheCodeBarreEditText);
            rechercheNomAutoCompleteTextView = findViewById(R.id.rechercheNomAutoCompleteTextView);
            confirmerRechercheButton = findViewById(R.id.confirmerRechercheButton);
            alimentsFavorisListView = findViewById(R.id.alimentsFavorisListView);
            repasOrdinal = getIntent().getIntExtra("repas", 0);
            afm = new AlimentsFavorisManager();
            am = new AlimentManager();
            afm.open();
            am.open();
            alimentsPlusRecherches = new ArrayList<>();
            nomsAlimentsPlusRecherches = new ArrayList<>();
            confirmerRechercheButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!rechercheCodeBarreEditText.getText().toString().isEmpty()) {
                        final Long idAliment = Long.parseLong(rechercheCodeBarreEditText.getText().toString());
                        DatabaseReference ref = am.prepare(idAliment);
                        am.prepare(idAliment).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    aliment = am.get(dataSnapshot);
                                    Intent i = new Intent(RechercheAliment.this, AjoutAliment.class);
                                    i.putExtra("repas", repasOrdinal);
                                    i.putExtra("date", getIntent().getStringExtra("date"));
                                    i.putExtra("aliment", aliment);
                                    finish();
                                    startActivity(i);
                                } else {
                                        /* A compléter */
                                }
                            }
                            
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
                        
                    }
                    
                }
            });
            rechercheCodeBarreImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(RechercheAliment.this, ScannerActivity.class);
                    intent.putExtra("repas", repasOrdinal);
                    intent.putExtra("date", getIntent().getStringExtra("date"));
                    startActivity(intent);
                    finish();
                    
                }
            });
            addSuggestions();
            rechercheNomAutoCompleteTextView.setThreshold(1);
            root.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    alimentsFavoris = afm.get(dataSnapshot.child("AlimentsFavoris").child(user.getUid()));
                    ArrayList<OccurenceRechercheAliment> alimentsTries = alimentsFavoris.trierListeAliments();
                    for (OccurenceRechercheAliment a : alimentsTries) {
                        Aliment al = am.get(dataSnapshot.child("Aliment").child(String.valueOf(a.getIdAliment())));
                        alimentsPlusRecherches.add(al);
                        nomsAlimentsPlusRecherches.add(al.getNom());
                    }
                    alimentsFavorisAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, nomsAlimentsPlusRecherches);
                    alimentsFavorisListView.setAdapter(alimentsFavorisAdapter);
                    alimentsFavorisListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            aliment = alimentsPlusRecherches.get(i);
                            Intent intent = new Intent(RechercheAliment.this, AjoutAliment.class);
                            intent.putExtra("repas", repasOrdinal);
                            intent.putExtra("date", getIntent().getStringExtra("date"));
                            intent.putExtra("aliment", aliment);
                            startActivity(intent);
                            finish();
                            
                        }
                    });
                    
                }
                
                
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
            rechercheNomAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    am.open();
                    am.prepare(Long.valueOf(alimentsTrouves.get(adapterView.getItemAtPosition(i).toString()))).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Intent intent = new Intent(RechercheAliment.this, AjoutAliment.class);
                            intent.putExtra("repas", repasOrdinal);
                            intent.putExtra("date", getIntent().getStringExtra("date"));
                            intent.putExtra("aliment", am.get(dataSnapshot));
                            startActivity(intent);
                            finish();
                        }
                        
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                    
                }
            });
            Intent i = getIntent();
            if (i.hasExtra("code")) {
                am = new AlimentManager();
                am.open();
                am.prepare(Long.valueOf(i.getStringExtra("code"))).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            aliment = am.get(dataSnapshot);
                            Intent intent = new Intent(RechercheAliment.this, AjoutAliment.class);
                            intent.putExtra("repas", repasOrdinal);
                            intent.putExtra("date", getIntent().getStringExtra("date"));
                            intent.putExtra("aliment", aliment);
                            startActivity(intent);
                            finish();
                            
                        }
                        
                    }
                    
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
                
            }
        } else {
        }
    }
    
    private void addSuggestions() {
        Utils.getDatabase().getReference().child("Aliment").orderByChild("nom")/*.startAt(charSequence.toString()).endAt(charSequence.toString() + "\uf8ff")*/.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    alimentsTrouves = new HashMap<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        alimentsTrouves.put(snapshot.child("nom").getValue().toString(), snapshot.getKey());
                    }
                    rechercheNomAdapter = new LimitArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, alimentsTrouves.keySet());
                    rechercheNomAutoCompleteTextView.setAdapter(rechercheNomAdapter);
                }
                
            }
            
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
}
