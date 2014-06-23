package peksa.irisr.app;

import android.graphics.Bitmap;

import java.util.Arrays;

/**
 * Created by Witold on 14.06.14.
 */
public class BitmapArray {

        private int[] ARR; // tutaj jest bitmapa
        private int W, H; // tutaj sa wymiary

        public BitmapArray(int wid, int hei){
            W = wid; H = hei;
            ARR = new int[W*H];
        }
        // tego konstruktora wystarczy uzyc
        public BitmapArray(BitmapArray original){
            W = original.getWidth();
            H = original.getHeight();
            ARR = Arrays.copyOf(original.getARR(), W * H);
        }

        public BitmapArray(Bitmap bm){
            W = bm.getWidth(); H = bm.getHeight();
            ARR = new int[W*H];
            bm.getPixels(ARR, 0, W, 0, 0, W, H);
        }
        // tak wracasz do bitmapy
        public Bitmap toBitmap(){
            Bitmap out = Bitmap.createBitmap(W, H, Bitmap.Config.RGB_565);
            out.setPixels(ARR, 0, W, 0, 0, W, H);
            return out;
        }
        // taka sama metoda jak w bitmapie
        public int getPixel(int i, int j){
            return ARR[i + j*W];
        }
        // taka sama metoda jak w bitmapie
        public void setPixel(int i, int j, int COLOR){
            ARR[i + j*W] = COLOR;
        }

        public int getWidth() {
            return W;
        }
        public int getHeight() {
            return H;
        }

        public int[] getARR() {
            return ARR;
        }
    }
