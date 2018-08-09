package com.ysnows.wxshareapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new AccessibilityUtils().openAccessibility(AssistantService.class.getName(), this);

    }

    public void share(View view) {
        ArrayList<String> urls = new ArrayList<>();
        urls.add("https://upload.jianshu.io/users/upload_avatars/752480/e46fb48f8772.jpeg?imageMogr2/auto-orient/strip|imageView2/1/w/120/h/120");
        urls.add("https://upload.jianshu.io/users/upload_avatars/752480/e46fb48f8772.jpeg?imageMogr2/auto-orient/strip|imageView2/1/w/120/h/120");
        WxShareUtil.sharePhotoToWX(this, "HelloWorld", urls);
    }

}
