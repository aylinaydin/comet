package com.aylin.comet;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Aylin on 13.03.2018.
 */

public class ChatActivity extends AppCompatActivity {
    private RecyclerView mChatsRecyclerView;
    private EditText mMessageEditText;
    private ImageButton mSendImageButton;
    private DatabaseReference mMessagesDBRef, deneme;
    private List<UserMessage> mMessagesList = new ArrayList<>();
    private String groupId;
    private String groupName;
    private String senderId, senderIdForTest;
    private String message;
    private static final int GALLERY_PICK = 1;
    private FirebaseStorage storage;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            Log.e("groupID is null", "NULL GROUP ID");
        } else {
            groupId = extras.getString("GROUP_ID");
        }
        groupName = getIntent().getStringExtra("GROUP_NAME");
        setContentView(R.layout.activity_message_list);
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(groupName);
        //initialize the views
        //Add to Activity

        mChatsRecyclerView = findViewById(R.id.messagesRecyclerView);
        mMessageEditText = findViewById(R.id.messageEditText);
        mSendImageButton = findViewById(R.id.sendMessageImagebutton);
        ImageButton galleryImageButton = findViewById(R.id.galleryImageButton);
        mChatsRecyclerView.setHasFixedSize(true);
        //get group id from intent

        deneme = FirebaseDatabase.getInstance().getReference();
        //use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setStackFromEnd(true);
        mChatsRecyclerView.setLayoutManager(mLayoutManager);
        //init Firebase
        if (groupId != null) {
            mMessagesDBRef = FirebaseDatabase.getInstance().getReference().child("Messages").child(groupId);
        }
        //listening gallery image button
        galleryImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                //galleryIntent.setType("image/*");
                // galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/jpeg");
                galleryIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);

            }
        });
        // Get the Firebase app and all primitives we'll use
        FirebaseApp app = FirebaseApp.getInstance();
        storage = FirebaseStorage.getInstance(app);
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            senderIdForTest = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
        mSendImageButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
            @Override
            public void onClick(View v) {
                //testCode();
                message = mMessageEditText.getText().toString();
                //message = "test";
                @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                Date today = Calendar.getInstance().getTime();
                long millisecond = today.getTime();
                String messageTime = df.format(today);
                String testMessageTime = "message time: " + millisecond;
                deneme.child("Time").push().setValue(testMessageTime);
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    senderId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                }
                if (message.isEmpty()) {
                    Toast.makeText(ChatActivity.this, "You must enter a message", Toast.LENGTH_SHORT).show();
                } else {
                    sendMessageToFirebase(message, senderId, groupName, messageTime);
                }
            }
        });
        queryMessagesAndAddThemToList();

        //testCode_();
    }

    public void testCode() {
        int count = 0;
        while (count < 3) {
            count++;
            String test = "deneme";
            queryMessagesAndAddThemToList();
            SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            Date today = Calendar.getInstance().getTime();
            String messageTimeForTest = df.format(today);
            sendMessageToFirebase(test, senderIdForTest, groupName, messageTimeForTest);
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    public void testCode_() {
        final int[] count = {0};
        mSendImageButton.post(new Runnable() {
            @Override
            public void run() {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Do something after 5s = 5000ms
                        mSendImageButton.performClick();
                        if (count[0] < 5) {
                            handler.postDelayed(this, 5000);
                            count[0] += 1;
                        }
                    }
                }, 5000);
                   /* new CountDownTimer(3000, 1000) {

                        public void onTick(long millisUntilFinished) {

                        }

                        public void onFinish() {
                            mSendImageButton.performClick();
                        }
                    }.start();*/
               /* mSendImageButton.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSendImageButton.performClick();
                    }
                }, 9000);*/


            }
        });
    }

    protected void onStart() {
        super.onStart();

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.leave:
                deleteMemberFromUserMemberList();
                deleteMemberFromGroupMemberList();
                break;
            case R.id.membersOfGroup:
                Intent goToUpdate = new Intent(ChatActivity.this, MemberListActivity.class);
                goToUpdate.putExtra("GROUP_ID", groupId);
                ChatActivity.this.startActivity(goToUpdate);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteMemberFromUserMemberList() {
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        assert currentFirebaseUser != null;
        String user_id = currentFirebaseUser.getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("MemberGroup");
        Query deleteQuery = ref.orderByChild("Group Id").equalTo(groupId);
        deleteQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot Snapshot : dataSnapshot.getChildren()) {
                    //deneme.child("deletedId").push().setValue(uid);
                    Snapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void deleteMemberFromGroupMemberList() {
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        assert currentFirebaseUser != null;
        String user_id = currentFirebaseUser.getUid();
        final String email = currentFirebaseUser.getEmail();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Groups").child(groupId).child("Members");
        Query deleteQuery = ref.orderByChild("User Id").equalTo(user_id);
        deleteQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot Snapshot : dataSnapshot.getChildren()) {
                    //deneme.child("deletedId").push().setValue(uid);
                    Snapshot.getRef().removeValue();
                    assert email != null;
                    if (email.equals("aylin.aydin1@std.yeditepe.edu.tr")) {
                        startActivity(new Intent(ChatActivity.this, GroupsActivity.class));
                    } else {
                        startActivity(new Intent(ChatActivity.this, MemberGroupsActivity.class));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            senderId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            final DatabaseReference imageReference = mMessagesDBRef.push();
            final String id_push = imageReference.getKey();
            // Get a reference to the location where we'll store our photos
            StorageReference imageStorageRef = storage.getReference("chat_photos");
            // Get a reference to store file at chat_photos/<FILENAME>
            if (imageUri != null) {
                final StorageReference photoRef = imageStorageRef.child(imageUri.getLastPathSegment());
                photoRef.putFile(imageUri)
                        .addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // When the image has successfully uploaded, we get its download URL
                                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                // Set the download URL to the message box, so that the user can send it to the database
                                mMessageEditText.setMovementMethod(LinkMovementMethod.getInstance());
                                assert downloadUrl != null;
                                mMessageEditText.setText(downloadUrl.toString());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
            }
           /* StorageReference filepath = imageStorageRef.child("message_images").child(id_push + ".jpg");
            if(imageUri!=null) {
                filepath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>(){
                    public void onComplete(Task<UploadTask.TaskSnapshot> task){
                        if(task.isSuccessful()){
                            String download_url = task.getResult().getDownloadUrl().toString();
                            UserMessage message = new UserMessage(download_url,senderId,groupName);
                            imageReference.setValue(message).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                 if(task.isSuccessful()){
                                     Toast.makeText(ChatActivity.this, "Message sent successfully!", Toast.LENGTH_SHORT).show();
                                    // mMessageEditText.setText(null);
                                   //  hideSoftKeyboard();
                                 }else{
                                     Toast.makeText(ChatActivity.this, "Error " + task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                 }
                                }
                            });


                        }

                    }
                });
            }*/

        }

    }

    private void sendMessageToFirebase(String message, String senderId, String groupName, String messageTime) {
        mMessagesList.clear();
        UserMessage newMsg = new UserMessage(message, senderId, groupName, messageTime);
        mMessagesDBRef.push().setValue(newMsg).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(ChatActivity.this, "Error " + task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ChatActivity.this, "Message sent successfully!", Toast.LENGTH_SHORT).show();
                    mMessageEditText.setText(null);
                }
            }
        });


    }

    private void queryMessagesAndAddThemToList() {
        mMessagesDBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mMessagesList.clear();
                Date today = Calendar.getInstance().getTime();
                long millisecond = today.getTime();
                String messageTimeForTest = "end of querying data " + millisecond;
                deneme.child("Time").push().setValue(messageTimeForTest);

                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    UserMessage chatMessage = snap.getValue(UserMessage.class);
                    //if(chatMessage.getSenderId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) ){
                    mMessagesList.add(chatMessage);

                    //}
                }
                //populate messages
                populateMessagesRecyclerView();


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void populateMessagesRecyclerView() {
        MessagesAdapter adapter = new MessagesAdapter(mMessagesList, this);
        mChatsRecyclerView.setAdapter(adapter);


    }


}
