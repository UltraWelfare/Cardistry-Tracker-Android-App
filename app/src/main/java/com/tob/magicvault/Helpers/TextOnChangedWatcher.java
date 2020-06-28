package com.tob.magicvault.Helpers;

import android.text.Editable;
import android.text.TextWatcher;

import java.util.function.Consumer;

/**
 * The same as a TextWatcher, but only implements onTextChanged
 * and accepts the editable string into a consumer.
 */
public class TextOnChangedWatcher implements TextWatcher {
    private final Consumer<String> consumerFunction;

    public TextOnChangedWatcher(Consumer<String> consumerFunction) {
        this.consumerFunction = consumerFunction;
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        consumerFunction.accept(s.toString());
    }
}