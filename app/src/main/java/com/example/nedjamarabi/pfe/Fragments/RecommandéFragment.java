package com.example.nedjamarabi.pfe.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.nedjamarabi.pfe.Managers.AlimentManager;
import com.example.nedjamarabi.pfe.Managers.RecommandationManager;
import com.example.nedjamarabi.pfe.Managers.RegimeManager;
import com.example.nedjamarabi.pfe.R;
import com.example.nedjamarabi.pfe.Suivi.Aliment;
import com.example.nedjamarabi.pfe.Suivi.Bilan;
import com.example.nedjamarabi.pfe.Suivi.Consommation;
import com.example.nedjamarabi.pfe.Suivi.Recommandation;
import com.example.nedjamarabi.pfe.Suivi.Regime;
import com.example.nedjamarabi.pfe.Suivi.Repas;
import com.example.nedjamarabi.pfe.Suivi.TypeAliment;
import com.example.nedjamarabi.pfe.Suivi.Utilisateur;
import com.example.nedjamarabi.pfe.Utils.Utils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.facebook.FacebookSdk.getApplicationContext;

public class RecommandéFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    ArrayList mAlimentDayArray;
    CheckBox snack1CheckBox, snack2CheckBox, snack3CheckBox;
    Consommation conso;
    Recommandation mRecommandation;
    float[] repasPourcentage = {0, 0, 0, 0, 0, 0};
    RegimeManager mRegimeManager;
    Regime regime;
    private View mView;
    private Dialog myDialog;
    private Utilisateur user;
    private boolean repasVisibility[] = {false, false, false};
    private int nbRepas = 6;
    private ArrayList<Consommation> mAlimentListBreakfast, mAlimentListLunch, mAlimentListDinner, mAlimentListSnack1, mAlimentListSnack2, mAlimentListSnack3;
    private String mParam1;
    private String mParam2;
    private ArrayList<ImageView> mAddButtons;
    private ListView mListViewBreakfast, mListViewLunch, mListViewDinner, mListViewSnack1, mListViewSnack2, mListViewSnack3;
    private LinearLayout mViewBreakfast, mViewLunch, mViewDinner, mViewSnack1, mViewSnack2, mViewSnack3;
    private LayoutInflater mInflater;
    private OnFragmentInteractionListener mListener;
    private RecommandationManager rm;
    private AlimentManager am;
    private ArrayList<Bilan> bilans;
    private ArrayList<Long> idAlimentsSet;
    private HashMap<Long, Aliment> alimentsSet;
    
    public RecommandéFragment() {
    }
    
    
    public static RecommandéFragment newInstance(String param1, String param2) {
        RecommandéFragment fragment = new RecommandéFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    
    private static void setListViewHeightBasedOnItems(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter != null) {
            int numberOfItems = listAdapter.getCount();
            int totalItemsHeight = 0;
            for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
                View item = listAdapter.getView(itemPos, null, listView);
                float px = 500 * (listView.getResources().getDisplayMetrics().density);
                item.measure(View.MeasureSpec.makeMeasureSpec((int) px, View.MeasureSpec.AT_MOST), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                totalItemsHeight += item.getMeasuredHeight();
            }
            int totalDividersHeight = listView.getDividerHeight() * (numberOfItems - 1);
            int totalPadding = listView.getPaddingTop() + listView.getPaddingBottom();
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalItemsHeight + totalDividersHeight + totalPadding;
            listView.setLayoutParams(params);
            listView.requestLayout();
            
        } else {
        }
        
    }
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = (Utilisateur) getArguments().getSerializable(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        mAddButtons = new ArrayList<>();
        mAlimentListBreakfast = new ArrayList<>();
        mAlimentListSnack1 = new ArrayList<>();
        mAlimentListLunch = new ArrayList<>();
        mAlimentListSnack2 = new ArrayList<>();
        mAlimentListDinner = new ArrayList<>();
        mAlimentListSnack3 = new ArrayList<>();
        mAlimentDayArray = new ArrayList<ArrayList>();
        mAlimentDayArray.add(0, mAlimentListBreakfast);
        mAlimentDayArray.add(1, mAlimentListSnack1);
        mAlimentDayArray.add(2, mAlimentListLunch);
        mAlimentDayArray.add(3, mAlimentListSnack2);
        mAlimentDayArray.add(4, mAlimentListDinner);
        mAlimentDayArray.add(5, mAlimentListSnack3);
        Regime regime = new Regime(user);
        regime.calculPlanAlimentaire(user);
        
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mInflater = inflater;
        mView = inflater.inflate(R.layout.fragment_recommandee, container, false);
        myDialog = new Dialog(mView.getContext());
        initRepasView();
        return mView;
    }
    
    private void initRepasView() {
        LinearLayout main_layout = mView.findViewById(R.id.root);
        mViewBreakfast = (LinearLayout) mInflater.inflate(R.layout.repas_layout, null);
        ((TextView) mViewBreakfast.findViewById(R.id.repas_name)).setText("Petit déjeuner");
        mListViewBreakfast = mViewBreakfast.findViewById(R.id.listView_repas);
        main_layout.addView(mViewBreakfast, 0);
        mViewSnack1 = (LinearLayout) mInflater.inflate(R.layout.repas_layout, null);
        ((TextView) mViewSnack1.findViewById(R.id.repas_name)).setText("Gouter 1");
        mListViewSnack1 = mViewSnack1.findViewById(R.id.listView_repas);
        main_layout.addView(mViewSnack1, 1);
        mViewLunch = (LinearLayout) mInflater.inflate(R.layout.repas_layout, null);
        ((TextView) mViewLunch.findViewById(R.id.repas_name)).setText("Déjeuner");
        mListViewLunch = mViewLunch.findViewById(R.id.listView_repas);
        main_layout.addView(mViewLunch, 2);
        mViewSnack2 = (LinearLayout) mInflater.inflate(R.layout.repas_layout, null);
        ((TextView) mViewSnack2.findViewById(R.id.repas_name)).setText("Gouter 2");
        mListViewSnack2 = mViewSnack2.findViewById(R.id.listView_repas);
        main_layout.addView(mViewSnack2, 3);
        mViewDinner = (LinearLayout) mInflater.inflate(R.layout.repas_layout, null);
        ((TextView) mViewDinner.findViewById(R.id.repas_name)).setText("Dinner");
        mListViewDinner = mViewDinner.findViewById(R.id.listView_repas);
        main_layout.addView(mViewDinner, 4);
        mViewSnack3 = (LinearLayout) mInflater.inflate(R.layout.repas_layout, null);
        ((TextView) mViewSnack3.findViewById(R.id.repas_name)).setText("Gouter 3");
        mListViewSnack3 = mViewSnack3.findViewById(R.id.listView_repas);
        main_layout.addView(mViewSnack3, 5);
        mViewBreakfast.findViewById(R.id.add_aliment_repas).setVisibility(View.GONE);
        mViewSnack1.findViewById(R.id.add_aliment_repas).setVisibility(View.GONE);
        mViewLunch.findViewById(R.id.add_aliment_repas).setVisibility(View.GONE);
        mViewSnack2.findViewById(R.id.add_aliment_repas).setVisibility(View.GONE);
        mViewDinner.findViewById(R.id.add_aliment_repas).setVisibility(View.GONE);
        mViewSnack3.findViewById(R.id.add_aliment_repas).setVisibility(View.GONE);
        mListViewBreakfast.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ShowPopup(mView, i, 0);
            }
        });
        mListViewSnack1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ShowPopup(mView, i, 1);
            }
        });
        mListViewLunch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ShowPopup(mView, i, 2);
            }
        });
        mListViewSnack2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ShowPopup(mView, i, 3);
            }
        });
        mListViewDinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ShowPopup(mView, i, 4);
            }
        });
        mListViewSnack3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ShowPopup(mView, i, 5);
            }
        });
        snack1CheckBox = mView.findViewById(R.id.Snack1CheckBox);
        snack2CheckBox = mView.findViewById(R.id.Snack2CheckBox);
        snack3CheckBox = mView.findViewById(R.id.Snack3CheckBox);
        SetConsomationView();
        showCheckBox();
        
    }
    
    private void SetConsomationView() {
        idAlimentsSet = new ArrayList<>();
        alimentsSet = new HashMap<>();
        for (Object arrayList : mAlimentDayArray)
            ((ArrayList) arrayList).clear();
        rm = new RecommandationManager();
        am = new AlimentManager();
        DatabaseReference ref = Utils.getDatabase().getReference();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    mRecommandation = rm.get(dataSnapshot.child("RegimeRecommande").child(user.getIdUtilisateur()));
                    ArrayList<Consommation> consommations = mRecommandation.getAlimentsRecommandes();
                    System.out.println(consommations);
                    for (Consommation c : consommations) {
                        idAlimentsSet.add(c.getIdAliment());
                        Consommation consommation = new Consommation(c.getIdAliment(), c.getRepas(), c.getQuantite());
                        ((ArrayList<Consommation>) mAlimentDayArray.get(c.getRepas().ordinal())).add(consommation);
                    }
                    for (Long l : idAlimentsSet) {
                        if (dataSnapshot.child("Aliment").child(String.valueOf(l)).exists()) {
                            Aliment aliment = am.get(dataSnapshot.child("Aliment").child(String.valueOf(l)));
                            alimentsSet.put(l, aliment);
                        }
                    }
                    updateList(mAlimentListBreakfast, mListViewBreakfast);
                    updateList(mAlimentListSnack1, mListViewSnack1);
                    updateList(mAlimentListLunch, mListViewLunch);
                    updateList(mAlimentListSnack2, mListViewSnack2);
                    updateList(mAlimentListDinner, mListViewDinner);
                    updateList(mAlimentListSnack3, mListViewSnack3);
                    updateTotal();
                    setListViewHeightBasedOnItems(mListViewBreakfast);
                    setListViewHeightBasedOnItems(mListViewSnack1);
                    setListViewHeightBasedOnItems(mListViewLunch);
                    setListViewHeightBasedOnItems(mListViewSnack2);
                    setListViewHeightBasedOnItems(mListViewDinner);
                    setListViewHeightBasedOnItems(mListViewSnack3);
                    snack1CheckBox.setChecked(false);
                    snack2CheckBox.setChecked(false);
                    snack3CheckBox.setChecked(false);
                    snack1CheckBox.setChecked(!mAlimentListSnack1.isEmpty());
                    snack2CheckBox.setChecked(!mAlimentListSnack2.isEmpty());
                    snack3CheckBox.setChecked(!mAlimentListSnack3.isEmpty());
                    
                }
            }
            
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        
    }
    
    private void updateList(ArrayList<Consommation> arrayList, ListView listView) {
        List<Map<String, String>> data = new ArrayList<>();
        for (Consommation item : arrayList) {
            HashMap<String, String> dataMap = new HashMap<>();
            dataMap.put("name", alimentsSet.get(item.getIdAliment()).getNom()); //icon
            dataMap.put("qte", (int) item.getQuantite() + " g");
            data.add(dataMap);
        }
        ListAdapter listAdapter = new SimpleAdapter(getApplicationContext(), data, R.layout.list_item, new String[]{"name", "qte"}, new int[]{R.id.name, R.id.qte});
        listView.setAdapter(listAdapter);
    }
    
    // TODO: Rename method, update argument and hook method into UI event
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
    
    private void showCheckBox() {
        CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton == snack1CheckBox) {
                    if (compoundButton.isChecked()) {
                        mViewSnack1.setVisibility(View.VISIBLE);
                        repasVisibility[0] = true;
                        nbRepas += 1;
                    } else {
                        mViewSnack1.setVisibility(View.GONE);
                        repasVisibility[0] = false;
                        nbRepas -= 1;
                        
                    }
                    
                } else if (compoundButton == snack2CheckBox) {
                    if (compoundButton.isChecked()) {
                        mViewSnack2.setVisibility(View.VISIBLE);
                        repasVisibility[1] = true;
                        nbRepas += 1;
                        
                    } else {
                        mViewSnack2.setVisibility(View.GONE);
                        repasVisibility[1] = false;
                        nbRepas -= 1;
                        
                    }
                } else if (compoundButton == snack3CheckBox) {
                    if (compoundButton.isChecked()) {
                        mViewSnack3.setVisibility(View.VISIBLE);
                        repasVisibility[2] = true;
                        nbRepas += 1;
                        
                    } else {
                        mViewSnack3.setVisibility(View.GONE);
                        repasVisibility[2] = false;
                        nbRepas -= 1;
                        
                    }
                }
                calculeRepartition();
            }
        };
        snack1CheckBox.setOnCheckedChangeListener(onCheckedChangeListener);
        snack2CheckBox.setOnCheckedChangeListener(onCheckedChangeListener);
        snack3CheckBox.setOnCheckedChangeListener(onCheckedChangeListener);
        
    }
    
    private void calculeRepartition() {
        switch (nbRepas) {
            case 3:
                repasPourcentage[0] = 0.30f;
                repasPourcentage[1] = 0;
                repasPourcentage[2] = 0.40f;
                repasPourcentage[3] = 0;
                repasPourcentage[4] = 0.30f;
                repasPourcentage[5] = 0;
                break;
            case 4:
                repasPourcentage[0] = 0.25f;
                repasPourcentage[2] = 0.30f;
                repasPourcentage[4] = 0.25f;
                if (repasVisibility[0] == true) {
                    repasPourcentage[1] = 0.2f;
                    repasPourcentage[3] = 0;
                    repasPourcentage[5] = 0;
                } else if (repasVisibility[1] == true) {
                    repasPourcentage[1] = 0;
                    repasPourcentage[3] = 0.2f;
                    repasPourcentage[5] = 0;
                } else if (repasVisibility[2] == true) {
                    repasPourcentage[1] = 0;
                    repasPourcentage[3] = 0;
                    repasPourcentage[5] = 0.2f;
                }
                break;
            case 5:
                repasPourcentage[0] = 0.225f;
                repasPourcentage[2] = 0.25f;
                repasPourcentage[4] = 0.225f;
                if (repasVisibility[0] == false) {
                    repasPourcentage[1] = 0;
                    repasPourcentage[3] = 0.15f;
                    repasPourcentage[5] = 0.15f;
                } else if (repasVisibility[1] == false) {
                    repasPourcentage[1] = 0.15f;
                    repasPourcentage[3] = 0;
                    repasPourcentage[4] = 0.15f;
                } else if (repasVisibility[2] == false) {
                    repasPourcentage[1] = 0.15f;
                    repasPourcentage[3] = 0.15f;
                    repasPourcentage[5] = 0;
                }
                break;
            case 6:
                repasPourcentage[0] = 0.1750f;
                repasPourcentage[1] = 0.15f;
                repasPourcentage[2] = 0.20f;
                repasPourcentage[3] = 0.15f;
                repasPourcentage[4] = 0.1750f;
                repasPourcentage[5] = 0.15f;
                break;
        }
        mRegimeManager = new RegimeManager();
        mRegimeManager.open();
        mRegimeManager.prepare(user.getIdUtilisateur()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    regime = mRegimeManager.get(dataSnapshot);
                    recalibrage();
                    
                }
            }
            
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        
    }
    
    private void recalibrage() {
        regime.calculPlanAlimentaire(user);
        am.open();
        //am.insert(new Aliment(11, "Frommage 0%", 49, 0, 0, 8, TypeAliment.SOLIDE));
        alimentsSet.put(1l, new Aliment(1, "Flocon d'avoine", 356, 60, 8, 11, TypeAliment.SOLIDE));
        alimentsSet.put(2l, new Aliment(2, "Banane", 89, 23, 0, 1, TypeAliment.SOLIDE));
        alimentsSet.put(3l, new Aliment(3, "Riz Basmati cuit", 118, 25, 0, 4, TypeAliment.SOLIDE));
        alimentsSet.put(4l, new Aliment(4, "Patate douce cuite", 86, 18, 0, 0, TypeAliment.SOLIDE));
        alimentsSet.put(5l, new Aliment(5, "Blanc d'oeuf", 52, 0.7f, 0.2f, 11, TypeAliment.SOLIDE));
        alimentsSet.put(6l, new Aliment(6, "Blanc de poulet", 125, 1.3f, 1.8f, 21, TypeAliment.SOLIDE));
        alimentsSet.put(7l, new Aliment(7, "Steak de boeuf", 271, 0, 19, 25, TypeAliment.SOLIDE));
        alimentsSet.put(8l, new Aliment(8, "Avocat", 169, 3.1f, 16, 1.8f, TypeAliment.SOLIDE));
        alimentsSet.put(9l, new Aliment(9, "Huile d'olive", 884, 0, 100, 0, TypeAliment.SOLIDE));
        alimentsSet.put(10l, new Aliment(10, "Jaune d'oeuf", 345, 0, 31, 16, TypeAliment.SOLIDE));
        alimentsSet.put(11l, new Aliment(11, "Frommage 0%", 49, 0, 0, 8, TypeAliment.SOLIDE));
        float pro = 0, lip = 0, glu = 0, qte = 0;
        Aliment aliment;
        if (repasPourcentage[0] != 0) {
            mAlimentListBreakfast.clear();
            pro = regime.getProteinesRecommandees() * repasPourcentage[0];
            glu = regime.getGlucidesRecommandees() * repasPourcentage[0];
            lip = regime.getLipidesRecommandees() * repasPourcentage[0];
            aliment = alimentsSet.get(10l);
            qte = lip / aliment.getQuantiteLipides();
            pro -= aliment.getQuantiteProteines() * qte;
            mAlimentListBreakfast.add(new Consommation(10, Repas.get(0), qte * 100));
            aliment = alimentsSet.get(5l);
            qte = pro / aliment.getQuantiteProteines();
            glu -= aliment.getQuantiteGlucides() * qte;
            mAlimentListBreakfast.add(new Consommation(5, Repas.get(0), qte * 100));
            aliment = alimentsSet.get(1l);
            qte = glu / aliment.getQuantiteGlucides();
            mAlimentListBreakfast.add(new Consommation(1, Repas.get(0), qte * 100));
            updateList(mAlimentListBreakfast, mListViewBreakfast);
            setListViewHeightBasedOnItems(mListViewBreakfast);
        }
        if (repasPourcentage[1] != 0) {
            mAlimentListSnack1.clear();
            pro = regime.getProteinesRecommandees() * repasPourcentage[1];
            glu = regime.getGlucidesRecommandees() * repasPourcentage[1];
            lip = regime.getLipidesRecommandees() * repasPourcentage[1];
            aliment = alimentsSet.get(10l);
            qte = pro / aliment.getQuantiteLipides();
            pro -= aliment.getQuantiteProteines() * qte;
            mAlimentListSnack1.add(new Consommation(10, Repas.get(1), qte * 100));
            aliment = alimentsSet.get(5l);
            qte = pro / aliment.getQuantiteProteines();
            glu -= aliment.getQuantiteGlucides() * qte;
            mAlimentListSnack1.add(new Consommation(5, Repas.get(1), qte * 100));
            aliment = alimentsSet.get(2l);
            qte = glu / aliment.getQuantiteGlucides();
            mAlimentListSnack1.add(new Consommation(2, Repas.get(1), qte * 100));
            updateList(mAlimentListSnack1, mListViewSnack1);
            setListViewHeightBasedOnItems(mListViewSnack1);
        }
        if (repasPourcentage[2] != 0) {
            mAlimentListLunch.clear();
            pro = regime.getProteinesRecommandees() * repasPourcentage[2];
            glu = regime.getGlucidesRecommandees() * repasPourcentage[2];
            lip = regime.getLipidesRecommandees() * repasPourcentage[2];
            aliment = alimentsSet.get(7l);
            qte = pro / aliment.getQuantiteProteines();
            glu -= aliment.getQuantiteGlucides() * qte;
            lip -= aliment.getQuantiteLipides() * qte;
            mAlimentListLunch.add(new Consommation(7, Repas.get(2), qte * 100));
            aliment = alimentsSet.get(3l);
            qte = glu / aliment.getQuantiteGlucides();
            lip -= aliment.getQuantiteLipides() * qte;
            mAlimentListLunch.add(new Consommation(3, Repas.get(2), qte * 100));
            aliment = alimentsSet.get(9l);
            qte = pro / aliment.getQuantiteLipides();
            mAlimentListLunch.add(new Consommation(9, Repas.get(2), qte * 100));
            updateList(mAlimentListLunch, mListViewLunch);
            setListViewHeightBasedOnItems(mListViewLunch);
        }
        if (repasPourcentage[3] != 0) {
            mAlimentListSnack2.clear();
            pro = regime.getProteinesRecommandees() * repasPourcentage[3];
            glu = regime.getGlucidesRecommandees() * repasPourcentage[3];
            lip = regime.getLipidesRecommandees() * repasPourcentage[3];
            aliment = alimentsSet.get(10l);
            qte = pro / aliment.getQuantiteLipides();
            pro -= aliment.getQuantiteProteines() * qte;
            mAlimentListSnack2.add(new Consommation(10, Repas.get(3), qte * 100));
            aliment = alimentsSet.get(5l);
            qte = pro / aliment.getQuantiteProteines();
            glu -= aliment.getQuantiteGlucides() * qte;
            mAlimentListSnack2.add(new Consommation(5, Repas.get(3), qte * 100));
            aliment = alimentsSet.get(2l);
            qte = glu / aliment.getQuantiteGlucides();
            mAlimentListSnack2.add(new Consommation(2, Repas.get(3), qte * 100));
            updateList(mAlimentListSnack2, mListViewSnack2);
            setListViewHeightBasedOnItems(mListViewSnack2);
        }
        if (repasPourcentage[4] != 0) {
            mAlimentListDinner.clear();
            pro = regime.getProteinesRecommandees() * repasPourcentage[2];
            glu = regime.getGlucidesRecommandees() * repasPourcentage[2];
            lip = regime.getLipidesRecommandees() * repasPourcentage[2];
            aliment = alimentsSet.get(6l);
            qte = pro / aliment.getQuantiteProteines();
            glu -= aliment.getQuantiteGlucides() * qte;
            lip -= aliment.getQuantiteLipides() * qte;
            mAlimentListDinner.add(new Consommation(6, Repas.get(4), qte * 100));
            aliment = alimentsSet.get(4l);
            qte = glu / aliment.getQuantiteGlucides();
            lip -= aliment.getQuantiteLipides() * qte;
            mAlimentListDinner.add(new Consommation(4, Repas.get(4), qte * 100));
            aliment = alimentsSet.get(9l);
            qte = pro / aliment.getQuantiteLipides();
            mAlimentListDinner.add(new Consommation(9, Repas.get(4), qte * 100));
            updateList(mAlimentListDinner, mListViewDinner);
            setListViewHeightBasedOnItems(mListViewDinner);
        }
        if (repasPourcentage[5] != 0) {
            mAlimentListSnack3.clear();
            pro = regime.getProteinesRecommandees() * repasPourcentage[5];
            glu = regime.getGlucidesRecommandees() * repasPourcentage[5];
            lip = regime.getLipidesRecommandees() * repasPourcentage[5];
            aliment = alimentsSet.get(11l);
            qte = pro / aliment.getQuantiteProteines();
            mAlimentListSnack3.add(new Consommation(11, Repas.get(5), qte * 100));
            aliment = alimentsSet.get(8l);
            qte = lip / aliment.getQuantiteLipides();
            glu -= aliment.getQuantiteGlucides() * qte;
            mAlimentListSnack3.add(new Consommation(8l, Repas.get(5), qte * 100));
            aliment = alimentsSet.get(1l);
            qte = glu / aliment.getQuantiteGlucides();
            mAlimentListSnack3.add(new Consommation(1, Repas.get(5), qte * 100));
            updateList(mAlimentListSnack3, mListViewSnack3);
            setListViewHeightBasedOnItems(mListViewSnack3);
        }
        updateTotal();
    }
    
    
    private void ShowPopup(View v, final int id, final int list) {
        ImageView exitView;
        switch (list) {
            case 0:
                conso = mAlimentListBreakfast.get(id);
                break;
            case 1:
                conso = mAlimentListSnack1.get(id);
                break;
            case 2:
                conso = mAlimentListLunch.get(id);
                break;
            case 3:
                conso = mAlimentListSnack2.get(id);
                break;
            case 4:
                conso = mAlimentListDinner.get(id);
                break;
            case 5:
                conso = mAlimentListSnack3.get(id);
                break;
            
        }
        myDialog.setContentView(R.layout.popup_aliment);
        updatePopup();
        final EditText qteEdit = myDialog.findViewById(R.id.qte_cpt);
        qteEdit.setText((int) conso.getQuantite() + "");
        qteEdit.setEnabled(false);
        qteEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                updatePopup();
            }
            
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        myDialog.findViewById(R.id.action_delete).setVisibility(View.GONE);
        exitView = myDialog.findViewById(R.id.action_exit);
        exitView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDialog.dismiss();
                myDialog.cancel();
                
            }
        });
        myDialog.findViewById(R.id.confirmButton).setVisibility(View.GONE);
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }
    
    private void updateTotal() {
        float pro = 0, cal = 0, lip = 0, glu = 0;
        for (Consommation c : mAlimentListBreakfast) {
            if (alimentsSet.containsKey(c.getIdAliment())) {
                Aliment a = alimentsSet.get(c.getIdAliment());
                cal += a.getNbCalories() * c.getQuantite() / 100;
                pro += a.getQuantiteProteines() * c.getQuantite() / 100;
                lip += a.getQuantiteLipides() * c.getQuantite() / 100;
                glu += a.getQuantiteGlucides() * c.getQuantite() / 100;
            }
        }
        ((TextView) mViewBreakfast.findViewById(R.id.repas_cal_cpt)).setText((int) cal + " Kcal");
        ((TextView) mViewBreakfast.findViewById(R.id.repas_prot_cpt)).setText((int) pro + " g");
        ((TextView) mViewBreakfast.findViewById(R.id.repas_lip_cpt)).setText((int) lip + " g");
        ((TextView) mViewBreakfast.findViewById(R.id.repas_glu_cpt)).setText((int) glu + " g");
        glu = lip = cal = pro = 0;
        for (Consommation c : mAlimentListSnack1) {
            if (alimentsSet.containsKey(c.getIdAliment())) {
                Aliment a = alimentsSet.get(c.getIdAliment());
                cal += a.getNbCalories() * c.getQuantite() / 100;
                pro += a.getQuantiteProteines() * c.getQuantite() / 100;
                lip += a.getQuantiteLipides() * c.getQuantite() / 100;
                glu += a.getQuantiteGlucides() * c.getQuantite() / 100;
            }
            
        }
        ((TextView) mViewSnack1.findViewById(R.id.repas_cal_cpt)).setText((int) cal + " Kcal");
        ((TextView) mViewSnack1.findViewById(R.id.repas_prot_cpt)).setText((int) pro + " g");
        ((TextView) mViewSnack1.findViewById(R.id.repas_lip_cpt)).setText((int) lip + " g");
        ((TextView) mViewSnack1.findViewById(R.id.repas_glu_cpt)).setText((int) glu + " g");
        glu = lip = cal = pro = 0;
        for (Consommation c : mAlimentListLunch) {
            if (alimentsSet.containsKey(c.getIdAliment())) {
                Aliment a = alimentsSet.get(c.getIdAliment());
                cal += a.getNbCalories() * c.getQuantite() / 100;
                pro += a.getQuantiteProteines() * c.getQuantite() / 100;
                lip += a.getQuantiteLipides() * c.getQuantite() / 100;
                glu += a.getQuantiteGlucides() * c.getQuantite() / 100;
            }
            
        }
        ((TextView) mViewLunch.findViewById(R.id.repas_cal_cpt)).setText((int) cal + " Kcal");
        ((TextView) mViewLunch.findViewById(R.id.repas_prot_cpt)).setText((int) pro + " g");
        ((TextView) mViewLunch.findViewById(R.id.repas_lip_cpt)).setText((int) lip + " g");
        ((TextView) mViewLunch.findViewById(R.id.repas_glu_cpt)).setText((int) glu + " g");
        glu = lip = cal = pro = 0;
        for (Consommation c : mAlimentListSnack2) {
            if (alimentsSet.containsKey(c.getIdAliment())) {
                Aliment a = alimentsSet.get(c.getIdAliment());
                cal += a.getNbCalories() * c.getQuantite() / 100;
                pro += a.getQuantiteProteines() * c.getQuantite() / 100;
                lip += a.getQuantiteLipides() * c.getQuantite() / 100;
                glu += a.getQuantiteGlucides() * c.getQuantite() / 100;
            }
            
        }
        ((TextView) mViewSnack2.findViewById(R.id.repas_cal_cpt)).setText((int) cal + " Kcal");
        ((TextView) mViewSnack2.findViewById(R.id.repas_prot_cpt)).setText((int) pro + " g");
        ((TextView) mViewSnack2.findViewById(R.id.repas_lip_cpt)).setText((int) lip + " g");
        ((TextView) mViewSnack2.findViewById(R.id.repas_glu_cpt)).setText((int) glu + " g");
        glu = lip = cal = pro = 0;
        for (Consommation c : mAlimentListDinner) {
            if (alimentsSet.containsKey(c.getIdAliment())) {
                Aliment a = alimentsSet.get(c.getIdAliment());
                cal += a.getNbCalories() * c.getQuantite() / 100;
                pro += a.getQuantiteProteines() * c.getQuantite() / 100;
                lip += a.getQuantiteLipides() * c.getQuantite() / 100;
                glu += a.getQuantiteGlucides() * c.getQuantite() / 100;
            }
            
        }
        ((TextView) mViewDinner.findViewById(R.id.repas_cal_cpt)).setText((int) cal + " Kcal");
        ((TextView) mViewDinner.findViewById(R.id.repas_prot_cpt)).setText((int) pro + " g");
        ((TextView) mViewDinner.findViewById(R.id.repas_lip_cpt)).setText((int) lip + " g");
        ((TextView) mViewDinner.findViewById(R.id.repas_glu_cpt)).setText((int) glu + " g");
        glu = lip = cal = pro = 0;
        for (Consommation c : mAlimentListSnack3) {
            if (alimentsSet.containsKey(c.getIdAliment())) {
                Aliment a = alimentsSet.get(c.getIdAliment());
                cal += a.getNbCalories() * c.getQuantite() / 100;
                pro += a.getQuantiteProteines() * c.getQuantite() / 100;
                lip += a.getQuantiteLipides() * c.getQuantite() / 100;
                glu += a.getQuantiteGlucides() * c.getQuantite() / 100;
            }
            
        }
        ((TextView) mViewSnack3.findViewById(R.id.repas_cal_cpt)).setText((int) cal + " Kcal");
        ((TextView) mViewSnack3.findViewById(R.id.repas_prot_cpt)).setText((int) pro + " g");
        ((TextView) mViewSnack3.findViewById(R.id.repas_lip_cpt)).setText((int) lip + " g");
        ((TextView) mViewSnack3.findViewById(R.id.repas_glu_cpt)).setText((int) glu + " g");
        
    }
    
    
    private void updatePopup() {
        Aliment a = alimentsSet.get(conso.getIdAliment());
        ((TextView) myDialog.findViewById(R.id.alimentname_cpt)).setText(a.getNom());
        ((TextView) myDialog.findViewById(R.id.prot_cpt)).setText((int) (a.getQuantiteProteines() * conso.getQuantite() / 100) + "");
        ((TextView) myDialog.findViewById(R.id.calorie_cpt)).setText((int) (a.getNbCalories() * conso.getQuantite() / 100) + "");
        ((TextView) myDialog.findViewById(R.id.fat_cpt)).setText((int) (a.getQuantiteLipides() * conso.getQuantite() / 100) + "");
        ((TextView) myDialog.findViewById(R.id.carb_cpt)).setText((int) (a.getQuantiteGlucides() * conso.getQuantite() / 100) + "");
    }
    
    
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}

