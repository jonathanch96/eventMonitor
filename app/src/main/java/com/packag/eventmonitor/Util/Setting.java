package com.packag.eventmonitor.Util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.developer.kalert.KAlertDialog;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.packag.eventmonitor.Data.AppConfig;

public class Setting {
    public static String appVersion="1.0";
    public static String updateLink = "https://drive.google.com/open?id=1pMYX8ZrXkWc2B8BkXx-R9cMNJzyve-6p";

    public static void checkAppVersion(final Context ctx) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("appConfig")
                .document("xpdrOpyZ0tg55EyEjoKa").get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        AppConfig appConfig = new AppConfig();
                        if (documentSnapshot.exists()) {
                            appConfig = documentSnapshot.toObject(AppConfig.class);
                        }
                        if (!appConfig.getVersion().equals(Setting.appVersion)) {
                            String message = "Notes : \n" + appConfig.getMessage().replace("[enter]"
                                    , "\n");
                            new KAlertDialog(ctx, KAlertDialog.WARNING_TYPE)
                                    .setTitleText("New Update!")
                                    .setContentText(message)
                                    .setConfirmText("Update Sekarang")
                                    .setConfirmClickListener(new KAlertDialog.KAlertClickListener() {
                                        @Override
                                        public void onClick(KAlertDialog kAlertDialog) {

                                            Intent openURL = new Intent(Intent.ACTION_VIEW);
                                            openURL.setData(Uri.parse(Setting.updateLink));
                                            ctx.startActivity(openURL);

                                        }
                                    })
                                    .show();

                        }
                    }
                });
    }
}
