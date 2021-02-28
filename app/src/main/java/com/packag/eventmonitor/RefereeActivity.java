package com.packag.eventmonitor;

import androidx.annotation.NonNull;
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
import android.widget.TextView;
import android.widget.Toolbar;

import com.developer.kalert.KAlertDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.packag.eventmonitor.Adapter.AdapterListTeam;
import com.packag.eventmonitor.Data.Events;
import com.packag.eventmonitor.Data.Penilaian;
import com.packag.eventmonitor.Data.PenilaianTraditional;
import com.packag.eventmonitor.Data.Referee;
import com.packag.eventmonitor.Data.RefereePenilaian;
import com.packag.eventmonitor.Data.Team;
import com.packag.eventmonitor.Util.Session;
import com.packag.eventmonitor.Util.Setting;

import java.util.Vector;

import javax.annotation.Nullable;

public class RefereeActivity extends AppCompatActivity {
    private Toolbar mTopToolbar;
    Session session;
    ListView lv_ar_listTeam;
    Vector<Team> dataTeam;
    FirebaseFirestore db;
    Events events;
    Referee referee;
    private static int REQUEST = 9;
    TextView tv_ar_event_code;
    TextView tv_ar_themes;
    TextView tv_ar_total_team;
    TextView tv_ar_status;
    TextView tv_ar_referee_name;
    FirestoreController fc;

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
        fc = new FirestoreController();


        session = new Session(this.getApplicationContext());
        fc.generateToken("referee",session.getData("refereeId"));
        lv_ar_listTeam = findViewById(R.id.lv_ar_listTeam);
        events = new Events();
        tv_ar_event_code = findViewById(R.id.tv_ar_event_code);
        tv_ar_themes = findViewById(R.id.tv_ar_themes);
        tv_ar_total_team = findViewById(R.id.tv_ar_total_team);
        tv_ar_status = findViewById(R.id.tv_ar_status);
        tv_ar_referee_name = findViewById(R.id.tv_ar_referee_name);
        Setting.checkAppVersion(RefereeActivity.this);

    }

    private void fetchData() {
        final DocumentReference eventRef = db.collection("events")
                .document(session.getData("eventId"));
        eventRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        events = document.toObject(Events.class);
                        events.setKey(document.getId());
                        tv_ar_event_code.setText("Kode Event : " + events.getCode());
                        tv_ar_themes.setText("Tema : " + events.getThemes());
                        tv_ar_total_team.setText("Total Team : " + events.getTotal_team());
                        if (events.getStatus() == 1) {
                            tv_ar_status.setText("Status : Open");
                        }
                    }

                }
            }
        });
        final DocumentReference refereeRef = eventRef
                .collection("referee")
                .document(session.getData("refereeId"));
//        refereeRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    DocumentSnapshot document = task.getResult();
//                    if (document.exists()) {
//                        referee = document.toObject(Referee.class);
//                        referee.setKey(document.getId());
//                        tv_ar_referee_name.setText("Nama Juri : Juri "+referee.getNumber()+" - "+referee.getName());
//                        setupBadge();
//                    }
//
//                }
//            }
//        });
        //realtime
        refereeRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@androidx.annotation.Nullable DocumentSnapshot value, @androidx.annotation.Nullable FirebaseFirestoreException error) {
                if (value.exists()) {
                    referee = value.toObject(Referee.class);
                    referee.setKey(value.getId());
                    tv_ar_referee_name.setText("Nama Juri : Juri " + referee.getNumber() + " - " + referee.getName());
                    setupBadge();
                }
            }
        });

        final CollectionReference teamRef = eventRef
                .collection("team");
        teamRef.orderBy("no_urut").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                dataTeam = new Vector<Team>();
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    final Team teamClass = document.toObject(Team.class);
                    teamClass.setKey(document.getId());

                    teamRef.document(document.getId())
                            .collection("penilaian")
                            .document(session.getData("refereeId"))
                            .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                @Override
                                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                                    if (documentSnapshot.exists()) {
                                        RefereePenilaian penilaian = documentSnapshot.toObject(RefereePenilaian.class);
                                        //PenilaianTraditional penilaian = documentSnapshot.toObject(PenilaianTraditional.class);
                                        penilaian.setKey(documentSnapshot.getId());
                                        teamClass.setPenilaian(penilaian);
                                    }

                                    lv_ar_listTeam.setAdapter(new AdapterListTeam(getApplicationContext(), dataTeam));

                                }
                            });

                    dataTeam.add(teamClass);
                    lv_ar_listTeam.setAdapter(new AdapterListTeam(getApplicationContext(), dataTeam));
                }


            }
        });

    }

    private void setListener() {
        lv_ar_listTeam.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                themes.add("Naga");
//                themes.add("Barongsai Tradisional");
//                themes.add("Barongsai Taolu Bebas");
//                themes.add("Pekingsai");
                Intent intent = new Intent(RefereeActivity.this, Scoring.class);
                if (events.getThemes().equals("Naga")) {
                    intent = new Intent(RefereeActivity.this, ScoringNagaActivity.class);
                } else if (events.getThemes().equals("Barongsai Taolu Bebas")) {
                    intent = new Intent(RefereeActivity.this, ScoringTaoluActivity.class);
                } else if (events.getThemes().equals("Pekingsai")) {
                    intent = new Intent(RefereeActivity.this, ScoringPekingsaiActivity.class);
                } else {
                    intent = new Intent(RefereeActivity.this, ScoringTraditionalActivity.class);
                }
                intent.putExtra("teamId", dataTeam.get(i).getKey());

                startActivityForResult(intent, REQUEST);

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        final MenuItem menuItem = menu.findItem(R.id.action_chat);
        View actionView = menuItem.getActionView();
        textCartItemCount = (TextView) actionView.findViewById(R.id.cart_badge);


        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(menuItem);
            }
        });
        fetchData();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_favorite) {
            session.removeData("loginType");
            Intent i = new Intent(RefereeActivity.this, MainActivity.class);
            fc.removeToken(fc.getToken());
            startActivity(i);
            this.finish();
            return true;
        } else if (id == R.id.action_chat) {
            Intent i = new Intent(RefereeActivity.this, ChatActivity.class);
            i.putExtra("eventId", events.getKey());
            i.putExtra("userId", referee.getKey());
            i.putExtra("name", "Juri " + referee.getNumber() + " (" + events.getCode() + ")" + " - " + referee.getName());
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST) {
            new KAlertDialog(RefereeActivity.this, KAlertDialog.SUCCESS_TYPE)
                    .setTitleText("Good job!")
                    .setContentText(data.getStringExtra("msg"))
                    .show();
        }
    }

    TextView textCartItemCount;

    private void setupBadge() {
        //get unread message
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Log.d("debug", "setupBadge eventId : " + events.getKey());
        Log.d("debug", "setupBadge destUserId  : " + referee.getKey());
        db.collection("chats")
                .whereEqualTo("eventId", events.getKey())
                .whereEqualTo("destUserId", referee.getKey())
                .whereEqualTo("userId", "admin")
                .whereEqualTo("is_read", false).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@androidx.annotation.Nullable QuerySnapshot value, @androidx.annotation.Nullable FirebaseFirestoreException error) {
                int unread_message = 0;
                for (QueryDocumentSnapshot doc : value) {
                    unread_message++;

                }
                if (unread_message == 0) {
                    // textCartItemCount.setVisibility(View.GONE);
                    textCartItemCount.setText(unread_message + "");
                } else {
                    textCartItemCount.setVisibility(View.VISIBLE);
                    textCartItemCount.setText(unread_message + "");
                }
            }
        });


    }

}
