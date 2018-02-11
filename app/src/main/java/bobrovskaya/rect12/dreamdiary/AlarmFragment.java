package bobrovskaya.rect12.dreamdiary;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by rect on 12/17/17.
 */

public class AlarmFragment extends Fragment {

    private Context mContext;

    Button alarm_on, alarm_off;
    TextView updateText;
    TimePicker timePicker;
    AlarmManager alarmManager;
    PendingIntent pendingIntent;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.alarm_fragment,
                container,false);

        alarm_on = (Button) view.findViewById(R.id.alarm_on);
        alarm_off = (Button) view.findViewById(R.id.alarm_off);
        updateText = (TextView) view.findViewById(R.id.updateTimeText);
        timePicker = (TimePicker) view.findViewById(R.id.timePicker);
        alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);

        final Calendar calendar = Calendar.getInstance();

        //intent
        final Intent my_intent = new Intent(getActivity().getApplicationContext(), AlarmReceiver.class);

        alarm_on.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.M)
            public void onClick(View v) {
                calendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
                calendar.set(Calendar.MINUTE, timePicker.getMinute());

                int hour = timePicker.getHour();
                int minute = timePicker.getMinute();

                String hour_string = String.valueOf(hour);
                String minute_string = String.valueOf(minute);

                if (minute < 10) {
                    minute_string = "0" + String.valueOf(minute);
                }

                setTimeText("Будильник поставлен на " + hour_string + ":" + minute_string);

                //pending intent
                pendingIntent = PendingIntent.getBroadcast(getContext(), 0, my_intent, PendingIntent.FLAG_UPDATE_CURRENT);
                //alarmmanager
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }
        });

        alarm_off.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setTimeText("Будильник остановлен!");
            }
        });

        return view;
    }

    public void setTimeText(String timeText) {
        this.updateText.setText(timeText);
    }
}
