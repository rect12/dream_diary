package bobrovskaya.rect12.dreamdiary;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

import bobrovskaya.rect12.dreamdiary.data.DreamDbHelper;
import bobrovskaya.rect12.dreamdiary.data.DreamContract.DreamsTable;


/**
 * Created by rect on 12/2/17.
 */

public class CreateDreamActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_dream_activity);

        Toolbar toolbar = findViewById(R.id.toolbar_create_dream);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dream_create, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.add_db_dream) {
            TextView nameView = findViewById(R.id.createDreamNameText);
            TextView descriptionView = findViewById(R.id.createDreamDreamText);

            String name = nameView.getText().toString();
            String description = descriptionView.getText().toString();
            Date date = new Date();

            DreamDbHelper mDbHelper = new DreamDbHelper(this);

            SQLiteDatabase db = mDbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(DreamsTable.COLUMN_NAME, name);
            values.put(DreamsTable.COLUMN_DESCRIPTION, description);
            values.put(DreamsTable.COLUMN_DATE, date.getTime());

//            long newRowId = db.insert(DreamsTable.TABLE_NAME, null, values);
//            if (newRowId == -1) {
//                // Если ID  -1, значит произошла ошибка
//                Toast.makeText(this, "Error while creating dream", Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(this, "Dream added " + newRowId, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(CreateDreamActivity.this, MainActivity.class);
                startActivity(intent);
//            }


        }

        return super.onOptionsItemSelected(item);
    }



//    private insertDream() {
//
//    }


}
