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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by nishant on 11/05/15.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private static RecyclerViewItemClickListener mRecyclerViewItemClickListener;
    private List<EventsVo> mDataset;

    // Provide a suitable constructor (depends on the kind of dataset)
    public RecyclerViewAdapter(List<EventsVo> items, RecyclerViewItemClickListener listener) {
        mDataset = items;
        mRecyclerViewItemClickListener = listener;
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
        holder.mTvHeading.setText(item.getName());

        try {
            Date date = new Date(item.getSchedule().getStart_date());
            SimpleDateFormat sdf2 = new SimpleDateFormat("dd MMM", Locale.ENGLISH);
            holder.mTvDate.setText(sdf2.format(date));

        } catch (Exception e) {
            e.printStackTrace();
        }

        ImageLoader.getInstance().displayImage(item.getThumbnail(), holder.mImage);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    // Provide a reference to the views for each data item. Complex data items may need more than one view per item,
    // and you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        private TextView mTvHeading, mTvDate;
        private ImageView mImage;
        private Button mBtnBook;
        private int position;

        public ViewHolder(View v) {
            super(v);
            mImage = (ImageView) v.findViewById(R.id.item_cardlayout_imageview);
            mBtnBook = (Button) v.findViewById(R.id.item_cardlayout_btn_book);
            mTvHeading = (TextView) v.findViewById(R.id.item_cardlayout_textview_heading);
            mTvDate = (TextView) v.findViewById(R.id.item_cardlayout_tv_date);
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
}
