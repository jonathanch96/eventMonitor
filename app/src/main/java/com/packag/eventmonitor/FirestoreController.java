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
import com.packag.eventmonitor.Data.FCMToken;
import com.packag.eventmonitor.Data.Penilaian;
import com.packag.eventmonitor.Data.Referee;
import com.packag.eventmonitor.Data.RefereePenilaian;
import com.packag.eventmonitor.Data.Team;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.Arrays;
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
                    double nilai_bersih = 0;
                    int jumlah_juri = 0;
                    double total_nilai = 0;
                    int division = 1;
                    int total_data = dataReferee.size();
                    double[] nilai_perjuri = new double[total_data];
                    for (RefereePenilaian trp : dataReferee) {
                        total_nilai += trp.getGrand_total();
                        nilai_perjuri[jumlah_juri] = trp.getGrand_total();
                        jumlah_juri++;
                    }
                    Arrays.sort(nilai_perjuri);
                    if (total_data == 9) {
                        division = total_data - 4;
                        total_nilai -= nilai_perjuri[0];
                        total_nilai -= nilai_perjuri[1];
                        total_nilai -= nilai_perjuri[total_data - 1];
                        total_nilai -= nilai_perjuri[total_data - 2];
                    } else if (total_data == 7 || total_data == 5) {
                        division = total_data - 2;
                        total_nilai -= nilai_perjuri[0];
                        total_nilai -= nilai_perjuri[total_data - 1];
                    } else {
                        total_nilai = 0;
                    }
                    nilai_bersih = total_nilai / (double) division;

                    Map<String, Double> dataToSave = new HashMap<>();
                    dataToSave.put("nilai_bersih", nilai_bersih);
                    db.collection("events").document(eventId).collection("team").document(teamId)
                            .set(dataToSave, SetOptions.merge());
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
