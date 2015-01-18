package com.app.numconv.anims;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class CalculatorActionExpandAnimation extends Animation {
    private int _initialHeight;
    private int _targetHeight;
    private int _targetWidth;
    private View _view;

    public CalculatorActionExpandAnimation(final View view, final View firstPanel) {
        _view = view;
        _view.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        _targetHeight = view.getMeasuredHeight();
        _targetWidth = view.getMeasuredWidth();
        _initialHeight = firstPanel.getTop();

        setDuration(200);
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        _view.getLayoutParams().height = getHeight(interpolatedTime);
        _view.getLayoutParams().width = getWidth(interpolatedTime);
        _view.requestLayout();
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }

    private int getHeight(float interpolatedTime) {
        if(interpolatedTime == 1)
            return ViewGroup.LayoutParams.WRAP_CONTENT;
        else
            return _initialHeight + (int)((_targetHeight - _initialHeight) * interpolatedTime);
    }

    private int getWidth(float interpolatedTime) {
        if(interpolatedTime == 1)
            return ViewGroup.LayoutParams.WRAP_CONTENT;
        else
            return (int)(_targetWidth * interpolatedTime);
    }
}
