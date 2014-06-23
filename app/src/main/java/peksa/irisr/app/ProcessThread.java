package peksa.irisr.app;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.FileOutputStream;
import java.lang.ref.WeakReference;

public class ProcessThread extends AsyncTask<Void, Void, Bitmap> {

    private Bitmap takenPicture;
    private final WeakReference<ImageView> imageViewReference; // referencja do ImageView z obrazkiem
    private final WeakReference<ProgressBar> progressBarReference;

    @Override
    protected void onPostExecute(Bitmap bitmap) { // co robic jak skonczy pracę, automatycznie łapie bitmapę z doInBackground
        if (imageViewReference != null && bitmap != null) { // sprawdzasz czy ImageView istnieje i czy
            final ImageView imageView = imageViewReference.get(); // bierzesz IMageView
            if (imageView != null) {
                imageView.setImageBitmap(bitmap); // i wstawiasz obrazek
            }
        }
        if (progressBarReference != null){
            final ProgressBar progressBar = progressBarReference.get();
            if(progressBar!=null){
                progressBar.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        //rotate(); // when taking landscape picture you don't need to rotate

        doGrayScale();

        //doSobel();

        //doBinarization(binType.iris);
        System.err.println("Worth it motherfuckers");
        return takenPicture;
    }

    public ProcessThread(Bitmap takenPic,ImageView imageView,ProgressBar progressBar) throws InterruptedException {
        progressBar.setVisibility(View.VISIBLE);
        imageViewReference = new WeakReference<ImageView>(imageView);
        progressBarReference = new WeakReference<ProgressBar>(progressBar);
        takenPicture = takenPic;
    }

    private enum binType {
        iris, pupil
    }


    private void save(String name) {
        try {
            String filePath = Environment.getExternalStorageDirectory() + "/" + name + ".jpg";
            FileOutputStream fOut;
            fOut = new FileOutputStream(filePath);
            takenPicture.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
            fOut.flush();
            fOut.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doGrayScale() {
        int t, r, g, b, alpha, pixelColor;
        for (int x = 0; x < takenPicture.getWidth(); ++x) {
            for (int y = 0; y < takenPicture.getHeight(); ++y) {
                pixelColor = takenPicture.getPixel(x, y);
                r = Color.red(pixelColor);
                g = Color.green(pixelColor);
                b = Color.blue(pixelColor);
                alpha = Color.alpha(pixelColor);
                t = (r + g + b) / 3;
                takenPicture.setPixel(x, y, Color.argb(alpha, t, t, t));
            }
        }
    }

    public void doSobel() {
        Bitmap bmOut = Bitmap.createBitmap(takenPicture.getWidth(), takenPicture.getHeight(), takenPicture.getConfig());

        int A;
        int pixelColor;

        int height = takenPicture.getHeight();
        int width = takenPicture.getWidth();
        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                pixelColor = takenPicture.getPixel(x, y);
                A = Color.alpha(pixelColor);
                int p0 = Color.red(takenPicture.getPixel(x - 1, y - 1));
                int p1 = Color.red(takenPicture.getPixel(x, y - 1));
                int p2 = Color.red(takenPicture.getPixel(x + 1, y - 1));
                int p3 = Color.red(takenPicture.getPixel(x + 1, y));
                int p4 = Color.red(takenPicture.getPixel(x + 1, y + 1));
                int p5 = Color.red(takenPicture.getPixel(x, y + 1));
                int p6 = Color.red(takenPicture.getPixel(x - 1, y + 1));
                int p7 = Color.red(takenPicture.getPixel(x - 1, y));
                int xx = ((p2 + 2 * p3 + p4) - (p0 + 2 * p7 + p6));
                int yy = ((p6 + 2 * p5 + p4) - (p0 + 2 * p1 + p2));
                int N = (int) Math.hypot(xx, yy);
                if (N > 255) N = 255;

                bmOut.setPixel(x, y, Color.argb(A, N, N, N));
            }
        }
        takenPicture = bmOut;
    }

    private void doBinarization(binType type) {
        double T = getThreshold();
        if (type == binType.iris) {
            T /= 4.5;
        } else {
            T /= 1.5;
        }
        int color, pixelColor, alpha;
        for (int x = 0; x < takenPicture.getWidth(); ++x) {
            for (int y = 0; y < takenPicture.getHeight(); ++y) {
                pixelColor = takenPicture.getPixel(x, y);
                color = Color.red(pixelColor);
                alpha = Color.alpha(pixelColor);
                if (color > T) {
                    takenPicture.setPixel(x, y, Color.argb(alpha, 255, 255, 255));
                } else {
                    takenPicture.setPixel(x, y, Color.argb(alpha, 0, 0, 0));
                }
            }
        }

    }

    private double getThreshold() {
        int width = takenPicture.getWidth();
        int height = takenPicture.getHeight();
        double P = 0;
        // wyznaczanie progu binaryzacji
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int pixel = takenPicture.getPixel(i, j);
                P = P + Color.red(pixel);
            }
        }
        P = P / (width * height);
        return P;
    }


    private void rotate() {
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        Bitmap tmp = Bitmap.createBitmap(takenPicture, 0, 0, takenPicture.getWidth(), takenPicture.getHeight(), matrix, true);
        takenPicture = tmp;
    }


    private void canny() {

    }

    public Bitmap getProcessed() {
        return takenPicture;
    }



}