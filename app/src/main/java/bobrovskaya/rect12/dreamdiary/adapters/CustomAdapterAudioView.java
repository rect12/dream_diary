package bobrovskaya.rect12.dreamdiary.adapters;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.List;

import bobrovskaya.rect12.dreamdiary.R;
import bobrovskaya.rect12.dreamdiary.activity.CreateDreamActivity;
import bobrovskaya.rect12.dreamdiary.data.Dream;
import bobrovskaya.rect12.dreamdiary.data.DreamDbHelper;
import bobrovskaya.rect12.dreamdiary.fragments.AudioViewFragment;
import lombok.Getter;
import lombok.Setter;


public class CustomAdapterAudioView extends RecyclerView.Adapter<CustomAdapterAudioView.ViewHolder> {
    private List<String> mRecords;
    private Context mContext;
    private DreamDbHelper dreamDbHelper;
    MediaPlayer mPlayer;
    private @Getter @Setter int position;
    private int flagForChanging;


    public static final int IDM_DELETE = 101;

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
    public void onBindViewHolder(final CustomAdapterAudioView.ViewHolder viewHolder, final int position) {
        // Get the data model based on position
        final String recordPath = mRecords.get(position);

        flagForChanging = ((CreateDreamActivity)mContext).getFlagForChanging();

        // Set item views based on your views and data model
        TextView audioNameTextView = viewHolder.audioNameTextView;

        audioNameTextView.setText(recordPath.substring(recordPath.lastIndexOf("/") + 1));

        if (flagForChanging == 2) {
            // появление кнопки "удалить" при долгом нажатии на запись
            viewHolder.itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {

                @Override
                public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                    contextMenu.add(0, IDM_DELETE, 0, R.string.context_menu_delete_audio);
                }
            });}

            // Проиграть запись при нажатии на кнопку проигрывания
            viewHolder.playRecordButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("PLAY_MUSIC", "i'm playing");
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


        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                setPosition(viewHolder.getAdapterPosition());
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mRecords.size();
    }

    public void deleteOneAudioRecord(int dreamId, int position) {
        SQLiteDatabase db = dreamDbHelper.getWritableDatabase();
        Dream curDream = dreamDbHelper.getDreamById(db, dreamId);
        List<String> newAudioList = curDream.getAudioPaths();
        String audioPath = newAudioList.remove(position);

        curDream.setAudioPaths(newAudioList);
        dreamDbHelper.changeItemById(db, dreamId, curDream);

        File audioFile = new File(audioPath);
        audioFile.delete();
    }

}
