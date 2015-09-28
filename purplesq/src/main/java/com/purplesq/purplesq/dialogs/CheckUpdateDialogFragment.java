package com.purplesq.purplesq.dialogs;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.purplesq.purplesq.R;
import com.purplesq.purplesq.vos.VersionVo;

/**
 * Created by nishant on 26/09/15.
 */
public class CheckUpdateDialogFragment extends DialogFragment {

    private VersionVo versionVo;
    private boolean isForced = false;

    private String whatsNew = "";

    private TextView versionNameTextView, whatsNewTextView;

    private Button laterButton, okButton;

    public CheckUpdateDialogFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_update_dialog, container);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        versionNameTextView = (TextView) view.findViewById(R.id.update_dialog_tv_versionname);
        whatsNewTextView = (TextView) view.findViewById(R.id.update_dialog_tv_whats_new);

        laterButton = (Button) view.findViewById(R.id.update_dialog_button_later);
        okButton = (Button) view.findViewById(R.id.update_dialog_button_ok);

        Bundle bundle = getArguments();
        this.versionVo = bundle.getParcelable("versionVo");
        this.isForced = bundle.getBoolean("forced");

        versionNameTextView.setText("Version : " + versionVo.getCurrentVersionName());

        for (int i = 0; i < versionVo.getWhatsnew().size(); i++) {
            whatsNew = whatsNew + versionVo.getWhatsnew().get(i).getChangelog() + "\n\n";
        }

        whatsNewTextView.setText(whatsNew);

        if (isForced) {
            laterButton.setText("Close App");
        }

        laterButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
                if (isForced) {
                    getActivity().finish();
                }
            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMarket();
            }
        });

        setCancelable(false);
        return view;
    }

    private void goToMarket() {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getActivity().getPackageName())));
    }


}
