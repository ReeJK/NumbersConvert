package com.app.numconv;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.support.v7.internal.widget.TintEditText;
import android.text.InputType;
import android.util.AttributeSet;

public class FixedEditText extends TintEditText {
    private static final int blinkTimeout = 500;

    private static boolean _blink = false;
    private final static Handler _BlinkHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            _blink = !_blink;

            FixedEditText sender = (FixedEditText) msg.obj;
            sender.invalidate();

            _BlinkHandler.sendMessageDelayed(Message.obtain(_BlinkHandler, msg.what, sender), blinkTimeout);
        }
    };

    public FixedEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public FixedEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FixedEditText(Context context) {
        super(context);
    }

    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);

        if(focused) {
            _BlinkHandler.removeMessages(1);
            _BlinkHandler.sendMessageDelayed(Message.obtain(_BlinkHandler, 1, this), blinkTimeout);
            setSelection(length());
        }
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(!hasFocus() || getInputType() != InputType.TYPE_NULL)
            return;

        if(!_blink) {
            int pos = getSelectionEnd();
            float offset = getPaint().measureText(getText().toString().substring(0,  pos)) + getPaddingLeft();
            canvas.drawLine(offset, getPaddingTop(), offset, getHeight() - getPaddingBottom(), getPaint());
        }
    }
}
