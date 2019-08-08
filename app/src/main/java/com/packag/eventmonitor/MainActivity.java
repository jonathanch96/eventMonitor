package com.packag.eventmonitor;

import android.app.ProgressDialog;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.developer.kalert.KAlertDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.packag.eventmonitor.Data.Events;
import com.packag.eventmonitor.Util.Session;

public class MainActivity extends AppCompatActivity {
    Button btn_referee_login;
    Button btn_admin_login;
    Session session ;
    Events events;
    ProgressDialog loadingDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeComponent();
        setListener();
        int flag = validateLogin();
        if(flag==1){
           //referee
            //check event status kalo ud closed ke judge login kalo open buka lg
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("events").document(session.getData("eventId"))
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        loadingDialog.hide();
                        if(document.exists()){
                            events = document.toObject(Events.class);
                            events.setKey(document.getId());
                            if(events.getStatus()==10){
                                new KAlertDialog(MainActivity.this, KAlertDialog.ERROR_TYPE)
                                        .setTitleText("Oops...")
                                        .setContentText("Event "+events.getCode()+" sudah berakhir silahkan untuk login ulang!")
                                        .show();
                            }else{
                                Intent i = new Intent(MainActivity.this,RefereeActivity.class);
                                startActivity(i);

                                finish();
                            }
                        }else{
                            new KAlertDialog(MainActivity.this, KAlertDialog.ERROR_TYPE)
                                    .setTitleText("Oops...")
                                    .setContentText("Terjadi sebuah masalah silahkan untuk login ulang!")
                                    .show();
                        }

                    }
                }
            });

        }else if(flag==2){
           //admin
            loadingDialog.hide();
            Intent i = new Intent(MainActivity.this,AdminActivity.class);
            startActivity(i);
            finish();
        }else{
            loadingDialog.hide();
        }

    }
    private void initializeComponent(){
        btn_admin_login = findViewById(R.id.btn_admin_login);
        btn_referee_login = findViewById(R.id.btn_referee_login);
        session = new Session(this.getApplicationContext());
        loadingDialog = new ProgressDialog(this);
        loadingDialog.setTitle("Processing your Request");
        loadingDialog.setMessage("Please Wait a second...");

    }
    private void setListener(){
        btn_referee_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, RefereeLoginActivity.class);
                startActivity(i);
                finish();
            }
        });
        btn_admin_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this,AdminLoginActivity.class);
                startActivity(i);
                finish();
            }
        });
    }
    private int validateLogin(){
        loadingDialog.show();
        int flag = 0;
        if(session.getData("loginType").equals("referee")){
           flag = 1;
        }else if(session.getData("loginType").equals("admin")){
           flag = 2;
        }
        return flag;
    }
}
