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
        /*
        HoughWersjaElipsa hough = new HoughWersjaElipsa();
        Bitmap grey = GreyScale.process(compressedImage);
        Bitmap greyGauss = GaussianFilter.process(grey);
        Bitmap BinarizationPupil = Binarization.process(greyGauss, "Pupil");
        Bitmap BinarizationIris = Binarization.process(greyGauss, "Iris");
        Bitmap sobelPupil = Sobel.process(BinarizationPupil);
        Bitmap sobelIris = Sobel.process(BinarizationIris);
        Circle pupil = hough.H(sobelPupil,30,70);
        Circle iris = hough.H(sobelIris, 40, 70 );
         */

        doGrayScale(workingPicture);
        BitmapArray grayPicture = new BitmapArray(workingPicture);
        doGaussianBlur(workingPicture);

        BitmapArray iris = new BitmapArray(workingPicture);
        BitmapArray pupil = new BitmapArray(workingPicture);

        doBinarization(iris,binType.iris);
        doBinarization(pupil,binType.pupil);
        System.err.println("1");
        doSobel(iris);
        doSobel(pupil);
        System.err.println("2");
        HoughElipse hough = new HoughElipse();

        Circle pupilCircle = hough.H(pupil,30,70);
        System.err.println("3");
        Circle irisCircle = hough.H(iris,40,50);
        System.err.println("4");
        if(pupilCircle == null && irisCircle !=null)
        {
            System.out.println("sama teczowka");
            pupilCircle = new Circle(irisCircle.getCenter(),irisCircle.getR()/3);
        }
        else if(pupilCircle != null && irisCircle ==null)
        {
            System.out.println("sama źrenica");
            irisCircle = new Circle(pupilCircle.getCenter(),pupilCircle.getR()*2);
        }





        BitmapArray[] IRISBOW = hough.IrisBow(grayPicture,pupilCircle,irisCircle);

        System.err.println("Worth it motherfuckers");
        return IRISBOW[0].toBitmap();
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

    private void doGrayScale(BitmapArray BitmapIn) {
        int t, r, g, b, alpha, pixelColor;
        for (int x = 0; x < BitmapIn.getWidth(); ++x) {
            for (int y = 0; y < BitmapIn.getHeight(); ++y) {
                pixelColor = BitmapIn.getPixel(x, y);
                r = Color.red(pixelColor);
                g = Color.green(pixelColor);
                b = Color.blue(pixelColor);
                alpha = Color.alpha(pixelColor);
                t = (r + g + b) / 3;
                BitmapIn.setPixel(x, y, Color.argb(alpha, t, t, t));
            }
        }
    }

    private void doSobel(BitmapArray BitmapIn) {
        BitmapArray resultPicture = new BitmapArray(BitmapIn);
        int alpha;
        int pixelColor;

        int height = BitmapIn.getHeight();
        int width = BitmapIn.getWidth();
        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                pixelColor = BitmapIn.getPixel(x, y);
                alpha = Color.alpha(pixelColor);
                int p0 = Color.red(BitmapIn.getPixel(x - 1, y - 1));
                int p1 = Color.red(BitmapIn.getPixel(x, y - 1));
                int p2 = Color.red(BitmapIn.getPixel(x + 1, y - 1));
                int p3 = Color.red(BitmapIn.getPixel(x + 1, y));
                int p4 = Color.red(BitmapIn.getPixel(x + 1, y + 1));
                int p5 = Color.red(BitmapIn.getPixel(x, y + 1));
                int p6 = Color.red(BitmapIn.getPixel(x - 1, y + 1));
                int p7 = Color.red(BitmapIn.getPixel(x - 1, y));
                int xx = ((p2 + 2 * p3 + p4) - (p0 + 2 * p7 + p6));
                int yy = ((p6 + 2 * p5 + p4) - (p0 + 2 * p1 + p2));
                int N = (int) Math.hypot(xx, yy);
                if (N > 255) N = 255;

                resultPicture.setPixel(x, y, Color.argb(alpha, N, N, N));
            }
        }
        for(int i=0;i<resultPicture.getWidth();++i){
            for(int j=0;j<resultPicture.getHeight();++j){
                BitmapIn.setPixel(i,j,resultPicture.getPixel(i,j));
            }
        }

        int[] tmp = resultPicture.getARR();
        BitmapIn.setARR(tmp);
    }

    private void doGaussianBlur(BitmapArray bitmapIn) {
        BitmapArray resultPicture = new BitmapArray(bitmapIn);
        int alpha;
        int pixelColor;
        double[][] gauss_matrix = new double[][]{
                {0.037, 0.039, 0.04, 0.039, 0.037},
                {0.039, 0.042, 0.042, 0.042, 0.039},
                {0.04, 0.042, 0.043, 0.042, 0.04},
                {0.039, 0.042, 0.042, 0.042, 0.039},
                {0.037, 0.039, 0.04, 0.039, 0.037}
        };

        for (int x = 2; x < bitmapIn.getWidth() - 2; ++x) {
            for (int y = 2; y < bitmapIn.getHeight() - 2; ++y) {
                double newValue = 0;
                pixelColor = bitmapIn.getPixel(x, y);
                alpha = Color.alpha(pixelColor);

                for (int i = x - 2, k = 0; k < 5; ++k) {
                    for (int j = y - 2, l = 0; l < 5; ++l) {
                        newValue = newValue + Color.red(bitmapIn.getPixel(i, j)) * gauss_matrix[k][l];
                    }
                }
                resultPicture.setPixel(x, y, Color.argb(alpha, (int) newValue, (int) newValue, (int) newValue));
            }
        }
        int[] tmp = resultPicture.getARR();
        bitmapIn.setARR(tmp);
    }

    private void doBinarization(BitmapArray bitmapIn, binType type) {
        double T = getThreshold(bitmapIn);
        if (type == binType.iris) {
            T /= 4.5;
        } else {
            T /= 1.5;
        }
        int color, pixelColor, alpha;
        for (int x = 0; x < bitmapIn.getWidth(); ++x) {
            for (int y = 0; y < bitmapIn.getHeight(); ++y) {
                pixelColor = bitmapIn.getPixel(x, y);
                color = Color.red(pixelColor);
                alpha = Color.alpha(pixelColor);
                if (color > T) {
                    bitmapIn.setPixel(x, y, Color.argb(alpha, 255, 255, 255));
                } else {
                    bitmapIn.setPixel(x, y, Color.argb(alpha, 0, 0, 0));
                }
            }
        }

    }

    private double getThreshold(BitmapArray BitmapIn) {
        int width = BitmapIn.getWidth();
        int height = BitmapIn.getHeight();
        double P = 0;
        
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int pixel = BitmapIn.getPixel(i, j);
                P = P + Color.red(pixel);
            }
        }
        P = P / (width * height);
        return P;
    }


    private void rotate(BitmapArray bitmapIn) {
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        Bitmap tmp = Bitmap.createBitmap(bitmapIn.toBitmap(), 0, 0, workingPicture.getWidth(), workingPicture.getHeight(), matrix, true);
        BitmapArray t = new BitmapArray(tmp);
        int[] result = t.getARR();
        bitmapIn.setARR(result);
    }



    public Bitmap getProcessed() {
        return workingPicture.toBitmap();
    }


}