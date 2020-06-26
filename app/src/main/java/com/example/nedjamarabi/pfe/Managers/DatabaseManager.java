package com.example.nedjamarabi.pfe.Managers;

import com.example.nedjamarabi.pfe.Utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public abstract class DatabaseManager {
    
    protected FirebaseAuth auth;
    protected DatabaseReference db;
    protected String TABLE_NAME;
    
    public DatabaseManager() {
        auth = FirebaseAuth.getInstance();
    }
    
    public void open() {
        db = Utils.getDatabase().getReference(TABLE_NAME);
    }
    
    public void close() {
    }
    
}
