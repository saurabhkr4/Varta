package com.example.varta.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.varta.Models.Message;
import com.example.varta.R;
import com.example.varta.databinding.RecievedMessageBinding;
import com.example.varta.databinding.SentMessageBinding;
import com.github.pgreze.reactions.ReactionPopup;
import com.github.pgreze.reactions.ReactionsConfig;
import com.github.pgreze.reactions.ReactionsConfigBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MessagesAdapter extends RecyclerView.Adapter {
    Context context;
    ArrayList<Message> messages;
    final int ITEM_SENT = 1;
    final int ITEM_RECEIVED = 2;
    String senderRoom;
    String receiverRoom;

    public MessagesAdapter(Context context, ArrayList<Message> messages, String senderRoom, String receiverRoom) {
        this.context = context;
        this.messages = messages;
        this.senderRoom = senderRoom;
        this.receiverRoom = receiverRoom;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == ITEM_SENT){
            View view = LayoutInflater.from(context).inflate(R.layout.sent_message, parent, false);
            return new SentViewHolder(view);
        }else{
            View view = LayoutInflater.from(context).inflate(R.layout.recieved_message, parent, false);
            return new ReceivedViewHolder(view);
        }

    }

    int reactions[] = new int[]{R.drawable.emoji_like,
            R.drawable.emoji_love,
            R.drawable.emoji_laugh,
            R.drawable.emoji_sarcastic,
            R.drawable.emoji_shock,
            R.drawable.emoji_anger
    };


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        Message message = messages.get(position);

        ReactionsConfig config = new ReactionsConfigBuilder(context)
                .withReactions(reactions)
                .build();

        ReactionPopup popup = new ReactionPopup(context, config, (pos) -> {




            if(holder.getClass() == SentViewHolder.class){
                SentViewHolder viewHolder = (SentViewHolder)holder;
                viewHolder.binding.reactionIconS.setImageResource(reactions[pos]);
                viewHolder.binding.reactionIconS.setVisibility(View.VISIBLE);//viewHolder.itemView.VISIBLE

            }else{
                ReceivedViewHolder viewHolder = (ReceivedViewHolder) holder;
                viewHolder.binding.reactionIcon.setImageResource(reactions[pos]);
                viewHolder.binding.reactionIcon.setVisibility(View.VISIBLE);//viewHolder.itemView.VISIBLE
            }

            message.setFeeling(pos);

            //setting the message again so that we have emojis set this time
            FirebaseDatabase.getInstance().getReference()
                    .child("chats")
                    .child(senderRoom)
                    .child("messages")
                    .child(message.getMessageId())
                    .setValue(message);

            FirebaseDatabase.getInstance().getReference()
                    .child("chats")
                    .child(receiverRoom)
                    .child("messages")
                    .child(message.getMessageId())
                    .setValue(message);




            return true; // true is closing popup, false is requesting a new selection
        });

        if(holder.getClass() == SentViewHolder.class){
            SentViewHolder viewHolder = (SentViewHolder)holder;
            viewHolder.binding.messageS.setText(message.getMessage());

            if(message.getFeeling() >= 0){
                //message.setFeeling(reactions[(int)message.getFeeling()]);
                viewHolder.binding.reactionIconS.setImageResource(reactions[(int)message.getFeeling()]);
                viewHolder.binding.reactionIconS.setVisibility(View.VISIBLE);
            }else{
                viewHolder.binding.reactionIconS.setVisibility(View.GONE);
            }


            //Setting action listeners for reactions on the messages
            viewHolder.binding.messageS.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    viewHolder.binding.getRoot().requestDisallowInterceptTouchEvent(true);


                    popup.onTouch(v,event);
                    return false;

                }
            });

        }else{
            ReceivedViewHolder viewHolder = (ReceivedViewHolder)holder;
            viewHolder.binding.message.setText(message.getMessage());

            if(message.getFeeling() >= 0){
                //message.setFeeling(reactions[(int)message.getFeeling()]);
                viewHolder.binding.reactionIcon.setImageResource(reactions[(int)message.getFeeling()]);
                viewHolder.binding.reactionIcon.setVisibility(View.VISIBLE);
            }else{
                viewHolder.binding.reactionIcon.setVisibility(View.GONE);
            }

            //Setting action listeners for reactions on the messages
            viewHolder.binding.message.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {


                    viewHolder.binding.getRoot().requestDisallowInterceptTouchEvent(true);
                    popup.onTouch(v,event);
                    return false;

                }
            });
        }


    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        if(FirebaseAuth.getInstance().getUid().equals(message.getSenderId())){
            return ITEM_SENT;
        }else{
            return ITEM_RECEIVED;
        }
        //return super.getItemViewType(position);
    }

    //2 viewholder liya jaa raha hai
    public class SentViewHolder extends RecyclerView.ViewHolder{
        SentMessageBinding binding;
        public SentViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = SentMessageBinding.bind(itemView);
        }
    }

    public class ReceivedViewHolder extends RecyclerView.ViewHolder{
        RecievedMessageBinding binding;

        public ReceivedViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = RecievedMessageBinding.bind(itemView);
        }
    }



}
