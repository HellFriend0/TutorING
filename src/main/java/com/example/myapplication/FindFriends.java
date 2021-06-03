package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.Normalizer;

import de.hdodenhof.circleimageview.CircleImageView;


public class FindFriends extends Fragment {

    private ImageButton SearchButton;
    private EditText SearchInputText;
    private FirebaseFirestore fStore;
    private RecyclerView SearchResultList;
    private Query query;
    private StorageReference storageReference;

    private FirestoreRecyclerAdapter adapter;

    @Nullable
    @Override
    public View onCreateView (@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.findfriends,container,false);
        getActivity().setTitle(getResources().getString(R.string.title_dashboard));

        fStore = FirebaseFirestore.getInstance();

        SearchButton = view.findViewById(R.id.search_people_friends_button);
        SearchInputText = view.findViewById(R.id.search_box_input);

        SearchResultList = view.findViewById(R.id.search_result_list);

        query = fStore.collection("users").orderBy("smallName");

        FirestoreRecyclerOptions<FindFriendModel> options = new FirestoreRecyclerOptions.Builder<FindFriendModel>()
                .setQuery(query, FindFriendModel.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<FindFriendModel, FindFriends.FindFriendsViewHolder>(options) {
            @NonNull
            @Override
            public FindFriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_users_display_layout, parent, false);
                return new FindFriends.FindFriendsViewHolder(view);
            }
            @Override
            protected void onBindViewHolder(@NonNull FindFriendsViewHolder holder, int position, @NonNull FindFriendModel model) {
                holder.mName.setText(model.getFullName());
                holder.mFaculty.setText(model.getFakulta());
                holder.mId.setText(model.getID());
                holder.setProfileImage(model.getID());
            }
        };
        SearchResultList.setHasFixedSize(true);
        SearchResultList.setLayoutManager(new LinearLayoutManager(getContext()));
        SearchResultList.setAdapter(adapter);

        SearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchBoxInput = StringUtils.unaccent(SearchInputText.getText().toString()).toLowerCase();

                Toast.makeText(getContext(), "Searching.....", Toast.LENGTH_SHORT).show();

                query = fStore.collection("users").orderBy("smallName").startAt(searchBoxInput).endAt(searchBoxInput + "\uf8ff");

                FirestoreRecyclerOptions<FindFriendModel> options = new FirestoreRecyclerOptions.Builder<FindFriendModel>()
                        .setQuery(query, FindFriendModel.class)
                        .build();

                adapter = new FirestoreRecyclerAdapter<FindFriendModel, FindFriends.FindFriendsViewHolder>(options) {
                    @NonNull
                    @Override
                    public FindFriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_users_display_layout, parent, false);
                        return new FindFriends.FindFriendsViewHolder(view);
                    }
                    @Override
                    protected void onBindViewHolder(@NonNull FindFriendsViewHolder holder, int position, @NonNull FindFriendModel model) {
                        holder.mName.setText(model.getFullName());
                        holder.mFaculty.setText(model.getFakulta());
                        holder.mId.setText(model.getID());
                        holder.setProfileImage(model.getID());
                    }
                };
                adapter.startListening();
                SearchResultList.setHasFixedSize(true);
                SearchResultList.setLayoutManager(new LinearLayoutManager(getContext()));
                SearchResultList.setAdapter(adapter);
            }
        });

        return view;
    }
    //----------------------------------------------------------------------------------------------
    public static class StringUtils {
        public static String unaccent(String src) {
            return Normalizer
                    .normalize(src, Normalizer.Form.NFD)
                    .replaceAll("[^\\p{ASCII}]", "");
        }

    }

    public class FindFriendsViewHolder extends RecyclerView.ViewHolder {
        private TextView mName,mFaculty,mId;
        private CircleImageView myImage;
        public FindFriendsViewHolder(@NonNull View itemView) {

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
