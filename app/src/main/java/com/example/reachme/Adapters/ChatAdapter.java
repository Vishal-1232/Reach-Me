package com.example.reachme.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.reachme.ChatsDetailedActivity;
import com.example.reachme.Models.MessageModel;
import com.example.reachme.Models.Users;
import com.example.reachme.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter {

    Context context;
    ArrayList<MessageModel> messageModels;
    String recID;

    int SENDER_VIEW_TYPE = 1;
    int RECIVER_VIEW_TYPE = 2;

    public ChatAdapter(Context context, ArrayList<MessageModel> messageModels) {
        this.context = context;
        this.messageModels = messageModels;
    }

    public ChatAdapter(Context context, ArrayList<MessageModel> messageModels, String recID) {
        this.context = context;
        this.messageModels = messageModels;
        this.recID = recID;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == SENDER_VIEW_TYPE) {
            View view = LayoutInflater.from(context).inflate(R.layout.sample_sender, parent, false);
            return new SenderViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.sample_reciver, parent, false);
            return new ReciverViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (messageModels.get(position).getUid().equals(FirebaseAuth.getInstance().getUid())) {
            return SENDER_VIEW_TYPE;
        } else {
            return RECIVER_VIEW_TYPE;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        MessageModel messageModel = messageModels.get(position);

        // deleting message
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                new AlertDialog.Builder(context)
                        .setTitle("Delete")
                        .setMessage("Are you sure you want to delete this message")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                String senderRoom = FirebaseAuth.getInstance().getUid() + recID;
                                database.getReference().child("Chats").child(senderRoom)
                                        .child(messageModel.getMessageId())
                                        .setValue(null);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).show();
                return false;
            }
        });

        if (holder.getClass() == SenderViewHolder.class) {
            ((SenderViewHolder) holder).senderMsg.setText(messageModel.getMessage());
            ((SenderViewHolder) holder).senderTime.setText(messageModel.getTimeDate(messageModel.getTimeStamp()));

        } else {
            ((ReciverViewHolder) holder).reciverMsg.setText(messageModel.getMessage());
            ((ReciverViewHolder) holder).reciverTime.setText(messageModel.getTimeDate(messageModel.getTimeStamp()));
        }
    }

    @Override
    public int getItemCount() {
        return messageModels.size();
    }

    public class ReciverViewHolder extends RecyclerView.ViewHolder {
        TextView reciverMsg, reciverTime;

        public ReciverViewHolder(@NonNull View itemView) {
            super(itemView);
            reciverMsg = itemView.findViewById(R.id.recivedText);
            reciverTime = itemView.findViewById(R.id.reciveTime);
        }
    }

    public class SenderViewHolder extends RecyclerView.ViewHolder {
        TextView senderMsg, senderTime;

        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            senderMsg = itemView.findViewById(R.id.senderText);
            senderTime = itemView.findViewById(R.id.senderTime);
        }
    }
}
