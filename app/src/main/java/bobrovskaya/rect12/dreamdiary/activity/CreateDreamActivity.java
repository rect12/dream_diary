package bobrovskaya.rect12.dreamdiary.activity;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
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

import java.util.List;
import java.util.UUID;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import bobrovskaya.rect12.dreamdiary.ThemeChanger;
import bobrovskaya.rect12.dreamdiary.data.Dream;
import bobrovskaya.rect12.dreamdiary.R;
import bobrovskaya.rect12.dreamdiary.data.DreamDbHelper;
import bobrovskaya.rect12.dreamdiary.fragments.AudioViewFragment;

import android.Manifest;

import static android.os.Environment.DIRECTORY_MUSIC;


public class CreateDreamActivity extends AppCompatActivity {

    private MediaRecorder mediaRecorder;
    private ImageButton startButton;
    private ImageButton pauseButton;
    private String filePath;
    private Boolean isRecording = false;
    private TextView nameView;
    private TextView descriptionView;

    AudioViewFragment audioViewFragment;

    private Dream curDream = null;
    private int flagForChanging = -1;
    FragmentTransaction fTrans;

    private String rootAppPath;
    private String cacheAppPath;
    private int dreamRecordsNumber = 0;
    private File dreamCacheFolder = null;

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private boolean permissionToRecordAccepted = false;
    private String[] permissions = {Manifest.permission.RECORD_AUDIO};

    public static final String APP_PREFERENCES_THEME = "THEME";

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted) finish();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeChanger.updateTheme(this);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.create_dream_activity);

        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
//        Toolbar toolbar = findViewById(R.id.toolbar_create_dream);
//        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        cacheAppPath = getExternalCacheDir().getAbsolutePath();
        rootAppPath = getExternalFilesDir(DIRECTORY_MUSIC).getAbsolutePath();

//        audioViewFragment = (AudioViewFragment) getFragmentManager().findFragmentById(R.id.audioListFragment);
        audioViewFragment = new AudioViewFragment();
        nameView = findViewById(R.id.createDreamNameText);
        descriptionView = findViewById(R.id.createDreamDreamText);
        startButton = findViewById(R.id.createDreamMicrophoneButtonStart);
        pauseButton = findViewById(R.id.createDreamMicrophoneButtonPause);
        pauseButton.setEnabled(false);

        // задаем директорию в кэше, куда будут класться записи
        dreamCacheFolder = initUniqueDirectoryInCache(cacheAppPath);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRecording) {
                    stopAudioRecording();
                    isRecording = false;
                } else {
                    if (mediaRecorder == null) {
                        initializeMediaRecord();
                    }
                    startAudioRecording();
                    isRecording = true;
                }
            }
        });

        // 0 -- создание нового элемента
        // 1 -- просмотр элемента без права на изменение
        // 2 -- просмотр элемента с правом на изменение
        flagForChanging = intent.getIntExtra("FLAG_FOR_CHANGING", 0);
        int dreamId = -1;
        if (flagForChanging > 0) {
            dreamId = intent.getIntExtra("DREAM_ID", -1);
            DreamDbHelper dbHelper = new DreamDbHelper(this);
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            curDream = dbHelper.getDreamById(db, dreamId);

            nameView.setText(curDream.getName());
            descriptionView.setText(curDream.getDescription());


            if (flagForChanging == 1) {
                nameView.setEnabled(false);
                descriptionView.setEnabled(false);
            }
        }

        if (flagForChanging > 0) {
            Bundle bundle = new Bundle();
            bundle.putInt("dreamId", dreamId);
            audioViewFragment.setArguments(bundle);
            fTrans = getFragmentManager().beginTransaction();
            fTrans.add(R.id.audioListFragment, audioViewFragment).commit();
        }

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
        if (flagForChanging == 1) {
            menu.findItem(R.id.add_db_dream).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        DreamDbHelper mDbHelper = new DreamDbHelper(this);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        String name = nameView.getText().toString();
        String description = descriptionView.getText().toString();
        Date date = new Date();
        ArrayList<String> audioPaths = new ArrayList<>();
        Dream newDream = new Dream(-1, name, date.toString(), description, audioPaths);

        if (itemId == R.id.add_db_dream && !(isRecording)) {
            if (flagForChanging == 0) {
                createNewDream(db, newDream);

            } else {
                newDream.setDate(curDream.getDate());
                int numUpdatedRows = mDbHelper.changeItemById(db, curDream.getId(), newDream);
                if (numUpdatedRows > 0) {
                    Toast.makeText(this, "Dream succesfully updated", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(CreateDreamActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Error while updating dream", Toast.LENGTH_SHORT).show();
                }
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void createNewDream(SQLiteDatabase db, Dream newDream) {
        long newRowId = DreamDbHelper.addItem(db, newDream);
        if (newRowId == -1) {
            // Если ID  -1, значит произошла ошибка
            Toast.makeText(this, "Error while creating dream", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Dream added " + newRowId, Toast.LENGTH_SHORT).show();

            // переносим папку из кэша и обновляем пути в базе
            File newDir = initDirectoryInRoot(rootAppPath, newRowId);
            List<String> paths = moveDirFromCacheToRoot(dreamCacheFolder, newDir);
            newDream.setAudioPaths(paths);
            DreamDbHelper.changeItemById(db, newRowId, newDream);

            Intent intent = new Intent(CreateDreamActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }

    private void startAudioRecording() {
        filePath = createFilePath();
        mediaRecorder.setOutputFile(filePath);
        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        startButton.setImageResource(android.R.drawable.ic_notification_overlay);
        pauseButton.setEnabled(true);
    }

    private void stopAudioRecording() {
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.reset();
            mediaRecorder.release();
            mediaRecorder = null;
            dreamRecordsNumber++;
            Log.d("recorder", "stop recording, save file");
        }
        pauseButton.setEnabled(false);
        startButton.setImageResource(android.R.drawable.ic_btn_speak_now);
    }

    private void initializeMediaRecord() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
    }

    private String createFilePath() {
        String newFilePath = dreamCacheFolder.getAbsolutePath() + "/dreamRecord-" + dreamRecordsNumber + ".3gp";
        Log.d("SET_FILE_PATH", newFilePath);
        return newFilePath;
    }

    public File initUniqueDirectoryInCache(String rootDirPath) {
        Log.d("recording", "create folder in cache");
        String dirName;
        File result = null;
        while (result == null) {
            dirName = UUID.randomUUID().toString();
            File file = new File(rootDirPath, dirName);
            if (!file.exists()) {
                file.mkdirs();
                result = file;
            }
        }
        return result;
    }

    public File initDirectoryInRoot(String rootDirPath, long dreamId) {
        File file = new File(rootDirPath, Long.toString(dreamId));
        file.mkdirs();
        return file;
    }

    public List<String> moveDirFromCacheToRoot(File oldDir, File newDir) {
        List<String> newAudioPaths = new ArrayList<>();

        if (!newDir.exists() && !newDir.mkdir()) {
            return null;
        }

        for (String file : oldDir.list()) {
            File newFile = new File(newDir, file);
            File oldFile = new File(oldDir, file);
            oldFile.renameTo(newFile);
            newAudioPaths.add(newFile.getAbsolutePath());
        }
        oldDir.delete();

        return newAudioPaths;
    }


}
