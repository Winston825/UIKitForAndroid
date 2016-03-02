package com.winston.uikit.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.winston.uikit.CursorSeekBar;
import com.winston.uikit.R;

public class MainActivity extends Activity {

    private Button cursorSeekBar_Button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cursorSeekBar_Button = (Button) findViewById(R.id.cursorSeekBar_Button);
        cursorSeekBar_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CursorSeekBarActitvity.class);
                startActivity(intent);
            }
        });
    }
}
