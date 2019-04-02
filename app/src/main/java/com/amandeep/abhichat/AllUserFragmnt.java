package com.amandeep.abhichat;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amandeep.abhichat.Adapter.UserAdapter;
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

public class AllUserFragmnt extends Fragment {
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    LinearLayoutManager mLinearLayoutmanager;
    private List<Users> muser;
    FirebaseUser fuser;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_freindsfragment,container,false);
        recyclerView=view.findViewById(R.id.user_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        fuser=FirebaseAuth.getInstance().getCurrentUser();
        muser= new ArrayList<>();
        readUser();
        return view;

    }

    private void readUser()
    {
       // final FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child("User");
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
                        Log.e("sgvxX4TTTXV4X", String.valueOf(user));
                        muser.add(user);
                        Log.e("adsfojf", String.valueOf(muser));
                        Log.e("firebaseuer", String.valueOf(fuser));

                        assert  user!=null;
                        assert  fuser != null;
                    }
                }
                userAdapter=new UserAdapter(getContext(),muser);
                mLinearLayoutmanager = new LinearLayoutManager(getActivity());
                mLinearLayoutmanager.setOrientation(LinearLayoutManager.VERTICAL);

                recyclerView.setLayoutManager(mLinearLayoutmanager);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
