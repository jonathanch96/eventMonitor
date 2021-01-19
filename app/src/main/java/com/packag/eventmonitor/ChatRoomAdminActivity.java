package com.packag.eventmonitor;

import android.content.Intent;
import android.os.Bundle;
import android.util.EventLog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.packag.eventmonitor.Adapter.AdapterChat;
import com.packag.eventmonitor.Adapter.AdapterChatRoom;
import com.packag.eventmonitor.Data.Chat;
import com.packag.eventmonitor.Data.Events;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ChatRoomAdminActivity extends AppCompatActivity {
    FirebaseFirestore db;
    Intent intent;
    AdapterChatRoom acr;
    ListView lv_cr_messages_view;
    List<Events> events;

    String eventId;
    String userId;
    String name;

    boolean firstRun = true;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        initComponent();
        getChatroomData();
        setListener();
    }
    private void getChatroomData(){
        Log.d("debug","getChatroomData() called");
        final Query chatRef = db.collection("chats").whereEqualTo("eventId",eventId)
                .orderBy("created_at", Query.Direction.DESCENDING);
        chatRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    for (QueryDocumentSnapshot d2 : task.getResult()) {

                        if (d2.exists()) {
                            Chat c = d2.toObject(Chat.class);
                            c.setBelongsToCurrentUser(true);
                            if(!c.getUserId().equals("admin")){
                                if(acr.getChat().size()==0){

                                    if(!c.isIs_read()) {
                                        c.setUnread_counter(1);
                                    }
                                    acr.add(c);

                                }
                                boolean flag_same = false;
                                for(int i = 0 ; i<acr.getCount();i++){
                                    Chat tc = (Chat)acr.getItem(i);
                                    if(c.getUserId().equals(tc.getUserId())||c.getDestUserId().equals(tc.getUserId())) {
                                        //same user
                                        if(!c.isIs_read()&&!c.getUserId().equals("admin")){
                                            ArrayList<Chat> temp_chats = new ArrayList<Chat>(acr.getChat());
                                            tc.setUnread_counter(tc.getUnread_counter()+1);
                                            temp_chats.set(i,tc);
                                            acr.updateData(temp_chats);
                                        }
                                        flag_same = true;
                                    }
                                }
                                if(!flag_same){
                                    if(!c.isIs_read()) {
                                        c.setUnread_counter(1);
                                    }
                                    acr.add(c);
                                }
//
                            }


                        }
                    }

                    //update last chat
                    for (QueryDocumentSnapshot d2 : task.getResult()) {

                        if (d2.exists()) {
                            Chat c = d2.toObject(Chat.class);
                            c.setBelongsToCurrentUser(true);

                                for(int i = 0 ; i<acr.getCount();i++){
                                    Chat tc = (Chat)acr.getItem(i);
                                    if(c.getUserId().equals(tc.getUserId())||c.getDestUserId().equals(tc.getUserId())) {
                                        //same user
                                        ArrayList<Chat> temp_chats = new ArrayList<Chat>(acr.getChat());

                                        if(tc.getCreated_at().compareTo(c.getCreated_at())<1){
                                            tc.setCreated_at(c.getCreated_at());
                                            tc.setText(c.getText());
                                        }


                                        temp_chats.set(i,tc);
                                        Collections.sort(temp_chats);
                                        acr.updateData(temp_chats);

                                    }
                                }


                        }
                    }
                    Log.d("debug",acr.getChat().toString());
                    Log.d("debug","Event"+eventId);
                    Log.d("debug","User:"+userId);
                    lv_cr_messages_view.setAdapter(acr);
                }
            }
        });
        chatRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w("DEBUG", "listen:error", error);
                    return;
                }
                if(firstRun){
                    firstRun = false;
                }else {

                    for (DocumentChange dc : value.getDocumentChanges()) {
                        Chat c = dc.getDocument().toObject(Chat.class);
                        c.setBelongsToCurrentUser(true);
                        switch (dc.getType()) {
                            case ADDED:

                                boolean flag_same = false;
                                for(int i = 0 ; i<acr.getCount();i++){
                                    Chat tc = (Chat)acr.getItem(i);
                                    if(c.getUserId().equals(tc.getUserId())||c.getDestUserId().equals(tc.getUserId())) {
                                        //same user
                                        ArrayList<Chat> temp_chats = new ArrayList<Chat>(acr.getChat());
                                        tc.setText(c.getText());
                                        tc.setCreated_at(c.getCreated_at());
                                        if(c.getDestUserId().equals(userId)) {
                                            tc.setUnread_counter(tc.getUnread_counter() + 1);
                                        }
                                        temp_chats.set(i,tc);
                                        Collections.sort(temp_chats);
                                        acr.updateData(temp_chats);
                                        flag_same = true;
                                    }
                                }
                                if(!flag_same){
                                    if(c.getUserId().equals(userId)) {
                                        c.setUnread_counter(c.getUnread_counter() + 1);

                                    }
                                    acr.add(c);
                                }
                                Log.d("DEBUG", "New Msg: " + c.getText());
                                break;
                            case MODIFIED:

                                Log.d("DEBUG", "New Msg: " + c.getText());
                                break;
                            case REMOVED:
                                Log.d("DEBUG", "Removed Msg: " + dc.getDocument().toObject(Chat.class));
                                break;

                        }

                    }
                }
            }
        });
    }
    private void setListener() {
        lv_cr_messages_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for(int i = 0 ; i<acr.getCount();i++) {
                    if (i == position){
                        Chat tc = (Chat) acr.getItem(i);

                        ArrayList<Chat> temp_chats = new ArrayList<Chat>(acr.getChat());
                        tc.setUnread_counter(0);
                        temp_chats.set(i, tc);
                        acr.updateData(temp_chats);
                    }
                }

                Intent i = new Intent(ChatRoomAdminActivity.this,ChatActivity.class);
                Chat tc = (Chat)acr.getItem(position);
                i.putExtra("eventId",tc.getEventId());
                i.putExtra("destUserId",tc.getUserId());
                i.putExtra("userId","admin");
                i.putExtra("name","Admin");
                startActivity(i);
            }
        });
    }


    private void initComponent() {
        db = FirebaseFirestore.getInstance();
        intent = getIntent();
        events = new ArrayList<Events>();
        lv_cr_messages_view = findViewById(R.id.lv_cr_messages_view);


        eventId = intent.getStringExtra("eventId");
        userId = intent.getStringExtra("userId");
        name = intent.getStringExtra("name");
        acr = new AdapterChatRoom(ChatRoomAdminActivity.this);

    }

}
