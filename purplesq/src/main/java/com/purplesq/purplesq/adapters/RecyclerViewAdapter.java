package com.purplesq.purplesq.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.purplesq.purplesq.R;
import com.purplesq.purplesq.dummy.DummyContent;

import java.util.List;

/**
 * Created by nishant on 11/05/15.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private List<DummyContent.DummyItem> mDataset;


    // Provide a reference to the views for each data item. Complex data items may need more than one view per item,
    // and you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private TextView mTextView1, mTextView2;
        private TextView mTvMonday, mTvTuesday, mTvWednesday, mTvThursday, mTvFriday, mTvSaturday, mTvSunday;
        private ImageView mImage;
        private Button mBtnBook;

        public ViewHolder(View v) {
            super(v);
            mTextView1 = (TextView) v.findViewById(R.id.item_cardlayout_textview1);
            mTextView2 = (TextView) v.findViewById(R.id.item_cardlayout_textview2);
            mTvMonday = (TextView) v.findViewById(R.id.item_cardlayout_tv_monday);
            mTvTuesday = (TextView) v.findViewById(R.id.item_cardlayout_tv_tuesday);
            mTvWednesday = (TextView) v.findViewById(R.id.item_cardlayout_tv_wednesday);
            mTvThursday = (TextView) v.findViewById(R.id.item_cardlayout_tv_thirsday);
            mTvFriday = (TextView) v.findViewById(R.id.item_cardlayout_tv_friday);
            mTvSaturday = (TextView) v.findViewById(R.id.item_cardlayout_tv_saturday);
            mTvSunday = (TextView) v.findViewById(R.id.item_cardlayout_tv_sunday);
            mImage = (ImageView) v.findViewById(R.id.item_cardlayout_imageview);
            mBtnBook = (Button) v.findViewById(R.id.item_cardlayout_btn_book);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public RecyclerViewAdapter(List<DummyContent.DummyItem> items) {
        mDataset = items;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cardview, parent, false);

        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(itemView);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        DummyContent.DummyItem item = mDataset.get(position);

        // - replace the contents of the view with that element
        holder.mTextView2.setText(item.toString());

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
