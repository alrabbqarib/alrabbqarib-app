package com.alrabbqarib.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Build;
import android.Manifest;
import android.content.pm.PackageManager;
import android.content.ClipboardManager;
import android.content.ClipData;
import android.content.Context;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends Activity {
    private WebView webView;

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

        webView.loadUrl("https://qabelteshemshatan.blogspot.com/");

        getFCMToken();
    }

    private void getFCMToken() {
        FirebaseMessaging.getInstance().getToken()
            .addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Toast.makeText(this,
                        "تعذر الحصول على رمز الإشعارات",
                        Toast.LENGTH_LONG).show();
                    return;
                }

                String token = task.getResult();
                showTokenDialog(token);
            });
    }

    private void showTokenDialog(String token) {
        new AlertDialog.Builder(this)
            .setTitle("رمز إشعارات التطبيق FCM")
            .setMessage(token)
            .setPositiveButton("نسخ الرمز", (dialog, which) -> {
                ClipboardManager clipboard =
                    (ClipboardManager) getSystemService(
                        Context.CLIPBOARD_SERVICE);

                ClipData clip = ClipData.newPlainText("FCM Token", token);
                clipboard.setPrimaryClip(clip);

                Toast.makeText(this,
                    "تم نسخ الرمز",
                    Toast.LENGTH_SHORT).show();
            })
            .setNegativeButton("إغلاق", null)
            .show();
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
