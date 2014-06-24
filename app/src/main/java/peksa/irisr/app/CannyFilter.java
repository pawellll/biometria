package peksa.irisr.app;

import android.graphics.Bitmap;
import android.graphics.Color;

/**
 * Created by pawel on 24.06.14.
 */

/* Canny filter class, picture should be in gray scale and processed with gaussian blur before using it*/

public class CannyFilter {

    private BitmapArray workingPicture;

    public CannyFilter(BitmapArray inBitmap) {
        workingPicture = inBitmap;
    }

    public void process() {
        double[][] gradDirections = new double[workingPicture.getWidth()][workingPicture.getHeight()];
        double[][] gradStrength = new double[workingPicture.getWidth()][workingPicture.getHeight()];

        findGradients(gradDirections, gradStrength);
        aprox(gradDirections);
        setGradients(gradStrength);
        suppression(gradDirections, gradStrength);
        doubleTresholding(40, 80);
        edgeTracking();
    }

    void findGradients(double[][] gradientDirections, double[][] gradientStrength) {
        for (int x = 1; x < workingPicture.getWidth() - 1; ++x) {
            for (int y = 1; y < workingPicture.getHeight() - 1; ++y) {
                double p0, p1, p2, p3, p4, p5, p6, p7;
                p0 = workingPicture.getPixel(x - 1, y - 1);
                p1 = workingPicture.getPixel(x, y - 1);
                p2 = workingPicture.getPixel(x + 1, y - 1);
                p3 = workingPicture.getPixel(x + 1, y);
                p4 = workingPicture.getPixel(x + 1, y + 1);
                p5 = workingPicture.getPixel(x, y + 1);
                p6 = workingPicture.getPixel(x - 1, y + 1);
                p7 = workingPicture.getPixel(x - 1, y);

                double g1 = (p2 + 2 * p3 + p4) - (p0 + 2 * p7 + p6);
                double g2 = (p6 + 2 * p5 + p4) - (p0 + 2 * p1 + p2);


                gradientStrength[x][y] = Math.hypot(g1, g2);

                double tmp;
                if (g1 == 0)
                    gradientDirections[x][y] = 0;
                else {
                    tmp = Math.atan2(g1, g2);
                    if (tmp < 0) tmp += Math.PI;
                    gradientDirections[x][y] = tmp;
                }


            }
        }
    }

    private void aprox(double[][] gradDirections) {
        double k = Math.PI / 8.0;
        for (int i = 0; i < gradDirections.length; ++i) {
            for (int j = 0; j < gradDirections[0].length; ++j) {
                double check = gradDirections[i][j];

                if (inRange(-1.0, k, check) || inRange(7.0 * k, Math.PI + Math.PI / 10, check))
                    gradDirections[i][j] = 0;
                else if (inRange(k, 3.0 * k, check))
                    gradDirections[i][j] = 1;
                else if (inRange(3 * k, 5.0 * k, check))
                    gradDirections[i][j] = 2;
                else
                    gradDirections[i][j] = 3;
            }
        }
    }

    private void setGradients(double[][] gradStrength) {
        int alpha; int pixelColor;
        for (int i = 0; i < workingPicture.getWidth(); ++i) {
            for (int j = 0; j < workingPicture.getHeight(); ++j) {
                pixelColor = workingPicture.getPixel(i,j);
                alpha = Color.alpha(pixelColor);
                int tmp = (int) gradStrength[i][j];
                if (tmp > 255) {
                    tmp = 255;
                }
                workingPicture.setPixel(i, j, Color.argb(alpha, tmp, tmp, tmp));
            }
        }
    }

    //non-maximum suppression
    private void suppression(double[][] gradDirections, double[][] gradStrength) {

        BitmapArray imageOut = new BitmapArray(workingPicture);
        int alpha; int pixelColor;
        for (int x = 1; x < imageOut.getWidth() - 1; ++x) {
            for (int y = 1; y < imageOut.getHeight() - 1; ++y) {
                pixelColor = workingPicture.getPixel(x,y);
                alpha = Color.alpha(pixelColor);
                if (gradStrength[x][y] > 20) {
                    int check = (int) gradDirections[x][y];


                    switch (check) {
                        case 0:
                            // (x,y-1), (x,y), (x,y+1)
                            if (max3(gradStrength[x][y], gradStrength[x][y-1], gradStrength[x][y+1])) {
                                imageOut.setPixel(x, y, Color.argb(alpha, 0, 0, 0));
                            }

                            break;
                        case 1:
                            //(x+1,y+1), (x,y), (x-1,y-1)
                            if (max3(gradStrength[x+1][y+1], gradStrength[x][y], gradStrength[x-1][y-1])) {
                                imageOut.setPixel(x, y, Color.argb(alpha, 0, 0, 0));
                            }

                            break;
                        case 2:
                            //(x+1,y), (x,y), (x-1,y)
                            if (max3(gradStrength[x+1][y], gradStrength[x][y], gradStrength[x-1][y])) {
                                imageOut.setPixel(x, y, Color.argb(alpha, 0, 0, 0));
                            }

                            break;
                        case 3:
                            // (x+1,y-1), (x,y), (x-1,y+1),
                            if (max3(gradStrength[x+1][y-1], gradStrength[x][y], gradStrength[x-1][y+1])) {
                                imageOut.setPixel(x, y, Color.argb(alpha, 0, 0, 0));
                            }

                            break;
                    }
                }
            }
        }

    }

    private void doubleTresholding(int t1, int t2) {
        BitmapArray imageTmp = new BitmapArray(workingPicture);
        int pixelColor,alpha,color;

        for (int x = 2; x < workingPicture.getWidth() - 2; ++x) {
            for (int y = 2; y < workingPicture.getHeight() - 2; ++y) {
                pixelColor = imageTmp.getPixel(x,y);
                alpha = Color.alpha(pixelColor);
                color = Color.red(pixelColor);

                if (color < t1)
                    workingPicture.setPixel(x, y, Color.argb(alpha,0,0,0));

                else if (color < t2) {
                    boolean flag = true;
                    check3:
                    for (int i = x - 1, l = 0; l < 3; ++l) {
                        for (int j = y - 1, k = 0; k < 3; ++k) {
                            pixelColor = imageTmp.getPixel(i+l,j+k);
                            alpha = Color.alpha(pixelColor);
                            color = Color.red(pixelColor);
                            int check = color;
                            if (check > t2) {
                                workingPicture.setPixel(x, y, Color.argb(alpha,255,255,255));
                                flag = false;
                                break check3;
                            }
                        }

                    }

                    if (flag) {
                        check5:
                        for (int ii = x - 2, ll = 0; ll < 5; ++ll) {
                            for (int jj = y - 2, kk = 0; kk < 5; ++kk) {
                                pixelColor = imageTmp.getPixel(ii+ll,jj+kk);
                                alpha = Color.alpha(pixelColor);
                                color = Color.red(pixelColor);
                                if (color > t2) {
                                    workingPicture.setPixel(x, y, Color.argb(alpha,255,255,255));
                                    break check5;
                                }
                            }
                        }
                    }

                    if (flag) {
                        workingPicture.setPixel(x, y, Color.argb(alpha,128,128,128));
                    }


                } else
                    workingPicture.setPixel(x, y, Color.argb(alpha,255,255,255));

            }
        }

    }

    private void edgeTracking() {
        BitmapArray imageTmp = new BitmapArray(workingPicture);
        int pixelColor,color,alpha;
        for (int x = 2; x < workingPicture.getWidth() - 2; ++x) {
            for (int y = 2; y < workingPicture.getHeight() - 2; ++y) {
                pixelColor = imageTmp.getPixel(x,y);
                alpha = Color.alpha(pixelColor);
                color = Color.red(pixelColor);
                boolean pixelBlack = true; // should pixel be black ?
                boolean greyPixel = false;
                if (color != 255 && color != 0) {
                    boolean flag = true;

                    check3:
                    for (int i = x - 1, l = 0; l < 3; ++l) {
                        for (int j = y - 1, k = 0; k < 3; ++k) {
                            pixelColor = imageTmp.getPixel(i+l,j+k);
                            alpha = Color.alpha(pixelColor);
                            color = Color.red(pixelColor);

                            int check = color;
                            if (check == 255) {
                                workingPicture.setPixel(x, y, Color.argb(alpha,255,255,255));
                                flag = false;
                                pixelBlack = false;
                                break check3;
                            }
                            if (check == 128)
                                greyPixel = true;
                        }

                    }

                    if (greyPixel) {
                        if (flag) {
                            check5:
                            for (int ii = x - 2, ll = 0; ll < 5; ++ll) {

                                for (int jj = y - 2, kk = 0; kk < 5; ++kk) {
                                    pixelColor = imageTmp.getPixel(ii+ll,jj+kk);
                                    alpha = Color.alpha(pixelColor);
                                    color = Color.red(pixelColor);

                                    if (color == 255) {
                                        workingPicture.setPixel(x, y, Color.argb(alpha,255,255,255));
                                        pixelBlack = false;
                                        break check5;
                                    }
                                }
                            }
                        }
                    }
                } else {
                    pixelBlack = false;
                }
                if (pixelBlack) {
                    workingPicture.setPixel(x, y, Color.argb(alpha,0,0,0));

                }
            }
        }
    }







    boolean inRange(double min, double max, double arg) {
        return arg > min && arg < max;
    }

    boolean max3(final double a, final double b, final double c) {
        return !((Math.max(a, c) == a) && (Math.max(a, b) == a));
    }


}
