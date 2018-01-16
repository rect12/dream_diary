package bobrovskaya.rect12.dreamdiary;

import android.app.Fragment;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

import bobrovskaya.rect12.dreamdiary.data.DreamDbHelper;

/**
 * Created by rect on 1/16/18.
 */

public class AudioViewFragment extends Fragment {
    private DreamDbHelper dreamDbHelper;
    private ArrayList<String> audioList;
    private CustomAdapterAudioView adapter;
    private RecyclerView recyclerView;
    private int dreamId;

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


        getAllRecords(dreamId);

        return view;
    }

    private void getAllRecords(int dreamId) {
        SQLiteDatabase db = dreamDbHelper.getReadableDatabase();
        Dream curDream = dreamDbHelper.getDreamById(db, dreamId);

        audioList.addAll(0, curDream.getAudioPaths());
        adapter.notifyDataSetChanged();
    }

}
