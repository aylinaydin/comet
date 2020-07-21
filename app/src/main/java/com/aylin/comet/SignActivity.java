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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static android.content.ContentValues.TAG;
import static com.aylin.comet.R.layout.activity_signin;

/**
 * Created by Aylin on 6.02.2018.
 */

public class SignActivity extends Activity {
    private FirebaseAuth auth;
    private DatabaseReference mUsersDBref;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(activity_signin);
        final EditText editText2 = findViewById(R.id.email_input);

        final EditText editText3 = findViewById(R.id.password_input);

        final EditText editText4 = findViewById(R.id.username_input);
        Button signUpButton = findViewById(R.id.register_button);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = editText2.getText().toString().trim();
                String password = editText3.getText().toString().trim();
                final String username = editText4.getText().toString().trim();
                String domain = email.substring(email.lastIndexOf("@") + 1);//taking domain of the mail

                if (email.equals("")) {
                    Toast.makeText(SignActivity.this, "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(username)) {
                    Toast.makeText(getApplicationContext(), "Enter username!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (domain.equals("std.yeditepe.edu.tr") || domain.equals("cse.yeditepe.edu.tr")) {//Users from yeditepe
                    auth = FirebaseAuth.getInstance();
                    auth.createUserWithEmailAndPassword(email, password)//creating user with email and password
                            .addOnCompleteListener(SignActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    Toast.makeText(SignActivity.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();

                                    // If sign in fails, display a message to the user. If sign in succeeds
                                    // the auth state listener will be notified and logic to handle the
                                    // signed in user can be handled in the listener.
                                    if (!task.isSuccessful()) {//unsuccessful registration
                                        Log.d(TAG, "signInWithEmail:unsuccessfull");
                                        Toast.makeText(SignActivity.this, "Authentication failed." + task.getException(),
                                                Toast.LENGTH_SHORT).show();

                                    } else {//successful registration
                                        Log.d(TAG, "signInWithEmail:successful");
                                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                .setDisplayName(username).build();
                                        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                        assert user != null;
                                        user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    createUserInDb(user.getUid(), user.getDisplayName(), user.getEmail());
                                                } else {
                                                    Toast.makeText(SignActivity.this, "Error " + task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                                                }
                                            }
                                        });

                                        user.sendEmailVerification().addOnCompleteListener(SignActivity.this, new OnCompleteListener() {

                                            @Override
                                            public void onComplete(@NonNull Task task) {
                                                if (task.isSuccessful()) {//verification mail has been sent
                                                    Toast.makeText(SignActivity.this,
                                                            "Verification email sent to " + user.getEmail(),
                                                            Toast.LENGTH_SHORT).show();


                                                } else {//verification mail not send
                                                    Log.e(TAG, "sendEmailVerification", task.getException());
                                                    Toast.makeText(SignActivity.this,
                                                            "Failed to send verification email.",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                        finish();
                                    }
                                }
                            });
                } else {//e-mail does not belong to Yeditepe
                    Toast.makeText(SignActivity.this, "Only with mail of Yeditepe University", Toast.LENGTH_SHORT).show();

                }

            }
        });

    }

    private void createUserInDb(String userId, String displayName, String email) {
        mUsersDBref = FirebaseDatabase.getInstance().getReference().child("Users");
        User user = new User(userId, displayName, email);
        mUsersDBref.child(userId).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()) {
                    //error
                    Toast.makeText(SignActivity.this, "Error " + task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    //success adding user to db as well
                    //go to users chat list
                    //goToChartUsersActivity();
                }
            }
        });


    }

    private void goToChartUsersActivity() {
        Intent intent = new Intent(new Intent(SignActivity.this, MainActivity.class));
        startActivity(intent);
        finish();
    }

    protected void onStart() {

        super.onStart();
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
    }
}
