```java
package com.alrabbqarib.app;

import android.app.Activity;
import android.os.Bundle;
import android.os.Build;
import android.Manifest;
import android.content.pm.PackageManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.view.View;
import android.webkit.WebResourceRequest;

import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends Activity {

    private WebView webView;

    private static final String BLOG_URL =
            "https://qabelteshemshatan.blogspot.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        );

        requestNotificationPermission();

        FirebaseMessaging.getInstance().subscribeToTopic("blog_updates");

        webView = new WebView(this);
        setContentView(webView);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(
                    WebView view, WebResourceRequest request) {
                return false;
            }
        });

        webView.setWebChromeClient(new WebChromeClient());

        openNotificationPage(getIntent());
    }

    private void openNotificationPage(android.content.Intent intent) {

        String url = null;

        if (intent != null) {
            url = intent.getStringExtra("url");
        }

        if (url != null &&
                url.startsWith(BLOG_URL)) {
            webView.loadUrl(url);
        } else {
            webView.loadUrl(BLOG_URL);
        }
    }

    @Override
    protected void onNewIntent(android.content.Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        openNotificationPage(intent);
    }

    private void requestNotificationPermission() {

        if (Build.VERSION.SDK_INT >= 33 &&
                checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    100
            );
        }
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
```
