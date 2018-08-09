package com.ysnows.wxshareapp;

import android.accessibilityservice.AccessibilityService;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

/**
 * 助手服务类
 * Created by zhangshuo on 2017/2/23.
 */

public class AssistantService extends AccessibilityService {

    private final String TAG = AssistantService.class.getSimpleName();


    /**
     * 助手服务是否正在运行
     */
    public static boolean isAssistantRunning = false;


    /**
     * 必须重写的方法：此方法用了接受系统发来的event。在你注册的event发生时被调用。在整个生命周期会被调用多次。
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        int eventType = event.getEventType();
        switch (eventType) {
            //窗口发生改变时会调用该事件
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                String className = event.getClassName().toString();
                Log.d(TAG, "窗口有变化：" + className);
                /*
      微信几个页面的包名+地址。用于判断在哪个页面
     */
//                String timeLineUi = "com.tencent.mm.ui.tools.ShareToTimeLineUI";
                String SnsLineUi = "com.tencent.mm.plugin.sns.ui.SnsUploadUI";
                if (className.equals(SnsLineUi)) {//发送朋友圈的界面
                    AccessibilityNodeInfo rootNode = getRootInActiveWindow();

                    if (rootNode == null) {
                        return;
                    }

                    List<AccessibilityNodeInfo> edt = rootNode.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/djk");
                    if (edt.size() > 0) {
                        Bundle arguments = new Bundle();
                        arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, WxShareUtil.textString);
                        AccessibilityNodeInfo edtView = edt.get(0);

                        edtView.performAction(AccessibilityNodeInfo.FOCUS_INPUT);
                        edtView.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
//                      edt.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }


                }

                break;
            default:
                break;
        }
    }

    @Override
    public void onInterrupt() {

    }


    /**
     * 服务已连接
     */
    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        isAssistantRunning = true;
    }


    /**
     *   AccessibilityService中常用的方法的介绍
     disableSelf()：禁用当前服务，也就是在服务可以通过该方法停止运行
     findFoucs(int falg)：查找拥有特定焦点类型的控件
     getRootInActiveWindow()：如果配置能够获取窗口内容,则会返回当前活动窗口的根结点
     getSeviceInfo()：获取当前服务的配置信息
     onAccessibilityEvent(AccessibilityEvent event)：有关AccessibilityEvent事件的回调函数，系统通过sendAccessibiliyEvent()不断的发送AccessibilityEvent到此处
     performGlobalAction(int action)：执行全局操作，比如返回，回到主页，打开最近等操作
     setServiceInfo(AccessibilityServiceInfo info)：设置当前服务的配置信息
     getSystemService(String name)：获取系统服务
     onKeyEvent(KeyEvent event)：如果允许服务监听按键操作，该方法是按键事件的回调，需要注意，这个过程发生了系统处理按键事件之前
     onServiceConnected()：系统成功绑定该服务时被触发，也就是当你在设置中开启相应的服务，系统成功的绑定了该服务时会触发，通常我们可以在这里做一些初始化操作
     onInterrupt()：服务中断时的回调
     */

    /**
     *   AccessibilityEvent的方法
     getEventType()：事件类型
     getSource()：获取事件源对应的结点信息
     getClassName()：获取事件源对应类的类型，比如点击事件是有某个Button产生的，那么此时获取的就是Button的完整类名
     getText()：获取事件源的文本信息，比如事件是有TextView发出的,此时获取的就是TextView的text属性。如果该事件源是树结构，那么此时获取的是这个树上所有具有text属性的值的集合
     isEnabled()：事件源(对应的界面控件)是否处在可用状态
     getItemCount()：如果事件源是树结构，将返回该树根节点下子节点的数量
     */
}
