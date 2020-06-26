package com.example.nedjamarabi.pfe.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.example.nedjamarabi.pfe.Managers.AlimentManager;
import com.example.nedjamarabi.pfe.Managers.BilanManager;
import com.example.nedjamarabi.pfe.Managers.HistoriquePoidsManager;
import com.example.nedjamarabi.pfe.R;
import com.example.nedjamarabi.pfe.Suivi.Aliment;
import com.example.nedjamarabi.pfe.Suivi.Bilan;
import com.example.nedjamarabi.pfe.Suivi.Consommation;
import com.example.nedjamarabi.pfe.Suivi.HistoriquePoids;
import com.example.nedjamarabi.pfe.Utils.Graphe;
import com.example.nedjamarabi.pfe.Utils.Point;
import com.example.nedjamarabi.pfe.Utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;


public class StatistiqueFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    View mView;
    private String mParam1;
    private String mParam2;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private GraphView graphView;
    private Spinner graphTypeSpinner;
    private Spinner graphDateSpinner;
    private LinearLayout macrosVisibilityLayout;
    private CheckBox[] macrosVisibilityCheckBoxes;
    private Graphe graphe;
    private Calendar c;
    
    private int graphType = Graphe.GRAPH_TYPE_POIDS, graphDate = -30;
    private HistoriquePoidsManager hm;
    private BilanManager bm;
    private AlimentManager am;
    private ArrayList<Bilan> bilans;
    private HashSet<Long> idAlimentsSet;
    private HashMap<Long, Aliment> alimentsSet;
    private OnFragmentInteractionListener mListener;
    
    public StatistiqueFragment() {
    }
    
    public static StatistiqueFragment newInstance(String param1, String param2) {
        StatistiqueFragment fragment = new StatistiqueFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        auth = FirebaseAuth.getInstance();
        mView = inflater.inflate(R.layout.fragment_statistique, container, false);
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            user = auth.getCurrentUser();
            graphView = mView.findViewById(R.id.graph);
            macrosVisibilityLayout = mView.findViewById(R.id.macrosVisibilityLayout);
            graphTypeSpinner = mView.findViewById(R.id.graphTypeSpinner);
            graphDateSpinner = mView.findViewById(R.id.graphDateSpinner);
            macrosVisibilityCheckBoxes = new CheckBox[]{mView.findViewById(R.id.proteinesCheckBox), mView.findViewById(R.id.lipidesCheckBox), mView.findViewById(R.id.glucidesCheckBox),};
            final ArrayList<ArrayList<Point>> values = new ArrayList<>();
            ArrayAdapter<CharSequence> graphTypeAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.graphTypeChoices, android.R.layout.simple_spinner_item);
            ArrayAdapter<CharSequence> graphDateAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.graphDateChoices, android.R.layout.simple_spinner_item);
            graphTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            graphDateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            graphTypeSpinner.setAdapter(graphTypeAdapter);
            graphDateSpinner.setAdapter(graphDateAdapter);
            values.add(new ArrayList<Point>());
            values.add(new ArrayList<Point>());
            values.add(new ArrayList<Point>());
            values.add(new ArrayList<Point>());
            values.add(new ArrayList<Point>());
            graphe = new Graphe(getActivity(), graphView, LineGraphSeries.class, values);
            graphDateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    c = Calendar.getInstance();
                    switch (position) {
                        case 0:
                            graphDate = -30;
                            break;
                        case 1:
                            graphDate = -6;
                            break;
                        default:
                            break;
                    }
                    c.add(Calendar.DAY_OF_MONTH, graphDate);
                    graphe.drawGraph(graphType, c.getTime(), new Date());
                }
                
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
            graphTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    c = Calendar.getInstance();
                    switch (position) {
                        case 0:
                            graphType = Graphe.GRAPH_TYPE_POIDS;
                            macrosVisibilityLayout.setVisibility(View.INVISIBLE);
                            break;
                        case 1:
                            graphType = Graphe.GRAPHE_TYPE_CALORIES;
                            macrosVisibilityLayout.setVisibility(View.INVISIBLE);
                            break;
                        case 2:
                            graphType = Graphe.GRAPH_TYPE_MACRO;
                            macrosVisibilityLayout.setVisibility(View.VISIBLE);
                            for (CheckBox macrosVisibilityCheckBoxe : macrosVisibilityCheckBoxes) {
                                macrosVisibilityCheckBoxe.setChecked(true);
                            }
                            break;
                        default:
                            break;
                    }
                    c.add(Calendar.DAY_OF_MONTH, graphDate);
                    graphe.drawGraph(graphType, c.getTime(), new Date());
                }
                
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
            for (int i = 0; i < macrosVisibilityCheckBoxes.length; i++) {
                final int iTemp = i;
                macrosVisibilityCheckBoxes[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        graphe.setVisibility(iTemp, ((CheckBox) v).isChecked());
                    }
                });
            }
            hm = new HistoriquePoidsManager();
            bm = new BilanManager();
            am = new AlimentManager();
            hm.open();
            bm.open();
            am.open();
            hm.prepare(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        ArrayList<HistoriquePoids> hList = hm.getAll(dataSnapshot);
                        for (HistoriquePoids h : hList) {
                            values.get(4).add(new Point(h.getDate(), h.getPoids()));
                        }
                        c = Calendar.getInstance();
                        c.add(Calendar.DAY_OF_MONTH, graphDate);
                        graphe.drawGraph(graphType, c.getTime(), new Date());
                    }
                }
                
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
            DatabaseReference ref = Utils.getDatabase().getReference();
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    bilans = bm.getAll(dataSnapshot.child("Bilan").child(user.getUid()));
                    idAlimentsSet = new HashSet<>();
                    alimentsSet = new HashMap<>();
                    for (Bilan b : bilans) {
                        for (Consommation c : b.getConsommations()) {
                            idAlimentsSet.add(c.getIdAliment());
                        }
                    }
                    for (Long l : idAlimentsSet) {
                        if (dataSnapshot.child("Aliment").child(String.valueOf(l)).exists()) {
                            alimentsSet.put(l, am.get(dataSnapshot.child("Aliment").child(String.valueOf(l))));
                        }
                    }
                    float caloriesCount, proteinesCount, lipidesCount, glucidesCount;
                    for (Bilan b : bilans) {
                        caloriesCount = 0;
                        proteinesCount = 0;
                        lipidesCount = 0;
                        glucidesCount = 0;
                        for (Consommation c : b.getConsommations()) {
                            if (alimentsSet.containsKey(c.getIdAliment())) {
                                Aliment a = alimentsSet.get(c.getIdAliment());
                                caloriesCount += a.getNbCalories() * c.getQuantite() / 100;
                                proteinesCount += a.getQuantiteProteines() * c.getQuantite() / 100;
                                lipidesCount += a.getQuantiteLipides() * c.getQuantite() / 100;
                                glucidesCount += a.getQuantiteGlucides() * c.getQuantite() / 100;
                            }
                        }
                        Point p1 = new Point(b.getDateBilan(), caloriesCount);
                        Point p2 = new Point(b.getDateBilan(), proteinesCount);
                        Point p3 = new Point(b.getDateBilan(), lipidesCount);
                        Point p4 = new Point(b.getDateBilan(), glucidesCount);
                        values.get(0).add(p1);
                        values.get(1).add(p2);
                        values.get(2).add(p3);
                        values.get(3).add(p4);
                        
                    }
                    c = Calendar.getInstance();
                    c.add(Calendar.DAY_OF_MONTH, graphDate);
                    graphe.drawGraph(graphType, c.getTime(), new Date());
                }
                
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
            
        } else {
        }
        return mView;
    }
    
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
    
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }
    
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    
    
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
