package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.Normalizer;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class Register extends AppCompatActivity {
    public static final String TAG = "TAG";
    EditText mMeno,mEmail,mHeslo;
    Button mRegisterButton, changeLang;
    TextView mRegisterText;
    FirebaseAuth mAuth;
    ProgressBar mRegisterBar;
    FirebaseFirestore firestore;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
        setContentView(R.layout.activity_register);

        changeLang = findViewById(R.id.registerLang);
        changeLang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //vypis listu dostupnych jazykov
                showChangeLanguageDialog();
            }
        });

        mEmail = findViewById(R.id.registerEmail);
        mMeno  = findViewById(R.id.registeName);
        mHeslo = findViewById(R.id.registerPassword);
        mRegisterButton = findViewById(R.id.registerButton);
        mRegisterText = findViewById(R.id.registerText);
        mRegisterBar = findViewById(R.id.registerBar);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        if(mAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }

        final Spinner mySpinner = findViewById(R.id.registerFacultyList);
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.fakulty));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner.setAdapter(myAdapter);

        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = mEmail.getText().toString().trim();
                String password = mHeslo.getText().toString().trim();
                final String fullName = mMeno.getText().toString();

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
                }if(mySpinner.getSelectedItemPosition() == 0){
                    Toast.makeText(Register.this,"Vyber si fakultu",Toast.LENGTH_SHORT).show();
                    return;
                }

                mRegisterBar.setVisibility(View.VISIBLE);

                // regeistrácia do firebase

                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            //poslanie verifikacie
                            FirebaseUser userVer = mAuth.getCurrentUser();
                            userVer.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(Register.this, "Verification email has been sent", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: Email not sent " + e.getMessage());
                                }
                            });
                            //

                            Toast.makeText(Register.this,"Užívateľ bol vytvorený",Toast.LENGTH_SHORT).show();
                            userID = mAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = firestore.collection("users").document(userID);

                            Map<String,Object> user = new HashMap<>();
                            user.put("ID",userID);
                            user.put("fullName",fullName);
                            user.put("smallName",StringUtils.unaccent(fullName).toLowerCase());
                            user.put("email",email);
                            user.put("fakulta",mySpinner.getSelectedItem().toString());
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "onSuccess: user profile is created for "+ userID);
                                }
                            });
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        }else {
                            Toast.makeText(Register.this,"Error !" + Objects.requireNonNull(task.getException()).getMessage(),Toast.LENGTH_SHORT).show();
                            //Toast.makeText(Register.this,"Error !" + task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            mRegisterBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });
        mRegisterText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Login.class));
            }
        });
    }
    //----------------------------------------------------------------------------------------------

    public static class StringUtils {
        public static String unaccent(String src) {
            return Normalizer
                    .normalize(src, Normalizer.Form.NFD)
                    .replaceAll("[^\\p{ASCII}]", "");
        }

    }

    private void showChangeLanguageDialog() {
        final String[] listItems = {"Slovensky", "English"};
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(Register.this);
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
