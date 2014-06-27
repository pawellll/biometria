package peksa.irisr.app;

import android.graphics.Bitmap;
import android.graphics.Color;

import java.util.Arrays;

/**
 * Created by Witold Matuszek on 14.06.14.
 */
public class BitmapArray {

    private int[] ARR; // tutaj jest bitmapa
    private int W, H; // tutaj sa wymiary

    public BitmapArray(int wid, int hei) {
        W = wid;
        H = hei;
        ARR = new int[W * H];
    }

    // tego konstruktora wystarczy uzyc
    public BitmapArray(BitmapArray original) {
        W = original.getWidth();
        H = original.getHeight();
        ARR = Arrays.copyOf(original.getARR(), W * H);
    }

    public BitmapArray(Bitmap bm) {
        W = bm.getWidth();
        H = bm.getHeight();
        ARR = new int[W * H];
        bm.getPixels(ARR, 0, W, 0, 0, W, H);
    }

    // tak wracasz do bitmapy
    public Bitmap toBitmap() {
        Bitmap out = Bitmap.createBitmap(W, H, Bitmap.Config.RGB_565);
        out.setPixels(ARR, 0, W, 0, 0, W, H);
        return out;
    }

    // taka sama metoda jak w bitmapie
    public int getPixel(int i, int j) {
        try {
            return ARR[i + j * W];
        }catch(Exception e){
            System.err.println(i+" "+j+" | "+W+" "+H);
            return 0;
        }
    }

    // taka sama metoda jak w bitmapie
    public void setPixel(int i, int j, int COLOR) {
        ARR[i + j * W] = COLOR;
    }

    public int getWidth() {
        return W;
    }

    public int getHeight() {
        return H;
    }

    public void cut(int x, int y) {
        int[] tmpARR = new int[W * H];
        int z = 0;
        for (int i = 0; i < H; ++i) {
            for (int j = 0; j < W; ++j) {
                if (i > y || j > x) {
                    tmpARR[z] = Color.BLUE;
                } else {
                    tmpARR[z] = ARR[z];
                }
                ++z;
            }
        }
        ARR = tmpARR;
    }

    public int[] getARR() {
        return ARR;
    }

    public void setARR(int[] arr) {
        ARR = arr;
    }
}
