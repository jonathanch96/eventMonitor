package com.packag.eventmonitor;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.developer.kalert.KAlertDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.packag.eventmonitor.Data.Users;
import com.packag.eventmonitor.Util.Session;
import com.packag.eventmonitor.Util.Setting;

public class AdminLoginActivity extends AppCompatActivity {
    EditText et_al_username;
    EditText et_al_password;
    Button btn_al_login;
    TextView tv_al_error;
    Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);
        initializeComponent();
        setListener();
    }
    private void initializeComponent() {
        et_al_username = findViewById(R.id.et_al_username);
        et_al_password = findViewById(R.id.et_al_password);
        btn_al_login = findViewById(R.id.btn_al_login);
        tv_al_error = findViewById(R.id.tv_al_error);
        session = new Session(this.getApplicationContext());
        Setting.checkAppVersion(AdminLoginActivity.this);
    }
    private void setListener() {
        btn_al_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_al_error.setText("");
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                db.collection("users")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Users users = document.toObject(Users.class);
                                        if(et_al_username.getText().toString().equals(users.getUsername())
                                                && et_al_password.getText().toString().equals(users.getPassword())){
                                            session.setData("loginType","admin");

                                            Intent i = new Intent(AdminLoginActivity.this,AdminActivity.class);
                                            startActivity(i);
                                            finish();
                                        }else{

                                            new KAlertDialog(AdminLoginActivity.this, KAlertDialog.ERROR_TYPE)
                                                    .setTitleText("Oops...")
                                                    .setContentText("Invalid Username / Password!")
                                                    .show();

                                        }
                                    }
                                } else {
                                    Log.d("Debug", "Error getting documents.", task.getException());
                                }
                            }
                        });


            }
        });
    }
}
