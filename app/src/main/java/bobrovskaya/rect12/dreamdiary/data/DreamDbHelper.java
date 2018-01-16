package bobrovskaya.rect12.dreamdiary.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import java.util.Date;

import bobrovskaya.rect12.dreamdiary.Dream;
import bobrovskaya.rect12.dreamdiary.data.DreamContract.DreamsTable;
/**
 * Created by rect on 12/16/17.
 */

public class DreamDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "dream.db";

    //TODO при изменении схемы увеличить на единицу!
    private static final int DATABASE_VERSION = 1;

    public DreamDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Строка для создания таблицы
        String SQL_CREATE_GUESTS_TABLE = "CREATE TABLE " + DreamsTable.TABLE_NAME + " ("
                + DreamContract.DreamsTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + DreamsTable.COLUMN_NAME + " TEXT NOT NULL, "
                + DreamsTable.COLUMN_DESCRIPTION + " TEXT NOT NULL, "
                + DreamsTable.COLUMN_AUDIO_PATH + " TEXT NOT NULL, "
                + DreamsTable.COLUMN_DATE + " INTEGER NOT NULL DEFAULT 0);";

        // Запускаем создание таблицы
        sqLiteDatabase.execSQL(SQL_CREATE_GUESTS_TABLE);
    }

    /**
     * вызывается при обновлении схемы базы данных
     * params: DataBase, oldVersion, newVersion
     */

    //TODO реализовать, если есть изменения структуры таблицы бд
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void deleteItemById(SQLiteDatabase sqLiteDatabase, int id) {
        sqLiteDatabase.delete(DreamsTable.TABLE_NAME, "_ID = " + id, null);
        //TODO удаление аудио записи
    }

    
    public int changeItemById(SQLiteDatabase sqLiteDatabase, int dreamId, Dream newDream) {
        ContentValues values = new ContentValues();
        values.put(DreamsTable.COLUMN_NAME, newDream.getName());
        values.put(DreamsTable.COLUMN_DESCRIPTION, newDream.getDescription());
//        values.put(DreamsTable.COLUMN_DATE, newDream.getDate());
//        values.put(DreamsTable.COLUMN_AUDIO_PATH, filePath);

        return sqLiteDatabase.update(DreamsTable.TABLE_NAME, values, "_ID = " + dreamId, null);
    }

    @Nullable
    public Dream getDreamById(SQLiteDatabase sqLiteDatabase, int id) {
        Dream dream = null;
        String[] projection = {
                DreamsTable._ID,
                DreamsTable.COLUMN_DATE,
                DreamsTable.COLUMN_NAME,
                DreamsTable.COLUMN_AUDIO_PATH,
                DreamsTable.COLUMN_DESCRIPTION
        };

        Cursor cursor = sqLiteDatabase.query(
                DreamsTable.TABLE_NAME,
                projection,
                "_id = " + id,
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

            Date time;
            cursor.moveToNext();
            // Используем индекс для получения строки или числа
            int currentID = cursor.getInt(idColumnIndex);
            String currentName = cursor.getString(nameColumnIndex);
            long currentDate = cursor.getLong(dateColumnIndex);
            time = new Date(currentDate);
            String currentDescription = cursor.getString(descriptionColumnIndex);
            // Добавляем значения каждого столбца
            dream = new Dream(currentID, currentName, time.toString(), currentDescription);

        } finally {
            // Закрываем курсор после чтения
            cursor.close();
        }

        return dream;


    }
}
