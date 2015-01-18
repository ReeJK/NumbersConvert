package com.app.numconv;

import java.util.ArrayList;

public final class Extensions {
    public static Integer[] toArray(ArrayList<Integer> list) {
        return list.toArray(new Integer[list.size()]);
    }

    public static boolean isZero(Integer[] digits) {
        for (Integer digit : digits) {
            if (digit != 0)
                return false;
        }
        return true;
    }

    public static String format(int resId, Object... params) {
        return String.format(Application.getContext().getString(resId), params);
    }
}
