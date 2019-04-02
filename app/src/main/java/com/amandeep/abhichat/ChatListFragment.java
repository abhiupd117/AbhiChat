package com.amandeep.abhichat;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amandeep.abhichat.Adapter.ChatsusersAdapter;
import com.amandeep.abhichat.Adapter.MessageAdapter;
import com.amandeep.abhichat.Adapter.UserAdapter;
import com.amandeep.abhichat.Model.Chat;
import com.amandeep.abhichat.Model.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatListFragment extends Fragment {
    private RecyclerView recyclerView;
    private ChatsusersAdapter chatsusersAdapter;
    LinearLayoutManager mLinearLayoutmanager;
    private List<Users> muser;
    FirebaseUser fuser;
    private MessageAdapter messageAdapter;
    private DatabaseReference reference;
    private List<Users> chattedUserList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_chat,container,false);
        recyclerView=view.findViewById(R.id.users_chats_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        fuser=FirebaseAuth.getInstance().getCurrentUser();
        muser= new ArrayList<>();
        readUser();
        return view;

    }

    private void readUser() {
        // final FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child("User");
        //FirebaseDatabase mDatabase = databaseReference.getDatabase();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                muser.clear();
                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                {

                    Users user=dataSnapshot1.getValue(Users.class);
                    String userKey = dataSnapshot1.getKey();
                    user.setId(userKey);
                    if(!userKey.equals(fuser.getUid())){
                     // muser.add(user);

                      // readMessage(fuser.getUid(),user); //checking if chat exist*/
                        assert  user!=null;
                        assert  fuser != null;
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private void readMessage(final String myId, final Users user)
    {
        reference=FirebaseDatabase.getInstance().getReference().child("messages");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                    Chat chat = snapshot.getValue(Chat.class);
                    if( chat.getReceiver() != null && !chat.getReceiver().isEmpty()) {
                       // if (!chat.getReceiver().isEmpty() && !chat.getReceiver().isEmpty()) {
                            if (chat.getReceiver().equals(myId) && chat.getSender().equals(user.getId()) || chat.getReceiver().equals(user.getId()) && chat.getSender().equals(myId)) {
                                Log.e("Add Chat","called");
                                if(chat.getMessage() != null && !chat.getMessage().isEmpty())
                                {
                                    if (chat.getMessage_img()!=null && !chat.getMessage_img().isEmpty() || chat.getTimestamp()!=null && chat.getTimestamp().isEmpty() ||chat .getLongitude()!=null && chat.getLongitude().isEmpty() || chat.getLat()!=null && chat.getLat().isEmpty()){
                                        muser.add(user);
                                        Log.e("Added User",user.getName()+"");
                                        break;
                                    }
                                }
                            }
                       // }
                    }
                }
                Log.e("Added User size = ",muser.size()+"");
                chatsusersAdapter=new ChatsusersAdapter(getContext(),muser);
                mLinearLayoutmanager = new LinearLayoutManager(getActivity());
                mLinearLayoutmanager.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(mLinearLayoutmanager);
                recyclerView.setAdapter(chatsusersAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
