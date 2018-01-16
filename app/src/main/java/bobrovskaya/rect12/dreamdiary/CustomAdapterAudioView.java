package bobrovskaya.rect12.dreamdiary;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import bobrovskaya.rect12.dreamdiary.data.DreamDbHelper;

/**
 * Created by rect on 1/16/18.
 */

public class CustomAdapterAudioView extends RecyclerView.Adapter<CustomAdapterAudioView.ViewHolder> {
    private List<String> mRecords;
    private Context mContext;
    private DreamDbHelper dreamDbHelper;

    public CustomAdapterAudioView(Context context, List<String> records) {
        mRecords = records;
        mContext = context;
        dreamDbHelper = new DreamDbHelper(context);
    }


    private Context getContext() {
            return mContext;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView audioNameTextView;
        public ImageButton playRecordButton;

        public ViewHolder(View itemView) {
            super(itemView);

            audioNameTextView = itemView.findViewById(R.id.audioNameView);
            playRecordButton = itemView.findViewById(R.id.playRecordButton);
        }
    }

        @Override
        public CustomAdapterAudioView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            // Inflate the custom layout
            View contactView = inflater.inflate(R.layout.card_view_audio, parent, false);

            // Return a new holder instance
            ViewHolder viewHolder = new ViewHolder(contactView);
            return viewHolder;
        }

        //TODO переделать этот метод, остальные уже переделаны. Подумать над типом переменной mRecords.
        @Override
        public void onBindViewHolder(CustomAdapterAudioView.ViewHolder viewHolder, final int position) {
            // Get the data model based on position
            final String recordPath= mRecords.get(position);

            // Set item views based on your views and data model
            TextView audioNameTextView = viewHolder.audioNameTextView;
            audioNameTextView.setText("Record " + (position+1));

            // Смена активности при нажатии на элемент списка
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            // Удаление записи из списка снов
            viewHolder.playRecordButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }

    @Override
    public int getItemCount() {
        return mRecords.size();
    }

}
