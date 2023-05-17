package com.example.reachme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.reachme.Models.Users;
import com.example.reachme.databinding.ActivitySignUpBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;

import java.util.HashSet;

public class SignUpActivity extends AppCompatActivity {

    ImageView google;
    GoogleSignInClient googleSignInClient;

    ActivitySignUpBinding binding;
    ProgressDialog progressDialog;

    private FirebaseAuth auth;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        google = findViewById(R.id.goog);

        progressDialog = new ProgressDialog(SignUpActivity.this);
        progressDialog.setTitle("Creating Account");
        progressDialog.setMessage("We are creating your account please wait...");

        //        Email password Authentication
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        binding.signupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.username.getText().toString().isEmpty()) {
                    binding.username.setError("Set your Name");
                    return;
                }
                if (binding.email.getText().toString().isEmpty()) {
                    binding.email.setError("Enter your email");
                    return;
                }
                if (binding.password.getText().toString().isEmpty()){
                    binding.password.setError("Set your password");
                    return;
                }
                if (binding.repassword.getText().toString().isEmpty()){
                    binding.repassword.setError("Confirm your password");
                    return;
                }
                if (!binding.password.getText().toString().equals(binding.repassword.getText().toString()))
                {
                    Toast.makeText(SignUpActivity.this, "Password not matched", Toast.LENGTH_SHORT).show();
                    return;
                }
                progressDialog.show();
                auth.createUserWithEmailAndPassword(binding.email.getText().toString(), binding.password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            String id = task.getResult().getUser().getUid();

                            Users users = new Users(binding.username.getText().toString(), binding.password.getText().toString(), binding.email.getText().toString(),"Online");
                            users.setAbout("Student");
                            database.getReference().child("Users").child(id).setValue(users);

                            Toast.makeText(SignUpActivity.this, "Account Created Successfully", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(SignUpActivity.this, "Error : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        binding.face.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SignUpActivity.this, "Feature not available", Toast.LENGTH_SHORT).show();
            }
        });
        binding.goog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                google();
            }
        });
    }

    private void google() {
        //      Google sign in
        google = findViewById(R.id.goog);
        // Initialize sign in options
        // the client-id is copied form
        // google-services.json file
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN
        ).requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Initialize sign in client
        googleSignInClient = GoogleSignIn.getClient(this
                , googleSignInOptions);

        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Initialize sign in intent
                Intent intent = googleSignInClient.getSignInIntent();
                // Start activity for result
                startActivityForResult(intent, 100);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Check condition
        if (requestCode == 100) {
            // When request code is equal to 100
            // Initialize task
            Task<GoogleSignInAccount> signInAccountTask = GoogleSignIn
                    .getSignedInAccountFromIntent(data);

            // check condition
            if (signInAccountTask.isSuccessful()) {
                // When google sign in successful
                // Initialize string
                String s = "Google sign in successful";
                // Display Toast
                displayToast(s);
                // Initialize sign in account
                try {
                    // Initialize sign in account
                    GoogleSignInAccount googleSignInAccount = signInAccountTask
                            .getResult(ApiException.class);
                    // Check condition
                    if (googleSignInAccount != null) {
                        // When sign in account is not equal to null
                        // Initialize auth credential
                        AuthCredential authCredential = GoogleAuthProvider
                                .getCredential(googleSignInAccount.getIdToken()
                                        , null);
                        // Check credential
                        auth.signInWithCredential(authCredential)
                                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        // Check condition
                                        if (task.isSuccessful()) {
                                            // When task is successful
                                            // Redirect to profile activity
                                            /*startActivity(new Intent(SignInActivity.this
                                                    , MainActivity.class)
                                                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));*/
                                            startActivity(new Intent(SignUpActivity.this,MainActivity.class));
                                            finish();
                                            // Display Toast
                                            displayToast("Authentication successful");

                                            FirebaseUser User = auth.getCurrentUser();
                                            database = FirebaseDatabase.getInstance();

                                            Users users = new Users();
                                            users.setUserID(User.getUid());
                                            users.setUserName(User.getDisplayName());
                                            users.setProfilePic(User.getPhotoUrl().toString());
                                            users.setMail(User.getEmail());
                                            users.setAbout("Student");
                                            // users.setPhone(User.getPhoneNumber());
                                            HashSet<String>list = new HashSet<>();
                                            database.getReference().child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    list.clear();
                                                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                                        list.add(dataSnapshot.getKey());
                                                    }
                                                    if(!list.contains(User.getUid())){
                                                        database.getReference().child("Users").child(User.getUid()).setValue(users);
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });

                                        } else {
                                            // When task is unsuccessful
                                            // Display Toast
                                            displayToast("Authentication Failed :" + task.getException()
                                                    .getMessage());
                                        }
                                    }
                                });

                    }
                } catch (ApiException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void displayToast(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}