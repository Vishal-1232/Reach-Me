package com.example.reachme.Adapters;

import static android.view.View.INVISIBLE;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.reachme.ChatsDetailedActivity;
import com.example.reachme.R;

import com.example.reachme.Models.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.viewHolder> {
    ArrayList<Users> list;
    Context context;

    public UsersAdapter(ArrayList<Users> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sample_show_users, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        Users user = list.get(position);
        Picasso.get().load(user.getProfilePic()).placeholder(R.drawable.avatar).into(holder.profilePic);
        holder.userName.setText(user.getUserName());
        //holder.lastMsg.setText();
        FirebaseDatabase.getInstance().getReference().child("Chats").child(FirebaseAuth.getInstance().getUid()+user.getUserID())
                        .orderByChild("timestamp").limitToLast(1)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.hasChildren()){
                                    for(DataSnapshot snapshot1 : snapshot.getChildren()){
                                        holder.lastMsg.setText(snapshot1.child("message").getValue(String.class).toString());
                                        Long timing = snapshot1.child("timeStamp").getValue(Long.class);
                                        holder.time.setText(getTimeDate(timing));
                                    }

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

        if (user.getConnectionStatus().equals("Online"))
        {
            holder.online.setVisibility(View.VISIBLE);
        }
        else{
            holder.online.setVisibility(INVISIBLE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChatsDetailedActivity.class);
                intent.putExtra("userID",user.getUserID());
                intent.putExtra("userName",user.getUserName());
                intent.putExtra("profilePic",user.getProfilePic());

                context.startActivity(intent);
            }
        });
/*
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context,"You clicked on image", Toast.LENGTH_SHORT).show();
            }
        });
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "You clicked on text", Toast.LENGTH_SHORT).show();
            }
        });
*/
/*
        switch (position) {
            case 0:
                holder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(context,"You clicked on image1", Toast.LENGTH_SHORT).show();
                    }
                });
                holder.textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(context, "You clicked on text1", Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case 1:
                holder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(context, "You clicked on image2", Toast.LENGTH_SHORT).show();
                    }
                });
                holder.textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(context, "You clicked on text2", Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case 2:
                holder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(context,"You clicked on image3", Toast.LENGTH_SHORT).show();
                    }
                });
                holder.textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(context, "You clicked on text3", Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case 3:
                holder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(context,"You clicked on image4", Toast.LENGTH_SHORT).show();
                    }
                });
                holder.textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(context, "You clicked on text4", Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case 4:
                holder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(context,"You clicked on image5", Toast.LENGTH_SHORT).show();
                    }
                });
                holder.textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(context, "You clicked on text5", Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            default:
                break;
        }

 */
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        ImageView profilePic,online;
        TextView userName,lastMsg,time;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            profilePic = itemView.findViewById(R.id.profileimg);
            online = itemView.findViewById(R.id.online);
            userName = itemView.findViewById(R.id.userName);
            lastMsg = itemView.findViewById(R.id.lastMsg);
            time = itemView.findViewById(R.id.timestmp);
        }
    }

    public String getTimeDate(long timeStamp)
    {
        try{
            Date netDate = (new Date(timeStamp));
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM d  h:mm a", Locale.getDefault());
            return simpleDateFormat.format(netDate);
        }catch (Exception e){
            return "Time";
        }
    }
}
