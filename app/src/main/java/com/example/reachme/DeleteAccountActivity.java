package com.example.reachme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.reachme.Models.Users;
import com.example.reachme.databinding.ActivityDeleteAccountBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DeleteAccountActivity extends AppCompatActivity {

    ActivityDeleteAccountBinding binding;

    FirebaseDatabase database;
    FirebaseStorage storage;
    FirebaseAuth auth;

    String uid;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDeleteAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();

        binding.loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (binding.password.getText().toString().isEmpty()) {
                    binding.password.setError("Enter your password");
                    return;
                }



                uid = FirebaseAuth.getInstance().getUid();
                user = FirebaseAuth.getInstance().getCurrentUser();

                //Toast.makeText(DeleteAccountActivity.this, pass+" : "+mail, Toast.LENGTH_SHORT).show();

                AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), binding.password.getText().toString());
                // credential = GoogleAuthProvider.getCredential(user.getEmail(),"6554");
                // Toast.makeText(DeleteAccountActivity.this, credential.toString(), Toast.LENGTH_SHORT).show();
                try {
                    user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(DeleteAccountActivity.this, "Reauthentication success", Toast.LENGTH_SHORT).show();
                                deleteUser();


                            } else {
                                Toast.makeText(DeleteAccountActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                } catch (Exception e) {
                    Toast.makeText(DeleteAccountActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void deleteUser() {

        user.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(DeleteAccountActivity.this, "User account deleted.", Toast.LENGTH_SHORT).show();
                            // FirebaseDatabase database = FirebaseDatabase.getInstance();

                            clearChats();
                            deleteProfilePic();
                            database.getReference().child("Users").
                                    child(uid).removeValue();


                            FirebaseAuth.getInstance().signOut(); // need to sign out user otherwise signin activity redirect us to main activity

                            Intent intent = new Intent(DeleteAccountActivity.this, SignInActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(DeleteAccountActivity.this, "Error:" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    private void deleteProfilePic() {
        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();

// Create a reference to the file to delete
        final StorageReference reference = storage.getReference().child("Profile Pictures")
                .child(uid);

// Delete the file
        reference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
            }
        });
    }

    private void clearChats() {
        database.getReference().child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Users users = dataSnapshot.getValue(Users.class);
                     users.setUserID(dataSnapshot.getKey());
                    if (!users.getUserID().equals(uid)) {
                        database.getReference().child("Chats").
                                child(uid + dataSnapshot.getKey()).removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}