package com.example.brand.p9;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    EditText etEmail;
    EditText etPassword;
    Button btLogIn;
    Context context = this;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = mAuth.getCurrentUser();
                if(user != null){
                    loadMainActivity();
                }
            }
        };

        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        btLogIn = (Button) findViewById(R.id.btLogIn);

        btLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();

                if(email.length() > 0 && password.length() > 0){
                    mAuth.signInWithEmailAndPassword(email,password)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    final FirebaseUser user = mAuth.getCurrentUser();
                                    if(user != null){
                                        DatabaseReference userRef = mDatabase.getReference("user/");
                                        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                DataSnapshot userData = dataSnapshot.child(user.getUid());
                                                String uId = userData.getKey();
                                                String userName = userData.child("Name").getValue().toString();
                                                String userGoal = userData.child("Goal").getValue().toString();
                                                String partnerId = userData.child("Partner").getValue().toString();

                                                DataSnapshot partnerData = dataSnapshot.child(partnerId);
                                                String partnerName = partnerData.child("Name").getValue().toString();
                                                String partnerGoal = partnerData.child("Goal").getValue().toString();

                                                DataSenderMobile dataSenderMobile = new DataSenderMobile(context);
                                                dataSenderMobile.sendSettings(uId,partnerId,partnerName,userName, userGoal,partnerGoal);

                                                LocalStorageMobile localStorageMobile = new LocalStorageMobile(context);
                                                localStorageMobile.storeSettings(uId, partnerId, userName, partnerName, userGoal,partnerGoal, new LocalStorageInterface() {
                                                    @Override
                                                    public void onStorageDone() {
                                                        loadMainActivity();
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
                                            }
                                        });

                                    }

                                }
                            })
                            .addOnFailureListener(LoginActivity.this, new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    String message = e.getMessage();
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(context, "Please fill out all required inputs", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
    private void loadMainActivity(){
        Intent intent = new Intent(context,MainActivity.class);
        startActivity(intent);
    }


}
