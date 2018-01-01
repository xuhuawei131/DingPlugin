package net.fenzz.dingplug;

import java.util.ArrayList;
import java.util.List;

import android.accessibilityservice.AccessibilityService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

/**
 *
 * http://blog.csdn.net/angcyo/article/details/51100898
 *
 * TYPE_WINDOW_STATE_CHANGED   :32
 * TYPE_VIEW_SELECTED          :4
 * TYPE_VIEW_FOCUSED           :8
 * TYPE_WINDOW_CONTENT_CHANGED :2048
 * TYPE_VIEW_CLICKED           :1
 * TYPE_VIEW_SCROLLED          :4096
 * TYPE_VIEW_TEXT_SELECTION_CHANGED :8192
 * TYPE_VIEW_TEXT_CHANGED           :16
 */
public class DingService extends AccessibilityService {

    private String TAG = getClass().getSimpleName();

    private boolean isFinish = false;
    public static DingService instance;
    private int index = 1;

    public DingService() {
        super();
    }


    @Override
    public void onCreate() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MyConstant.ACTION_START_DING_SERVICE);
        registerReceiver(onBroadcast, intentFilter);
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(onBroadcast);
    }

    /**
     * 获取到短信通知
     * 0.唤醒屏幕
     * 1.打开钉钉
     * 2.确保当前页是主页界面
     * 3.找到“工作”tab并且点击
     * 4.确保到达签到页面
     * 5.找到签到按钮，并且点击
     * 6.判断签到是否成功
     * 1.成功，退出程序
     * 2.失败，返回到主页，重新从1开始签到
     */
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // TODO Auto-generated method stub
//       final int eventType = event.getEventType();
        ArrayList<String> texts = new ArrayList<String>();
        Log.i(TAG, "事件---->" + event.getEventType());
        if (isFinish) {
            return;
        }
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo == null) {
            Log.w(TAG, "rootWindow为空");
            return;
        }
//       nodeInfo.
//       System.out.println("nodeInfo"+nodeInfo);
        System.out.println("index:" + index);
        switch (index) {

            case 1: //进入主页
                OpenHome(event.getEventType(), nodeInfo);
                break;
            case 2: //进入签到页
                OpenQianDao(event.getEventType(), nodeInfo);
                break;
            case 3:
                doQianDao(event.getEventType(), nodeInfo);
                break;

            default:
                break;
        }
    }

    private ArrayList<String> getTextList(AccessibilityNodeInfo node, ArrayList<String> textList) {
        if (node == null) {
            Log.w(TAG, "rootWindow为空");
            return null;
        }
        if (textList == null) {
            textList = new ArrayList<String>();
        }
        String text = node.getText().toString();
        if (text != null && text.equals("")) {
            textList.add(text);
        }
//        node.get
        return null;
    }


    private void OpenHome(int type, AccessibilityNodeInfo nodeInfo) {
        if (type == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            //判断当前是否是钉钉主页
            List<AccessibilityNodeInfo> homeList = nodeInfo.findAccessibilityNodeInfosByText("工作");
            if (!homeList.isEmpty()) {
                //点击
                boolean isHome = click("工作");
                System.out.println("---->" + isHome);
                index = 2;
                System.out.println("点击进入主页签到");
            }
        }
    }

    private void OpenQianDao(int type, AccessibilityNodeInfo nodeInfo) {
        if (type == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
            //判断当前是否是主页的签到页
            List<AccessibilityNodeInfo> qianList = nodeInfo.findAccessibilityNodeInfosByText("工作");
            if (!qianList.isEmpty()) {
                boolean ret = click("签到");
                index = 3;
                System.out.println("点击进入签到页面详情");
            }
//           index = ret?3:1;
        }
    }

    private void doQianDao(int type, AccessibilityNodeInfo nodeInfo) {
        if (type == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
            //判断当前页是否是签到页
            List<AccessibilityNodeInfo> case1 = nodeInfo.findAccessibilityNodeInfosByText("开启我的签到之旅");
            if (!case1.isEmpty()) {
                click("开启我的签到之旅");
                System.out.println("点击签到之旅");
            }
            List<AccessibilityNodeInfo> case2 = nodeInfo.findAccessibilityNodeInfosByText("我知道了");
            if (!case2.isEmpty()) {
                click("我知道了");
                System.out.println("点击我知道对话框");
            }
            List<AccessibilityNodeInfo> case3 = nodeInfo.findAccessibilityNodeInfosByText("签到");
            if (!case3.isEmpty()) {
                Toast.makeText(getApplicationContext(), "发现目标啦！！~~", Toast.LENGTH_SHORT).show();
                System.out.println("发现目标啦！");
                click("签到");
                isFinish = true;
            }
        }

//      if(type == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED){
//          List<AccessibilityNodeInfo> case3 = nodeInfo.findAccessibilityNodeInfosByText("签到");
//          if(!case3.isEmpty()){
//              Toast.makeText(getApplicationContext(), "发现目标啦！！~~", 1).show();
//          }
//      }
    }


    //通过文字点击
    private boolean click(String viewText) {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo == null) {
            Log.w(TAG, "点击失败，rootWindow为空");
            return false;
        }
        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText(viewText);
        if (list.isEmpty()) {
            //没有该文字的控件
            Log.w(TAG, "点击失败，" + viewText + "控件列表为空");
            return false;
        } else {
            //有该控件
            //找到可点击的父控件
            AccessibilityNodeInfo view = list.get(0);
            return onclick(view);  //遍历点击
        }
    }

    private boolean onclick(AccessibilityNodeInfo view) {
        if (view.isClickable()) {
            view.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            Log.w(TAG, "点击成功");
            return true;
        } else {
            AccessibilityNodeInfo parent = view.getParent();
            if (parent == null) {
                return false;
            }
            onclick(parent);
        }
        return false;
    }

    //点击返回按钮事件
    private void back() {
        performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
    }

    @Override
    public void onInterrupt() {

    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.i(TAG, "service connected!");
        Toast.makeText(getApplicationContext(), "连接成功！", Toast.LENGTH_SHORT).show();
        instance = this;
    }

    public void setServiceEnable() {
        isFinish = false;
        Toast.makeText(getApplicationContext(), "服务可用开启！", Toast.LENGTH_SHORT).show();
        index = 1;
    }

    private BroadcastReceiver onBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            isFinish = false;
            Toast.makeText(getApplicationContext(), "服务可用开启！", 1).show();
            index = 1;
        }
    };
}