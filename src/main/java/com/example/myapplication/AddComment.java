package com.example.myapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
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

import java.sql.Time;
import java.util.HashMap;
import java.util.Map;

public class AddComment extends AppCompatActivity {
    ActionBar actionBar;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseUser user;
    String userId;
    Button uploadBtn;
    EditText added_comment;
    StorageReference storageReference;
    String fullName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_comment);
        //actionBar.setTitle("Add Comment");

        Intent data = getIntent();
        String mId = data.getStringExtra("mId");
        String mName = data.getStringExtra("mName");
        String mFaculty = data.getStringExtra("mFaculty");

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        user = fAuth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();
        userId = fAuth.getCurrentUser().getUid();

        added_comment = findViewById(R.id.add_comment_textField);
        uploadBtn = findViewById(R.id.add_comment_button);

        DocumentReference documentReference = fStore.collection("users").document(userId);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                fullName = value.getString("fullName");
            }
        });

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(added_comment.getText().toString().isEmpty()){
                    Toast.makeText(AddComment.this, "Text field is empty", Toast.LENGTH_SHORT).show();
                    return;
                }else {

                    DocumentReference documentReference = fStore.collection("users/"+mId+"/podakovanie").document();
                    Map<String, Object> message = new HashMap<>();
                    message.put("meno", fullName);
                    message.put("komentar", added_comment.getText().toString());
                    documentReference.set(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("TAG", "Your comment was succesfully upload");
                        }
                    });
                    finish();
                }


            }
        });
    }
}
