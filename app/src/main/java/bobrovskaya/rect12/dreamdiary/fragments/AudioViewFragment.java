package bobrovskaya.rect12.dreamdiary.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import java.io.File;
import java.util.ArrayList;

import bobrovskaya.rect12.dreamdiary.adapters.CustomAdapterAudioView;
import bobrovskaya.rect12.dreamdiary.data.Dream;
import bobrovskaya.rect12.dreamdiary.R;
import bobrovskaya.rect12.dreamdiary.data.DreamDbHelper;
import lombok.Getter;


public class AudioViewFragment extends Fragment {
    private DreamDbHelper dreamDbHelper;
    private ArrayList<String> audioList;
    private @Getter CustomAdapterAudioView adapter;
    private RecyclerView recyclerView;
    private int dreamId;
    private Context mContext;
    private String dreamCacheFolderPath;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.audio_view_fragment,
                container,false);
        dreamId = this.getArguments().getInt("dreamId");

        dreamDbHelper = new DreamDbHelper(getActivity());
        recyclerView = view.findViewById(R.id.AudioListRecyclerView);

        audioList = new ArrayList<>();
        adapter = new CustomAdapterAudioView(getActivity(), audioList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        this.mContext = getActivity();

        Bundle args = getArguments();
        dreamCacheFolderPath = args.getString("dreamCacheFolder", null);

        // регистрация контекстного меню для каждой cardView
        registerForContextMenu(recyclerView);

        getAllRecords(dreamId);

        return view;
    }

    private void getAllRecords(int dreamId) {
        SQLiteDatabase db = dreamDbHelper.getReadableDatabase();
        Dream curDream = dreamDbHelper.getDreamById(db, dreamId);

        audioList.addAll(0, curDream.getAudioPaths());
        if (dreamCacheFolderPath != null) {
            Log.d("ADD_RECORD", "11");
            File dreamCacheFolder = new File(dreamCacheFolderPath);
            for(String filePath: dreamCacheFolder.list()) {
                Log.d("ADD_RECORD", dreamCacheFolder + "\\" + filePath);
                audioList.add(dreamCacheFolder + "\\" + filePath);
            }

        }
        adapter.notifyDataSetChanged();
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final int position = adapter.getPosition();


        switch (item.getItemId()) {
            case CustomAdapterAudioView.IDM_DELETE:

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle(R.string.delete_alert_dialog_title)
                        .setMessage(R.string.delete_alert_dialog_text)
                        .setCancelable(true)
                        .setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        adapter.deleteOneAudioRecord(dreamId, position);

                                        Toast.makeText(mContext, R.string.delete_alert_dialog_positive_toast, Toast.LENGTH_SHORT).show();
                                        audioList.remove(position);

                                        // Сообщить адаптеру, что удалили элемент
                                        adapter.notifyItemRemoved(position);
                                        adapter.notifyItemRangeChanged(position, audioList.size());
                                    }
                                })
                        .setNegativeButton("No",
                                null);
                AlertDialog alert = builder.create();
                alert.show();
                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }

    public void addElementToAudioList(String newRecord) {
        audioList.add(newRecord);
        adapter.notifyDataSetChanged();
    }
}
