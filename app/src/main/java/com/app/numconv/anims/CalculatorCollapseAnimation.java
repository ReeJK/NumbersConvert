package com.app.numconv.anims;

import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class CalculatorCollapseAnimation extends Animation {
    private int _initialHeight;
    private View _view;

    public CalculatorCollapseAnimation(final View view) {
        _view = view;
        _view.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        _initialHeight = view.getMeasuredHeight();

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
            return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, _view.getContext().getResources().getDisplayMetrics());
        else
            return (int)(_initialHeight * (1 - interpolatedTime));
    }
}
