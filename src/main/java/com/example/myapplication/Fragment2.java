package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;
import static com.example.myapplication.Register.TAG;

public class Fragment2 extends Fragment {
    private MainFragment.onFragmentBtnSelected listener;

    TextView fullName,email, fakulta;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    String userId;
    ImageView profileImage;
    StorageReference storageReference;
    Button changeLang;

    @Nullable
    @Override
    public View onCreateView (@NonNull LayoutInflater inflater, @Nullable ViewGroup container,@Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment2,container,false);
        getActivity().setTitle(getResources().getString(R.string.title_notifications));

        changeLang = view.findViewById(R.id.fragment2Lang);
        changeLang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //vypis listu dostupnych jazykov
                showChangeLanguageDialog();
            }
        });

        fullName = view.findViewById(R.id.fragment2FName);
        email = view.findViewById(R.id.fragment2Email);
        fakulta = view.findViewById(R.id.fragment2Fakulta);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();
        //uploadovanie obrazku
        storageReference = FirebaseStorage.getInstance().getReference();

        StorageReference profileRef = storageReference.child("users/"+firebaseAuth.getCurrentUser().getUid()+"/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profileImage);
            }
        });
        //
        //zobrazenie profilovvch prvkov
        final DocumentReference documentReference = firebaseFirestore.collection("users").document(userId);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                fullName.setText(documentSnapshot != null ? documentSnapshot.getString("fullName") : null);
                email.setText(documentSnapshot != null ? documentSnapshot.getString("email") : null);
                fakulta.setText(documentSnapshot != null ? documentSnapshot.getString("fakulta") : null);
            }
        });
        //
        //zobrazenie správy o neverifikovanom emaile a tlačidlo na odoslanie
        Button verify = view.findViewById(R.id.fragment2VerifyBtn);
        TextView verifyMsg = view.findViewById(R.id.fragment2Verify);
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user.isEmailVerified()){
            verify.setVisibility(View.GONE);
            verifyMsg.setVisibility(View.GONE);
        }
        if(!user.isEmailVerified()){
            verify.setVisibility(View.VISIBLE);
            verifyMsg.setVisibility(View.VISIBLE);

            verify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(v.getContext(), "Verification email has been sent", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: Email not sent " + e.getMessage());
                        }
                    });
                }
            });
        }
        //resetovanie hesla - lokálne
        Button passreset = view.findViewById(R.id.fragment2ResetPassBtn);
        passreset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onButtonSelectedResetPass();
            }
        });
        //zmena obrazku
        profileImage = view.findViewById(R.id.fragment2Picture);
        Button change = view.findViewById(R.id.fragment2ChangeProfileBtn);
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(),EditProfile.class);
                i.putExtra("fullName",fullName.getText().toString());
                i.putExtra("email",email.getText().toString());
                startActivity(i);
            }
        });

        Button clickme2 = view.findViewById(R.id.fragment2Logout);
        clickme2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onButtonSelectedLogout();
            }
        });

        return view;
    }
    //----------------------------------------------------------------------------------------------

    @Override
    public void onAttach(@NonNull Context context){
        super.onAttach(context);
        if(context instanceof MainFragment.onFragmentBtnSelected){
            listener = (MainFragment.onFragmentBtnSelected) context;
        }else{
            throw new ClassCastException(context.toString() + " must implement listener");
        }
    }

    private void showChangeLanguageDialog() {
        final String[] listItems = {"Slovensky", "English"};
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
        mBuilder.setTitle("Choose language");
        mBuilder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                if(i == 0){
                    //slovensky
                    setLocale("sk");
                    getActivity().recreate();
                }else if(i == 1){
                    //anglicky
                    setLocale("en");
                   getActivity().recreate();
                }

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
        getActivity().getBaseContext().getResources().updateConfiguration(config,getActivity().getBaseContext().getResources().getDisplayMetrics());
        //uloženie dát do preferencií
        SharedPreferences.Editor editor = getActivity().getSharedPreferences("Settings",MODE_PRIVATE).edit();
        editor.putString("My_Lang",lang);
        editor.apply();
        startActivity(new Intent(getContext(),MainActivity.class));
    }

    //načítanie uložených preferenci
    public void loadLocale(){
        SharedPreferences prefs = getActivity().getSharedPreferences("Settings", MODE_PRIVATE);
        String language = prefs.getString("My_Lang","");
        setLocale(language);
    }
}
