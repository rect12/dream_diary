package bobrovskaya.rect12.dreamdiary.data;

import android.provider.BaseColumns;


public final class DreamContract {

    private DreamContract() {
    }

    public static final class DreamsTable implements BaseColumns {
        public final static String TABLE_NAME = "Dreams";

        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_NAME = "name";
        public final static String COLUMN_DESCRIPTION = "description";
        public final static String COLUMN_AUDIO_PATH = "audio_path";
        public final static String COLUMN_DATE = "date";
    }
}