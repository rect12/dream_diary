package bobrovskaya.rect12.dreamdiary;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Date;

import bobrovskaya.rect12.dreamdiary.data.DreamDbHelper;
import bobrovskaya.rect12.dreamdiary.data.DreamContract.DreamsTable;

/**
 * Created by rect on 12/2/17.
 */

public class DreamListFragment extends Fragment {

    private DreamDbHelper dreamDbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dream_list_fragment,
                container,false);

        dreamDbHelper = new DreamDbHelper(getActivity());
        displayDatabaseInfo(view);
        return view;
    }

    private void displayDatabaseInfo(View view) {
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

        TextView displayTextView = view.findViewById(R.id.textView_12);
        try {
            displayTextView.setText("Таблица содержит " + cursor.getCount() + " записей.\n\n");
            //Log.d("MY_TAG", Integer.toString(cursor.getCount()));
            displayTextView.append(DreamsTable._ID + " - " +
                    DreamsTable.COLUMN_NAME + " - " +
                    DreamsTable.COLUMN_DATE + " - " +
                    DreamsTable.COLUMN_DESCRIPTION + " - " +
                    DreamsTable.COLUMN_AUDIO_PATH + "\n");

            // Узнаем индекс каждого столбца
            int idColumnIndex = cursor.getColumnIndex(DreamsTable._ID);
            int nameColumnIndex = cursor.getColumnIndex(DreamsTable.COLUMN_NAME);
            int cityColumnIndex = cursor.getColumnIndex(DreamsTable.COLUMN_DATE);
            int genderColumnIndex = cursor.getColumnIndex(DreamsTable.COLUMN_DESCRIPTION);
            int ageColumnIndex = cursor.getColumnIndex(DreamsTable.COLUMN_AUDIO_PATH);

            Date time;
            // Проходим через все ряды
            while (cursor.moveToNext()) {
                // Используем индекс для получения строки или числа
                int currentID = cursor.getInt(idColumnIndex);
                String currentName = cursor.getString(nameColumnIndex);
                int currentCity = cursor.getInt(cityColumnIndex);
                time = new Date(currentCity);
                String currentGender = cursor.getString(genderColumnIndex);
                String currentAge = cursor.getString(ageColumnIndex);
                // Выводим значения каждого столбца
                displayTextView.append(("\n" + currentID + " - " +
                        currentName + " - " +
                        time.toString() + " - " +
                        currentGender + " - " +
                        currentAge));

                }
        } finally {
            // Всегда закрываем курсор после чтения
            cursor.close();
        }


    }


}
