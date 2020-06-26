package com.example.nedjamarabi.pfe.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nedjamarabi.pfe.R;
import com.example.nedjamarabi.pfe.Utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by salimdeepside on 31/01/2018.
 */

public class SignUpActivity extends Activity implements View.OnClickListener {
    
    private final static String TAG = "SignUpActivity";
    private EditText fullName, emailId, password, confirmPassword;
    private String getEmailId, getPassword;
    private TextView already_user;
    private Button signUpButton;
    private CheckBox terms_conditions;
    private FirebaseAuth mAuth;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String languageToLoad = "fr"; // your language
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        setContentView(R.layout.activity_signup);
        fullName = findViewById(R.id.userName);
        emailId = findViewById(R.id.userEmailId);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirmPassword);
        signUpButton = findViewById(R.id.signUpBtn);
        already_user = findViewById(R.id.already_user);
        terms_conditions = findViewById(R.id.terms_conditions);
        mAuth = FirebaseAuth.getInstance();
        signUpButton.setOnClickListener(this);
        already_user.setOnClickListener(this);
        
    }
    
    private boolean checkValidation() {
        String getFullName = fullName.getText().toString().trim();
        String getConfirmPassword = confirmPassword.getText().toString().trim();
        getEmailId = emailId.getText().toString().trim();
        getPassword = password.getText().toString().trim();
        Pattern p = Pattern.compile(Utils.regEx);
        Matcher m = p.matcher(getEmailId);
        if (getFullName.equals("") || getFullName.length() == 0 || getEmailId.equals("") || getEmailId.length() == 0 || getPassword.equals("") || getPassword.length() == 0 || getConfirmPassword.equals("") || getConfirmPassword.length() == 0) {
            Toast.makeText(SignUpActivity.this, "All fields are required.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!m.find()) {
            Toast.makeText(SignUpActivity.this, R.string.fui_invalid_email_address, Toast.LENGTH_SHORT).show();
            return false;
        } else if (!getConfirmPassword.equals(getPassword)) {
            Toast.makeText(SignUpActivity.this, "Both password doesn't match.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (getPassword.length() < 6) {
            Toast.makeText(SignUpActivity.this, "You need at least 6 characters.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!terms_conditions.isChecked()) {
            Toast.makeText(SignUpActivity.this, "Please select Terms and Conditions.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.signUpBtn:
                if (checkValidation()) {
                    if (Utils.isOnline(this)) {
                        mAuth.createUserWithEmailAndPassword(getEmailId, getPassword).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "signInWithEmail:success");
                                    sendVerificationEmail();
                                    
                                } else {
                                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                                    Toast.makeText(SignUpActivity.this, "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    findViewById(R.id.signup_layout).startAnimation(AnimationUtils.loadAnimation(SignUpActivity.this, R.anim.shake));
                                    
                                }
                                // ...
                            }
                        });
                    } else
                        Snackbar.make(view, "No internet connection", Snackbar.LENGTH_LONG).show();
                    
                } else
                    findViewById(R.id.signup_layout).startAnimation(AnimationUtils.loadAnimation(SignUpActivity.this, R.anim.shake));
                break;
            case R.id.already_user:
                onBackPressed();
                break;
            
        }
    }
    
    private void sendVerificationEmail() {
        FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(SignUpActivity.this, R.string.fui_email_sent, Toast.LENGTH_SHORT).show();
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                    finish();
                } else {
                    sendVerificationEmail();
                    
                }
            }
        });
    }
    
}
