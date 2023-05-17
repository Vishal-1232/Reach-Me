package com.example.reachme;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.reachme.Adapters.ChatAdapter;
import com.example.reachme.Models.MessageModel;
import com.example.reachme.databinding.ActivityChatBotBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class ChatBotActivity extends AppCompatActivity {
    ActivityChatBotBinding binding;
    private String apiURL = "https://api.openai.com/v1/completions";
    String accessTokken = "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";

    private static final int REQUEST_CODE_SPEECH_INPUT = 1;
    ArrayList<MessageModel> messageList;
    ChatAdapter chatAdapter;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBotBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database = FirebaseDatabase.getInstance();

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // chat adapter set
        messageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(this, messageList);
        binding.chatsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.chatsRecyclerView.setAdapter(chatAdapter);

        // load chats
        database.getReference().child("Chat Bot")
                .child(FirebaseAuth.getInstance().getUid())
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()){
                                    messageList.clear();
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                        MessageModel messageModel = dataSnapshot.getValue(MessageModel.class);
                                        messageModel.setMessageId(dataSnapshot.getKey());
                                        messageList.add(messageModel);
                                    }
                                    chatAdapter.notifyDataSetChanged();
                                    binding.chatsRecyclerView.scrollToPosition(messageList.size()-1);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

        // speech to text
        binding.emoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent
                        = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                        Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text");

                try {
                    startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
                } catch (Exception e) {
                    Toast
                            .makeText(ChatBotActivity.this, " " + e.getMessage(),
                                    Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
        // chatting logic
        binding.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = binding.message.getText().toString();
                processAi(message);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(
                        RecognizerIntent.EXTRA_RESULTS);
//                binding.message.setText(
//                        Objects.requireNonNull(result).get(0));
                processAi(Objects.requireNonNull(result).get(0));
            }
        }
    }

    private void processAi(String message){
        if (message.isEmpty()) {
            binding.message.setError("Required");
            return;
        }
        binding.message.setText("");
        MessageModel messageModel = new MessageModel(FirebaseAuth.getInstance().getUid(), message);
        messageModel.setTimeStamp(new Date().getTime());
        database.getReference().child("Chat Bot").child(FirebaseAuth.getInstance().getUid())
                .push().setValue(messageModel);
        //messageList.add(new MessageModel(FirebaseAuth.getInstance().getUid(), message));
//        messageList.add(new MessageModel("ChatBot","Typing...."));
//        chatAdapter.notifyItemInserted(messageList.size() - 1);
//        binding.chatsRecyclerView.scrollToPosition(messageList.size() - 1);

        // Api call
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("model", "text-davinci-003");
            requestBody.put("prompt", message);
            requestBody.put("max_tokens", 4000);
            requestBody.put("temperature", 1);
            requestBody.put("top_p", 1);
            requestBody.put("frequency_penalty", 0.0);
            requestBody.put("presence_penalty", 0.0);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, apiURL, requestBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray js = response.getJSONArray("choices");
                    JSONObject jsonObject = js.getJSONObject(0);
                    String text = jsonObject.getString("text");
                    messageList.remove(messageList.size()-1);
                    chatAdapter.notifyItemRemoved(messageList.size());
                   // messageList.add(new MessageModel("ChatBot", text.replaceFirst("\n", "").replaceFirst("\n", "")));
                   // chatAdapter.notifyItemInserted(messageList.size() - 1);
                    //binding.chatsRecyclerView.scrollToPosition(messageList.size() - 1);
                    messageModel.setMessage(text.replaceFirst("\n", "").replaceFirst("\n", ""));
                    messageModel.setUid("ChatBot");
                    messageModel.setTimeStamp(new Date().getTime());
                    database.getReference().child("Chat Bot").child(FirebaseAuth.getInstance().getUid())
                            .push().setValue(messageModel);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
               // messageList.remove(messageList.size()-1);
                //chatAdapter.notifyItemRemoved(messageList.size());
               // messageList.add(new MessageModel("ChatBot", "ERROR!"));
               // chatAdapter.notifyItemInserted(messageList.size() - 1);
                //binding.chatsRecyclerView.scrollToPosition(messageList.size() - 1);
                Toast.makeText(ChatBotActivity.this, "Error : "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        })
                // passing Header for authentication
        {
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + accessTokken);
                headers.put("Content-Type", "application/json");
                return headers;
            }

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                return super.parseNetworkResponse(response);
            }
        };

        int timeOut = 25000; // 25 seconds
        RetryPolicy policy = new DefaultRetryPolicy(timeOut, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
    }
}