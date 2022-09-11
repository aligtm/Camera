package com.techkiii;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.techkiii.Camera.CameraActivity;

public class WifiCameraActivity_Backup extends AppCompatActivity {

    ImageView ivCancel;
    String cameraAddress = "";
    Intent intent;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_camera_view);

        ivCancel = findViewById(R.id.iv_cancel);
        WebView webView = (WebView) findViewById(R.id.webview);
        //progressBar = findViewById(R.id.progressBar);

        intent = getIntent();
        cameraAddress = intent.getStringExtra("camAddress");


        //String url = "http://192.168.43.178:6677";
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.setData(Uri.parse(url));
//        startActivity(intent);

        //setContentView(webview);

        webView.setWebViewClient(new WebViewClient(){
            boolean anyError = false;
            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
                anyError = false;
            }

            @Override public void onReceivedError(WebView view, WebResourceRequest request,
                                                  WebResourceError error) {
                super.onReceivedError(view, request, error);
                // Do something
                anyError = true;
                view.setVisibility(View.GONE);
                //view.setBackgroundColor(Color.BLACK);
            }

            // Load the URL
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                //return super.shouldOverrideUrlLoading(view, request);
                view.loadUrl(request.getUrl().getPath());
                return false;
            }

            // ProgressBar will disappear once page is loaded
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                //progressBar.setVisibility(View.GONE);
                if(anyError) {
                    view.reload();
                } else {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    view.setVisibility(View.VISIBLE);
                }
            }
        });
        webView.loadUrl(cameraAddress);
//        try {
//            Thread.sleep(10000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        ivCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(WifiCameraActivity_Backup.this, CameraActivity.class);
                startActivity(intent1);
            }
        });
    }

}
