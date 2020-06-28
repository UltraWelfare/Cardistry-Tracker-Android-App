package com.tob.magicvault.Helpers;

import android.text.Editable;
import android.text.TextWatcher;

import java.util.function.Consumer;

/**
 * The same as a TextWatcher, but only implements afterTextChanged
 * and accepts the editable string into a consumer.
 */
public class TextAfterChangedWatcher implements TextWatcher {
    private final Consumer<String> consumerFunction;

    public TextAfterChangedWatcher(Consumer<String> consumerFunction) {
        this.consumerFunction = consumerFunction;
    }

    @Override
    public void afterTextChanged(Editable s) {
        consumerFunction.accept(s.toString());
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }
}