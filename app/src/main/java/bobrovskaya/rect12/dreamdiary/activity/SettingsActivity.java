package bobrovskaya.rect12.dreamdiary.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import bobrovskaya.rect12.dreamdiary.R;


public class SettingsActivity extends AppCompatActivity {

    SharedPreferences mSettings;
    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeChanger.updateTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        spinner = findViewById(R.id.spinner);

        //адаптер
        ArrayAdapter<?> adapter = ArrayAdapter.createFromResource(this, R.array.themelist, android.R.layout.simple_dropdown_item_1line);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        mSettings = getSharedPreferences(ThemeChanger.APP_PREFERENCES, Context.MODE_PRIVATE);

        if(mSettings.contains(ThemeChanger.APP_PREFERENCES_THEME)) {
            int position = 0;
            switch (mSettings.getInt(ThemeChanger.APP_PREFERENCES_THEME, 0)) {
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


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void applyTheme(View view) {
        int selectedItemPosition = spinner.getSelectedItemPosition();
        String[] choose = getResources().getStringArray(R.array.themelist);
        SharedPreferences.Editor editor = mSettings.edit();
        switch (choose[selectedItemPosition]) {
            case "lightTheme":
                editor.putInt(ThemeChanger.APP_PREFERENCES_THEME, R.style.AppTheme);
                break;
            case "darkTheme":
                editor.putInt(ThemeChanger.APP_PREFERENCES_THEME, R.style.AppThemeDark);
                break;
            default:
                Toast toastTheme = Toast.makeText(getApplicationContext(),
                        "Выбранной темы не существует", Toast.LENGTH_SHORT);
                toastTheme.show();
        }

        editor.commit();
        Toast toast = Toast.makeText(getApplicationContext(),
                "Ваш выбор: " + choose[selectedItemPosition], Toast.LENGTH_SHORT);
        toast.show();
        recreate();

        }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getBaseContext(), MainActivity.class));
    }

}