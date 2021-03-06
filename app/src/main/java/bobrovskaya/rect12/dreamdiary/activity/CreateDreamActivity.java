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

import java.nio.file.Files;
import java.util.List;
import java.util.UUID;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import bobrovskaya.rect12.dreamdiary.ThemeChanger;
import bobrovskaya.rect12.dreamdiary.adapters.CustomAdapter;
import bobrovskaya.rect12.dreamdiary.adapters.CustomAdapterAudioView;
import bobrovskaya.rect12.dreamdiary.data.Dream;
import bobrovskaya.rect12.dreamdiary.R;
import bobrovskaya.rect12.dreamdiary.data.DreamDbHelper;
import bobrovskaya.rect12.dreamdiary.fragments.AudioViewFragment;
import lombok.Getter;

import android.Manifest;

import static android.os.Environment.DIRECTORY_MUSIC;


public class CreateDreamActivity extends AppCompatActivity {

    private MediaRecorder mediaRecorder;
    private ImageButton startButton;
    private String filePath;
    private Boolean isRecording = false;
    private TextView nameView;
    private TextView descriptionView;

    AudioViewFragment audioViewFragment;


    private Dream curDream = null;
    private @Getter int flagForChanging = -1;
    FragmentTransaction fTrans;

    private String rootAppPath;
    private String cacheAppPath;
    private int dreamRecordsNumber = 0;
    private File dreamCacheFolder = null;

    private static final String CACHED_AUDIO = "cached_audio";
    private static final String IS_RECORDING = "recording";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private boolean permissionToRecordAccepted = false;
    private String[] permissions = {Manifest.permission.RECORD_AUDIO};


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
        Log.d("ARGS0", "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_dream_activity);

        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        cacheAppPath = getExternalCacheDir().getAbsolutePath();
        rootAppPath = getExternalFilesDir(DIRECTORY_MUSIC).getAbsolutePath();

        // задаем директорию в кэше, куда будут класться записи
        if (dreamCacheFolder == null)
            dreamCacheFolder = initUniqueDirectoryInCache(cacheAppPath);

        Bundle args = new Bundle();
        args.putString("dreamCacheFolder", dreamCacheFolder.getAbsolutePath());
        Log.d("ARGS", dreamCacheFolder.getAbsolutePath());

        audioViewFragment = new AudioViewFragment();
        audioViewFragment.setArguments(args);
        nameView = findViewById(R.id.createDreamNameText);
        descriptionView = findViewById(R.id.createDreamDreamText);
        startButton = findViewById(R.id.createDreamMicrophoneButtonStart);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRecording) {
                    if (flagForChanging == 2)
                        audioViewFragment.addElementToAudioList(filePath);
                    stopAudioRecording();
                    Log.d("record", "i'm here");
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
                startButton.setEnabled(false);
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
                curDream = mDbHelper.getDreamById(db, curDream.getId());
                newDream.setDate(curDream.getDate());
                File newDir = initDirectoryInRoot(rootAppPath, curDream.getId());
                List<String> newAudio = moveDirFromCacheToRoot(dreamCacheFolder, newDir);
                Log.d("ARGS1", dreamCacheFolder.getAbsolutePath());
                List<String> newAudioList = curDream.getAudioPaths();
                newAudioList.addAll(newAudio);
                newDream.setAudioPaths(newAudioList);

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

    @Override
    protected void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);
        if (dreamCacheFolder != null)
            outState.putString(CACHED_AUDIO, dreamCacheFolder.getAbsolutePath());
        outState.putBoolean(IS_RECORDING, isRecording);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String path = savedInstanceState.getString(CACHED_AUDIO, null);
        if (path != null) {
            dreamCacheFolder = new File(path);
            if (curDream != null) {
                int dreamId = curDream.getId();
                if (audioViewFragment != null && dreamId > 0)
                    audioViewFragment.getAllRecords(dreamId, dreamCacheFolder.getAbsolutePath());
            }

        }
//        isRecording = savedInstanceState.getBoolean(IS_RECORDING, false);
//        if (isRecording){
//            Log.d("record", "recording");
//        }
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
    }

    private void stopAudioRecording() {
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.reset();
            mediaRecorder.release();
            mediaRecorder = null;
            filePath = "";
            dreamRecordsNumber++;
            Log.d("recorder", "stop recording, save file");
        }
        startButton.setImageResource(android.R.drawable.ic_btn_speak_now);
    }

    private void initializeMediaRecord() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
    }

    @Override
    protected void onDestroy() {
        if (isRecording) {
            stopAudioRecording();
            Log.d("record", "Destroy");
            isRecording = false;
        }
        super.onDestroy();
    }

    private String createFilePath() {
        String newFilePath = dreamCacheFolder.getAbsolutePath() + "/newDreamRecord-" + dreamRecordsNumber + ".3gp";
        File tmp = new File(newFilePath);
        while(tmp.exists()){
            dreamRecordsNumber++;
            newFilePath = dreamCacheFolder.getAbsolutePath() + "/newDreamRecord-" + dreamRecordsNumber + ".3gp";
            tmp = new File(newFilePath);
        }
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
        if (!file.isDirectory())
            file.mkdirs();
        return file;
    }

    public List<String> moveDirFromCacheToRoot(File oldDir, File newDir) {
        List<String> newAudioPaths = new ArrayList<>();

        if (!newDir.exists() && !newDir.mkdir()) {
            return null;
        }

        for (String file : oldDir.list()) {
            String newFileName = generateNewFileName(newDir);
            File newFile = new File(newDir, newFileName);
            File oldFile = new File(oldDir, file);
            oldFile.renameTo(newFile);
            newAudioPaths.add(newFile.getAbsolutePath());
        }
        oldDir.delete();

        return newAudioPaths;
    }

    String generateNewFileName(File newDir) {
        String newFileName = "dreamRecord-";
        int newFileNameLength = newFileName.length();

        int maxInd = -1;
        int ind = -1;
        for(String file: newDir.list()) {
            Log.d("record_ind", file.substring(0, newFileNameLength) + "   " + file.substring(newFileNameLength, file.lastIndexOf(".")));
            if (file.substring(0, newFileNameLength).equals(newFileName)) {
                ind = Integer.parseInt(file.substring(newFileNameLength, file.lastIndexOf(".")));
                Log.d("record_ind", "" + ind);
                if (ind > maxInd) maxInd = ind;
            }
        }

        newFileName = newFileName + (maxInd + 1) + ".3gp";
        return newFileName;
    }
}