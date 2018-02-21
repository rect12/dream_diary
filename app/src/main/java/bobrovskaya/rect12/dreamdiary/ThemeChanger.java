package bobrovskaya.rect12.dreamdiary;

import android.content.Context;
import android.content.SharedPreferences;


public class ThemeChanger {
    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_THEME = "THEME";

    public static void updateTheme(Context context) {
        SharedPreferences mSettings = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        int theme = mSettings.getInt(APP_PREFERENCES_THEME, R.style.AppTheme);

        context.setTheme(theme);
    }
}
