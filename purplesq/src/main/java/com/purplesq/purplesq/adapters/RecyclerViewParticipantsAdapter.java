package com.purplesq.purplesq.adapters;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.purplesq.purplesq.R;
import com.purplesq.purplesq.vos.ParticipantVo;

import java.util.ArrayList;

/**
 * Created by nishant on 01/08/15.
 */
public class RecyclerViewParticipantsAdapter extends RecyclerView.Adapter<RecyclerViewParticipantsAdapter.ParticipantViewHolder> {

    private ArrayList<ParticipantVo> mDataset;
    private Context mContext;
    private boolean inEditMode = false;
    private String mUserEmail;

    public RecyclerViewParticipantsAdapter(Context context, ArrayList<ParticipantVo> items, String userEmail) {
        mContext = context;
        mDataset = items;
        mUserEmail = userEmail;
    }


    @Override
    public ParticipantViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_participants, parent, false);
        return new ParticipantViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ParticipantViewHolder holder, int position) {

        holder.position = position;

        if (position == 0 && isParticipantEmpty(position)) {
            holder.tvAddMore.setVisibility(View.VISIBLE);
            holder.tvLabel.setVisibility(View.VISIBLE);
            holder.editlayout.setVisibility(View.GONE);
            holder.cardlayout.setVisibility(View.VISIBLE);
            holder.isSaved = false;
            inEditMode = true;
        } else {
            holder.tvNo.setText((position + 1) + "");
            String fname = mDataset.get(position).getFirstname();
            String lname = mDataset.get(position).getLastname();
            String email = mDataset.get(position).getEmail();
            String phone = mDataset.get(position).getPhone();
            String institute = mDataset.get(position).getInstitute();

            holder.etFirstName.setText(fname);
            holder.etLastName.setText(lname);
            holder.etEmail.setText(email);
            holder.etPhone.setText(phone);
            holder.etInstitute.setText(institute);

            if (!TextUtils.isEmpty(mDataset.get(position).getEmail())) {
                if (mDataset.get(position).getEmail().contentEquals(mUserEmail)) {
                    holder.tvName.setText(fname + " " + lname + " (You)");
                } else {
                    holder.tvName.setText(fname + " " + lname);
                }
            } else {
                holder.tvName.setText("");
                holder.isSaved = false;
                inEditMode = true;
            }

            holder.tvInstitute.setText(institute);
            holder.tvNumber.setText((position + 1) + "");

            if (position == 0) {
                holder.tvLabel.setVisibility(View.VISIBLE);
                if (mDataset.get(position).getEmail().contentEquals(mUserEmail)) {
                    if (!TextUtils.isEmpty(mDataset.get(position).getInstitute())) {
                        holder.isSaved = true;
                    }
                }
            } else {
                holder.tvLabel.setVisibility(View.GONE);
            }

            if (holder.isSaved) {
                holder.editlayout.setVisibility(View.GONE);
                holder.cardlayout.setVisibility(View.VISIBLE);

                inEditMode = false;
            } else {
                holder.editlayout.setVisibility(View.VISIBLE);
                holder.cardlayout.setVisibility(View.GONE);
                inEditMode = true;
            }

            if (position == mDataset.size() - 1) {
                if (!inEditMode) {
                    holder.tvAddMore.setVisibility(View.VISIBLE);

                    holder.tvAddMore.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ParticipantVo participantVo = new ParticipantVo(mDataset.size());
                            mDataset.add(participantVo);
                            notifyDataSetChanged();
                            inEditMode = true;
                        }
                    });

                } else {
                    holder.tvAddMore.setVisibility(View.GONE);
                }
            } else {
                holder.tvAddMore.setVisibility(View.GONE);
            }


            holder.tvSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isDataCorrect = true;
                    String fname = holder.etFirstName.getText().toString().trim();
                    String lname = holder.etLastName.getText().toString().trim();
                    String email = holder.etEmail.getText().toString().trim();
                    String phone = holder.etPhone.getText().toString().trim();
                    String institute = holder.etInstitute.getText().toString().trim();

                    if (!TextUtils.isEmpty(fname)) {
                        mDataset.get(holder.position).setFirstname(fname);
                    } else {
                        isDataCorrect = false;
                        holder.etFirstName.setError(mContext.getString(R.string.error_field_required));
                        holder.etFirstName.requestFocus();
                    }

                    if (!TextUtils.isEmpty(lname)) {
                        mDataset.get(holder.position).setLastname(lname);
                    } else {
                        isDataCorrect = false;
                        holder.etLastName.setError(mContext.getString(R.string.error_field_required));
                        holder.etLastName.requestFocus();
                    }

                    if (!TextUtils.isEmpty(email) && email.contains("@")) {
                        mDataset.get(holder.position).setEmail(email);
                    } else {
                        isDataCorrect = false;
                        if (TextUtils.isEmpty(email))
                            holder.etEmail.setError(mContext.getString(R.string.error_field_required));
                        else
                            holder.etEmail.setError(mContext.getString(R.string.error_invalid_email));
                        holder.etEmail.requestFocus();
                    }

                    if (!TextUtils.isEmpty(phone) && (phone.length() > 9) && (phone.length() < 14)) {
                        mDataset.get(holder.position).setPhone(phone);
                    } else {
                        isDataCorrect = false;
                        if (TextUtils.isEmpty(email))
                            holder.etPhone.setError(mContext.getString(R.string.error_field_required));
                        else
                            holder.etPhone.setError("Phone no should be between 10-13");
                        holder.etPhone.requestFocus();
                    }

                    if (!TextUtils.isEmpty(institute)) {
                        mDataset.get(holder.position).setInstitute(institute);
                    } else {
                        isDataCorrect = false;
                        holder.etInstitute.setError(mContext.getString(R.string.error_field_required));
                        holder.etInstitute.requestFocus();
                    }

                    if (isDataCorrect) {
                        holder.tvName.setText(fname + " " + lname + " (You)");
                        holder.tvInstitute.setText(institute);

                        holder.editlayout.setVisibility(View.GONE);
                        holder.cardlayout.setVisibility(View.VISIBLE);

                        inEditMode = false;
                        holder.isSaved = true;
                        notifyDataSetChanged();
                    }
                }
            });

            holder.tvDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDataset.remove(holder.position);
                    holder.setIsRecyclable(true);
                    notifyDataSetChanged();

                    if (mDataset.isEmpty()) {
                        ParticipantVo participantVo = new ParticipantVo(mDataset.size());
                        mDataset.add(participantVo);
                        notifyDataSetChanged();
                        inEditMode = true;
                    }
                }
            });

            holder.tvEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.editlayout.setVisibility(View.VISIBLE);
                    holder.cardlayout.setVisibility(View.GONE);
                    holder.isSaved = false;
                    inEditMode = true;
                }
            });

            holder.tvCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (isParticipantEmpty(holder.position)) {
                        mDataset.remove(holder.position);
                        notifyDataSetChanged();
                    } else {
                        holder.editlayout.setVisibility(View.GONE);
                        holder.cardlayout.setVisibility(View.VISIBLE);

                        holder.isSaved = true;
                        inEditMode = false;
                    }
                }
            });

            holder.cardlayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.editDeletelayout.getVisibility() == View.VISIBLE) {
                        holder.editDeletelayout.setVisibility(View.GONE);
                        holder.cardlayout.setSelected(false);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            holder.cardlayout.setElevation(0f);
                        }
                    } else {
                        holder.editDeletelayout.setVisibility(View.VISIBLE);
                        holder.cardlayout.setSelected(true);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            holder.cardlayout.setElevation(5f);
                        }
                    }
                }
            });
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public class ParticipantViewHolder extends RecyclerView.ViewHolder {
        private CardView editlayout, cardlayout;
        private LinearLayout editDeletelayout;
        private EditText etFirstName, etLastName, etEmail, etPhone, etInstitute;
        private TextView tvLabel, tvNo, tvNumber, tvName, tvInstitute, tvSave, tvCancel, tvEdit, tvDelete, tvAddMore;
        private int position;
        private boolean isSaved = false;


        public ParticipantViewHolder(View v) {
            super(v);

            editlayout = (CardView) v.findViewById(R.id.item_participants_card_view_edit);
            cardlayout = (CardView) v.findViewById(R.id.item_participants_card_view);
            editDeletelayout = (LinearLayout) v.findViewById(R.id.item_participants_layout_edit_delete);

            etFirstName = (EditText) v.findViewById(R.id.item_participants_et_firstname);
            etLastName = (EditText) v.findViewById(R.id.item_participants_et_lastname);
            etEmail = (EditText) v.findViewById(R.id.item_participants_et_email);
            etPhone = (EditText) v.findViewById(R.id.item_participants_et_phone);
            etInstitute = (EditText) v.findViewById(R.id.item_participants_et_insitute);
            tvNo = (TextView) v.findViewById(R.id.item_participants_tv_no);
            tvSave = (TextView) v.findViewById(R.id.item_participants_tv_save);
            tvCancel = (TextView) v.findViewById(R.id.item_participants_tv_cancel);

            tvNumber = (TextView) v.findViewById(R.id.item_participants_tv_number);
            tvName = (TextView) v.findViewById(R.id.item_participants_tv_name);
            tvInstitute = (TextView) v.findViewById(R.id.item_participants_tv_insitute);
            tvEdit = (TextView) v.findViewById(R.id.item_participants_tv_edit);
            tvDelete = (TextView) v.findViewById(R.id.item_participants_tv_delete);

            tvLabel = (TextView) v.findViewById(R.id.item_participants_tv_label);
            tvAddMore = (TextView) v.findViewById(R.id.item_participants_tv_add_participants);
            tvLabel.setVisibility(View.GONE);
            tvAddMore.setVisibility(View.GONE);
        }
    }


    public ArrayList<ParticipantVo> getDataset() {
        return mDataset;
    }

    private boolean isParticipantEmpty(int position) {
        if (!TextUtils.isEmpty(mDataset.get(position).getFirstname()))
            return false;
        if (!TextUtils.isEmpty(mDataset.get(position).getLastname()))
            return false;
        return true;
    }
}


