package com.aylin.comet;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static android.content.ContentValues.TAG;
import static com.aylin.comet.R.layout.activity_login;

/**
 * Created by Aylin on 22.02.2018.
 */

public class MainActivity extends Activity {
    private DatabaseReference mDatabase;
    private FirebaseAuth auth;
    //private FirebaseListAdapter<UserMessage> adapter;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        /*FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String email =user.getEmail();
        if(user==null) {
            setContentView(activity_login);
        }else{
            if(user.isEmailVerified()){
                if (email.equals("aylin.aydin1@std.yeditepe.edu.tr")) {
                    startActivity(new Intent(MainActivity.this, GroupsActivity.class));
                }else{
                    startActivity(new Intent(MainActivity.this, MemberGroupsActivity.class));
                }
            }


        }*/
        setContentView(activity_login);
        mDatabase = FirebaseDatabase.getInstance()
                .getReference();
        checkGoogleServices();


    }

    public void checkGoogleServices() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int availability = googleApiAvailability.isGooglePlayServicesAvailable(this);
        if (availability != ConnectionResult.SUCCESS) {
            googleApiAvailability.makeGooglePlayServicesAvailable(this);
        }
    }

    protected void onStart() {
        super.onStart();
        //adapter.startListening();
        Button sign_button = findViewById(R.id.signup_button);
        sign_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(new Intent(MainActivity.this, SignActivity.class));
                startActivity(intent);

            }
        });
        Button login_button = findViewById(R.id.login_ok_button);
        final EditText email_editText = findViewById(R.id.editText2);//
        final EditText password_editText = findViewById(R.id.editText3);
        login_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                final String email = email_editText.getText().toString().trim();
                String password = password_editText.getText().toString().trim();
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(MainActivity.this, "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }
                auth = FirebaseAuth.getInstance();
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {//login process is successful
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            assert user != null;
                            if (user.isEmailVerified()) {

                                Log.d(TAG, "signInWithEmail:success");

                                Toast.makeText(MainActivity.this, "Authentication success.",
                                        Toast.LENGTH_SHORT).show();
                                // displayChatMessages();
                                if (email.equals("aylin.aydin1@std.yeditepe.edu.tr")) {
                                    startActivity(new Intent(MainActivity.this, GroupsActivity.class));
                                } else {
                                    startActivity(new Intent(MainActivity.this, MemberGroupsActivity.class));
                                }
                            } else {
                                FirebaseAuth.getInstance().signOut();
                            }

                        } else {//login process is unsuccessful
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }


                    }
                });

            }

        });
        Button reset_password = findViewById(R.id.resetPassword);
        reset_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = email_editText.getText().toString().trim();
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(MainActivity.this, "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }
                auth = FirebaseAuth.getInstance();
                auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "E-mail sent.");
                                    Toast.makeText(MainActivity.this, "Reset password e-mail is sent!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(MainActivity.this, "Failed to reset password", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }

        });

    }


    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {

        super.onStop();
        //adapter.stopListening();
    }
}
