package bobrovskaya.rect12.dreamdiary.fragments;

import android.app.Fragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Date;

import bobrovskaya.rect12.dreamdiary.adapters.CustomAdapter;
import bobrovskaya.rect12.dreamdiary.data.Dream;
import bobrovskaya.rect12.dreamdiary.R;
import bobrovskaya.rect12.dreamdiary.data.DreamDbHelper;
import bobrovskaya.rect12.dreamdiary.data.DreamContract.DreamsTable;

import static bobrovskaya.rect12.dreamdiary.data.GsonMethods.getListFromJson;


public class DreamListFragment extends Fragment {

    private DreamDbHelper dreamDbHelper;
    private ArrayList<Dream> dreamList;
    private CustomAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dream_list_fragment,
                container,false);

        dreamDbHelper = new DreamDbHelper(getActivity());
        //displayDatabaseInfo(view);
        recyclerView = view.findViewById(R.id.dreamListRecyclerView);

        dreamList = new ArrayList<>();
        adapter = new CustomAdapter(getActivity(), dreamList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        final FloatingActionButton fab = getActivity().findViewById(R.id.fab);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && fab.getVisibility() == View.VISIBLE) {
                    fab.hide();
                } else if (dy < 0 && fab.getVisibility() != View.VISIBLE) {
                    fab.show();
                }
            }
        });

        getAllDreams();

        return view;
    }

    private void getAllDreams() {
        SQLiteDatabase db = dreamDbHelper.getReadableDatabase();
        String[] projection = {
                DreamsTable._ID,
                DreamsTable.COLUMN_DATE,
                DreamsTable.COLUMN_NAME,
                DreamsTable.COLUMN_AUDIO_PATH,
                DreamsTable.COLUMN_DESCRIPTION
        };

        Cursor cursor = db.query(
                DreamsTable.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null);

        try {
            // Узнаем индекс каждого столбца
            int idColumnIndex = cursor.getColumnIndex(DreamsTable._ID);
            int nameColumnIndex = cursor.getColumnIndex(DreamsTable.COLUMN_NAME);
            int dateColumnIndex = cursor.getColumnIndex(DreamsTable.COLUMN_DATE);
            int descriptionColumnIndex = cursor.getColumnIndex(DreamsTable.COLUMN_DESCRIPTION);
            int audioPathColumnIndex = cursor.getColumnIndex(DreamsTable.COLUMN_AUDIO_PATH );

            Date time;
            // Проходим через все ряды
            while (cursor.moveToNext()) {
                // Используем индекс для получения строки или числа
                int currentID = cursor.getInt(idColumnIndex);
                String currentName = cursor.getString(nameColumnIndex);
                String currentDate = cursor.getString(dateColumnIndex);
                //time = new Date(currentDate);
                String currentDescription = cursor.getString(descriptionColumnIndex);
                ArrayList<String> audioPaths = getListFromJson(cursor.getString(audioPathColumnIndex));
                // Добавляем значения каждого столбца
                dreamList.add(new Dream(currentID, currentName, currentDate, currentDescription, audioPaths));
            }
        } finally {
            // Закрываем курсор после чтения
            cursor.close();
            adapter.notifyDataSetChanged();
        }
    }


}
