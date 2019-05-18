package com.woodyhi.imageselector.demo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.woodyhi.imageselector.IntentImageCrop;
import com.woodyhi.imageselector.IntentImageSelector;
import com.woodyhi.imageselector.OnResultCallback;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = MainActivity.class.getSimpleName();

    public static int DEFAULT_REQUEST_CODE_IMAGE_SELECTOR = 0x2923;
    public static int REQUEST_CODE_IMAGE_SELECTOR_CORP = 0x2924;

    ImageView imageView;
    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = findViewById(R.id.btn);
        imageView = findViewById(R.id.image_view);

        btn.setOnClickListener(v -> {
            IntentImageSelector.startIntentForResult(this, DEFAULT_REQUEST_CODE_IMAGE_SELECTOR);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult -> requestCode:" + requestCode + ", resultCode:" + resultCode + ", data:" + data);

        if (requestCode == DEFAULT_REQUEST_CODE_IMAGE_SELECTOR) {
            IntentImageSelector.onHandleResult(this, resultCode, data, new OnResultCallback() {

                @Override
                public void onResult(Uri uri, String path) {
                    Log.d(TAG, "IntentImageSelector:onHandleResult：  uri : " + uri + "\n"
                            + ", path : " + path);
                    if (uri != null
                            && !IntentImageCrop
                            .startActivityForCorp(MainActivity.this,
                                    path,
                                    200,
                                    200,
                                    REQUEST_CODE_IMAGE_SELECTOR_CORP)) {
                        showImage(path);
                    }
                }
            });
        } else if (requestCode == REQUEST_CODE_IMAGE_SELECTOR_CORP) {
            IntentImageCrop.onHandleResult(resultCode, new OnResultCallback() {
                @Override
                public void onResult(Uri uri, String path) {
                    Log.d(TAG, "IntentImageCrop.onHandleResult：  uri : " + uri + "\n"
                            + ", path : " + path);
                    showImage(path);
                }
            });
        }
    }

    private void showImage(String path) {
        if (path != null) Glide.with(getApplicationContext()).load(path).into(imageView);
    }

}
