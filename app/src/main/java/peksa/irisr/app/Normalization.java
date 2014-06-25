package peksa.irisr.app;

/**
 * Created by pawel on 25.06.14.
 */


import android.graphics.Color;

/**
 * Created by Magda on 2014-06-19.
 */
public class Normalization {
    public static BitmapArray process(BitmapArray im, int x0, int y0, int r_min, int r_max) {
        final int width = 360;
        final int height = r_max - r_min;
        if (height <= 0 || x0 < 0 || y0 < 0 || x0 >= im.getWidth() || y0 >= im.getHeight()) {
            return null;
        }
        BitmapArray ret = new BitmapArray(im);

        for (double i = 0; i < width; i += 1.) {
            final double theta = i / width * 2. * Math.PI;
            for (double j = 0; j < height; j += 1.) {
                final double r = j / height;

                final int x = (int) ((1 - r) * (x0 + r_min * Math.cos(theta)) + r * (x0 + r_max * Math.cos(theta)));
                final int y = (int) ((1 - r) * (y0 + r_min * Math.sin(theta)) + r * (y0 + r_max * Math.sin(theta)));

                try {
                    int pixel = im.getPixel(x, y);
                    int A = Color.alpha(pixel);
                    int R = Color.red(pixel);
                    int G = Color.green(pixel);
                    int B = Color.blue(pixel);
                    ret.setPixel((int) i, (int) j, Color.argb(A, R, G, B));
                } catch (java.lang.ArrayIndexOutOfBoundsException e) {
                }
            }
        }
        return ret;
    }
}