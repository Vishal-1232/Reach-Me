package com.example.reachme.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
import com.github.pgreze.reactions.ReactionPopup;
import com.github.pgreze.reactions.ReactionsConfig;
import com.github.pgreze.reactions.ReactionsConfigBuilder;
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

        // Reactions
        int[] reactions = new int[]{
                R.drawable.ic_fb_like,
                R.drawable.ic_fb_love,
                R.drawable.ic_fb_laugh,
                R.drawable.ic_fb_wow,
                R.drawable.ic_fb_sad,
                R.drawable.ic_fb_angry
        };
        ReactionsConfig config = new ReactionsConfigBuilder(context)
                .withReactions(reactions)
                .build();
        ReactionPopup popup = new ReactionPopup(context, config, (pos) -> {
            if (pos < 0) {
                return true;
            }
            if (holder.getClass() == SenderViewHolder.class) {
                SenderViewHolder viewHolder = (SenderViewHolder) holder;
                viewHolder.feeling.setImageResource(reactions[pos]);
            } else {
                ReciverViewHolder viewHolder = (ReciverViewHolder) holder;
                viewHolder.feeling.setImageResource(reactions[pos]);
            }

            // storing felling in database
            String senderID = FirebaseAuth.getInstance().getUid();
            String senderRoom = senderID + recID;
            String reciverRoom = recID + senderID;

            messageModel.setFeeling(pos);
            FirebaseDatabase.getInstance().getReference().child("Chats")
                    .child(senderRoom)
                    .child(messageModel.getMessageId())
                    .setValue(messageModel);

            FirebaseDatabase.getInstance().getReference().child("Chats")
                    .child(reciverRoom)
                    .child(messageModel.getMessageId())
                    .setValue(messageModel);

            // ---------------------STORED----------------------

            return true; // true is closing popup, false is requesting a new selection
        });

        if (holder.getClass() == SenderViewHolder.class) {
            ((SenderViewHolder) holder).senderMsg.setText(messageModel.getMessage());
            ((SenderViewHolder) holder).senderTime.setText(messageModel.getTimeDate(messageModel.getTimeStamp()));

            if (messageModel.getFeeling() >= 0) {
                ((SenderViewHolder) holder).feeling.setImageResource(reactions[messageModel.getFeeling()]);
            }

            ((SenderViewHolder) holder).senderMsg.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    popup.onTouch(view, motionEvent);
                    return false;
                }
            });
        } else {
            ((ReciverViewHolder) holder).reciverMsg.setText(messageModel.getMessage());
            ((ReciverViewHolder) holder).reciverTime.setText(messageModel.getTimeDate(messageModel.getTimeStamp()));

            if (messageModel.getFeeling() >= 0) {
                ((ReciverViewHolder) holder).feeling.setImageResource(reactions[messageModel.getFeeling()]);
            }

            ((ReciverViewHolder) holder).reciverMsg.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    popup.onTouch(view, motionEvent);
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return messageModels.size();
    }

    public class ReciverViewHolder extends RecyclerView.ViewHolder {
        TextView reciverMsg, reciverTime;
        ImageView feeling;

        public ReciverViewHolder(@NonNull View itemView) {
            super(itemView);
            reciverMsg = itemView.findViewById(R.id.recivedText);
            reciverTime = itemView.findViewById(R.id.reciveTime);
            feeling = itemView.findViewById(R.id.feeling);
        }
    }

    public class SenderViewHolder extends RecyclerView.ViewHolder {
        TextView senderMsg, senderTime;
        ImageView feeling;

        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            senderMsg = itemView.findViewById(R.id.senderText);
            senderTime = itemView.findViewById(R.id.senderTime);
            feeling = itemView.findViewById(R.id.sfeeling);
        }
    }
}
