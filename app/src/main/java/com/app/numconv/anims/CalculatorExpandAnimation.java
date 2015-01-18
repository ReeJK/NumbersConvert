package com.app.numconv.anims;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class CalculatorExpandAnimation extends Animation {
    private int _targetHeight;
    private View _view;

    public CalculatorExpandAnimation(final View view) {
        _view = view;
        _view.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        _targetHeight = view.getMeasuredHeight();

        setDuration(200);
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        _view.getLayoutParams().height = getHeight(interpolatedTime);
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
            return (int)(_targetHeight * interpolatedTime);
    }
}
