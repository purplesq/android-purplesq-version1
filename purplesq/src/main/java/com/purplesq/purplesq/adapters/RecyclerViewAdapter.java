package com.purplesq.purplesq.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.purplesq.purplesq.R;
import com.purplesq.purplesq.interfces.RecyclerViewItemClickListener;
import com.purplesq.purplesq.vos.EventsVo;

import java.util.List;

/**
 * Created by nishant on 11/05/15.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private List<EventsVo> mDataset;
    private static RecyclerViewItemClickListener mRecyclerViewItemClickListener;

    // Provide a suitable constructor (depends on the kind of dataset)
    public RecyclerViewAdapter(List<EventsVo> items, RecyclerViewItemClickListener listener) {
        mDataset = items;
        mRecyclerViewItemClickListener = listener;
    }

    // Provide a reference to the views for each data item. Complex data items may need more than one view per item,
    // and you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        private TextView mTextViewHeading;
        private ImageView mImage;
        private Button mBtnBook;
        private int position;

        public ViewHolder(View v) {
            super(v);
            mImage = (ImageView) v.findViewById(R.id.item_cardlayout_imageview);
            mBtnBook = (Button) v.findViewById(R.id.item_cardlayout_btn_book);
            mTextViewHeading = (TextView) v.findViewById(R.id.item_cardlayout_textview_heading);
            v.setOnClickListener(this);
            mBtnBook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mRecyclerViewItemClickListener.OnRecyclerViewItemClick(position);
                }
            });
        }

        @Override
        public void onClick(View v) {
            Log.i("Nish", "Click detected at : " + position);
            mRecyclerViewItemClickListener.OnRecyclerViewItemClick(position);
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cardview, parent, false);

        // set the view's size, margins, paddings and layout parameters
        return new ViewHolder(itemView);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        EventsVo item = mDataset.get(position);

        // - replace the contents of the view with that element
        holder.position = position;
        holder.mTextViewHeading.setText(item.getName());
        ImageLoader.getInstance().displayImage(item.getThumbnail(), holder.mImage);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
