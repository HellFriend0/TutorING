package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class AddMessage extends AppCompatActivity {
    ActionBar actionBar;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseUser user;
    String userId;
    Button uploadBtn;
    EditText added_message;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_message);
        actionBar = getSupportActionBar();
        actionBar.setTitle("Add Message");

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        user = fAuth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();
        userId = fAuth.getCurrentUser().getUid();

        added_message = findViewById(R.id.add_message_textField);
        uploadBtn = findViewById(R.id.add_message_button);

        final Spinner mySpinner = findViewById(R.id.messageList);
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.kategorie));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner.setAdapter(myAdapter);

        final String[] fakulta = new String[1];
        final String[] fullName = new String[1];
        final String[] id = new String[1];
        final DocumentReference dc = fStore.collection("users").document(userId);
        dc.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                fakulta[0] = documentSnapshot.getString("fakulta");
                fullName[0] = documentSnapshot.getString("fullName");
                id[0] = documentSnapshot.getString("ID");
            }
        });

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(added_message.getText().toString().isEmpty() || mySpinner.getSelectedItemPosition() == 0){
                    Toast.makeText(AddMessage.this, "Text field or category field is empty", Toast.LENGTH_SHORT).show();
                    return;
                }else {

                    DocumentReference documentReference = fStore.collection("nastenka").document();
                    Map<String, Object> message = new HashMap<>();
                    message.put("email", user.getEmail());
                    message.put("text", added_message.getText().toString());
                    message.put("fakulta", fakulta[0]);
                    message.put("fullName", fullName[0]);
                    message.put("time", Timestamp.now().toDate());
                    message.put("ID",id[0]);
                    message.put("kategoria", mySpinner.getSelectedItem().toString());
                    message.put("documentID",documentReference.getId());
                    documentReference.set(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("TAG", "Your message was succesfully upload");
                            Toast.makeText(AddMessage.this, documentReference.getId(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }
            }
        });
    }
}
