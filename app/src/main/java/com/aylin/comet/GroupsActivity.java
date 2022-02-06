package com.aylin.comet;


import android.content.Context;
import android.content.DialogInterface;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aylin on 28.03.2018.
 */

public class GroupsActivity extends AppCompatActivity {

    // private EditText groupName, groupKey;
    final Context context = this;
    private DatabaseReference mGroupsReference, deneme;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private GroupAdapter adapter;
    private List<Group> mGroupsList = new ArrayList<>();
    private Button createGroup, deleteGroup;
    private int control = 0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_groups);
        Toolbar myToolbar = findViewById(R.id.app_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("GROUPS");
        mGroupsReference = FirebaseDatabase.getInstance().getReference().child("Groups");
        deneme = FirebaseDatabase.getInstance().getReference();
        //initialize the recyclerview variables
        mRecyclerView = findViewById(R.id.usersRecyclerView);
        mRecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        createGroup = findViewById(R.id.createGroup);
        deleteGroup = findViewById(R.id.delete_group);
        createGroup.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {


                //setContentView(R.layout.group_information);
                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.group_information, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                final EditText groupName = promptsView
                        .findViewById(R.id.group_name);
                final EditText groupKey = promptsView
                        .findViewById(R.id.group_key);
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // get user input and set it to result
                                // edit text

                                String group_name = groupName.getText().toString().trim();
                                String group_key = groupKey.getText().toString();
                                //sendGroupToFirebase(group_name,group_key);
                                controlUniqueGroupKey(group_name, group_key);
                            }
                        })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();


            }
        });
        deleteGroup.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //setContentView(R.layout.group_information);
                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.delete_group, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);
                final EditText groupKeyDelete = promptsView
                        .findViewById(R.id.delete_group_key);
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // get user input and set it to result
                                // edit text

                                String delete_group_key = groupKeyDelete.getText().toString();
                                deleteFromFirebase(delete_group_key);
                            }
                        })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
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

    private void deleteFromFirebase(String groupKey) {
        mGroupsList.clear();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Groups");
        final DatabaseReference moveRef = FirebaseDatabase.getInstance().getReference().child("Deleted Groups").push();
        Query deleteQuery = ref.orderByChild("groupKey").equalTo(groupKey);
        deleteQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (final DataSnapshot Snapshot : dataSnapshot.getChildren()) {
                    moveRef.setValue(Snapshot.getValue(), new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError firebaseError, DatabaseReference firebase) {
                            if (firebaseError != null) {
                                Toast.makeText(GroupsActivity.this, "Delete Failed!", Toast.LENGTH_SHORT).show();
                            } else {
                                FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                String uid = Snapshot.getKey();//group id
                                assert currentFirebaseUser != null;
                                String user_id = currentFirebaseUser.getUid();
                                deleteGroupFromUser(uid, user_id);
                                Snapshot.getRef().removeValue();

                            }
                        }
                    });

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void deleteGroupFromUser(String group_id, String user_id) {
        //.child(user_id).child("MemberGroup")
        mGroupsList.clear();
        DatabaseReference deleteUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("MemberGroup");
        Query deleteMemberQuery = deleteUserRef.orderByChild("Group Id").equalTo(group_id);
        deleteMemberQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot deleteSnapshot : dataSnapshot.getChildren()) {
                    deleteSnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void controlUniqueGroupKey(final String groupName, final String groupKey) {
        mGroupsReference.orderByChild("groupKey").equalTo(groupKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // code if data exists
                    Toast.makeText(GroupsActivity.this, "This key was used!", Toast.LENGTH_SHORT).show();
                } else {
                    // code if data does not  exists
                    sendGroupToFirebase(groupName, groupKey);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    private void sendGroupToFirebase(String groupName, String groupKey) {

        mGroupsList.clear();
        DatabaseReference newRef = mGroupsReference.push();
        String uniqueId = newRef.getKey();
        Group creatingGroup = new Group(uniqueId, groupName, groupKey);
        newRef.setValue(creatingGroup).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()) {
                    //error
                    //set invisible layout
                    //setContentView(R.layout.activity_chat_groups);
                    Toast.makeText(GroupsActivity.this, "Error " + task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                } else {

                    Toast.makeText(GroupsActivity.this, "Group created successfully!", Toast.LENGTH_SHORT).show();
                    //queryGroupsAndAddThemToList();
                }
            }
        });


    }

    private void populateRecyclerView() {
        adapter = new GroupAdapter(mGroupsList, this);
        mRecyclerView.setAdapter(adapter);

    }

    private void queryGroupsAndAddThemToList() {
        mGroupsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //if(dataSnapshot.getChildrenCount() > 0){
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    Group group = snap.getValue(Group.class);
                    mGroupsList.add(group);

                }

                //}
                populateRecyclerView();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}
