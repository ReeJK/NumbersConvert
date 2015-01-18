package com.app.numconv;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.ActionBarActivity;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

public class AppActivity extends ActionBarActivity {
    private GestureDetectorCompat _gestureDetector;
    protected SharedPreferences _preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        _preferences = PreferenceManager.getDefaultSharedPreferences(this);
        applyTheme();

        super.onCreate(savedInstanceState);

        _gestureDetector = new GestureDetectorCompat(this, new GestureDetector.SimpleOnGestureListener () {
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                return super.onDoubleTap(e);
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                float x1 = e1.getX();
                float y1 = e1.getY();
                float x2 = e2.getX();
                float y2 = e2.getY();

                float dx = x1 - x2;
                float dy = y1 - y2;

                float d = Math.abs(dx / dy);
                if(0.75 < d && d < 1.25) {
                    return false;
                }

                final float dpi = getResources().getDisplayMetrics().density;

                if(Math.abs(dx) < Math.abs(dy)) {
                    if(Math.abs(dy) > dpi) {
                        onVerticalSwipe(dy < 0);
                        return true;
                    }
                } else {
                    if(Math.abs(dx) > dpi) {
                        onHorizontalSwipe(dx < 0);
                        return true;
                    }
                }

                return false;
            }
        });
    }

    @Override
    public View onCreateView(String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        View view = super.onCreateView(name, context, attrs);

        /*View.OnTouchListener touchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                _gestureDetector.onTouchEvent(event);
                return false;
            }
        };

        if(view != null)
            view.setOnTouchListener(touchListener);*/

        return view;
    }

    private void applyTheme() {
        String theme = _preferences.getString(getResources().getString(R.string.pref_id_color_theme), "Blue");
        if (theme.equals("Black")) {
            setTheme(R.style.AppTheme_Black);
        } else if (theme.equals("Blue")) {
            setTheme(R.style.AppTheme_Blue);
        } else if (theme.equals("Green")) {
            setTheme(R.style.AppTheme_Green);
        } else if (theme.equals("Red")) {
            setTheme(R.style.AppTheme_Red);
        } else if (theme.equals("White")) {
            setTheme(R.style.AppTheme_White);
        }
    }

    protected void onHorizontalSwipe(boolean leftToRight) {
    }

    protected void onVerticalSwipe(boolean upToDown) {
    }

    protected static void setEnabledTree(View view, boolean enabled) {
        view.setEnabled(enabled);

        if(view instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) view;
            for(int i = vg.getChildCount() - 1; i >= 0; i--)
                setEnabledTree(vg.getChildAt(i), enabled);
        }
    }

    @Override
    public boolean dispatchTouchEvent(@NonNull MotionEvent ev) {
        super.dispatchTouchEvent(ev);
        return _gestureDetector.onTouchEvent(ev);
    }

    public boolean onTouchEvent(MotionEvent event) {
        return _gestureDetector.onTouchEvent(event);
        //return super.onTouchEvent(event);
    }
}
