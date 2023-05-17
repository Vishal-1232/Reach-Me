package com.example.reachme.Adapters;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.reachme.ChatsDetailedActivity;
import com.example.reachme.Encryption.AES;
import com.example.reachme.Models.MessageModel;
import com.example.reachme.Models.Users;
import com.example.reachme.R;
import com.example.reachme.databinding.SampleReciverPhotoBinding;
import com.github.pgreze.reactions.ReactionPopup;
import com.github.pgreze.reactions.ReactionsConfig;
import com.github.pgreze.reactions.ReactionsConfigBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter {

    Context context;
    ArrayList<MessageModel> messageModels;
    String recID;

    int SENDER_VIEW_TYPE_TEXT = 10;
    int SENDER_VIEW_TYPE_PHOTO = 11;
    int RECIVER_VIEW_TYPE_TEXT = 20;
    int RECIVER_VIEW_TYPE_PHOTO = 21;

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
        if (viewType == SENDER_VIEW_TYPE_TEXT) {
            View view = LayoutInflater.from(context).inflate(R.layout.sample_sender, parent, false);
            return new SenderViewHolder_TEXT(view);
        } else if (viewType == RECIVER_VIEW_TYPE_TEXT) {
            View view = LayoutInflater.from(context).inflate(R.layout.sample_reciver, parent, false);
            return new ReciverViewHolder_TEXT(view);
        } else if (viewType == SENDER_VIEW_TYPE_PHOTO) {
            View view = LayoutInflater.from(context).inflate(R.layout.sample_sender_photo, parent, false);
            return new SenderViewHolder_Photo(view);
        } else if (viewType == RECIVER_VIEW_TYPE_PHOTO) {
            View view = LayoutInflater.from(context).inflate(R.layout.sample_reciver_photo, parent, false);
            return new ReciverViewHolder_PHOTO(view);
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        MessageModel message = messageModels.get(position);
        if (message.getUid().equals(FirebaseAuth.getInstance().getUid())) {
            if (message.getType().equals("Text")) {
                return SENDER_VIEW_TYPE_TEXT;
            } else if (message.getType().equals("Photo")) {
                return SENDER_VIEW_TYPE_PHOTO;
            }
        } else {
            if (message.getType().equals("Text")) {
                return RECIVER_VIEW_TYPE_TEXT;
            } else if (message.getType().equals("Photo")) {
                return RECIVER_VIEW_TYPE_PHOTO;
            }
        }
        return -1;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        MessageModel messageModel = messageModels.get(position);
        String senderID = FirebaseAuth.getInstance().getUid();
        String senderRoom = senderID + recID;
        String reciverRoom = recID + senderID;

        if (messageModel.getType().equals("Text")) {
            // deleting message

            if (holder.getClass() == SenderViewHolder_TEXT.class && !messageModel.getMessage().isEmpty() && !messageModel.getMessage().equals("This Message is Deleted")) {
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        Dialog dialog = new Dialog(context);
                        dialog.setContentView(R.layout.custom_dialog);
                        //dialog.setCancelable();
                        TextView deleteForMe = dialog.findViewById(R.id.dltForMe);
                        TextView cancel = dialog.findViewById(R.id.cancel);
                        TextView deleteForEverryone = dialog.findViewById(R.id.dltForEveryone);

                        deleteForMe.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                database.getReference().child("Chats").child(senderRoom)
                                        .child(messageModel.getMessageId())
                                        .setValue(null);

                                dialog.dismiss();
                            }
                        });

                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });

                        deleteForEverryone.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                database.getReference().child("Chats").child(senderRoom)
                                        .child(messageModel.getMessageId()).child("message")
                                        .setValue(AES.encrypt("This Message is Deleted"));
                                database.getReference().child("Chats").child(reciverRoom)
                                        .child(messageModel.getMessageId()).child("message")
                                        .setValue("This Message is Deleted");
                                dialog.dismiss();
                            }
                        });

                        dialog.show();
                        return false;
                    }
                });
            } else {
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
            }
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
                if (holder.getClass() == SenderViewHolder_TEXT.class) {
                    SenderViewHolder_TEXT viewHolder = (SenderViewHolder_TEXT) holder;
                    viewHolder.feeling.setImageResource(reactions[pos]);
                } else {
                    ReciverViewHolder_TEXT viewHolder = (ReciverViewHolder_TEXT) holder;
                    viewHolder.feeling.setImageResource(reactions[pos]);
                }

                // storing felling in database
                messageModel.setMessage(AES.encrypt(messageModel.getMessage()));
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

            if (holder.getClass() == SenderViewHolder_TEXT.class) {
                ((SenderViewHolder_TEXT) holder).senderMsg.setText(messageModel.getMessage());
                ((SenderViewHolder_TEXT) holder).senderTime.setText(messageModel.getTimeDate(messageModel.getTimeStamp()));

                if (messageModel.getFeeling() >= 0) {
                    ((SenderViewHolder_TEXT) holder).feeling.setImageResource(reactions[messageModel.getFeeling()]);
                    ((SenderViewHolder_TEXT) holder).feeling.setVisibility(View.VISIBLE);
                } else {
                    ((SenderViewHolder_TEXT) holder).feeling.setVisibility(View.GONE);
                }

                ((SenderViewHolder_TEXT) holder).senderMsg.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        popup.onTouch(view, motionEvent);
                        return false;
                    }
                });
            } else {
                ((ReciverViewHolder_TEXT) holder).reciverMsg.setText(messageModel.getMessage());
                ((ReciverViewHolder_TEXT) holder).reciverTime.setText(messageModel.getTimeDate(messageModel.getTimeStamp()));

                if (messageModel.getFeeling() >= 0) {
                    ((ReciverViewHolder_TEXT) holder).feeling.setImageResource(reactions[messageModel.getFeeling()]);
                    ((ReciverViewHolder_TEXT) holder).feeling.setVisibility(View.VISIBLE);
                } else {
                    ((ReciverViewHolder_TEXT) holder).feeling.setVisibility(View.GONE);
                }

                ((ReciverViewHolder_TEXT) holder).reciverMsg.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        popup.onTouch(view, motionEvent);
                        return false;
                    }
                });
            }

        } else if (messageModel.getType().equals("Photo")) {
            if (holder.getClass() == SenderViewHolder_Photo.class ){
                // photo load
                Picasso.get().load(messageModel.getPhoto()).placeholder(R.drawable.placeholder_photo).into(((SenderViewHolder_Photo) holder).photo);
                if (messageModel.getMessage().isEmpty()){
                    ((SenderViewHolder_Photo) holder).msgLayout.setVisibility(View.GONE);
                }else{
                    ((SenderViewHolder_Photo) holder).msgLayout.setVisibility(View.VISIBLE);
                    ((SenderViewHolder_Photo) holder).senderMsg.setText(messageModel.getMessage());
                    ((SenderViewHolder_Photo) holder).senderTime.setText(messageModel.getTimeDate(messageModel.getTimeStamp()));
                }

            }else{
                Picasso.get().load(messageModel.getPhoto()).placeholder(R.drawable.placeholder_photo).into(((ReciverViewHolder_PHOTO) holder).binding.photo);
                if (messageModel.getMessage().isEmpty()){
                    ((ReciverViewHolder_PHOTO) holder).binding.msg.setVisibility(View.GONE);
                }else{
                    ((ReciverViewHolder_PHOTO) holder).binding.msg.setVisibility(View.VISIBLE);
                    ((ReciverViewHolder_PHOTO) holder).binding.recivedText.setText(messageModel.getMessage());
                    ((ReciverViewHolder_PHOTO) holder).binding.reciveTime.setText(messageModel.getTimeDate(messageModel.getTimeStamp()));
                }
                // download image
                if (messageModel.isDownloaded()){
                    ((ReciverViewHolder_PHOTO) holder).binding.downloadImg.setVisibility(View.GONE);
                }else{
                    ((ReciverViewHolder_PHOTO) holder).binding.downloadImg.setVisibility(View.VISIBLE);
                    ((ReciverViewHolder_PHOTO) holder).binding.downloadImg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            downloadImg("IMG", messageModel.getPhoto());
                            FirebaseDatabase.getInstance().getReference().child("Chats")
                                    .child(senderRoom).child(messageModel.getMessageId())
                                    .child("downloaded").setValue(true);

                        }
                    });
                }
            }
        }

    }

    @Override
    public int getItemCount() {
        return messageModels.size();
    }

    public class ReciverViewHolder_PHOTO extends RecyclerView.ViewHolder {
        SampleReciverPhotoBinding binding;
        public ReciverViewHolder_PHOTO(@NonNull View itemView) {
            super(itemView);
            binding = SampleReciverPhotoBinding.bind(itemView);
        }
    }

    public class ReciverViewHolder_TEXT extends RecyclerView.ViewHolder {
        TextView reciverMsg, reciverTime;
        ImageView feeling;

        public ReciverViewHolder_TEXT(@NonNull View itemView) {
            super(itemView);
            reciverMsg = itemView.findViewById(R.id.recivedText);
            reciverTime = itemView.findViewById(R.id.reciveTime);
            feeling = itemView.findViewById(R.id.feeling);
        }
    }

    public class SenderViewHolder_Photo extends RecyclerView.ViewHolder {
        ImageView photo;
        TextView senderMsg, senderTime;
        ConstraintLayout msgLayout;

        public SenderViewHolder_Photo(@NonNull View itemView) {
            super(itemView);
            photo = itemView.findViewById(R.id.photo);
            senderMsg = itemView.findViewById(R.id.senderText);
            senderTime = itemView.findViewById(R.id.senderTime);
            msgLayout = itemView.findViewById(R.id.smsg);
        }
    }

    public class SenderViewHolder_TEXT extends RecyclerView.ViewHolder {
        TextView senderMsg, senderTime;
        ImageView feeling;

        public SenderViewHolder_TEXT(@NonNull View itemView) {
            super(itemView);
            senderMsg = itemView.findViewById(R.id.senderText);
            senderTime = itemView.findViewById(R.id.senderTime);
            feeling = itemView.findViewById(R.id.sfeeling);
        }
    }

    void downloadImg(String name, String url){
        try {
            DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            Uri downloadUri = Uri.parse(url);
            DownloadManager.Request request = new DownloadManager.Request(downloadUri);
            request.setAllowedNetworkTypes((DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE))
                    .setTitle(name)
                    .setMimeType("image/jpeg")
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, "/Reach Me/"+ name + ".jpg");
            dm.enqueue(request);
            Toast.makeText(context, "Image Download started.", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
