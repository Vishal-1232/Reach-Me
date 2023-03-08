package com.example.reachme.Fragments;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.reachme.Adapters.PostAdapter;
import com.example.reachme.Adapters.StoryAdapter;
import com.example.reachme.AddPostActivity;
import com.example.reachme.MainActivity;
import com.example.reachme.Models.FollowerModel;
import com.example.reachme.Models.PostModel;
import com.example.reachme.Models.Story;
import com.example.reachme.Models.UserStoriesModel;
import com.example.reachme.R;
import com.example.reachme.databinding.FragmentPostBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

public class PostFragment extends Fragment {
    ArrayList<Story>Storylist;
    ArrayList<PostModel>postList;
    MainActivity mainActivity;
    FirebaseDatabase database;
    FirebaseStorage storage;
    public PostFragment() {
        // Required empty public constructor
    }
    FragmentPostBinding binding;
    ActivityResultLauncher<String>galleryLauncher;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentPostBinding.inflate(inflater,container,false);
        MainActivity mainActivity = (MainActivity)getActivity();
        mainActivity.getSupportActionBar().hide();
        storage=FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();

        //=======================================================
        //Friend list
        HashSet<String> friendList = new HashSet<>();
        database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                .child("Friends").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            FollowerModel follower = dataSnapshot.getValue(FollowerModel.class);
                            follower.setId(dataSnapshot.getKey());
                            friendList.add(follower.getId());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        //===============================================

        binding.post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                galleryLauncher.launch("image/*");
            }
        });
        galleryLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.dialog_loading);
                if(dialog.getWindow()!=null){
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                }
                dialog.show();
                binding.post.setImageURI(result);
                final StorageReference storageReference = storage.getReference().child("Stories")
                        .child(FirebaseAuth.getInstance().getUid())
                        .child(new Date().getTime()+"");
                storageReference.putFile(result).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Story story = new Story();
                                story.setStoryAt(new Date().getTime());
                                database.getReference().child("Stories")
                                        .child(FirebaseAuth.getInstance().getUid())
                                        .child("Latest Story Time")
                                        .setValue(story.getStoryAt())
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                UserStoriesModel userStoriesModel = new UserStoriesModel(uri.toString(),story.getStoryAt());
                                                database.getReference().child("Stories").child(FirebaseAuth.getInstance().getUid())
                                                        .child("User Stories")
                                                        .push()
                                                        .setValue(userStoriesModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                dialog.dismiss();
                                                            }
                                                        });
                                            }
                                        });
                            }
                        });
                    }
                });
            }
        });


        binding.addPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AddPostActivity.class);
                startActivity(intent);
            }
        });


        Storylist = new ArrayList<>();

        StoryAdapter storyAdapter = new StoryAdapter(Storylist,getContext());
        binding.storyRv.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        binding.storyRv.setNestedScrollingEnabled(false);
        binding.storyRv.setAdapter(storyAdapter);

        database.getReference().child("Stories").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    Storylist.clear();
                    for (DataSnapshot storySnapshot : snapshot.getChildren()){
                        Story story = new Story();
                        story.setStoryBy(storySnapshot.getKey());
                        if (friendList.contains(story.getStoryBy()) || FirebaseAuth.getInstance().getUid().equals(story.getStoryBy())) {
                            story.setStoryAt(storySnapshot.child("Latest Story Time").getValue(Long.class));

                            ArrayList<UserStoriesModel> storiesModelArrayList = new ArrayList<>();
                            for (DataSnapshot snapshot1 : storySnapshot.child("User Stories").getChildren()) {
                                UserStoriesModel userStoriesModel = snapshot1.getValue(UserStoriesModel.class);
                                storiesModelArrayList.add(userStoriesModel);
                            }
                            story.setStoriesList(storiesModelArrayList);
                            Storylist.add(story);
                        }
                    }
                    storyAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    // posts...................................
        postList = new ArrayList<>();
        PostAdapter postAdapter = new PostAdapter(postList,getContext());
        binding.postRv.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.postRv.setAdapter(postAdapter);
        binding.postRv.setNestedScrollingEnabled(false);


        database.getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    PostModel post = dataSnapshot.getValue(PostModel.class);
                    post.setPostId(dataSnapshot.getKey());
                    if (friendList.contains(post.getPostedBy()) || FirebaseAuth.getInstance().getUid().equals(post.getPostedBy())) {
                        postList.add(post);
                    }
                }
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




        return binding.getRoot();
    }


}