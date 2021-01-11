package com.app.shopncart.utils;

import android.text.InputFilter;
import android.text.Spanned;

/**
 * Created by Eldho on 29/11/17.
 * Summary: to set min and max limits to input field
 */

public class InputFilterMinMax implements InputFilter {

    private Float min;
    private Float max;

    public InputFilterMinMax(float min, float max) {
        this.min = min;
        this.max = max;
    }

    public InputFilterMinMax(String min, String max) {
        this.min = Float.parseFloat(min);
        this.max = Float.parseFloat(max);
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        if ((dest.toString() + source.toString()).equals(".")) {
            return "0.";
        }
        try {
            float input = Float.parseFloat(dest.toString() + source.toString());
            if (isInRange(min, max, input))
                return null;
        } catch (NumberFormatException nfe) {
        }
        return "";
    }

    private boolean isInRange(float a, float b, float c) {
        return b > a ? c >= a && c <= b : c >= b && c <= a;
    }
}
