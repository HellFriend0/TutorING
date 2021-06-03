package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;
import java.util.Objects;

public class Login extends AppCompatActivity {
    EditText mEmail,mHeslo;
    Button mLoginButton, changeLang;
    TextView mLoginText, mForgotText;
    FirebaseAuth mAuth;
    ProgressBar mLoginBar;
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
        setContentView(R.layout.activity_login);
        //zmena názvy aplikácie v bare na vrchu
        actionBar = getSupportActionBar();
        actionBar.setTitle(getResources().getString(R.string.app_name));

        changeLang = findViewById(R.id.loginLang);
        changeLang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //vypis listu dostupnych jazykov
                showChangeLanguageDialog();
            }
        });

        mEmail = findViewById(R.id.loginEmail);
        mHeslo = findViewById(R.id.loginHeslo);
        mLoginButton = findViewById(R.id.loginButton);
        mLoginBar = findViewById(R.id.loginBar);
        mLoginText = findViewById(R.id.loginText);
        mAuth = FirebaseAuth.getInstance();
        mForgotText = findViewById(R.id.loginForgotPass);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString().trim();
                String password = mHeslo.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    mEmail.setError("Musíš zadať email");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    mHeslo.setError("Musíš zadať heslo");
                    return;
                }

                if(password.length() < 6){
                    mEmail.setError("Heslo musí mať aspoň 6 znakov");
                    return;
                }

                mLoginBar.setVisibility(View.VISIBLE);

                //autentifikácia

                mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(Login.this,"Úspešne prihlásený",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        }else{
                            Toast.makeText(Login.this,"Error !" + Objects.requireNonNull(task.getException()).getMessage(),Toast.LENGTH_SHORT).show();
                            //Toast.makeText(Login.this,"Error !" + task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            mLoginBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });
        mLoginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(),Register.class));
            }
        });
        //zabudnté heslo
        mForgotText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText resetMail = new EditText(v.getContext());
                AlertDialog.Builder passwordresettext = new AlertDialog.Builder(v.getContext());
                passwordresettext.setTitle("Reset password?");
                passwordresettext.setMessage("Enter your email to receive reset link.");
                passwordresettext.setView(resetMail);

                passwordresettext.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //ziskanie mailu a poslanie linku
                        String mail = resetMail.getText().toString();
                        mAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(Login.this, "Reset link has been send to your email",Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Login.this, "Error! Link is not sent. " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                passwordresettext.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //zatvor dialog (netreba nič viac)
                    }
                });

                passwordresettext.create().show();
            }
        });
    }
    //----------------------------------------------------------------------------------------------

    private void showChangeLanguageDialog() {
        final String[] listItems = {"Slovensky", "English"};
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(Login.this);
        mBuilder.setTitle("Choose language");
        mBuilder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                if(i == 0){
                    //slovensky
                    setLocale("sk");
                    recreate();
                }else if(i == 1){
                    //anglicky
                    setLocale("en");
                    recreate();
                }

                //vypnutie zobrazenia
                dialog.dismiss();
            }
        });
        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,getBaseContext().getResources().getDisplayMetrics());
        //uloženie dát do preferencií
        SharedPreferences.Editor editor = getSharedPreferences("Settings",MODE_PRIVATE).edit();
        editor.putString("My_Lang",lang);
        editor.apply();
    }

    //načítanie uložených preferenci
    public void loadLocale(){
        SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String language = prefs.getString("My_Lang","");
        setLocale(language);
    }
}