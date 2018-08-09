package com.ysnows.wxshareapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<String> urls;
    private ArrayList<Integer> ids;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new AccessibilityUtils().openAccessibility(AssistantService.class.getName(), this);
        ids = new ArrayList<>();
        ids.add(R.id.img1);
        ids.add(R.id.img2);
        ids.add(R.id.img3);

        urls = new ArrayList<>();
//        urls.add("http://img4.imgtn.bdimg.com/it/u=1138118090,868285634&fm=27&gp=0.jpg");
//        urls.add("http://img5.imgtn.bdimg.com/it/u=33602104,2461243711&fm=27&gp=0.jpg");
//        urls.add("http://img0.imgtn.bdimg.com/it/u=2889218585,4204387241&fm=27&gp=0.jpg");

        urls.add("http://img.lanrentuku.com/img/allimg/1601/5-1601121202370-L.jpg");
        urls.add("http://img.lanrentuku.com/img/allimg/1503/5-1503141603130-L.jpg");
        urls.add("http://img.lanrentuku.com/img/allimg/1503/5-15031H030430-L.jpg");

        for (int i = 0; i < urls.size(); i++) {
            Glide.with(this)
                    .load(urls.get(i))
                    .into((ImageView) findViewById(ids.get(i)));
        }

    }

    public void share(View view) {
        WxShareUtil.sharePhotoToWX(this, ((TextView) findViewById(R.id.tv)).getText().toString(), urls);
    }

}
