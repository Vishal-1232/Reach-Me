package com.example.reachme.Fragments;

import static org.webrtc.ContextUtils.getApplicationContext;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.reachme.R;
import com.example.reachme.chatPhotoSendActivity;
import com.example.reachme.databinding.FragmentChatAttachmentsBinding;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class ChatAttachmentsFragment extends BottomSheetDialogFragment {

    FragmentChatAttachmentsBinding binding;

    public ChatAttachmentsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChatAttachmentsBinding.inflate(inflater, container, false);
        String senderRoom = getArguments().getString("SENDER_ROOM");
        String reciverRoom = getArguments().getString("RECIVER_ROOM");

        binding.document.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Doc click", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });
        binding.camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Camera click", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });
        binding.location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "location click", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });
        binding.gallery1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Gallery click", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getContext(),chatPhotoSendActivity.class);
                intent.putExtra("SENDER_ROOM",senderRoom);
                intent.putExtra("RECIVER_ROOM",reciverRoom);
                startActivity(intent);
                dismiss();
            }
        });
        binding.voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "voice click", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });
        binding.contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "contact click", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });
        binding.audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "audio click", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });
        return binding.getRoot();
    }

}