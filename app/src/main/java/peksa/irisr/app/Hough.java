package peksa.irisr.app;


import android.graphics.Color;

import java.util.ArrayList;

public class Hough {

    public Circle H(BitmapArray src, int amin, int amax) {

        int aMin = amin;
        int aMax = amax;
        BitmapArray bmOut = src;
        int maxVotes = 0;
        Circle bestCircle = null;
        int height = src.getHeight();
        int width = src.getWidth();
        // 1. Store all edge pixels in a one dimensional array.
        ArrayList<Point> WHITEPIX = new ArrayList<Point>(); // sprawdzone na bank dobrze
        for (int i = width / 5; i < width - (width / 5); i++) {
            for (int j = height / 5; j < height - height / 5; j++) {
                int pixel = src.getPixel(i, j);
                if (Color.red(pixel) == 255) // jezeli jest bialy
                {
                    WHITEPIX.add(new Point(i, j));
                }
            }
        }


        int[] acc = new int[aMax + 1];
        for (int k = 0; k < WHITEPIX.size(); k++) { // dla kazdego bialego piksela
            for (int p = 0; p < WHITEPIX.size(); p++) {
                Point temp = WHITEPIX.get(k);
                Point candidate = WHITEPIX.get(p);
                final double a = Util.calcDistance(temp, candidate) / 2;
                if (temp.getX() + 2 * aMin <= candidate.getX() && Math.abs(temp.getY() - candidate.getY()) < 2) // tak zeby byly mniej wiecej na tym samym poziomie
                {
                    if (temp != candidate && (int) a < aMax && (int) a > aMin) {
                        for (int i = 0; i < acc.length; i++) {
                            acc[i] = 0;
                        }
                        final int x0 = (candidate.getX() + temp.getX()) / 2; //srodek elipsy
                        final int y0 = (candidate.getY() + temp.getY()) / 2;
                        Point center = new Point(x0, y0);
                        for (int i = 0; i < WHITEPIX.size(); i++) { // count how many points are on the ellipse
                            if (WHITEPIX.get(i) != temp && WHITEPIX.get(i) != candidate) {
                                int d = Util.calcDistance(WHITEPIX.get(i), center);
                                if (d > aMin && d < aMax) {
                                    if (Math.abs(a - d) < 3) {
                                        acc[d]++; // licze dla ilu pikseli udalo sie policzyc takie samo b.*/
                                    }
                                }
                            }
                        }

                        int max = max(acc);


                        if (acc[max] > maxVotes) {
                            maxVotes = acc[max];
                            bestCircle = new Circle(center, (int) a);
                        }

                    }
                }
            }
        }
        return bestCircle;
    }

    public BitmapArray[] IrisBow(BitmapArray bmOut, Circle Pupil, Circle Iris) {
        int width = bmOut.getWidth();
        int height = bmOut.getHeight();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if ((Util.calcDistance(new Point(x, y), Iris.getCenter()) > Iris.getR()) ||
                        Util.calcDistance(new Point(x, y), Iris.getCenter()) < Pupil.getR()) {
                    bmOut.setPixel(x, y, Color.argb(0, 0, 0, 0));
                }
            }
        }

        BitmapArray bmOut1 = Normalization.process(bmOut, Iris.getX(), Iris.getY(), Pupil.getR(), Iris.getR());
        BitmapArray[] BitmapArrays = new BitmapArray[2];
        BitmapArrays[0] = bmOut;
        BitmapArrays[1] = bmOut1;
        return BitmapArrays;
    }

    public int max(int[] tab) {
        int m = 0;
        for (int i = 0; i < tab.length; ++i)
            if (tab[i] > tab[m])
                m = i;
        return m;
    }


}