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

    private BitmapArray workingPicture;
    private final WeakReference<ImageView> imageViewReference; // referencja do ImageView z obrazkiem
    private final WeakReference<ProgressBar> progressBarReference;

    public ProcessThread(Bitmap takenPic, ImageView imageView, ProgressBar progressBar) throws InterruptedException {
        progressBar.setVisibility(View.VISIBLE);
        imageViewReference = new WeakReference<ImageView>(imageView);
        progressBarReference = new WeakReference<ProgressBar>(progressBar);
        workingPicture = new BitmapArray(takenPic);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) { // co robic jak skonczy pracę, automatycznie łapie bitmapę z doInBackground
        if (imageViewReference != null && bitmap != null) { // sprawdzasz czy ImageView istnieje i czy
            final ImageView imageView = imageViewReference.get(); // bierzesz IMageView
            if (imageView != null) {
                imageView.setImageBitmap(bitmap); // i wstawiasz obrazek
            }
        }
        if (progressBarReference != null) {
            final ProgressBar progressBar = progressBarReference.get();
            if (progressBar != null) {
                progressBar.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        //rotate(); // when taking landscape picture you don't need to rotate

        doGrayScale();
        doGaussianBlur();
        /*
        CannyFilter p = new CannyFilter(workingPicture);
        p.process();
        */
        //doSobel();
        //doBinarization(binType.iris);
        System.err.println("Worth it motherfuckers");
        return workingPicture.toBitmap();
    }


    private enum binType {
        iris, pupil
    }


    private void save(String name) {
        try {
            String filePath = Environment.getExternalStorageDirectory() + "/" + name + ".jpg";
            FileOutputStream fOut;
            fOut = new FileOutputStream(filePath);
            workingPicture.toBitmap().compress(Bitmap.CompressFormat.JPEG, 85, fOut);
            fOut.flush();
            fOut.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doGrayScale() {
        int t, r, g, b, alpha, pixelColor;
        for (int x = 0; x < workingPicture.getWidth(); ++x) {
            for (int y = 0; y < workingPicture.getHeight(); ++y) {
                pixelColor = workingPicture.getPixel(x, y);
                r = Color.red(pixelColor);
                g = Color.green(pixelColor);
                b = Color.blue(pixelColor);
                alpha = Color.alpha(pixelColor);
                t = (r + g + b) / 3;
                workingPicture.setPixel(x, y, Color.argb(alpha, t, t, t));
            }
        }
    }

    public void doSobel() {
        BitmapArray resultPicture = new BitmapArray(workingPicture);
        int alpha;
        int pixelColor;

        int height = workingPicture.getHeight();
        int width = workingPicture.getWidth();
        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                pixelColor = workingPicture.getPixel(x, y);
                alpha = Color.alpha(pixelColor);
                int p0 = Color.red(workingPicture.getPixel(x - 1, y - 1));
                int p1 = Color.red(workingPicture.getPixel(x, y - 1));
                int p2 = Color.red(workingPicture.getPixel(x + 1, y - 1));
                int p3 = Color.red(workingPicture.getPixel(x + 1, y));
                int p4 = Color.red(workingPicture.getPixel(x + 1, y + 1));
                int p5 = Color.red(workingPicture.getPixel(x, y + 1));
                int p6 = Color.red(workingPicture.getPixel(x - 1, y + 1));
                int p7 = Color.red(workingPicture.getPixel(x - 1, y));
                int xx = ((p2 + 2 * p3 + p4) - (p0 + 2 * p7 + p6));
                int yy = ((p6 + 2 * p5 + p4) - (p0 + 2 * p1 + p2));
                int N = (int) Math.hypot(xx, yy);
                if (N > 255) N = 255;

                resultPicture.setPixel(x, y, Color.argb(alpha, N, N, N));
            }
        }
        workingPicture = new BitmapArray(resultPicture);
    }

    private void doGaussianBlur() {
        BitmapArray resultPicture = new BitmapArray(workingPicture);
        int alpha;
        int pixelColor;
        double[][] gauss_matrix = new double[][]{
                {0.037, 0.039, 0.04, 0.039, 0.037},
                {0.039, 0.042, 0.042, 0.042, 0.039},
                {0.04, 0.042, 0.043, 0.042, 0.04},
                {0.039, 0.042, 0.042, 0.042, 0.039},
                {0.037, 0.039, 0.04, 0.039, 0.037}
        };

        for (int x = 2; x < workingPicture.getWidth() - 2; ++x) {
            for (int y = 2; y < workingPicture.getHeight() - 2; ++y) {
                double newValue = 0;
                pixelColor = workingPicture.getPixel(x, y);
                alpha = Color.alpha(pixelColor);

                for (int i = x - 2, k = 0; k < 5; ++k) {
                    for (int j = y - 2, l = 0; l < 5; ++l) {
                        newValue = newValue + Color.red(workingPicture.getPixel(i, j)) * gauss_matrix[k][l];
                    }
                }
                resultPicture.setPixel(x, y, Color.argb(alpha, (int) newValue, (int) newValue, (int) newValue));
            }
        }
        workingPicture = resultPicture;
    }

    private void doBinarization(binType type) {
        double T = getThreshold();
        if (type == binType.iris) {
            T /= 4.5;
        } else {
            T /= 1.5;
        }
        int color, pixelColor, alpha;
        for (int x = 0; x < workingPicture.getWidth(); ++x) {
            for (int y = 0; y < workingPicture.getHeight(); ++y) {
                pixelColor = workingPicture.getPixel(x, y);
                color = Color.red(pixelColor);
                alpha = Color.alpha(pixelColor);
                if (color > T) {
                    workingPicture.setPixel(x, y, Color.argb(alpha, 255, 255, 255));
                } else {
                    workingPicture.setPixel(x, y, Color.argb(alpha, 0, 0, 0));
                }
            }
        }

    }

    private double getThreshold() {
        int width = workingPicture.getWidth();
        int height = workingPicture.getHeight();
        double P = 0;
        // wyznaczanie progu binaryzacji
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int pixel = workingPicture.getPixel(i, j);
                P = P + Color.red(pixel);
            }
        }
        P = P / (width * height);
        return P;
    }


    private void rotate() {
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        Bitmap tmp = Bitmap.createBitmap(workingPicture.toBitmap(), 0, 0, workingPicture.getWidth(), workingPicture.getHeight(), matrix, true);
        workingPicture = new BitmapArray(tmp);
    }


    private void canny() {

    }

    public Bitmap getProcessed() {
        return workingPicture.toBitmap();
    }


}