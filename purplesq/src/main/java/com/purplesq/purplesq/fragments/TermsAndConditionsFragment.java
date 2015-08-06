package com.purplesq.purplesq.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.purplesq.purplesq.R;

/**
 * Created by nishant on 06/08/15.
 */
public class TermsAndConditionsFragment extends Fragment {

    public TermsAndConditionsFragment() {
        // Required empty public constructor
    }

    public static TermsAndConditionsFragment newInstance() {
        TermsAndConditionsFragment fragment = new TermsAndConditionsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        WebView rootWebView = (WebView) inflater.inflate(R.layout.fragment_terms_n_conditions, container, false);

        rootWebView.loadUrl("http://purplesq.com/home/terms-and-conditions/");

        return rootWebView;
    }
}
