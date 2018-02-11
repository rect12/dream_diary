package bobrovskaya.rect12.dreamdiary;

import android.app.AlarmManager;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

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

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.alarm_fragment,
                container,false);

        alarm_on = (Button) view.findViewById(R.id.alarm_on);
        alarm_off = (Button) view.findViewById(R.id.alarm_off);
        updateText = (TextView) view.findViewById(R.id.updateTimeText);
        timePicker = (TimePicker) view.findViewById(R.id.timePicker);
        alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);

        alarm_on.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setTimeText("Будильник включен!");
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
