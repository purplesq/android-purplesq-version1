package com.payu.sdk;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.apache.http.util.EncodingUtils;

public class ProcessPaymentActivity extends FragmentActivity {
    private WebView webView;
    private BroadcastReceiver mReceiver = null;
    boolean cancelTransaction = false;

    @Override
    @android.webkit.JavascriptInterface
    protected void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            super.onCreate(null);
            finish();//call activity u want to as activity is being destroyed it is restarted
        } else {
            super.onCreate(savedInstanceState);
        }


        setContentView(R.layout.activity_process_payment);
        webView = (WebView) findViewById(R.id.webview);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);


        webView.getSettings().setSupportMultipleWindows(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.addJavascriptInterface(new Object() {
            @android.webkit.JavascriptInterface
            public void onSuccess() {
                onSuccess("");
            }

            @android.webkit.JavascriptInterface
            public void onSuccess(final String result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent();
                        intent.putExtra("result", result);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
//                }
                });
            }

            @android.webkit.JavascriptInterface
            public void onFailure() {
                onFailure("");
            }

            @android.webkit.JavascriptInterface
            public void onFailure(final String result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent();
                        intent.putExtra("result", result);
                        setResult(RESULT_CANCELED, intent);
                        finish();
                    }
                });
            }
        }, "PayU");

        webView.setWebChromeClient(new WebChromeClient() {

        });
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (url.contains("http://purplesq.com/events/")) {
                    final String result = url.substring((url.indexOf("=")) + 1);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent();
                            intent.putExtra("result", result);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    });
                }
            }
        });

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.postUrl(Constants.PAYMENT_URL, EncodingUtils.getBytes(getIntent().getExtras().getString("postData"), "base64"));
    }


    @Override
    public void onBackPressed() {
        boolean disableBack = false;
        if (cancelTransaction) {
            cancelTransaction = false;
            Intent intent = new Intent();
            intent.putExtra("result", "Transaction canceled due to back pressed!");
            setResult(RESULT_CANCELED, intent);
            super.onBackPressed();
            return;
        }
        try {
            Bundle bundle = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA).metaData;
            disableBack = bundle.containsKey("payu_disable_back") && bundle.getBoolean("payu_disable_back");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (!disableBack) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            ;
            alertDialog.setCancelable(false);
            alertDialog.setMessage("Do you really want to cancel the transaction ?");
            alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    cancelTransaction = true;
                    dialog.dismiss();
                    onBackPressed();
                }
            });
            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alertDialog.show();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        webView.clearCache(true);
        webView.clearHistory();
        webView.destroy();
    }

}