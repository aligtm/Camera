package com.techkiii;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.techkiii.Camera.CameraActivity;

public class WifiCameraActivity extends AppCompatActivity {

    ImageView ivBack;
    String[] wifiCameraAddress = {"192.168.43.231:6677",
                                    "192.168.43.178:6677",
                                    "192.168.43.44:6677",
                                    "192.168.43.214:6677"};
    //Intent intent;
    ProgressBar progressBar;
    WebView webView;
    RelativeLayout rv_1, rv_2, rv_3, rv_4;
    EditText addCamera1, addCamera2, addCamera3, addCamera4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_camera_demo);

        ivBack = findViewById(R.id.iv_back);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        webView = (WebView) findViewById(R.id.wifiCameraWebView);

        rv_1 = findViewById(R.id.rv_1);
        rv_2 = findViewById(R.id.rv_2);
        rv_3 = findViewById(R.id.rv_3);
        rv_4 = findViewById(R.id.rv_4);
        //progressBar = findViewById(R.id.progressBar);

        addCamera1 = (EditText) findViewById(R.id.add_camera_1);
        addCamera2 = (EditText) findViewById(R.id.add_camera_2);
        addCamera3 = (EditText) findViewById(R.id.add_camera_3);
        addCamera4 = (EditText) findViewById(R.id.add_camera_4);

        addCamera1.setText(wifiCameraAddress[0]);
        addCamera2.setText(wifiCameraAddress[1]);
        addCamera3.setText(wifiCameraAddress[2]);
        addCamera4.setText(wifiCameraAddress[3]);
        //intent = getIntent();
        //cameraAddress = intent.getStringExtra("camAddress");

        //String url = "http://192.168.43.178:6677";
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.setData(Uri.parse(url));
//        startActivity(intent);

        //setContentView(webview);
        setWebViewClient(webView);
        //webView.loadUrl(wifiCameraAddress[2]);

//        try {
//            Thread.sleep(10000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        ivBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(WifiCameraActivity.this, CameraActivity.class);
                startActivity(intent1);
            }
        });

        rv_1.setOnClickListener(v -> {
            findViewById(R.id.input_lyout).setVisibility(View.GONE);
            //webView.loadUrl(wifiCameraAddress[0]);
            webView.loadUrl(addCamera1.getText().toString());
            //setWebViewClient(webView);
            //webView.loadUrl(wifiCameraAddress[0]);
            //webView.reload();
        });

        rv_2.setOnClickListener(v -> {
            //webView.loadUrl(wifiCameraAddress[1]);
            webView.loadUrl(addCamera2.getText().toString());
            //setWebViewClient(webView);
            //webView.loadUrl(wifiCameraAddress[1]);
            //webView.reload();
        });

        rv_3.setOnClickListener(v -> {
            //webView.loadUrl(wifiCameraAddress[2]);
            webView.loadUrl(addCamera3.getText().toString());
            //setWebViewClient(webView);
            //webView.loadUrl(wifiCameraAddress[2]);
            //webView.reload();
        });

        rv_4.setOnClickListener(v -> {
            //webView.loadUrl(wifiCameraAddress[3]);
            webView.loadUrl(addCamera4.getText().toString());
            //setWebViewClient(webView);
            //webView.loadUrl(wifiCameraAddress[3]);
            //webView.reload();
        });
    }

    public void setWebViewClient(WebView webView){
        webView.setWebViewClient(new MyWebViewClient());
    }

    class MyWebViewClient extends WebViewClient {
        boolean anyError = false;
        int reloadCount = 0;

        boolean loadingFinished = true;
        boolean redirect = false;

        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
            //progressBar.setVisibility(View.VISIBLE);
            anyError = false;
            findViewById(R.id.textView).setVisibility(View.GONE);
            //reloadCount = 0;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap facIcon) {
            loadingFinished = false;
            //SHOW LOADING IF IT ISNT ALREADY VISIBLE
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request,
                WebResourceError error) {
            super.onReceivedError(view, request, error);
            // Do something
            anyError = true;
            //view.setVisibility(View.GONE);
            //progressBar.setVisibility(View.VISIBLE);
            //view.setBackgroundColor(Color.BLACK);
//            try {
//                Thread.sleep(5000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            reloadCount++;
            if(reloadCount < 2) {
                view.reload();
            }
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String urlNewString) {
            if (!loadingFinished) {
                redirect = true;
            }

            loadingFinished = false;
            view.loadUrl(urlNewString);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
//            if(!anyError || reloadCount >= 2) {
//                progressBar.setVisibility(View.GONE);
//            }
            progressBar.setVisibility(View.GONE);
//            if(!redirect){
//                loadingFinished = true;
//            }
//
//            if(loadingFinished && !redirect){
//                //HIDE LOADING IT HAS FINISHED
//            } else{
//                redirect = false;
//            }

        }

        // Load the URL
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return super.shouldOverrideUrlLoading(view, request);
            //view.loadUrl(request.getUrl().getPath());
            //return true;
        }

    }
}
