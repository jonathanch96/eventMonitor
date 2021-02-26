package com.packag.eventmonitor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.packag.eventmonitor.Adapter.AdapterListReferee;
import com.packag.eventmonitor.Data.PenilaianTraditional;
import com.packag.eventmonitor.Data.Referee;
import com.packag.eventmonitor.Data.Team;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;

import javax.annotation.Nullable;



public class DetailPenilaianActivity extends AppCompatActivity {
    String eventId;
    String teamId;
    RecyclerView rv_adp_list_referee;
    TextView tv_adp_team_name;
    TextView tv_adp_no_urut;
    Vector<Referee> dataReferee;
    Vector<PenilaianTraditional> dataPenilaian;
    FirebaseFirestore db;

    FloatingActionButton fab_adp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_penilaian);
        System.setProperty("org.apache.poi.javax.xml.stream.XMLInputFactory", "com.fasterxml.aalto.stax.InputFactoryImpl");
        System.setProperty("org.apache.poi.javax.xml.stream.XMLOutputFactory", "com.fasterxml.aalto.stax.OutputFactoryImpl");
        System.setProperty("org.apache.poi.javax.xml.stream.XMLEventFactory", "com.fasterxml.aalto.stax.EventFactoryImpl");
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        getIntentData();
        initData();
        getData();
        setListener();
    }
    public void initData(){
        rv_adp_list_referee = findViewById(R.id.rv_adp_list_referee);
        tv_adp_team_name = findViewById(R.id.tv_adp_team_name);
        tv_adp_no_urut = findViewById(R.id.tv_adp_no_urut);
        fab_adp = findViewById(R.id.fab_adp);
        dataReferee=new Vector<Referee>();
        dataPenilaian=new Vector<PenilaianTraditional>();
        db = FirebaseFirestore.getInstance();

    }
    public void getData(){
        final DocumentReference eventRef =
                db.collection("events").document(eventId);
                        ;

            eventRef.collection("team").document(teamId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        Team temp_team = task.getResult().toObject(Team.class);
                        tv_adp_team_name.setText("Team : "+temp_team.getTeam_name());
                        tv_adp_no_urut.setText("No Urut : "+temp_team.getNo_urut());
                    }
                }
            });
            eventRef.collection("team").document(teamId)
                .collection("penilaian").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                dataReferee = new Vector<Referee>();
                dataPenilaian = new Vector<PenilaianTraditional>();
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    final PenilaianTraditional penilaian = document.toObject(PenilaianTraditional.class);
                    penilaian.setKey(document.getId());
                    dataPenilaian.add(penilaian);
                    eventRef.collection("referee").document(document.getId()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                            if(documentSnapshot.exists()) {
                                final Referee refereeClass = documentSnapshot.toObject(Referee.class);
                                refereeClass.setKey(documentSnapshot.getId());
                                dataReferee.add(refereeClass);
                                rv_adp_list_referee.setAdapter(new AdapterListReferee(DetailPenilaianActivity.this,dataReferee,eventId,teamId));
                            }
                        }
                    });

                }
                // use this setting to improve performance if you know that changes
                // in content do not change the layout size of the RecyclerView
                rv_adp_list_referee.setHasFixedSize(true);

                // use a linear layout manager
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(DetailPenilaianActivity.this);
                rv_adp_list_referee.setLayoutManager(layoutManager);
                rv_adp_list_referee.setAdapter(new AdapterListReferee(DetailPenilaianActivity.this,dataReferee,eventId,teamId));
            }
        });
    }
    public void getIntentData(){
        Intent intent = getIntent();
        eventId = intent.getStringExtra("eventId");
        teamId = intent.getStringExtra("teamId");
    }
    public void setListener(){
        fab_adp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(DetailPenilaianActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    // Permission is not granted
                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(DetailPenilaianActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        // Show an explanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.
                    } else {
                        // No explanation needed; request the permission
                        ActivityCompat.requestPermissions(DetailPenilaianActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                1);

                        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                        // app-defined int constant. The callback method gets the
                        // result of the request.
                    }
                } else {
                    // Permission has already been granted
                    if (ContextCompat.checkSelfPermission(DetailPenilaianActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {

                        // Permission is not granted
                        // Should we show an explanation?
                        if (ActivityCompat.shouldShowRequestPermissionRationale(DetailPenilaianActivity.this,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            // Show an explanation to the user *asynchronously* -- don't block
                            // this thread waiting for the user's response! After the user
                            // sees the explanation, try again to request the permission.
                        } else {
                            // No explanation needed; request the permission
                            ActivityCompat.requestPermissions(DetailPenilaianActivity.this,
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    1);

                            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                            // app-defined int constant. The callback method gets the
                            // result of the request.
                        }
                    } else {
                        // Permission has already been granted
                        exportPenilaianToExcel(dataPenilaian,dataReferee);

                    }
                }

            }
        });



    }


    private void exportPenilaianToExcel(Vector<PenilaianTraditional> penilaians, Vector<Referee> referees){

         String[] columns = {
                 "Juri",
                 "N1",
                 "N2",
                 "N3",
                 "N4",
                 "N5",
                 "N6",
                 "N7",
                 "N8",
                 "N9",
                 "N10",
                 "KS1",
                 "KS2",
                 "KS3",
                 "KS4",
                 "Total Kotor",
                 "Total Pengurangan",
                 "Total Bersih"
         };
        Workbook workbook = new XSSFWorkbook(); // new HSSFWorkbook() for generating `.xls` file

        /* CreationHelper helps us create instances of various things like DataFormat,
           Hyperlink, RichTextString etc, in a format (HSSF, XSSF) independent way */
        CreationHelper createHelper = workbook.getCreationHelper();

        // Create a Sheet
        Sheet sheet = workbook.createSheet("Employee");

        // Create a Font for styling header cells
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 14);
        headerFont.setColor(IndexedColors.RED.getIndex());

        // Create a CellStyle with the font
        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        // Create a Row
        Row headerRow = sheet.createRow(0);

        // Create cells
        for(int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerCellStyle);
        }

        // Create Cell Style for formatting Date
        CellStyle dateCellStyle = workbook.createCellStyle();
        dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd-MM-yyyy"));

        // Create Other rows and cells with employees data
        int rowNum = 0;
        for (PenilaianTraditional p : penilaians){
            Row row = sheet.createRow(rowNum+1);

            row.createCell(0)
                    .setCellValue(referees.get(rowNum).getName());

            row.createCell(1)
                    .setCellValue(p.getN1());

            /*Cell dateOfBirthCell = row.createCell(2);
            dateOfBirthCell.setCellValue(employee.getDateOfBirth());
            dateOfBirthCell.setCellStyle(dateCellStyle);*/
            row.createCell(2)
                    .setCellValue(p.getN2());

            row.createCell(3)
                    .setCellValue(p.getN3());
            row.createCell(4)
                    .setCellValue(p.getN4());
            row.createCell(5)
                    .setCellValue(p.getN5());
            row.createCell(6)
                    .setCellValue(p.getN6());
            row.createCell(7)
                    .setCellValue(p.getN7());
            row.createCell(8)
                    .setCellValue(p.getN8());
            row.createCell(9)
                    .setCellValue(p.getN9());
            row.createCell(10)
                    .setCellValue(p.getN10());
            row.createCell(11)
                    .setCellValue(p.getKs1());
            row.createCell(12)
                    .setCellValue(p.getKs2());
            row.createCell(13)
                    .setCellValue(p.getKs3());
            row.createCell(14)
                    .setCellValue(p.getKs4());
            row.createCell(15)
                    .setCellValue(p.getTk());
            row.createCell(16)
                    .setCellValue(p.getP());
            row.createCell(17)
                    .setCellValue(p.getTb());
            rowNum++;
        }

        // Resize all columns to fit the content size
       /* for(int i = 0; i < columns.length; i++) {
            sheet.setColumnWidth(2, 250);
        }*/
       String path_folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/Event Monitor/";
       File directory = new File(path_folder);
       /*check Dir*/
        if (! directory.exists()){
            directory.mkdir();
            // If you require it to make the entire directory path including parents,
            // use directory.mkdirs(); here instead.
        }
        // Write the output to a file
        FileOutputStream fileOut = null;
        try {
            fileOut = new FileOutputStream
                    (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                            +"/Event Monitor/Exported Data.xlsx");
            workbook.write(fileOut);
            fileOut.close();


            // Closing the workbook
            workbook.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                +"/Event Monitor/Exported Data.xlsx");
        Uri path = Uri.fromFile(file);
        Intent pdfOpenintent = new Intent(Intent.ACTION_VIEW);
        pdfOpenintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        pdfOpenintent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION );
        pdfOpenintent.setDataAndType(path, "application/vnd.ms-excel");
        try {
            startActivity(pdfOpenintent);
        }
        catch (ActivityNotFoundException e) {

        }

    }

}
