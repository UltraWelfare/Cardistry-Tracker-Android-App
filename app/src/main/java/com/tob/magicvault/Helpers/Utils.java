package com.tob.magicvault.Helpers;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.tob.magicvault.R;

public class Utils {
    private static final String YOUTUBE_THUMBNAIL_URL = "https://img.youtube.com/vi/$/0.jpg";
    private static final String YOUTUBE_URL_REGEX = "^(http(s)?:\\/\\/)?((w){3}.)?youtu(be|.be)?(\\.com)?\\/.+";
    private static final int IMAGE_PREVIEW_PLACEHOLDER = R.drawable.broken_image;

    /**
     * Converts a plain youtube url to a thumbnail url
     *
     * @param youtubeUrl A url pointing to a youtube video
     * @return The url that points to the thumbnail image of the video
     */
    public static String convertToThumbnailUrl(String youtubeUrl) {
        youtubeUrl = youtubeUrl.replace("watch?v=", "");
        String videoId = youtubeUrl.substring(youtubeUrl.lastIndexOf('/') + 1);
        return YOUTUBE_THUMBNAIL_URL.replace("$", videoId);
    }

    /**
     * Downloads an image from a uri based on if its a Youtube URL or a Gallery URI.
     * Will return the default "Not Found Image" if the URI is inaccessible
     *
     * @param ctx  The activity context
     * @param uri  The uri of the image to be loaded
     * @param view The ImageView to place the image into
     */
    public static void getImageFromUri(Context ctx, String uri, ImageView view) {
        if (uri == null || uri.trim().isEmpty()) {
            Glide.with(ctx).load(IMAGE_PREVIEW_PLACEHOLDER).into(view);
            return;
        }
        if (isYoutubeUrl(uri)) {
            Glide.with(ctx).load(Utils.convertToThumbnailUrl(uri)).placeholder(IMAGE_PREVIEW_PLACEHOLDER).into(view);
        } else { // Possibly a path uri from gallery
            Glide.with(ctx).load(uri).placeholder(IMAGE_PREVIEW_PLACEHOLDER).into(view);
        }
    }


    /**
     * @param url A website url
     * @return Whether the url is a youtube one.
     */
    public static boolean isYoutubeUrl(String url) {
        if (!url.isEmpty() && url.matches(YOUTUBE_URL_REGEX)) {
            return true;
        } else if (url.isEmpty()) {
            return false;
        }
        return false;
    }

    /**
     * @param url A website url
     * @return Whether the url is a youtube thumbnail pointing to an image.
     */
    public static boolean isYoutubeThumbnailUrl(String url) {
        // Works for now, can be made with regex
        // TODO : RegEx implementation.
        if (!url.isEmpty() && url.contains("https://img.youtube.com/vi/")) {
            return true;
        } else if (url.isEmpty()) {
            return false;
        }
        return false;
    }

    private static final String capEnding = "...";

    /**
     * Caps a string, adds "capEnding" if the original string has bigger length than maxLength
     *
     * @param original  The string to be capped
     * @param maxLength The max length desired
     * @return The capped string
     */
    public static String cap(String original, int maxLength) {
        if (maxLength >= original.length()) {
            return original;
        }
        return original.substring(0, maxLength - capEnding.length()) + capEnding;
    }

    /**
     * Returns a difficulty from string resources according to the arithmetic difficulty.
     *
     * @param ctx        Application Context.
     * @param difficulty The arithmetical difficulty.
     * @return The string that contains the correct description for the difficulty.
     */
    public static String getDifficulty(Context ctx, int difficulty) {
        Resources r = ctx.getResources();
        switch (difficulty) {
            case 0:
                return r.getString(R.string.difficulty_1);
            case 1:
                return r.getString(R.string.difficulty_2);
            case 2:
                return r.getString(R.string.difficulty_3);
            case 3:
                return r.getString(R.string.difficulty_4);
            case 4:
                return r.getString(R.string.difficulty_5);
        }
        return null;
    }

    public static int convertPixelsToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        int dp = (int) (px / (metrics.densityDpi / 160f));
        return dp;
    }

}
