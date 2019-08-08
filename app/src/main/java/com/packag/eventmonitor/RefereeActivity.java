package com.packag.eventmonitor;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toolbar;

import com.developer.kalert.KAlertDialog;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.packag.eventmonitor.Adapter.AdapterListTeam;
import com.packag.eventmonitor.Data.Team;
import com.packag.eventmonitor.Util.Session;

import java.util.Vector;

import javax.annotation.Nullable;

public class RefereeActivity extends AppCompatActivity {
    private Toolbar mTopToolbar;
    Session session;
    ListView lv_ar_listTeam;
    Vector<Team> dataTeam;
    FirebaseFirestore db;
    private static int REQUEST = 9;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_referee);

        initializeComponent();
        setListener();
    }
    private void initializeComponent() {
        dataTeam = new Vector<Team>();
        db = FirebaseFirestore.getInstance();
        session = new Session(this.getApplicationContext());
        lv_ar_listTeam = findViewById(R.id.lv_ar_listTeam);
        fetchData();

    }
    private void fetchData(){
         final CollectionReference team = db.collection("events")
                .document(session.getData("eventId"))
                .collection("team");
        team.orderBy("no_urut").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                dataTeam=new Vector<Team>();
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    Team teamClass = document.toObject(Team.class);
                    teamClass.setKey(document.getId());
                    dataTeam.add(teamClass);
                }

                lv_ar_listTeam.setAdapter(new AdapterListTeam(getApplicationContext(),dataTeam));
            }
        });

    }
    private void setListener() {
        lv_ar_listTeam.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(RefereeActivity.this,Scoring.class);
                intent.putExtra("teamId",dataTeam.get(i).getKey());
                startActivityForResult(intent,REQUEST);

            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_favorite) {
            session.removeData("loginType");
            Intent i = new Intent(RefereeActivity.this,MainActivity.class);
            startActivity(i);
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode == Activity.RESULT_OK&&requestCode== REQUEST){
            new KAlertDialog(RefereeActivity.this, KAlertDialog.SUCCESS_TYPE)
                    .setTitleText("Good job!")
                    .setContentText(data.getStringExtra("msg"))
                    .show();
        }
    }
}
