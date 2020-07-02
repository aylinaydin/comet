package com.aylin.comet;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aylin on 10.05.2018.
 */

public class MemberListActivity extends AppCompatActivity {
    private DatabaseReference UsersReference, GroupsReference, testReference;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private MemberAdapter adapter;
    private List<User> mUserlist = new ArrayList<>();
    private String groupId;
    private Button createGroup,deleteGroup;
    private int control = 0;
    // private EditText groupName, groupKey;
    final Context context = this;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            Log.e("groupID is null","NULL GROUP İD");
        } else {
            groupId = extras.getString("GROUP_ID");
        }
        setContentView(R.layout.activity_member_list);
        Toolbar myToolbar = findViewById(R.id.toolbar_member_list);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("MEMBERS");

        UsersReference = FirebaseDatabase.getInstance().getReference().child("Users");
        GroupsReference = FirebaseDatabase.getInstance().getReference().child("Groups").child(groupId);
        recyclerView = findViewById(R.id.membersRecyclerView);
        recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        queryMemberListAndAddThemToList();
    }
    protected void onStart() {
        super.onStart();
        /**query groups and add them to a list**/
        // queryGroupsAndAddThemToList();
    }
    private void populateRecyclerView(){
        adapter = new MemberAdapter(mUserlist, this);
        recyclerView.setAdapter(adapter);

    }
    private void queryMemberListAndAddThemToList(){
       final ValueEventListener valueEventListener = GroupsReference.child("Members").addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(DataSnapshot dataSnapshot) {
               for (DataSnapshot snap : dataSnapshot.getChildren()) {
                   String user_id = (String) snap.child("User Id").getValue();
                   if(user_id!=null){
                       UsersReference.child(user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                           @Override
                           public void onDataChange(DataSnapshot dataSnapshot) {
                               User user = dataSnapshot.getValue(User.class);

                               mUserlist.add(user);
                               populateRecyclerView();
                           }

                           @Override
                           public void onCancelled(DatabaseError databaseError) {

                           }
                       });
                   }
               }
           }

           @Override
           public void onCancelled(DatabaseError databaseError) {

           }
       });
    }


}
