package peksa.irisr.app;

import android.graphics.Bitmap;

public class Util {
    static Bitmap resizeBitmap(Bitmap inBitmap, final int maxSize){
        int outWidth;
        int outHeight;
        int inWidth = inBitmap.getWidth();
        int inHeight = inBitmap.getHeight();
        if(inWidth > inHeight){
            outWidth = maxSize;
            outHeight = (inHeight * maxSize) / inWidth;
        } else {
            outHeight = maxSize;
            outWidth = (inWidth * maxSize) / inHeight;
        }

        Bitmap resizedBitmap = Bitmap.createScaledBitmap(inBitmap, outWidth, outHeight, false);
        inBitmap.recycle();
        return resizedBitmap;
    }

    static int calcDistance(Point a, Point b){
        return (int) Math.hypot(b.getX()-a.getX(),b.getY()-a.getY());
    }


}
