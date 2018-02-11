package bobrovskaya.rect12.dreamdiary;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by natasha on 11.02.18.
 */

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("TAG_LOG", "YEAH BIITCH!");
    }
}
