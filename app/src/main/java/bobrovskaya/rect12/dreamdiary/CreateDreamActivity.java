package bobrovskaya.rect12.dreamdiary;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Date;

import bobrovskaya.rect12.dreamdiary.data.DreamDbHelper;
import bobrovskaya.rect12.dreamdiary.data.DreamContract.DreamsTable;
import android.Manifest;

/**
 * Created by rect on 12/2/17.
 */

public class CreateDreamActivity extends AppCompatActivity {

    private MediaRecorder mediaRecorder;
    private ImageButton startButton;
    private ImageButton pauseButton;
    private String filePath;
    private Boolean isRecording = false;

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted ) finish();

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_dream_activity);

        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
        Toolbar toolbar = findViewById(R.id.toolbar_create_dream);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        startButton = findViewById(R.id.createDreamMicrophoneButtonStart);
        pauseButton = findViewById(R.id.createDreamMicrophoneButtonPause);
        pauseButton.setEnabled(false);

        filePath = getExternalCacheDir().getAbsolutePath();
        filePath += "/audiorecordtest.3gp";
        Log.d("MYTAG", filePath);

        initializeMediaRecord();

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRecording) {
                    stopAudioRecording();
                    isRecording = false;
                }
                else {
                    if (mediaRecorder == null) {
                        initializeMediaRecord();
                    }
                    startAudioRecording();
                    isRecording = true;
                }
            }
        });


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dream_create, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.add_db_dream) {
            if (!(isRecording)) {
                TextView nameView = findViewById(R.id.createDreamNameText);
                TextView descriptionView = findViewById(R.id.createDreamDreamText);

                String name = nameView.getText().toString();
                String description = descriptionView.getText().toString();
                Date date = new Date();

                DreamDbHelper mDbHelper = new DreamDbHelper(this);

                SQLiteDatabase db = mDbHelper.getWritableDatabase();

                ContentValues values = new ContentValues();
                values.put(DreamsTable.COLUMN_NAME, name);
                values.put(DreamsTable.COLUMN_DESCRIPTION, description);
                values.put(DreamsTable.COLUMN_DATE, date.getTime());
                values.put(DreamsTable.COLUMN_AUDIO_PATH, filePath);

                long newRowId = db.insert(DreamsTable.TABLE_NAME, null, values);
                if (newRowId == -1) {
                    // Если ID  -1, значит произошла ошибка
                    Toast.makeText(this, "Error while creating dream", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Dream added " + newRowId, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(CreateDreamActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }

        }

        return super.onOptionsItemSelected(item);
    }


    private void startAudioRecording(){
        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        startButton.setImageResource(android.R.drawable.ic_notification_overlay);
        pauseButton.setEnabled(true);
    }

    private void stopAudioRecording(){
        if(mediaRecorder != null){
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
        }
        pauseButton.setEnabled(false);
        startButton.setImageResource(android.R.drawable.ic_btn_speak_now);
    }

    private void initializeMediaRecord(){
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setOutputFile(filePath);
    }





}
