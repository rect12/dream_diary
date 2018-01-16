package bobrovskaya.rect12.dreamdiary;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import bobrovskaya.rect12.dreamdiary.data.DreamDbHelper;

/**
 * Created by rect on 12/17/17.
 */

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    private List<Dream> mDreams;
    private Context mContext;
    private DreamDbHelper dreamDbHelper;

    public CustomAdapter(Context context, List<Dream> dreams) {
        mDreams = dreams;
        mContext = context;
        dreamDbHelper = new DreamDbHelper(context);
    }

    private Context getContext() {
        return mContext;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public TextView dateTextView;
        public TextView descriptionTextView;
        public Button deleteDreamButton;
        public Button changeDreamButton;


        public ViewHolder(View itemView) {
            super(itemView);

            nameTextView = itemView.findViewById(R.id.nameView);
            dateTextView = itemView.findViewById(R.id.dateView);
            descriptionTextView = itemView.findViewById(R.id.descriptionView);
            deleteDreamButton = itemView.findViewById(R.id.delete_dream_button);
            changeDreamButton = itemView.findViewById(R.id.change_dream_button);
        }
    }

    @Override
    public CustomAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.card_view_dream, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomAdapter.ViewHolder viewHolder, final int position) {
        // Get the data model based on position
        final Dream dream = mDreams.get(position);

        // Set item views based on your views and data model
        TextView nameView = viewHolder.nameTextView;
        TextView descriptionView = viewHolder.descriptionTextView;
        TextView dateView = viewHolder.dateTextView;
        nameView.setText(dream.getName());
        descriptionView.setText(dream.getDescription());
        dateView.setText(dream.getDate());

        // Смена активности при нажатии на элемент списка
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CreateDreamActivity.class);
                intent.putExtra("FLAG_FOR_CHANGING", 1); // 1 -- просмотр элемента, без права на изменение
                intent.putExtra("DREAM_ID", dream.getId());
                mContext.startActivity(intent);
            }
        });

        // Удаление записи из списка снов
        viewHolder.deleteDreamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle(R.string.delete_alert_dialog_title)
                        .setMessage(R.string.delete_alert_dialog_text)
                        .setCancelable(true)
                        .setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        removeDreamFromDb(dream.getId());
                                        Toast.makeText(mContext, R.string.delete_alert_dialog_positive_toast, Toast.LENGTH_SHORT).show();
                                        mDreams.remove(position);

                                        // Сообщить адаптеру, что удалили элемент
                                        notifyItemRemoved(position);
                                        notifyItemRangeChanged(position, mDreams.size());
                                    }
                                    })
                        .setNegativeButton("No",
                                null);
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        // открытие активити с изменением данных записи
        viewHolder.changeDreamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, CreateDreamActivity.class);
                intent.putExtra("FLAG_FOR_CHANGING", 2); //2 -- просмотр элемента с правом на изменение
                intent.putExtra("DREAM_ID", dream.getId());

                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mDreams.size();
    }

    private void removeDreamFromDb(int id) {
        dreamDbHelper.deleteItemById(dreamDbHelper.getWritableDatabase(), id);

    }
}
