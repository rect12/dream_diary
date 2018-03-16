package bobrovskaya.rect12.dreamdiary.adapters;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.List;

import bobrovskaya.rect12.dreamdiary.R;
import bobrovskaya.rect12.dreamdiary.data.DreamDbHelper;


public class CustomAdapterAudioView extends RecyclerView.Adapter<CustomAdapterAudioView.ViewHolder> {
    private List<String> mRecords;
    private Context mContext;
    private DreamDbHelper dreamDbHelper;
    MediaPlayer mPlayer;

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

        // Проиграть запись
        viewHolder.playRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File audioFile = new File(recordPath);
                Uri myUri = Uri.fromFile(audioFile);
                MediaPlayer mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                try {
                    mediaPlayer.setDataSource(getContext(), myUri);
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mediaPlayer.start();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mRecords.size();
    }

}
