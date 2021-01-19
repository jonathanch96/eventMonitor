package com.packag.eventmonitor;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.packag.eventmonitor.Adapter.AdapterChat;
import com.packag.eventmonitor.Data.Chat;


import java.util.Arrays;

import javax.annotation.Nullable;

public class ChatActivity extends AppCompatActivity {
    FirebaseFirestore db;
    ListView lv_message_view;
    EditText et_c_input;
    ImageButton ib_c_submit;

    String eventId;

    String userId;
    String destUserId;
    String name;
    Intent intent;
    AdapterChat ac;
    boolean firstRun = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initComponent();
        getChatData();
        setListener();
    }
    private void setListener(){
        ib_c_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String message =  et_c_input.getText().toString();
               if(!message.equals("")){
                   String temp_destUserId = "admin";
                   if(userId.equals("admin")){
                       temp_destUserId = destUserId;
                   }
                   Chat chat = new Chat(message,name,userId,temp_destUserId);
                   message="";
                   et_c_input.setText("");
                   chat.setEventId(eventId);
                   db.collection("chats").add(chat);
               }
            }
        });
        et_c_input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                   ib_c_submit.callOnClick();
                }
                return false;
            }
        });
    }
    private void getChatData(){
        Query chatref;
        if(userId.equals("admin")){
            chatref = db.collection("chats").orderBy("created_at", Query.Direction.ASCENDING)
                    .whereIn("userId", Arrays.asList("admin", destUserId));
        }else{
            chatref = db.collection("chats").orderBy("created_at", Query.Direction.ASCENDING)
                    .whereIn("userId", Arrays.asList("admin", userId));
        }


        chatref .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot d2 : task.getResult()) {
                        if (d2.exists()) {
                            Chat chat =  d2.toObject(Chat.class);
                            if(chat.getUserId().equals(userId)){
                                chat.setBelongsToCurrentUser(true);
                            }else{
                                chat.setBelongsToCurrentUser(false);
                                chat.setIs_read(true);
                                db.collection("chats").document(d2.getId()).set(chat);

                            }
                            Log.d("debug","showing comparing : "+destUserId+"= "+chat.getDestUserId());
                            if((userId.equals("admin")&&(chat.getDestUserId().equals(destUserId))
                                    ||chat.getUserId().equals(destUserId))||!userId.equals("admin")){
                                ac.add(chat);
                            }

                        }
                    }

                    lv_message_view.setAdapter(ac);
                    lv_message_view.setSelection(ac.getCount() - 1);
                }
            }
        });
        chatref.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("DEBUG", "listen:error", e);
                    return;
                }
                if(firstRun){
                    firstRun = false;
                }else {


                    for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                        Chat chat = dc.getDocument().toObject(Chat.class);
                        if (chat.getUserId().equals(userId)) {
                            chat.setBelongsToCurrentUser(true);
                        } else {
                            chat.setBelongsToCurrentUser(false);
                        }
                        switch (dc.getType()) {

                            case ADDED:

                                ac.add(chat);
                                lv_message_view.setSelection(ac.getCount() - 1);
                                Log.d("DEBUG", "New Msg: " + chat.getText());
                                break;
                            case MODIFIED:
                                Log.d("DEBUG", "Modified Msg: " + dc.getDocument().toObject(Chat.class));
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
    private void initComponent(){
        db = FirebaseFirestore.getInstance();
        intent = getIntent();

        lv_message_view = findViewById(R.id.lv_c_messages_view);
        et_c_input = findViewById(R.id.et_c_input);
        ib_c_submit = findViewById(R.id.ib_c_submit);
        eventId = intent.getStringExtra("eventId");
        destUserId = intent.getStringExtra("destUserId");
        userId = intent.getStringExtra("userId");
        name = intent.getStringExtra("name");
        ac = new AdapterChat(ChatActivity.this);


    }
}
