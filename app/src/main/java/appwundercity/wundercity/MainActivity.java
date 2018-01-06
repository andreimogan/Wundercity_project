package appwundercity.wundercity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

    private static final String target_url = "http://wundercity.de";
    private static final String target_url_prefix = "http://wundercity.de";
    private Context mContext;
    private WebView mWebview;
    private WebView mWebviewPop;
    private FrameLayout mContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        mWebview = (WebView) findViewById(R.id.webView);

        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        mWebview.getSettings().setLoadWithOverviewMode(true);
        mContainer = (FrameLayout) findViewById(R.id.webview_frame);
        WebSettings webSettings = mWebview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setSupportMultipleWindows(true);
        mWebview.getSettings().setJavaScriptEnabled(true);
        mWebview.getSettings().setAllowFileAccess(true);
        mWebview.getSettings().setGeolocationEnabled(true);
        mWebview.getSettings().setUseWideViewPort(true);
        mWebview.getSettings().setLoadWithOverviewMode(true);
        mWebview.setWebViewClient(new UriWebViewClient());
        mWebview.setWebChromeClient(new UriChromeClient());

        mWebview.loadUrl(target_url);

        mContext = this.getApplicationContext();
        // disable scroll on touch
        mWebview.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                return (event.getAction() == MotionEvent.ACTION_MOVE);
            }
        });

     //   ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) mWebview.getLayoutParams();
      //  p.height = mWebview.getHeight();
       // Log.e(" webpage.getHeight()", String.valueOf(mWebview.getHeight()));
        //mWebview.setLayoutParams(p);

    }

    private class UriWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // When user clicks a hyperlink, load in the existing WebView
            if(url.contains("https://www.google.com/maps/"))
            {
                Uri IntentUri = Uri.parse(url);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, IntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");

                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                }
                return true;
            }
            view.loadUrl(url);
            return true;
        }


        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler,
                                       SslError error) {
            Log.d("onReceivedSslError", "onReceivedSslError");
            //super.onReceivedSslError(view, handler, error);
        }
    }

    class UriChromeClient extends WebChromeClient {

        private String filePath;
        private ValueCallback<Uri> mUploadMessage;

        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
            mWebviewPop = new WebView(mContext);
            mWebviewPop.setVerticalScrollBarEnabled(false);
            mWebviewPop.setHorizontalScrollBarEnabled(false);
            mWebviewPop.setWebViewClient(new UriWebViewClient());
            mWebviewPop.getSettings().setJavaScriptEnabled(true);
            mWebview.getSettings().setDomStorageEnabled(true);
            mWebviewPop.getSettings().setSavePassword(false);
            mWebviewPop.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            mContainer.addView(mWebviewPop);
            WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
            transport.setWebView(mWebviewPop);
            resultMsg.sendToTarget();
            return true;
        }

        @Override
        public void onCloseWindow(WebView window) {
            Log.d("onCloseWindow", "called");
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebview.canGoBack()) {
            mWebview.goBack();
            return true;
        }
        else
        {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
