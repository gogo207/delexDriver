package com.delex.portal;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.delex.driver.R;
import com.delex.utility.SessionManager;
import com.delex.utility.Utility;


/**
 * Created by anubhootigupta on 29/11/16.
 */

public class PortalFrag extends Fragment {
    private ProgressDialog mDialog;
    private WebView webView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("vrmall", "vre");
        mDialog = new ProgressDialog(getContext());
        mDialog.setMessage(getResources().getString(R.string.loading));
        mDialog.setCancelable(false);
        mDialog.show();

    }

    /******************************************************/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.portal_frag, null);
        Log.d("vrmall5", "vre");
        initialization(view);

        return view;
    }

    /******************************************************/

    private void initialization(View mView) {
        SessionManager session = new SessionManager(getActivity());

        webView = (WebView) mView.findViewById(R.id.webView1);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String ur1l) {
                if (mDialog != null) {
                    mDialog.dismiss();
                    mDialog.cancel();
                }
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            //If you will not use this method url links are open in new browser not in web view
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                Utility.printLog("URL " + url);
                /*if (url1.contains("summary.php")) {
                    //  actionBar.setTitle(getResources().getString(R.string.SUMMARY));
                    //isSummaryOpen=true;
                }*/
                return true;
            }

            //Show loader on url load
            public void onLoadResource(WebView view, String url) {

            }

            public void onPageFinished(WebView view, String url) {
                if (mDialog != null) {
                    mDialog.dismiss();
                    mDialog.cancel();
                }
            }

        });

        Utility.printLog("Portal frag dashboard link " + "https://portal.ebba.cr/?" + session.getMid());
//        webView.loadUrl("http://192.241.153.106/payrollform/tabsform.html?" + session.getMid());
        webView.loadUrl("https://portal.ebba.cr/?" + session.getMid());
        webView.getSettings().setJavaScriptEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {

            if (webView.canGoBack()) {
                webView.goBack();
                webView.clearHistory();
            } else {
                // Let the system handle the back button
                getActivity().finish();
                getActivity().overridePendingTransition(R.anim.stay, R.anim.top_to_bottom);
            }
            return true;

        }
        return super.onOptionsItemSelected(item);
    }

    public void onBackPress(){
        if (webView.canGoBack()) {
            webView.goBack();
            webView.clearHistory();
        } else {
            // Let the system handle the back button
 /*           Intent intent=new Intent(getContext(), HomeFrag2.class);
            getContext().startActivity(intent);
//*/            getActivity().overridePendingTransition(R.anim.stay, R.anim.top_to_bottom);
        }
    }

}