通过Intent的方式条用微信发圈页面并传递相关参数：

一、代码文件 WeiXinShareUtil

```
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.widget.Toast;

import java.io.File;

/**
 * Created by Administrator on 2015/6/24.
 */
public class WeiXinShareUtil {
    public static void sharePhotoToWX(Context context, String text, String photoPath) {
        if (!uninstallSoftware(context, "com.tencent.mm")) {
            Toast.makeText(context, "微信没有安装！", Toast.LENGTH_SHORT).show();
            return;
        }

        File file = new File(photoPath);
        if (!file.exists()) {
            String tip = "文件不存在";
            Toast.makeText(context, tip + " path = " + photoPath, Toast.LENGTH_LONG).show();
            return;
        }

        Intent intent = new Intent();
        ComponentName componentName = new ComponentName("com.tencent.mm",
                "com.tencent.mm.ui.tools.ShareToTimeLineUI");
        intent.setComponent(componentName);
        intent.setAction("android.intent.action.SEND");
        intent.setType("image/*");
        intent.putExtra("Kdescription", text);
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        context.startActivity(intent);
    }

    private static boolean uninstallSoftware(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);
            if (packageInfo != null) {
                return true;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }
}
```

二、调用方式
      `
		WeiXinShareUtil.sharePhotoToWX(context, "test", photoPath);`
                第一个参数：上下文Context
              第二次参数：你要分享的文字text
            第三个参数：你要分享的图片路径photoPath


[参考文章](http://blog.csdn.net/langtuteng136/article/details/46621941)


关于7.0分享多图出现的问题，可以参考这篇文章：[使用原生intent分享图片获取资源失败问题
](http://blog.csdn.net/vv_gool/article/details/53230504)


三、 补充说明：
        安卓微信6.6.7之后，微信对'Kdescription'做了处理，所有文字带不过去了。
![image.png](https://upload-images.jianshu.io/upload_images/752480-858383cfd81cdd94.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

四、微信 6.6.7不能传送文字的问题的解决方案
可以通过安卓的`辅助服务`功能，实现自动复制。就是`监测微信发送朋友圈的页面->通过辅助服务把文字写入到Edittext`。具体的代码已经放到下边的[github仓库](https://github.com/ysnows/wxshareapp)里了。


#### Github开源地址
  1.  [wxshareapp](https://github.com/ysnows/wxshareapp)