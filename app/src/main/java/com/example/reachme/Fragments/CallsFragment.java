package com.example.reachme.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.reachme.MainActivity;
import com.example.reachme.R;
import com.example.reachme.databinding.FragmentCallsBinding;

import org.jitsi.meet.sdk.JitsiMeet;
import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

import java.net.MalformedURLException;
import java.net.URL;

public class CallsFragment extends Fragment {

    public CallsFragment() {
        // Required empty public constructor
    }

    FragmentCallsBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCallsBinding.inflate(inflater, container, false);
        MainActivity mainActivity = (MainActivity)getActivity();
        mainActivity.getSupportActionBar().hide();

        URL serverURL;
        try {
            serverURL = new URL("https://meet.jit.si");
            JitsiMeetConferenceOptions defaultOoptions = new JitsiMeetConferenceOptions.Builder()
                    .setServerURL(serverURL)
                    .setFeatureFlag("welcomepage.enabled", false)
                    .setFeatureFlag("invite.enabled",false)
                    .build();
            JitsiMeet.setDefaultConferenceOptions(defaultOoptions);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        binding.join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String roomKey = binding.secretCode.getText().toString();
                if (roomKey.isEmpty()) {
                    binding.secretCode.setError("Required");
                    return;
                }

                try {
                    JitsiMeetConferenceOptions options = new JitsiMeetConferenceOptions.Builder()
                            .setRoom(roomKey)
                            .build();
                    JitsiMeetActivity.launch(getContext(), options);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        binding.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String roomKey = binding.secretCode.getText().toString();
                if (roomKey.isEmpty()) {
                    binding.secretCode.setError("Required");
                    return;
                }
                String str = binding.secretCode.getText().toString();
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, str);
                intent.setType("text/plain");
                Intent shareIntent = Intent.createChooser(intent, null);
                startActivity(shareIntent);
            }
        });

        return binding.getRoot();
    }
}