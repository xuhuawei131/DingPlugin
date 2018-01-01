package net.fenzz.dingplug;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import static net.fenzz.dingplug.MyConstant.ACTION_START_DING_SERVICE;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
//		Utils.openCLD("com.alibaba.android.rimet", this);
        System.out.println("start ...");
    }

    public void ServiceEnable(View v) {
        if (Utils.isServiceEnabled(this, DingService.class)) {
            Utils.jump2Setting(this);
        } else {
            sendBroadcast(new Intent(ACTION_START_DING_SERVICE));
        }
    }
}
