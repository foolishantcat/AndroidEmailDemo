package me.hanqin.demos.email;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

import me.hanqin.demos.email.util.MailUtility;
import me.hanqin.demos.email.util.SystemUiHider;

public class EmailActivity extends Activity {
    private static final boolean AUTO_HIDE = true;
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
    private static final boolean TOGGLE_ON_CLICK = false;
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;
    private SystemUiHider mSystemUiHider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen);

        findViewById(R.id.intent_broadcast).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MailUtility.sendMail(EmailActivity.this, "Hello Android", "Hello Email");
            }
        });
        findViewById(R.id.intent_broadcast_enhanced).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MailUtility.sendMailEnhanced(EmailActivity.this, "Hello Android", "Hello Email");
            }
        });
        findViewById(R.id.java_mail_api).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MailUtility.sendMailWithJavaApi(EmailActivity.this, "Hello Android", "Hello Email");
            }
        });
        initSystemUiHider();
    }

    private void initSystemUiHider() {
        final View controlsView = findViewById(R.id.fullscreen_content_controls);
        final View contentView = findViewById(R.id.fullscreen_content);

        mSystemUiHider = SystemUiHider.getInstance(this, contentView, HIDER_FLAGS);
        mSystemUiHider.setup();
        mSystemUiHider.setOnVisibilityChangeListener(new SystemUiVisibilityChangeListener(controlsView));

        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TOGGLE_ON_CLICK) {
                    mSystemUiHider.toggle();
                } else {
                    mSystemUiHider.show();
                }
            }
        });

        findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        delayedHide(100);
    }

    View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    Handler mHideHandler = new Handler();
    Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            mSystemUiHider.hide();
        }
    };

    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    private class SystemUiVisibilityChangeListener implements SystemUiHider.OnVisibilityChangeListener {

        private final View controlsView;
        int mControlsHeight;
        int mShortAnimTime;

        public SystemUiVisibilityChangeListener(View controlsView) {
            this.controlsView = controlsView;
        }

        @Override
        @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
        public void onVisibilityChange(boolean visible) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                if (mControlsHeight == 0) {
                    mControlsHeight = controlsView.getHeight();
                }
                if (mShortAnimTime == 0) {
                    mShortAnimTime = getResources().getInteger(
                            android.R.integer.config_shortAnimTime);
                }
                controlsView.animate().translationY(visible ? 0 : mControlsHeight)
                        .setDuration(mShortAnimTime);
            } else {
                controlsView.setVisibility(visible ? View.VISIBLE : View.GONE);
            }

            if (visible && AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
        }
    }
}
