package bobrovskaya.rect12.dreamdiary;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by rect on 12/2/17.
 */

public class SettingsActivity extends AppCompatActivity {

    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_THEME = "THEME";

    SharedPreferences mSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        Toolbar toolbar = findViewById(R.id.toolbar_create_dream);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        final Spinner spinner = (Spinner)findViewById(R.id.spinner);

        //адаптер
        ArrayAdapter<?> adapter = ArrayAdapter.createFromResource(this, R.array.themelist, android.R.layout.simple_dropdown_item_1line);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        if(mSettings.contains(APP_PREFERENCES_THEME)) {
            int position = 0;
            switch (mSettings.getInt(APP_PREFERENCES_THEME, 0)) {
                case R.style.AppThemeDark:
                    position = 0;
                    break;
                case R.style.AppTheme:
                    position = 1;
                    break;
                default:
            }
            spinner.setSelection(position);
        }


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int selectedItemPosition, long id) {
                String[] choose = getResources().getStringArray(R.array.themelist);
                /*Toast toast = Toast.makeText(getApplicationContext(),
                        "Ваш выбор: " + choose[selectedItemPosition], Toast.LENGTH_SHORT);
                toast.show();*/

                //TODO: в будущем нужно менять toolbar, а не AppTheme

                SharedPreferences.Editor editor = mSettings.edit();
                switch (choose[selectedItemPosition]) {
                    case "lightTheme":
                        editor.putInt(APP_PREFERENCES_THEME, R.style.AppTheme);
                        break;
                    case "darkTheme":
                        editor.putInt(APP_PREFERENCES_THEME, R.style.AppThemeDark);
                        break;
                    default:
                        Toast toastTheme = Toast.makeText(getApplicationContext(),
                                "Выбранной темы не существует", Toast.LENGTH_SHORT);
                        toastTheme.show();
                }
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
