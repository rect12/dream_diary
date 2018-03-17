package bobrovskaya.rect12.dreamdiary.activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;

import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import bobrovskaya.rect12.dreamdiary.fragments.AlarmFragment;
import bobrovskaya.rect12.dreamdiary.fragments.DreamListFragment;
import bobrovskaya.rect12.dreamdiary.R;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_THEME = "THEME";

    SharedPreferences mSettings;

    FragmentTransaction fTrans;
    FloatingActionButton fab;
    Toolbar toolbar;
//    private DreamDbHelper dreamDbHelper;

    public void checkSharedPreferences() {
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        if(mSettings.contains(APP_PREFERENCES_THEME)) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "THEME: " + mSettings.getInt(APP_PREFERENCES_THEME, 0),
                    Toast.LENGTH_SHORT);
            toast.show();
        }
        else {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "THEME не существует", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        int theme = sp.getInt(APP_PREFERENCES_THEME, R.style.AppTheme);
        setTheme(theme);*/
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        checkSharedPreferences();


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //open first page on startup
        if (savedInstanceState == null) {
            onNavigationItemSelected(navigationView.getMenu().getItem(0));
            navigationView.getMenu().getItem(0).setChecked(true);
        }
    }

    @Override
    public void onBackPressed() {
        checkSharedPreferences();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        checkSharedPreferences();
        int id = item.getItemId();

        Fragment fragment = null;
        Class fragmentClass = null;

        if (id == R.id.nav_my_dreams) {
            fragmentClass = DreamListFragment.class;
            fab = findViewById(R.id.fab);
            fab.show();
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, CreateDreamActivity.class);
                    intent.putExtra("FLAG_FOR_CHANGING", 0); // 0 -- создание нового элемента
                    startActivity(intent);
                }
            });

        } else if (id == R.id.nav_search) {
            fab = findViewById(R.id.fab);
            fab.hide();

        } else if (id == R.id.nav_manage) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_alarm_clock) {
            fab = findViewById(R.id.fab);
            fab.hide();
            fragmentClass = AlarmFragment.class;

        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //TODO удалить это условие (оно временно, чтобы приложение на падало)
        if (fragment != null) {
            fTrans = getFragmentManager().beginTransaction();
            fTrans.replace(R.id.frgmCont, fragment).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}