package com.example.nedjamarabi.pfe.Managers;


import com.example.nedjamarabi.pfe.Suivi.ActivitePhysique;
import com.example.nedjamarabi.pfe.Suivi.Morphotype;
import com.example.nedjamarabi.pfe.Suivi.Niveau;
import com.example.nedjamarabi.pfe.Suivi.Objectif;
import com.example.nedjamarabi.pfe.Suivi.Sexe;
import com.example.nedjamarabi.pfe.Suivi.Utilisateur;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

public class UtilisateurManager extends DatabaseManager {
    
    public UtilisateurManager() {
        TABLE_NAME = "Utilisateur";
    }
    
    public void insert(Utilisateur u) {
        DatabaseReference ref = db.child(String.valueOf(u.getIdUtilisateur()));
        ref.child("username").setValue(u.getUsername());
        ref.child("date").setValue(u.getDateInscription());
        ref.child("age").setValue(u.getAge());
        ref.child("taille").setValue(u.getTaille());
        ref.child("poids").setValue(u.getPoids());
        ref.child("poidsDesire").setValue(u.getPoidsDesire());
        ref.child("sexe").setValue(u.getSexe().ordinal());
        ref.child("activite").setValue(u.getActivite().ordinal());
        ref.child("morphotype").setValue(u.getMorphotype().ordinal());
        ref.child("niveau").setValue(u.getNiveau().ordinal());
        ref.child("objectif").setValue(u.getObjectif().ordinal());
    }
    
    public DatabaseReference prepare(String idUtilisateur) {
        return db.child(idUtilisateur);
    }
    
    public Utilisateur get(DataSnapshot dataSnapshot) {
        Utilisateur u = new Utilisateur();
        u.setIdUtilisateur(dataSnapshot.getKey());
        try {
            u.setUsername(dataSnapshot.child("username").getValue().toString());
            u.setDateInscription(dataSnapshot.child("date").getValue().toString());
            u.setAge(Integer.parseInt(dataSnapshot.child("age").getValue().toString()));
            u.setTaille(Integer.parseInt(dataSnapshot.child("taille").getValue().toString()));
            u.setPoids(Float.parseFloat(dataSnapshot.child("poids").getValue().toString()));
            u.setPoidsDesire(Float.parseFloat(dataSnapshot.child("poidsDesire").getValue().toString()));
            u.setSexe(Sexe.get(Integer.parseInt(dataSnapshot.child("sexe").getValue().toString())));
            u.setActivite(ActivitePhysique.get(Integer.parseInt(dataSnapshot.child("activite").getValue().toString())));
            u.setMorphotype(Morphotype.get(Integer.parseInt(dataSnapshot.child("morphotype").getValue().toString())));
            u.setNiveau(Niveau.get(Integer.parseInt(dataSnapshot.child("niveau").getValue().toString())));
            u.setObjectif(Objectif.get(Integer.parseInt(dataSnapshot.child("objectif").getValue().toString())));
        } catch (NullPointerException e) {
            u = null;
        }
        return u;
    }
    
    public void delete(String idUtilisateur) {
        DatabaseReference ref = db.child(idUtilisateur);
        ref.removeValue();
    }
}
