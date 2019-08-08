package com.packag.eventmonitor;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.packag.eventmonitor.Data.Events;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.Vector;

public class AddEventActivity extends AppCompatActivity {
    TextView tv_ae_tanggal_event_btn;
    EditText et_ae_themes;
    EditText et_ae_judges;
    Button btn_ae_submit;
    DatePickerDialog.OnDateSetListener mDateSetListener;
    SimpleDateFormat formatter;
    Date todayDate;
    FirebaseFirestore db;
    ProgressDialog loadingDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        initializeComponent();
        setListener();
    }

    private void initializeComponent() {
        tv_ae_tanggal_event_btn =findViewById(R.id.tv_ae_tanggal_event_btn);
        et_ae_themes = findViewById(R.id.et_ae_themes);
        et_ae_judges = findViewById(R.id.et_ae_judges);
        btn_ae_submit = findViewById(R.id.btn_ae_submit);
        todayDate = new Date();
        formatter = new SimpleDateFormat("dd/MM/yyyy");
        db = FirebaseFirestore.getInstance();
        loadingDialog = new ProgressDialog(this);
        loadingDialog.setTitle("Processing your Request");
        loadingDialog.setMessage("Please Wait a second...");

    }
    private void setListener() {
        tv_ae_tanggal_event_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal =Calendar.getInstance();
                int year =cal.get(Calendar.YEAR);
                int month =cal.get(Calendar.MONTH);
                int date =cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog  dialog = new DatePickerDialog(AddEventActivity.this
                        ,R.style.Theme_AppCompat_Light_Dialog,mDateSetListener,year,month,date);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                dialog.show();
            }
        });
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                // String date = year+"-"+String.format("%02d", month+1)+"-"+dayOfMonth;
                String date = String.format("%02d",dayOfMonth)+"/"+String.format("%02d", month+1)+
                        "/"+year;
                tv_ae_tanggal_event_btn.setText(date);
            }
        };
        btn_ae_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateInput()){
                    loadingDialog.show();
                    final String input_code = getUniqueCode();
                    String input_date = tv_ae_tanggal_event_btn.getText().toString();
                    String input_themes = et_ae_themes.getText().toString();
                    int input_total_referee = Integer
                            .parseInt(et_ae_judges.getText().toString());
                    db.collection("events")
                            .add(new Events(input_code,input_date,1,input_themes
                                    ,input_total_referee))
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Intent return_i = new Intent();
                            return_i.putExtra("msg","Event "
                                    +input_code+" Berhasil dibuat!!");
                            setResult(Activity.RESULT_OK,return_i);
                            loadingDialog.hide();
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("debug", "Error writing document", e);
                        }
                    });
                }
            }
        });
    }
    public boolean validateInput(){
        boolean flag = false;

        flag=true;

        return flag;

    }
    public String randomString(int n){
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghjkmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder builder = new StringBuilder(n);

        for (int i = 0; i < n; i++) {
            builder.append(alphabet.charAt(random.nextInt(alphabet.length())));
        }

        return builder.toString();

    }
    String code;
    boolean exist = false;
    public String getUniqueCode(){

        final Vector<Events> list_event = new Vector<Events>();
        do {
            exist = false;
            code = randomString(5);
            CollectionReference events = db.collection("events");
            events.whereEqualTo("status",1)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Events events = document.toObject(Events.class);
                                    list_event.add(events);

                                }
                                for(int i = 0 ; i< list_event.size();i++){
                                    if(list_event.get(i).getCode().equals(code)){
                                        exist = true;
                                    }
                                }
                            }
                        }
                    });

        }while (exist);


        return code;
    }
}
