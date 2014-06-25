package peksa.irisr.app;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;
import android.hardware.*;


import java.lang.reflect.Method;

import static android.graphics.Camera.*;

/**
 * Created by pawel on 09.06.14.
 */
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

    static double calcDistance(Point a, Point b){
        return Math.hypot(b.getX()-a.getX(),b.getY()-a.getY());
    }


}
