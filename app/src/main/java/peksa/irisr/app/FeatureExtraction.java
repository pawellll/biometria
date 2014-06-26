package peksa.irisr.app;

import android.graphics.Color;

import java.util.ArrayList;

/**
 * Created by pawel on 26.06.14.
 */
public class FeatureExtraction {

    ArrayList<Integer> groups;

    FeatureExtraction() {
        groups = new ArrayList<Integer>();
    }

    public void process(BitmapArray bitmapIn) {
        fillValues(bitmapIn);

    }


    private void fillValues(BitmapArray bitmapIn){
        int marginX = 2;
        int marginY = 1;
        int pixelColor,alpha,r,average;

        for(int x = marginX;x<bitmapIn.getWidth()-marginX;++x){
            for(int y = marginY;y<bitmapIn.getHeight()-marginY;++y){
                average = 0;
                for(int l = x-marginX,ll=0;ll<5;++l,++ll){
                    for(int k =x-marginY,kk=0;kk<3;++k,++kk){
                        pixelColor = bitmapIn.getPixel(l,k);
                        alpha = Color.alpha(pixelColor);
                        r = Color.red(pixelColor);
                        average+=r;
                    }
                }
                average /= 15;
            }
        }
    }



}
