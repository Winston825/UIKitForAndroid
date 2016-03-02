package com.winston.uikit;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends Activity {

    private CursorSeekBar mCursorSeekBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCursorSeekBar = (CursorSeekBar) findViewById(R.id.rankseekbar);
        mCursorSeekBar.setOnCursorChangeListener(new CursorSeekBar.OnCursorChangeListener() {
            @Override
            public void onCursorChanged(int location, String textMark) {
                Log.i("mRangeSeekBar", "location = " + location + "textMark = " + textMark);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        mCursorSeekBar.setCursorIndex(1);
    }
}
