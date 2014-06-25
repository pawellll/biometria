package peksa.irisr.app;

import android.graphics.Color;

import java.util.ArrayList;

/**
 *
 * Created by pawel on 24.06.14.
 */
public class DaughmanOperator {
    BitmapArray workingPicture;
    ArrayList<Point> bestCircle;
    double maxGradient;

    DaughmanOperator(BitmapArray inBitmap) {
        workingPicture = inBitmap;
        bestCircle = new ArrayList<Point>();
        maxGradient = 0;
    }

    void process() {
        double tmpGradient;
        int W = workingPicture.getWidth();
        int H = workingPicture.getHeight();
        int maxRadius = W > H ? H : W;

        for (int x = 1; x < H - 1; ++x) {
            for (int y = 1; y < W - 1; ++y) {

                for (int r = 3; r < maxRadius; ++r) {
                    ArrayList<Point> tmpCircle = new ArrayList<Point>();
                    getCircle(x, y, r, tmpCircle);
                    tmpGradient = calcGradientCircle(tmpCircle);
                    if (tmpGradient > maxGradient) {
                        maxGradient = tmpGradient;
                        bestCircle = tmpCircle;
                    }

                }

            }
        }

        for (Point p : bestCircle) {
            workingPicture.setPixel(p.getX(), p.getY(), Color.argb(255, 255, 255, 0));
        }

    }


    double calcGradient(int x, int y) {
        double gradient;
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
        gradient = Math.hypot(xx, yy);
        return gradient;
    }

    double calcGradientCircle(ArrayList<Point> Circle) {
        double gradient = 0.0;
        for (Point p : Circle) {
            gradient = calcGradient(p.getX(), p.getY());
        }
        return gradient;
    }

    void getCircle(int i, int j, int r, ArrayList<Point> Circle) {
        Circle.clear();
        int H = workingPicture.getHeight();
        int W = workingPicture.getWidth();
        for (int x = i - r; (x < i + r + 1) && (x < W ); ++x) {
            for (int y = j - r; (y < j + r + 1) && (y < H ); ++y) {

                if(x-r<0 || y-r<0){
                    return;
                }

                if (getDistance(x, y, i, j) == r) {
                    Circle.add(new Point(x, y));
                }
            }
        }
    }

    double getDistance(int x0, int y0, int x1, int y1) {
        return Math.hypot(x1 - x0, y1 - y0);
    }
}
