package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;


public class UserProfile extends AppCompatActivity {
    private ActionBar actionBar;
    private ImageView profileImage;
    private TextView profileName,profileFaculty,numberOfLikes;
    private StorageReference storageReference;
    private RecyclerView commentList;
    private Query query;
    private FirebaseFirestore fStore;
    private ImageButton commentButton;
    private ImageButton like_button;
    private Button emailButton;
    private FirebaseAuth fAuth;
    private FirebaseUser user;
    private String userId;
    private boolean checker = false;

    private FirestoreRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        actionBar = getSupportActionBar();
        actionBar.setTitle(getResources().getString(R.string.student_profile));

        Intent data = getIntent();
        String mName = data.getStringExtra("mName");
        String mId = data.getStringExtra("mId");
        String mFaculty = data.getStringExtra("mFaculty");

        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();
        userId = fAuth.getCurrentUser().getUid();
        fStore = FirebaseFirestore.getInstance();


        profileFaculty = findViewById(R.id.user_profile_faculty);
        profileName = findViewById(R.id.user_profile_name);
        profileImage = findViewById(R.id.user_profile_image);
        commentButton = findViewById(R.id.user_profile_comment_button);
        like_button = findViewById(R.id.user_profile_like_button);
        numberOfLikes = findViewById(R.id.user_profile_like_text);
        emailButton = findViewById(R.id.user_profile_mail_button);

        profileName.setText(mName);
        profileFaculty.setText(mFaculty);
        setProfileImage(mId);

        DocumentReference documentReference = fStore.collection("users/"+mId+"/likes").document(userId);
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    checker = true;
                    like_button.setBackgroundColor(Color.CYAN);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                checker = false;
                Log.d("TAG","NEMáš LIKY");
            }
        });

        CollectionReference collectionReference = fStore.collection("users/"+mId+"/likes");
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                int count;
                count = value.size();
                if(count!=0) {
                    numberOfLikes.setText(count + " Likes");
                }else{
                    numberOfLikes.setText("0 Likes");
                }
            }
        });

        commentList = findViewById(R.id.user_comment_list);
        fStore = FirebaseFirestore.getInstance();
        query = fStore.collection("users/"+mId+"/podakovanie");

        FirestoreRecyclerOptions<CommentModel> options = new FirestoreRecyclerOptions.Builder<CommentModel>()
                .setQuery(query,CommentModel.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<CommentModel, CommentViewHolder>(options) {
            @NonNull
            @Override
            public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_layout,parent,false);
                return new CommentViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull CommentViewHolder holder, int position, @NonNull CommentModel model) {
                holder.list_meno.setText(model.getMeno());
                holder.list_komentar.setText(model.getKomentar());
            }
        };

        commentList.setHasFixedSize(true);
        commentList.setLayoutManager(new LinearLayoutManager(this));
        commentList.setAdapter(adapter);


        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(),AddComment.class);
                intent.putExtra("mName",mName);
                intent.putExtra("mId",mId);
                intent.putExtra("mFaculty",mFaculty);
                startActivity(intent);

            }
        });

        like_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!checker){
                    checker = true;
                    like_button.setBackgroundColor(Color.CYAN);
                    DocumentReference dc = fStore.collection("users/"+mId+"/likes").document(userId);
                    Map<String, Object> like = new HashMap<>();
                    like.put("like", 1);
                    dc.set(like).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("TAG", "Your like was added");
                        }
                    });
                }else if(checker){
                    checker = false;
                    like_button.setBackgroundColor(Color.WHITE);
                    DocumentReference dc = fStore.collection("users/"+mId+"/likes").document(userId);
                    dc.delete();
                }
            }
        });

        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMail(mId);
            }
        });

    }

//////////////////////////////////////////////////////////////////////////////

    private void sendMail(String mId) {
        final String[] receiver = new String[1];
        DocumentReference documentReference = fStore.collection("users").document(mId);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                receiver[0] = value.getString("email");
                String subject = "Žiadosť o doučovanie/Request for tutoring";

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_EMAIL,receiver);
                intent.putExtra(Intent.EXTRA_SUBJECT,subject);

                intent.setType("message/rfc822");
                startActivity(Intent.createChooser(intent,getResources().getString(R.string.zvolenie_emailu)));

            }
        });
    }

    private void setProfileImage(String mId) {
        storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference profileRef = storageReference.child("users/"+mId+"/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profileImage);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("TAG","Nebolo možné načítať obrázok");
            }
        });
    }

    private class CommentViewHolder extends RecyclerView.ViewHolder{

        private TextView list_meno;
        private TextView list_komentar;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);

            list_meno = itemView.findViewById(R.id.comment_name);
            list_komentar = itemView.findViewById(R.id.comment_text);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }
}
