package com.ysnows.wxshareapp;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.Toast;

import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**
 * Created by weihuagu on 2017/7/5.
 */

public class WxShareUtil {

    private static final String SHARE_PIC_NAME = "Name";
    public static String textString;//要分享得文字
    private static int needDownloadSize = 0;//需要下载的图片数量
    private static int downloadedSize = 0;//已经下载的图片数量

    private static ArrayList<Uri> uriList;//图片的uri
    private static ProgressDialog progressDialog;

    /**
     * @param context
     * @param text 分享得文字
     * @param imgUrls 网络图片urls
     * <p>
     * 分享网络图片到朋友圈
     */
    public static void sharePhotoToWX(final Context context, String text, final ArrayList<String> imgUrls) {
        if (!checkAppInstalled(context)) {
            return;
        }

        initFields(text, imgUrls);

        AndPermission.with(context)
                .runtime()
                .permission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        startDownImg(imgUrls, context);
                    }
                })
                .start();

    }

    /**
     * 重置一些参数
     *
     * @param text
     * @param imgUrls
     */
    private static void initFields(String text, ArrayList<String> imgUrls) {
        uriList = new ArrayList<>();
        for (String imgUrl : imgUrls) {
            uriList.add(null);
        }

        textString = text;
        needDownloadSize = 0;
        downloadedSize = 0;
    }

    /**
     * @param imgUrls 下载图片
     */
    private static void startDownImg(ArrayList<String> imgUrls, Context context) {
        needDownloadSize = imgUrls.size();
        downloadedSize = 0;
        //先下载图片

        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("正在下载图片...");
        progressDialog.setIndeterminate(true);
        progressDialog.show();
        for (int i = 0; i < needDownloadSize; i++) {
            if (!TextUtils.isEmpty(imgUrls.get(i))) {//判断图片地址是否存在
                okHttpSaveImg(imgUrls.get(i), i, context);
            } else {
                needDownloadSize--;
            }
        }
    }

    /**
     * 分享
     */
    private static void share(Context context) {

        ComponentName componentName = new ComponentName("com.tencent.mm",
                "com.tencent.mm.ui.tools.ShareToTimeLineUI");
        Intent intent = new Intent();
        intent.setComponent(componentName);
        intent.setType("image/*");

        if (uriList.size() > 1) {
            intent.setAction(Intent.ACTION_SEND_MULTIPLE);
            intent.putExtra(Intent.EXTRA_STREAM, uriList); //图片数据（支持本地图片的Uri形式）
        } else if (uriList.size() == 1) {
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_STREAM, uriList.get(0)); //图片数据（支持本地图片的Uri形式）
        }
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.putExtra("Kdescription", textString);
        if (context != null) {
            context.startActivity(intent);
        }

    }


    /**
     * 检查微信是否安装
     *
     * @param context
     *
     * @return
     */
    private static boolean checkAppInstalled(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo("com.tencent.mm", PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);
            if (packageInfo != null) {
                return true;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Toast.makeText(context, "您还没有安装微信", Toast.LENGTH_SHORT).show();
        return false;
    }

    private static String insertImageToSystem(Context context, String imagePath) {
        String url = "";
        try {
            if (context != null) {
                url = MediaStore.Images.Media.insertImage(context.getContentResolver(), imagePath, SHARE_PIC_NAME, "你对图片的描述");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return url;
    }


    /**
     * 下载图片
     *
     * @param imgUrl
     * @param index 图片原来的排列序号
     * @param context
     */
    private static void okHttpSaveImg(String imgUrl, final int index, final Context context) {

        OkHttpUtils
                .get()
                .url(imgUrl)
                .build()
                .execute(new FileCallBack(context.getFilesDir().getAbsolutePath(), index + "img.jpg")//
                {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        needDownloadSize--;
                        checkDownload(context);
                    }

                    @Override
                    public void onResponse(File response, int id) {
                        if (response != null) {
                            Uri mUri;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                /*
                                  Android 7.0以上的方式
                                 */
                                mUri = Uri.parse(insertImageToSystem(context, response.getAbsolutePath()));
                            } else {
                                mUri = Uri.fromFile(response);
                            }

                            try {
                                uriList.set(index, mUri);
                                downloadedSize++;
                                checkDownload(context);
                            } catch (Exception ignored) {

                            }
                        }
                    }
                });


    }

    /**
     * 检查是否全部下载成功
     *
     * @param context
     */
    private static void checkDownload(Context context) {
        if (downloadedSize >= needDownloadSize) {//所有图片下载完成
            //并且图片全都替换好了
            progressDialog.dismiss();
            share(context);
        }

    }
}
