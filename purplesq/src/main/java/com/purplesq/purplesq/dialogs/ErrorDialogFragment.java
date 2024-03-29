package com.purplesq.purplesq.fragments;


import android.app.Dialog;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.purplesq.purplesq.R;
import com.purplesq.purplesq.utils.PSQConsts;
import com.purplesq.purplesq.vos.ErrorVo;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ErrorDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ErrorDialogFragment extends DialogFragment {

    private ErrorVo errorVo;

    public ErrorDialogFragment() {
        // Required empty public constructor
    }

    public static ErrorDialogFragment newInstance(ErrorVo errorVo) {
        ErrorDialogFragment fragment = new ErrorDialogFragment();
        fragment.errorVo = errorVo;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_error_dialog, container, false);
        getDialog().setCanceledOnTouchOutside(false);

        TextView tvError = (TextView) rootView.findViewById(R.id.fragment_error_dialog_errortext);
        TextView tvAction = (TextView) rootView.findViewById(R.id.fragment_error_dialog_action);

        String errorMsg = errorVo.getBody();
        JSONObject errorJson = null;
        try {
            errorJson = new JSONObject(errorMsg);
            if (errorJson.has(PSQConsts.JSON_PARAM_MESSAGE)) {
                errorMsg = errorJson.getString(PSQConsts.JSON_PARAM_MESSAGE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }

        tvError.setText(errorMsg);
        tvAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return rootView;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(), R.style.BorderlessDialogWithoutDim);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.BOTTOM);

        return dialog;
    }


}
