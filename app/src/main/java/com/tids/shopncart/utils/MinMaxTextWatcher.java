package com.tids.shopncart.utils;

import android.text.TextWatcher;


public abstract class MinMaxTextWatcher implements TextWatcher {
    double min, max;
    public MinMaxTextWatcher(double min, double max) {
        super();
        this.min = min;
        this.max = max;
    }

}