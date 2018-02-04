package bobrovskaya.rect12.dreamdiary.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import bobrovskaya.rect12.dreamdiary.R;

public class AlarmFragment extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.alarm_fragment,
                container,false);

        return view;
    }
}
