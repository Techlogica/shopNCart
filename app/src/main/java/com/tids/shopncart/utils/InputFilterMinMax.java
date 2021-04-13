package com.tids.shopncart.utils;

import android.text.InputFilter;
import android.text.Spanned;

/**
 * Created by Eldho on 29/11/17.
 * Summary: to set min and max limits to input field
 */

public class InputFilterMinMax implements InputFilter {

    private Double min;
    private Double max;

    public InputFilterMinMax(double min, double max) {
        this.min = min;
        this.max = max;
    }

    public InputFilterMinMax(String min, String max) {
        this.min = Double.parseDouble(min);
        this.max = Double.parseDouble(max);
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        if ((dest.toString() + source.toString()).equals(".")) {
            return "0.";
        }
        try {
            double input = Double.parseDouble(dest.toString() + source.toString());
            if (isInRange(min, max, input))
                return null;
        } catch (NumberFormatException nfe) {
        }
        return "";
    }

    private boolean isInRange(double a, double b, double c) {
        return b > a ? c >= a && c <= b : c >= b && c <= a;
    }
}
