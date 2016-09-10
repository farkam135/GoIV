package com.kamron.pogoiv;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;

import java.nio.ByteBuffer;

import timber.log.Timber;

/**
 * Created by Sarav on 8/27/2016.
 */
public class ScreenGrabber {

    private static ScreenGrabber instance = null;
    private ImageReader imageReader;
    private MediaProjection projection = null;
    private DisplayMetrics rawDisplayMetrics;
    private DisplayMetrics displayMetrics;
    private VirtualDisplay virtualDisplay;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private ScreenGrabber(MediaProjection mediaProjection, DisplayMetrics raw, DisplayMetrics display) {
        rawDisplayMetrics = raw;
        displayMetrics = display;
        projection = mediaProjection;
        imageReader = ImageReader.newInstance(rawDisplayMetrics.widthPixels, rawDisplayMetrics.heightPixels,
                PixelFormat.RGBA_8888, 2);
        virtualDisplay = projection.createVirtualDisplay("screen-mirror", rawDisplayMetrics.widthPixels,
                rawDisplayMetrics.heightPixels,
                rawDisplayMetrics.densityDpi, DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC, imageReader.getSurface(),
                null, null);
    }

    public static ScreenGrabber init(MediaProjection mediaProjection, DisplayMetrics raw, DisplayMetrics display) {
        if (instance == null) {
            instance = new ScreenGrabber(mediaProjection, raw, display);
        }
        return instance;
    }

    /**
     * Returns instance of ScreenGrabber; only use if you're <b>sure</b> it's already initialized.
     *
     * @return Singleton instance of ScreenGrabber, if initialized
     */
    public static ScreenGrabber getInstance() {
        assert instance != null;
        return instance;
    }

    @NonNull private static Rect getBitmapBounds(Bitmap bmp) {
        return new Rect(0, 0, bmp.getWidth(), bmp.getHeight());
    }

    static @Nullable @ColorInt int[] getPixelsSafe(Bitmap bmp, Point[] points) {
        Rect bounds = getBitmapBounds(bmp);
        @ColorInt int[] pixels = new int[points.length];
        for (int i = 0; i < points.length; i++) {
            Point p = points[i];
            if (bounds.contains(p.x, p.y)) {
                pixels[i] = bmp.getPixel(p.x, p.y);
            } else {
                return null;
            }
        }
        return pixels;
    }

    public @Nullable @ColorInt int[] grabPixels(Point[] points) {
        Bitmap bmp = grabScreen();
        if (bmp == null) {
            return null;
        }
        @ColorInt int[] pixels = ScreenGrabber.getPixelsSafe(bmp, points);
        bmp.recycle();

        return pixels;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void exit() {
        if (projection != null) {
            virtualDisplay.release();
            virtualDisplay = null;
            imageReader.close();
            imageReader = null;
            projection.stop();
            projection = null;
            rawDisplayMetrics = null;
            displayMetrics = null;
            instance = null;
        }
    }

    private Bitmap getBitmap(ByteBuffer buffer, int pixelStride, int rowPadding) {
        Bitmap bmp = Bitmap.createBitmap(rawDisplayMetrics.widthPixels + rowPadding / pixelStride,
                displayMetrics.heightPixels, Bitmap.Config.ARGB_8888);
        bmp.copyPixelsFromBuffer(buffer);
        return bmp;
    }

    public @Nullable Bitmap grabScreen() {
        Image image = null;
        Bitmap bmp = null;

        try {
            //Note: imageReader shouldn't be null, but apparently sometimes is.
            //Let's allow this to still happen.
            image = imageReader.acquireLatestImage();
        } catch (Exception exception) {
            Timber.e("Error thrown in grabScreen() - acquireLatestImage()");
            Timber.e(exception);
        }

        if (image != null) {
            final Image.Plane[] planes = image.getPlanes();
            final ByteBuffer buffer = planes[0].getBuffer();
            int pixelStride = planes[0].getPixelStride();
            int rowStride = planes[0].getRowStride();
            int rowPadding = rowStride - pixelStride * rawDisplayMetrics.widthPixels;
            image.close();

            try {
                bmp = getBitmap(buffer, pixelStride, rowPadding);
            } catch (Exception exception) {
                Timber.e("Exception thrown in grabScreen() - when creating bitmap");
                Timber.e(exception);
            }
        }

        return bmp;
    }
}
