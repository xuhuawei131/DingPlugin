package net.fenzz.dingplug;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View button2 = findViewById(R.id.button2);
        button2.setOnClickListener(listener);

        View button3 = findViewById(R.id.button3);
        button3.setOnClickListener(listener);
    }

    @Override
    protected void onStart() {
        super.onStart();
//		Utils.openCLD("com.alibaba.android.rimet", this);
        System.out.println("start ...");
    }


    public void ServiceEnable(View v) {
        AccessHelper.jumpAccessSettingPage(MainActivity.this, DingService.class);
        //  DingService.instance.setServiceEnable();
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.button1:
                    AccessHelper.jumpAccessSettingPage(MainActivity.this, DingService.class);
                    break;
                case R.id.button2:
                    Utils.openCLD("com.alibaba.android.rimet", MainActivity.this);
                    break;
                case R.id.button3:
                    DingService.instance.setServiceEnable();
                    break;
            }
        }
    };
}
