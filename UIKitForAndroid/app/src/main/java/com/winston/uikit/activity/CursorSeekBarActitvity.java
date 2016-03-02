package com.winston.uikit.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.winston.uikit.CursorSeekBar;
import com.winston.uikit.R;

/**
 * Created by tihong on 16-3-2.
 */
public class CursorSeekBarActitvity extends Activity {
    private final String TAG = "CursorSeekBarActitvity";
    private CursorSeekBar mCursorSeekBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cursor_seekbar);
        mCursorSeekBar = (CursorSeekBar) findViewById(R.id.rankseekbar);
        mCursorSeekBar.setOnCursorChangeListener(new CursorSeekBar.OnCursorChangeListener() {
            @Override
            public void onCursorChanged(int location, String textMark) {
                Log.i(TAG, "location = " + location + "textMark = " + textMark);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        mCursorSeekBar.setCursorIndex(1);
    }
}
