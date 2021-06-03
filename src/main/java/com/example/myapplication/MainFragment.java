package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;

import static androidx.constraintlayout.motion.widget.MotionScene.TAG;


public class MainFragment extends Fragment {
    private  onFragmentBtnSelected listener;

    FirebaseFirestore fStore;
    String userId;
    FirebaseAuth fAuth;
    RecyclerView recyclerView;
    Query query;
    String fakulta;

    private FirestoreRecyclerAdapter adapter;

    @Nullable
    @Override
    public View onCreateView (@NonNull LayoutInflater inflater, @Nullable ViewGroup container,@Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.mainfragment,container,false);
        getActivity().setTitle(getResources().getString(R.string.title_home));
        Button clickme = view.findViewById(R.id.mainfragmentbtn1);
        recyclerView = view.findViewById(R.id.mainFragmentFirestoreList);

        final Spinner mySpinner = view.findViewById(R.id.mainFragmentListSpinner);
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.kategorie));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner.setAdapter(myAdapter);

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        userId = fAuth.getCurrentUser().getUid();

        //DocumentReference queryReq = fStore.collection("users").document(userId);

        query = fStore.collection("nastenka").orderBy("time", Query.Direction.DESCENDING).limit(100);

        FirestoreRecyclerOptions<MessageModel> options = new FirestoreRecyclerOptions.Builder<MessageModel>()
                .setQuery(query, MessageModel.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<MessageModel, MessageViewHolder>(options) {
            @NonNull
            @Override
            public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_message, parent, false);
                return new MessageViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull MessageViewHolder holder, int position, @NonNull MessageModel model) {
                holder.mName.setText(model.getFullName());
                holder.mId.setText(model.getID());
                holder.mFaculty.setText(model.getFakulta());
                holder.mText.setText(model.getText());
                holder.mdocumentID.setText(model.getDocumentID());
                holder.mTime.setText(model.getTime().toString());
            }
        };
        adapter.startListening();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        clickme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onButtonAddOnBoard();
            }
        });

        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if(position == 0){
                    query = fStore.collection("nastenka").orderBy("time", Query.Direction.DESCENDING).limit(100);

                    //RecyclerOptions
                    FirestoreRecyclerOptions<MessageModel> options = new FirestoreRecyclerOptions.Builder<MessageModel>()
                            .setQuery(query, MessageModel.class)
                            .build();

                    adapter = new FirestoreRecyclerAdapter<MessageModel, MessageViewHolder>(options) {
                        @NonNull
                        @Override
                        public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_message, parent, false);
                            return new MessageViewHolder(view);
                        }

                        @Override
                        protected void onBindViewHolder(@NonNull MessageViewHolder holder, int position, @NonNull MessageModel model) {
                            holder.mName.setText(model.getFullName());
                            holder.mId.setText(model.getID());
                            holder.mFaculty.setText(model.getFakulta());
                            holder.mText.setText(model.getText());
                            holder.mdocumentID.setText(model.getDocumentID());
                            holder.mTime.setText(model.getTime().toString());
                        }
                    };
                    adapter.startListening();
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    recyclerView.setAdapter(adapter);
                }else {
                    query = fStore.collection("nastenka").whereEqualTo("kategoria", mySpinner.getSelectedItem().toString()).orderBy("time", Query.Direction.DESCENDING).limit(20);

                    FirestoreRecyclerOptions<MessageModel> options = new FirestoreRecyclerOptions.Builder<MessageModel>()
                            .setQuery(query, MessageModel.class)
                            .build();

                    adapter = new FirestoreRecyclerAdapter<MessageModel, MessageViewHolder>(options) {
                        @NonNull
                        @Override
                        public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_message, parent, false);
                            return new MessageViewHolder(view);
                        }

                        @Override
                        protected void onBindViewHolder(@NonNull MessageViewHolder holder, int position, @NonNull MessageModel model) {
                            holder.mName.setText(model.getFullName());
                            holder.mId.setText(model.getID());
                            holder.mFaculty.setText(model.getFakulta());
                            holder.mText.setText(model.getText());
                            holder.mdocumentID.setText(model.getDocumentID());
                            holder.mTime.setText(model.getTime().toString());
                        }
                    };
                    adapter.startListening();
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });

        return view;
    }
    //----------------------------------------------------------------------------------------------


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof onFragmentBtnSelected) {
            listener = (onFragmentBtnSelected) context;
        } else {
            throw new ClassCastException(context.toString() + " must implement listener");
        }
    }

    public interface  onFragmentBtnSelected{
        void onButtonAddOnBoard();
        void onButtonSelectedResetPass();
        void onButtonSelectedLogout();
}
    //ViewHolder   ////////////////////////

    private class MessageViewHolder extends RecyclerView.ViewHolder{
        private TextView mName,mFaculty,mText,mdocumentID,mId,mTime;
        private Button zoznam,profil;
        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            mName = itemView.findViewById(R.id.messageMeno);
            mFaculty = itemView.findViewById(R.id.messageFakulta);
            mText = itemView.findViewById(R.id.messageText);
            mId = itemView.findViewById(R.id.messageId);
            mdocumentID = itemView.findViewById(R.id.messageDocumentId);
            profil = itemView.findViewById(R.id.messageProfil);
            zoznam = itemView.findViewById(R.id.messageZoznam);
            mTime = itemView.findViewById(R.id.messageTime);

            profil.setClickable(true);
            profil.setOnClickListener(this::profilView);

            zoznam.setClickable(true);
            zoznam.setOnClickListener(this::zoznamView);
        }

        public void profilView(View v){
            DocumentReference documentReference = fStore.collection("nastenka/"+mdocumentID.getText().toString()+"/commentator").document(userId);
            Map<String, Object> message = new HashMap<>();
            message.put("ID",mId.getText().toString());
            message.put("fakulta",mFaculty.getText().toString());
            message.put("fullName",mName.getText().toString());
            documentReference.set(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d("AAAAAAAAAAAAAA","AAAAAAAAAAAAAAAAAAA");
                }
            });
            final Intent intent = new Intent(v.getContext(),UserProfile.class);
            intent.putExtra("mName",mName.getText().toString());
            intent.putExtra("mId",mId.getText().toString());
            intent.putExtra("mFaculty",mFaculty.getText().toString());
            startActivity(intent);
        }

        public void zoznamView(View v){
            final Intent intent = new Intent(v.getContext(),ListOfCommenting.class);
            intent.putExtra("mId",mId.getText().toString());
            intent.putExtra("mdocumentID",mdocumentID.getText().toString());
            intent.putExtra("mFaculty",mFaculty.getText().toString());
            intent.putExtra("mName",mName.getText().toString());
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
