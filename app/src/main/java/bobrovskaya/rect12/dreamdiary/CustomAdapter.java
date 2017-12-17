package bobrovskaya.rect12.dreamdiary;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by rect on 12/17/17.
 */

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    private List<Dream> mDreams;
    private Context mContext;

    public CustomAdapter(Context context, List<Dream> dreams) {
        mDreams = dreams;
        mContext = context;
    }

    private Context getContext() {
        return mContext;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public TextView dateTextView;
        public TextView descriptionTextView;


        public ViewHolder(View itemView) {
            super(itemView);

            nameTextView = itemView.findViewById(R.id.nameView);
            dateTextView = itemView.findViewById(R.id.dateView);
            descriptionTextView = itemView.findViewById(R.id.descriptionView);
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
    public void onBindViewHolder(CustomAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        Dream dream = mDreams.get(position);

        // Set item views based on your views and data model
        TextView nameView = viewHolder.nameTextView;
        TextView descriptionView = viewHolder.descriptionTextView;
        TextView dateView = viewHolder.dateTextView;
        nameView.setText(dream.getName());
        descriptionView.setText(dream.getDescription());
        dateView.setText(dream.getDate());

        //Button button = viewHolder.messageButton;
        //button.setText(contact.isOnline() ? "Message" : "Offline");
        //button.setEnabled(contact.isOnline());
    }

    @Override
    public int getItemCount() {
        return mDreams.size();
    }


}
