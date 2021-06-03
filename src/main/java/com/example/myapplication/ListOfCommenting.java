package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ListOfCommenting extends AppCompatActivity {

    private ImageButton SearchButton;
    private EditText SearchInputText;
    private FirebaseFirestore fStore;
    private RecyclerView ListofCommentators;
    private Query query;
    private StorageReference storageReference;
    private ActionBar actionBar;

    private FirestoreRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_commenting);

        fStore = FirebaseFirestore.getInstance();

        actionBar = getSupportActionBar();
        actionBar.setTitle(getResources().getString(R.string.list_of_students));

        ListofCommentators = findViewById(R.id.ListFirestoreList);

        Intent data = getIntent();
        String mName = data.getStringExtra("mName");
        String mId = data.getStringExtra("mId");
        String mFaculty = data.getStringExtra("mFaculty");
        String mdocumentID = data.getStringExtra("mdocumentID");

        query = fStore.collection("nastenka/"+mdocumentID+"/commentator").orderBy("fullName");

        FirestoreRecyclerOptions<FindFriendModel> options = new FirestoreRecyclerOptions.Builder<FindFriendModel>()
                .setQuery(query, FindFriendModel.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<FindFriendModel, ListOfCommenting.ListOfCommentingViewHolder>(options) {
            @NonNull
            @Override
            public ListOfCommenting.ListOfCommentingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_users_display_layout, parent, false);
                return new ListOfCommenting.ListOfCommentingViewHolder(view);
            }
            @Override
            protected void onBindViewHolder(@NonNull ListOfCommenting.ListOfCommentingViewHolder holder, int position, @NonNull FindFriendModel model) {
                holder.mName.setText(model.getFullName());
                holder.mFaculty.setText(model.getFakulta());
                holder.mId.setText(model.getID());
                holder.setProfileImage(model.getID());
            }
        };
        ListofCommentators.setHasFixedSize(true);
        ListofCommentators.setLayoutManager(new LinearLayoutManager(this));
        ListofCommentators.setAdapter(adapter);
    }

    public class ListOfCommentingViewHolder extends RecyclerView.ViewHolder {
        private TextView mName,mFaculty,mId;
        private CircleImageView myImage;
        public ListOfCommentingViewHolder(@NonNull View itemView) {

            super(itemView);

            mName = itemView.findViewById(R.id.all_users_profile_name);
            mFaculty = itemView.findViewById(R.id.all_users_profile_faculty);
            mId = itemView.findViewById(R.id.all_users_id);
            myImage = itemView.findViewById(R.id.all_users_profile_image);

            itemView.setClickable(true);
            itemView.setOnClickListener(this::onClick);
        }

        public void setProfileImage(String id){
            storageReference = FirebaseStorage.getInstance().getReference();
            StorageReference profileRef = storageReference.child("users/"+id+"/profile.jpg");
            profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).into(myImage);
                }
            });
        }

        public void onClick(View v){
            final Intent intent = new Intent(v.getContext(),UserProfile.class);
            intent.putExtra("mName",mName.getText().toString());
            intent.putExtra("mId",mId.getText().toString());
            intent.putExtra("mFaculty",mFaculty.getText().toString());
            startActivity(intent);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }
}


