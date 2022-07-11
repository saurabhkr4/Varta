package com.example.varta.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.example.varta.Adapters.MessagesAdapter;
import com.example.varta.Models.Message;
import com.example.varta.databinding.ActivityChatBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class chat_activity extends AppCompatActivity {

    ActivityChatBinding binding;
    MessagesAdapter adapter;
    ArrayList<Message> messages;

    //Two different rooms
    String senderRoom;
    String receiverRoom;
    FirebaseDatabase database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String name = getIntent().getStringExtra("name");
        String receiverUid = getIntent().getStringExtra("uid");
        String senderUid = FirebaseAuth.getInstance().getUid();

        senderRoom = senderUid + receiverUid;
        receiverRoom = receiverUid + senderUid;

        messages = new ArrayList<>();
        adapter = new MessagesAdapter(this, messages, senderRoom, receiverRoom);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);






        database = FirebaseDatabase.getInstance();

        database.getReference()
                .child("chats")
                .child(senderRoom)
                .child("messages")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int sizeofmsgs = messages.size();
                        messages.clear();
                        for(DataSnapshot snapshot1 : snapshot.getChildren() ){
                            Message message = snapshot1.getValue(Message.class);
                            message.setMessageId(snapshot1.getKey());       //setting the messageId equal to the key(randomKey) which is generated
                            messages.add(message);
                        }

                        adapter.notifyDataSetChanged();
                        binding.recyclerView.getLayoutManager().scrollToPosition(sizeofmsgs);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });







        binding.sendButtonIm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String messageText = binding.typeMessage.getText().toString();
                String str = messageText.trim();
                if (!str.equals("")) {
                    Date date = new Date();
                    Message message = new Message(messageText, senderUid, date.getTime());
                    binding.typeMessage.setText("");


                    //Making the key of a message same in both the rooms
                    String sameRandomKey = database.getReference().push().getKey();

                    HashMap<String, Object> lastMsgObject = new HashMap<>();
                    lastMsgObject.put("lastMsg", message.getMessage());
                    lastMsgObject.put("lastMsgTime", date.getTime());

                    database.getReference().child("chats").child(senderRoom).updateChildren(lastMsgObject);
                    database.getReference().child("chats").child(receiverRoom).updateChildren(lastMsgObject);



                    database.getReference()
                            .child("chats")
                            .child(senderRoom)
                            .child("messages")
                            .child(sameRandomKey)
                            .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                    database.getReference()
                                            .child("chats")
                                            .child(receiverRoom)
                                            .child("messages")
                                            .child(sameRandomKey)
                                            .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {

                                                }
                                            });






                                    binding.recyclerView.getLayoutManager().scrollToPosition(messages.size()-1);


                                }
                            });
                }
            }
        });


        //To set the title as sender's name
        getSupportActionBar().setTitle(name);

        //To go back to home
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}