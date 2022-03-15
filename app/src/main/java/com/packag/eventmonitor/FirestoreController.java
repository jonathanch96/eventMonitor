package com.packag.eventmonitor;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.packag.eventmonitor.Adapter.AdapterListReferee;
import com.packag.eventmonitor.Data.Events;
import com.packag.eventmonitor.Data.FCMToken;
import com.packag.eventmonitor.Data.Penilaian;
import com.packag.eventmonitor.Data.Referee;
import com.packag.eventmonitor.Data.RefereePenilaian;
import com.packag.eventmonitor.Data.Team;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class FirestoreController {
    FirebaseFirestore db;
    int counter;
    String token;
    public FirestoreController() {
        db = FirebaseFirestore.getInstance();
    }

    public void deleteTeam(String eventId, String teamId) {
        db.collection("events").document(eventId)
                .collection("team")
                .document(teamId).delete();
    }
    public void recalculateAllData(final String eventId){

        final DocumentReference eventRef= db.collection("events").document(eventId);
        final CollectionReference teamRef =  eventRef.collection("team");
        teamRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(QueryDocumentSnapshot qds :task.getResult()){
                    final Team team = qds.toObject(Team.class);
                    team.setKey(qds.getId());
                    CollectionReference refereeRef = teamRef.document(qds.getId())
                            .collection("penilaian");
                    refereeRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task2) {
                            for(QueryDocumentSnapshot qds2 :task2.getResult()){
                                final RefereePenilaian rp = qds2.toObject(RefereePenilaian.class);
                                rp.setKey(qds2.getId());
                                updateRefereePenilaianSummary(eventId,team.getKey(),rp.getKey());
                            }
                        }
                    });

                }
            }
        });
    }
    public void setDefaultPenilaian(String eventId, String teamId, final String refereeId){
        final DocumentReference eventRef= db.collection("events").document(eventId);
        final DocumentReference teamRef = eventRef.collection("team").document(teamId);
        eventRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot ds = task.getResult();
                    final Events events = ds.toObject(Events.class);
                    events.setKey(ds.getId());
                    teamRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                Vector<Penilaian> taolu_field = new Vector<Penilaian>();
                                taolu_field.add(new Penilaian(0,"=",
                                        null,"et_amdp_taolu_kesulitan"
                                        ,"et_amdp_taolu_kesulitan"));
                                taolu_field.add(new Penilaian(0,"+",
                                        null,"et_amdp_taolu_n1"
                                        ,"et_amdp_taolu_n1"));
                                taolu_field.add(new Penilaian(0,"+",
                                        null,"et_amdp_taolu_n2"
                                        ,"et_amdp_taolu_n2"));
                                taolu_field.add(new Penilaian(0,"+",
                                        null,"et_amdp_taolu_n3"
                                        ,"et_amdp_taolu_n3"));
                                taolu_field.add(new Penilaian(0,"+",
                                        null,"et_amdp_taolu_n4"
                                        ,"et_amdp_taolu_n4"));
                                taolu_field.add(new Penilaian(0,"+",
                                        null,"et_amdp_taolu_n5"
                                        ,"et_amdp_taolu_n5"));
                                taolu_field.add(new Penilaian(0,"+",
                                        null,"et_amdp_taolu_n6"
                                        ,"et_amdp_taolu_n6"));
                                taolu_field.add(new Penilaian(0,"+",
                                        null,"et_amdp_taolu_n7"
                                        ,"et_amdp_taolu_n7"));
                                taolu_field.add(new Penilaian(0,"+",
                                        null,"et_amdp_taolu_n8"
                                        ,"et_amdp_taolu_n8"));
                                taolu_field.add(new Penilaian(0,"+",
                                        null,"et_amdp_taolu_n9"
                                        ,"et_amdp_taolu_n9"));
                                taolu_field.add(new Penilaian(0,"-",
                                        null,"et_ap_taolu_p1"
                                        ,"et_ap_taolu_p1"));
                                taolu_field.add(new Penilaian(0,"-",
                                        null,"et_ap_taolu_p2"
                                        ,"et_ap_taolu_p1"));
                                taolu_field.add(new Penilaian(0,"-",
                                        null,"et_ap_taolu_p3"
                                        ,"et_ap_taolu_p1"));
                                taolu_field.add(new Penilaian(0,"-",
                                        null,"et_ap_taolu_p4"
                                        ,"et_ap_taolu_p1"));


                                DocumentSnapshot ds = task.getResult();
                                Team team = ds.toObject(Team.class);
                                team.setKey(ds.getId());

                                Vector<Penilaian> looping_field = new Vector<Penilaian>();
                                if(events.getThemes().equals("Barongsai Taolu Bebas")){
                                    looping_field = taolu_field;
                                }
                                for (final Penilaian p:looping_field) {
                                    teamRef.collection("penilaian")
                                            .document(refereeId).collection("field")
                                            .document(p.getKey()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if(task.isSuccessful()){
                                                DocumentSnapshot ds = task.getResult();
                                                if(!ds.exists()){
                                                    teamRef.collection("penilaian")
                                                            .document(refereeId).collection("field")
                                                            .document(p.getKey()).set(p,SetOptions.merge());
                                                }
                                            }
                                        }
                                    });
                                }


                            }
                        }
                    });

                }
            }
        });
    }
    public void updateRefereePenilaianSummary(final String eventId, final String teamId, final String refereeId){
        final CollectionReference referee = db.collection("events").document(eventId)
                .collection("team").document(teamId).collection("penilaian")
                ;
        final CollectionReference field = referee.document(refereeId).collection("field");
        field.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                final Map<String, Double> dataToSave = new HashMap<>();
                Vector<Penilaian> penilaians = new Vector<Penilaian>();
                BigDecimal grand_total = new BigDecimal(0);
                BigDecimal total_nilai = new BigDecimal(0);
                BigDecimal total_potongan = new BigDecimal(0);
                for(QueryDocumentSnapshot qds:task.getResult()) {
                    if (qds.exists()) {
                        Penilaian p = qds.toObject(Penilaian.class);
                        p.setKey(qds.getId());
                        penilaians.add(p);
                        if(p.getType().equals("-")){
                            total_potongan = total_potongan.add(new BigDecimal(p.getNilai()));
                        }else if(p.getType().equals("+")){
                            total_nilai = total_nilai.add(new BigDecimal(p.getNilai()));
                        }
                    }
                }
                total_nilai = total_nilai.setScale(2, RoundingMode.DOWN);
                total_potongan = total_potongan.setScale(2, RoundingMode.DOWN);
                grand_total = total_nilai.subtract(total_potongan);
                dataToSave.put("total_nilai",total_nilai.doubleValue());
                dataToSave.put("total_potongan",total_potongan.doubleValue());
                dataToSave.put("grand_total",grand_total.doubleValue());
                referee.document(refereeId).set(dataToSave,SetOptions.merge());
                recalculateNilaiBersih(eventId,teamId);
            }
        });
    }
    public void updateSingleNilai(final String eventId, final String teamId, final String formId,
                                  final Double nilai,final String type){

        final CollectionReference referee = db.collection("events").document(eventId)
                .collection("team").document(teamId).collection("penilaian");
        referee.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                final Map<String, Object> dataToSave = new HashMap<>();
                dataToSave.put("form_id",formId);
                dataToSave.put("keterangan",null);
                dataToSave.put("nilai",nilai);
                dataToSave.put("type",type);
                for(QueryDocumentSnapshot qds:task.getResult()){
                    if(qds.exists()){
                        RefereePenilaian rp = qds.toObject(RefereePenilaian.class);
                        rp.setKey(qds.getId());
                        referee.document(qds.getId()).collection("field")
                                .document(formId).set(dataToSave,SetOptions.merge());
                        updateRefereePenilaianSummary(eventId,teamId,qds.getId());
                    }
                }
            }
        });
    }
    public void sendMessage(String message, String title, String token, Context ctx) {
        final String appToken = "AAAAV01RqJE:APA91bHL1AHBltjXAYjRYDzHPAuJj4h8Hhaifaw_9-K8VMBoWgMokIEQhDUQTRG7Xj5cCtR_yz63WPI0cPALaw-52i6rX36pXZPZB1WzuylgsW22UIMkg3XhQMVV2JQmhAINpU4GKSx2";
        String postUrl = "https://fcm.googleapis.com/fcm/send";
        RequestQueue requestQueue = Volley.newRequestQueue(ctx);

        JSONObject postData = new JSONObject();
        JSONObject postData2 = new JSONObject();
        try {
            postData.put("to", token);
            postData2.put("body",message);
            postData2.put("title",title);
            postData.put("notification", postData2);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, postUrl, postData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Key=" + appToken);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };


        requestQueue.add(jsonObjectRequest);

    }

    public String getToken(){
        return this.token;
    }
    public void removeToken(final String token){
        db.collection("fcm_token").document(token).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("debug", "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("debug", "Error deleting document", e);
                    }
                });
    }

    public void generateToken(final String type,final String id) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("debug", "Fetching FCM registration token failed", task.getException());

                            return;
                        }

                        // Get new FCM registration token
                        token = task.getResult();

                        if(type.equals("admin")){
                            final FCMToken fcm_token = new FCMToken();
                            fcm_token.setToken(token);
                            fcm_token.setType("admin");
                            fcm_token.setId(null);
                            db.collection("fcm_token").document(token).set(fcm_token);
                        }else{
                            db.collection("fcm_token").whereEqualTo("id",id)
                                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        if(task.getResult().size()==0){
                                            //not exists
                                            FCMToken fcm_token = new FCMToken();
                                            fcm_token.setType("referee");
                                            fcm_token.setId(id);
                                            fcm_token.setToken(token);
                                            db.collection("fcm_token").document(token).set(fcm_token);
                                        }

                                    } else {
                                        Log.d("debug", "Error getting documents: ", task.getException());
                                    }
                                }
                            });

                        }



                        // Log and toast
                        Log.d("debug", "token=" + token);
                    }
                });
    }

    public void recalculateNilaiBersih(final String eventId, final String teamId) {
        final Vector<RefereePenilaian> dataReferee = new Vector<RefereePenilaian>();
        db.collection("events").document(eventId).collection("team")
                .document(teamId).collection("penilaian").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot d2 : task.getResult()) {
                        if (d2.exists()) {
                            RefereePenilaian rp = d2.toObject(RefereePenilaian.class);
                            rp.setKey(d2.getId());
                            dataReferee.add(rp);
                        }
                    }
                    BigDecimal nilai_bersih = new BigDecimal(0);
                    int jumlah_juri = 0;
                    BigDecimal total_nilai =  new BigDecimal(0);
                    int division = 1;
                    int total_data = dataReferee.size();
                    //double[] nilai_perjuri = new double[total_data];
                    Vector<BigDecimal> nilai_perjuri = new Vector<BigDecimal>();
                    for (RefereePenilaian trp : dataReferee) {

                        total_nilai = total_nilai.add(trp.getGrand_total_bd());

                        nilai_perjuri.add(trp.getGrand_total_bd());

                        if(teamId.equals("NUtMIA5SPUsMmSDVVwsi")){
                            Log.d("debug",total_nilai.doubleValue()+" total");
                            Log.d("debug",trp.getGrand_total_bd()+" nilai juri");
                        }
                        //nilai_perjuri[jumlah_juri] = trp.getGrand_total();
                        jumlah_juri++;

                    }
                    Collections.sort(nilai_perjuri);
                    //Arrays.sort(nilai_perjuri);
                    if (total_data == 9) {
                        division = total_data - 4;
                        total_nilai = total_nilai.subtract(nilai_perjuri.get(0));

                        total_nilai = total_nilai.subtract(nilai_perjuri.get(1));

                        total_nilai = total_nilai.subtract(nilai_perjuri.get(total_data - 1));

                        total_nilai = total_nilai.subtract(nilai_perjuri.get(total_data - 2));


                    } else if (total_data == 7 || total_data == 5) {
                        division = total_data - 2;
                        total_nilai = total_nilai.subtract(nilai_perjuri.get(0));

                        total_nilai = total_nilai.subtract(nilai_perjuri.get(total_data - 1));

                    } else {
                        total_nilai = new BigDecimal(0);
                    }
                    nilai_bersih = total_nilai.divide(new BigDecimal((double)division), MathContext.DECIMAL32);
                    if(teamId.equals("NUtMIA5SPUsMmSDVVwsi")){
                        Log.d("debug",nilai_bersih.doubleValue()+"");
                    }

                    final BigDecimal total_nilai_bersih =  nilai_bersih.setScale(2, RoundingMode.DOWN);

                    final Map<String, Double> dataToSave = new HashMap<>();
                    dataToSave.put("nilai_bersih", total_nilai_bersih.doubleValue());
                    db.collection("events").document(eventId)
                            .collection("team").document(teamId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot qds = task.getResult();
                            if(qds.exists()){
                                Team temp_team = qds.toObject(Team.class);
                                temp_team.setKey(qds.getId());
                                dataToSave.put("pengurangan_nb",temp_team.getPengurangan_nb());
                                dataToSave.put("total_nilai",total_nilai_bersih.subtract(new BigDecimal(temp_team.getPengurangan_nb())).doubleValue());
                                db.collection("events").document(eventId).collection("team").document(teamId)
                                        .set(dataToSave, SetOptions.merge());
                            }

                        }
                    });

                }
            }
        });

    }

    public void refereeNumbering(final String eventId) {
        final Vector<Referee> dataReferee = new Vector<Referee>();
        counter = 0;
        db.collection("events").document(eventId).collection("referee")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot d2 : task.getResult()) {
                        if (d2.exists()) {
                            Referee referee = d2.toObject(Referee.class);
                            referee.setKey(d2.getId());
                            Log.d("debug", referee.getNumber() + " TEST");
                            if (d2.get("number") != null && referee.getNumber() != 0) {
                                counter = referee.getNumber();

                            } else {
                                counter++;
                                referee.setNumber(counter);

                                db.collection("events").document(eventId).
                                        collection("referee").document(d2.getId()).set(referee, SetOptions.merge());
                            }
                            dataReferee.add(referee);


                        }

                    }
                }
            }
        });


    }

    public void recalculateTotalTeam(final String eventId) {
        final Vector<Team> teams = new Vector<Team>();
        CollectionReference teamRef = db.collection("events")
                .document(eventId)
                .collection("team");
        teamRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot d2 : task.getResult()) {
                        Team team = d2.toObject(Team.class);

                        teams.add(team);
                    }
                    db.collection("events")
                            .document(eventId)
                            .update("total_team", teams.size());
                }
            }
        });

    }

    public void addTeam(String eventId, Team team) {
        CollectionReference teamRef = db.collection("events")
                .document(eventId)
                .collection("team");
        teamRef.add(team);
    }

    public void updateTeam(String eventId, String teamId, Team team) {
        DocumentReference teamRef = db.collection("events")
                .document(eventId)
                .collection("team")
                .document(teamId);
        Map<String, Object> dataToSave = new HashMap<>();
        dataToSave.put("team_name", team.getTeam_name());
        dataToSave.put("no_urut", team.getNo_urut());
        teamRef.set(dataToSave, SetOptions.merge());
    }

    public void updateEventStatus(String eventId, int status) {
        db.collection("events")
                .document(eventId)
                .update("status", status);
    }
}
