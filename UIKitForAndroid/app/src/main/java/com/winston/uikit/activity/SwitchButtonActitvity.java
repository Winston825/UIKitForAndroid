package com.winston.uikit.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.winston.uikit.CursorSeekBar;
import com.winston.uikit.R;
import com.winston.uikit.SwitchButton;

/**
 * Created by tihong on 16-3-2.
 */
public class SwitchButtonActitvity extends Activity {
    private final String TAG = "SwitchButtonActitvity";
    private SwitchButton mSwitchButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_switch_button);
        mSwitchButton = (SwitchButton) findViewById(R.id.switchButton);
        mSwitchButton.setOpened(true);
        Log.i(TAG,"mSwitchButton " + mSwitchButton.isOpened());
        mSwitchButton.setOnStateChangedListener(new SwitchButton.OnStateChangedListener() {
            @Override
            public void toggleToOn(View view) {
                mSwitchButton.toggleSwitch(true);
                Log.i(TAG,"toggleToOn ");
            }

            @Override
            public void toggleToOff(View view) {
                Log.i(TAG,"toggleToOff ");
                mSwitchButton.toggleSwitch(false);

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
