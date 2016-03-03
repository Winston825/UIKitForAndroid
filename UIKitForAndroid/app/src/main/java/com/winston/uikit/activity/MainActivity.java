package com.winston.uikit.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.winston.uikit.R;

public class MainActivity extends Activity {

    private Button mCursorSeekBar_Button;
    private Button mSwitchButton_Button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCursorSeekBar_Button = (Button) findViewById(R.id.cursorSeekBar_Button);
        mCursorSeekBar_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CursorSeekBarActitvity.class);
                startActivity(intent);
            }
        });

        mSwitchButton_Button = (Button)findViewById(R.id.switchButton_Button);
        mSwitchButton_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SwitchButtonActitvity.class);
                startActivity(intent);
            }
        });
    }
}
