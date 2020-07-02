package com.aylin.comet;


import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Aylin on 28.03.2018.
 */

public class MemberGroupsActivity extends AppCompatActivity {

    private DatabaseReference mGroupsReference, UserReference, mUsersReference, deneme,deneme2, UserNewReference, GroupNewReference;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private GroupAdapter adapter;
    private List<Group> mGroupsList = new ArrayList<>();
    private Button memberGroup;
    private String group_key;
    // private EditText groupName, groupKey;
    final Context context = this;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_groups);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("GROUPS");
        //getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.cardview_shadow_end_color)));
        mUsersReference = FirebaseDatabase.getInstance().getReference().child("Users");
        deneme =  FirebaseDatabase.getInstance().getReference();
        deneme2 =  FirebaseDatabase.getInstance().getReference();
        UserNewReference =  FirebaseDatabase.getInstance().getReference().child("Users");
        mGroupsReference = FirebaseDatabase.getInstance().getReference().child("Groups");
        GroupNewReference = FirebaseDatabase.getInstance().getReference().child("Groups");
        //mUsersReference = FirebaseDatabase.getInstance().getReference().child("Users");
        //initialize the recyclerview variables
        mRecyclerView = (RecyclerView)findViewById(R.id.memberUsersRecyclerView);
        mRecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        //queryGroupsAndAddThemToList();
        memberGroup = findViewById(R.id.member_group_button);

        memberGroup.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {


                //setContentView(R.layout.group_information);
                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.course_membership, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                final EditText groupKey =  promptsView
                        .findViewById(R.id.group_member_key);
                alertDialogBuilder.setCancelable(false);
                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // get user input and set it to result
                        // edit text
                        //setContentView(R.layout.group_information);

                        group_key = groupKey.getText().toString();
                        mGroupsReference.orderByChild("groupKey").equalTo(group_key).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot snap: dataSnapshot.getChildren()) {
                                    if (snap.exists()) {
                                        String name = snap.child("groupName").getValue(String.class);
                                        //deneme.child("deneme").push().setValue(name);
                                        String text = "You have entered the " + name + " group";
                                        AlertDialog.Builder new_builder = new AlertDialog.Builder(MemberGroupsActivity.this);
                                        new_builder.setTitle("New Group")
                                                .setMessage(text)
                                                .setPositiveButton("OK  ", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        registerToGroupFirebase(group_key);
                                                    }
                                                })
                                                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {

                                                    }
                                                }).show();

                                    } else {
                                        Toast.makeText(MemberGroupsActivity.this, "There is not any group with that id!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                });
                alertDialogBuilder.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create(); // show it
                alertDialog.show();
            }
        });
        queryGroupsAndAddThemToList();

    }
    protected void onStart() {
        super.onStart();
        /**query groups and add them to a list**/
     // queryGroupsAndAddThemToList();

    }
    private void registerToGroupFirebase(String groupKey){
        mGroupsList.clear();
        //registering to group
        final ChildEventListener childEventListener = mGroupsReference.orderByChild("groupKey").equalTo(groupKey).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                String uid = dataSnapshot.getKey();
                assert currentFirebaseUser != null;
                String user_id = currentFirebaseUser.getUid();
                Map<String, String> mapMember = new HashMap<>();
                mapMember.put("User Id",user_id);
                mGroupsReference.child(uid).child("Members").push().setValue(mapMember);
                savingUserGroup(uid,user_id);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

    }
    private void savingUserGroup(String groupId, String userId){
        Map<String, String> mapGroup = new HashMap<>();
        mapGroup.put("Group Id",groupId);
        UserReference = FirebaseDatabase.getInstance().getReference().child("Users");
        UserReference.child(userId).child("MemberGroup").push().setValue(mapGroup).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(!task.isSuccessful()){
                    //error
                    //set invisible layout
                    //setContentView(R.layout.activity_chat_groups);
                    Toast.makeText(MemberGroupsActivity.this, "Error " + task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }else{

                    Toast.makeText(MemberGroupsActivity.this, "You have entered the group!", Toast.LENGTH_SHORT).show();
                    //queryGroupsAndAddThemToList();
                }
            }
        });

    }
    private void populateRecyclerView(){
        adapter = new GroupAdapter(mGroupsList, this);
        mRecyclerView.setAdapter(adapter);

    }

    private void queryGroupsAndAddThemToList() {


        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        assert currentFirebaseUser != null;
        String user_id = currentFirebaseUser.getUid();
        //UserNewReference = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("MemberGroup");
        final ValueEventListener valueEventListener = UserNewReference.child(user_id).child("MemberGroup").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    Member member = snap.getValue(Member.class);
                    String name = (String) snap.child("Group Id").getValue();
                    if (name != null) {
                        GroupNewReference.child(name).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                //for (DataSnapshot snap : dataSnapshot.getChildren()) {
                                //String name = (String) dataSnapshot.child("groupName").getValue();
                                Group group = dataSnapshot.getValue(Group.class);
                                if (group != null) {
                                    mGroupsList.add(group);
                                    populateRecyclerView();
                                }


                              //  }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                throw databaseError.toException();
                            }
                        });

                    }
                }
                //populateRecyclerView();


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });


    }



}
