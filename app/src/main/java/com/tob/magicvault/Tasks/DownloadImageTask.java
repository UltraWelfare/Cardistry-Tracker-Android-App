package com.tob.magicvault.Tasks;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.function.Consumer;

/**
 * Downloads an image based on the URL and returns a consumer with the bitmap.
 * Temporarily, if the URL is not found the consumer will never be accepted.
 * TODO: Add error image.
 */
public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

    private final Consumer<Bitmap> listener;
    private Bitmap bitmap = null;

    public DownloadImageTask(Consumer<Bitmap> fn) {
        this.listener = fn;
    }

    @Override
    protected Bitmap doInBackground(String... urls) {
        final String url = urls[0];
        try {
            final InputStream inputStream = new URL(url).openStream();
            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch (final IOException ignored) {
        }
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        listener.accept(bitmap);
    }
}