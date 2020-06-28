package com.tob.magicvault.Helpers;

import android.widget.RatingBar;

import java.util.function.Consumer;

/**
 * The same as a OnSeekBarChangeListener, but only implements onProgressChanged
 * and accepts the progress into a consumer.
 */
public class BasicRatingBarChangeListener implements RatingBar.OnRatingBarChangeListener {
    private final Consumer<Integer> consumerFunction;

    public BasicRatingBarChangeListener(Consumer<Integer> consumerFunction) {
        this.consumerFunction = consumerFunction;
    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

        int ratingInteger = (int) rating;
        if (ratingInteger <= 0) {
            ratingBar.setRating(1);
            return;
        }
        consumerFunction.accept(ratingInteger - 1);
    }


}
