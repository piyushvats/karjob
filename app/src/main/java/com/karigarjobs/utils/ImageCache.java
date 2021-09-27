package com.karigarjobs.utils;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.util.LruCache;

import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * Created by root on 8/2/19.
 */

public class ImageCache {

    private int cacheRefCount = 0;
    private int displayRefCount = 0;
    Set<SoftReference<Bitmap>> reusableBitmaps;
    private LruCache<String, BitmapDrawable> memoryCache;
    private boolean hasBeenDisplayed = false;
    Bitmap bit ;
    String mFilename;

    public void create()
    {
        // If you're running on Honeycomb or newer, create a
        // synchronized HashSet of references to reusable bitmaps.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            reusableBitmaps = Collections.synchronizedSet(new HashSet<SoftReference<Bitmap>>());
        }
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

        memoryCache = new LruCache<String, BitmapDrawable>(cacheSize); //{


        // Notify the removed entry that is no longer being cached.
/*            @Override
            protected void entryRemoved(boolean evicted, String key,BitmapDrawable oldValue, BitmapDrawable newValue) {
                if (RecyclingBitmapDrawable.class.isInstance(oldValue)) {
                    // The removed entry is a recycling drawable, so notify it
                    // that it has been removed from the memory cache.
                    ((RecyclingBitmapDrawable) oldValue).setIsCached(false);
                } else {
                    // The removed entry is a standard BitmapDrawable.
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        // We're running on Honeycomb or later, so add the bitmap
                        // to a SoftReference set for possible use with inBitmap later.
                        reusableBitmaps.add(new SoftReference<Bitmap>(oldValue.getBitmap()));
                    }
                }
            }*/
        //}
    }


/*    // Notify the drawable that the displayed state has changed.
// Keep a count to determine when the drawable is no longer displayed.
    public void setIsDisplayed(boolean isDisplayed) {
        synchronized (this) {
            if (isDisplayed) {
                displayRefCount++;
                hasBeenDisplayed = true;
            } else {
                displayRefCount--;
            }
        }
        // Check to see if recycle() can be called.
        checkState();
    }

    // Notify the drawable that the cache state has changed.
// Keep a count to determine when the drawable is no longer being cached.
    public void setIsCached(boolean isCached) {
        synchronized (this) {
            if (isCached) {
                cacheRefCount++;
            } else {
                cacheRefCount--;
            }
        }
        // Check to see if recycle() can be called.
        checkState();
    }

    private synchronized void checkState() {
        // If the drawable cache and display ref counts = 0, and this drawable
        // has been displayed, then recycle.
        if (cacheRefCount <= 0 && displayRefCount <= 0 && hasBeenDisplayed
                && hasValidBitmap()) {
            getBitmap().recycle();
        }
    }

    private synchronized boolean hasValidBitmap() {
        Bitmap bitmap = getBitmap();
        return bitmap != null && !bitmap.isRecycled();
    }*/



    public static Bitmap decodeSampledBitmapFromFile(String filename,
                                                     int reqWidth, int reqHeight, ImageCache cache) {

        final BitmapFactory.Options options = new BitmapFactory.Options();

        BitmapFactory.decodeFile(filename, options);


        // If we're running on Honeycomb or newer, try to use inBitmap.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            addInBitmapOptions(options, cache);
        }

        return BitmapFactory.decodeFile(filename, options);
    }


    private static void addInBitmapOptions(BitmapFactory.Options options,
                                           ImageCache cache) {
        // inBitmap only works with mutable bitmaps, so force the decoder to
        // return mutable bitmaps.
        options.inMutable = true;

        if (cache != null) {
            // Try to find a bitmap to use for inBitmap.
            Bitmap inBitmap = cache.getBitmapFromReusableSet(options);

            if (inBitmap != null) {
                // If a suitable bitmap has been found, set it as the value of
                // inBitmap.
                options.inBitmap = inBitmap;
            }
        }
    }

    // This method iterates through the reusable bitmaps, looking for one
    // to use for inBitmap:
    protected Bitmap getBitmapFromReusableSet(BitmapFactory.Options options) {
        Bitmap bitmap = null;

        if (reusableBitmaps != null && !reusableBitmaps.isEmpty()) {
            synchronized (reusableBitmaps) {
                final Iterator<SoftReference<Bitmap>> iterator
                        = reusableBitmaps.iterator();
                Bitmap item;

                while (iterator.hasNext()) {
                    item = iterator.next().get();

                    if (null != item && item.isMutable()) {
                        // Check to see it the item can be used for inBitmap.
                        if (canUseForInBitmap(item, options)) {
                            bitmap = item;

                            // Remove from reusable set so it can't be used again.
                            iterator.remove();
                            break;
                        }
                    } else {
                        // Remove from the set if the reference has been cleared.
                        iterator.remove();
                    }
                }
            }
        }
        return bitmap;
    }


    static boolean canUseForInBitmap(
            Bitmap candidate, BitmapFactory.Options targetOptions) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // From Android 4.4 (KitKat) onward we can re-use if the byte size of
            // the new bitmap is smaller than the reusable bitmap candidate
            // allocation byte count.
            int width = targetOptions.outWidth / targetOptions.inSampleSize;
            int height = targetOptions.outHeight / targetOptions.inSampleSize;
            int byteCount = width * height * getBytesPerPixel(candidate.getConfig());
            return byteCount <= candidate.getAllocationByteCount();
        }

        // On earlier versions, the dimensions must match exactly and the inSampleSize must be 1
        return candidate.getWidth() == targetOptions.outWidth
                && candidate.getHeight() == targetOptions.outHeight
                && targetOptions.inSampleSize == 1;
    }

    /**
     * A helper function to return the byte usage per pixel of a bitmap based on its configuration.
     */
    static int getBytesPerPixel(Bitmap.Config config) {
        if (config == Bitmap.Config.ARGB_8888) {
            return 4;
        } else if (config == Bitmap.Config.RGB_565) {
            return 2;
        } else if (config == Bitmap.Config.ARGB_4444) {
            return 2;
        } else if (config == Bitmap.Config.ALPHA_8) {
            return 1;
        }
        return 1;
    }


    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        int scaleFactor = Math.min(width / reqWidth, height / reqHeight);
        return scaleFactor;
    }


    public static  Bitmap decodeSampledBitmap(String filename, String parentdir,int reqWidth, int reqHeight) {


        //First convert to png
    /*
            try {
                Bitmap bmp = BitmapFactory.decodeFile(filename);
                String fprefix = filename.split("\\.")[0];
                FileOutputStream out = new FileOutputStream(fprefix+".png");
                bmp.compress(Bitmap.CompressFormat.PNG, 100, out); //100-best quality
                out.close();

            } catch (Exception e) {
                e.printStackTrace();
            }

            String newfilename = filename.split("\\.")[0]+".png";*/
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        //BitmapFactory.decodeResource(res, resId, options);
        BitmapFactory.decodeFile(filename, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        options.inPurgeable = true;

        return BitmapFactory.decodeFile(filename,options);
    }


}


