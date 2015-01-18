package com.app.numconv.anims;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class CalculatorActionCollapseAnimation extends Animation {
    private int _initialHeight;
    private int _initialWidth;
    private int _targetHeight;
    private View _view;

    public CalculatorActionCollapseAnimation(final View view, final View firstView) {
        _view = view;
        _view.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        _initialHeight = view.getMeasuredHeight();
        _initialWidth = view.getMeasuredWidth();
        _targetHeight = firstView.getTop();

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
            return 0;
        else
            return _targetHeight + (int)((_initialHeight - _targetHeight) * (1 - interpolatedTime));
    }

    private int getWidth(float interpolatedTime) {
        if(interpolatedTime == 1)
            return 0;
        else
            return (int)(_initialWidth * (1 - interpolatedTime));
    }
}