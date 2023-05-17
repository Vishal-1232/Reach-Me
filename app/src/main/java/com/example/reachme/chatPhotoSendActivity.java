package com.example.reachme;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.reachme.Models.MessageModel;
import com.example.reachme.databinding.ActivityChatPhotoSendBinding;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;

public class chatPhotoSendActivity extends AppCompatActivity {

    ActivityChatPhotoSendBinding binding;
    String senderRoom;
    String reciverRoom;
    FirebaseDatabase database;
    FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatPhotoSendBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        senderRoom = getIntent().getStringExtra("SENDER_ROOM");
        reciverRoom = getIntent().getStringExtra("RECIVER_ROOM");
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        binding.close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ImagePicker.with(this).galleryOnly().maxResultSize(1080,1080).crop().compress(500).start(1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri = data.getData();
        if (uri != null) {
            if (requestCode == 1) {
                binding.photo.setImageURI(uri);
                binding.send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ProgressDialog progressDialog = new ProgressDialog(chatPhotoSendActivity.this);
                        progressDialog.setTitle("Sending...");
                        progressDialog.setMessage("Please Wait");
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                        String randomKey = database.getReference().push().getKey();
                        final StorageReference storageReference = storage.getReference().child("Chats").child("Photos").child(randomKey);
                        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        MessageModel message = new MessageModel(FirebaseAuth.getInstance().getUid(), binding.caption.getText().toString());
                                        message.setType("Photo");
                                        message.setTimeStamp(new Date().getTime());
                                        message.setPhoto(uri.toString());

                                        database.getReference().child("Chats").child(senderRoom)
                                                .child(randomKey).setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        database.getReference().child("Chats").child(reciverRoom)
                                                                .child(randomKey).setValue(message);

                                                        progressDialog.dismiss();
                                                        finish();
                                                    }
                                                });
                                    }
                                });
                            }
                        });
                    }
                });
            }
        } else {
            Toast.makeText(this, "Image not Selected", Toast.LENGTH_SHORT).show();
        }
    }
}