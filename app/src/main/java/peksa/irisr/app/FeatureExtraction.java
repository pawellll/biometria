package peksa.irisr.app;

import android.graphics.Color;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by pawel on 26.06.14.
 */
public class FeatureExtraction {

    ArrayList<Integer[]> lines;
    ArrayList<Integer[]> groups;
    ArrayList<Integer[]> cumulativeSums;
    ArrayList<Integer> code;

    FeatureExtraction() {
        groups = new ArrayList<Integer[]>();
        lines = new ArrayList<Integer[]>();
        cumulativeSums = new ArrayList<Integer[]>();
        code = new ArrayList<Integer>();
    }

    public ArrayList<Integer> process(BitmapArray bitmapIn) {
        fillValues(bitmapIn);
        linesToGroups();
        calculateCumulativeSums();
        calculateCode();
        return code;
    }

    private void calculateCumulativeSums() {

        for (Integer[] group : groups) {
            double average = calcAverage(group);
            Integer[] cumulativeGroupSums = new Integer[5];
            for (int z = 0; z < cumulativeGroupSums.length; ++z) {
                cumulativeGroupSums[z] = new Integer(0);
            }
            cumulativeGroupSums[0] = 0;
            for (int i = 1; i < group.length; ++i) {
                cumulativeGroupSums[i] = (int) (cumulativeGroupSums[i - 1] + (group[i] - average));
            }
            cumulativeSums.add(cumulativeGroupSums);
        }
    }

    private void calculateCode() {
        for (Integer[] groupSum : cumulativeSums) {
            int maxIndex = maxValueIndex(groupSum);
            int minIndex = minValueIndex(groupSum);
            for (int i = 0; i < 5; ++i) {
                if (isBeetween(i, maxIndex, minIndex)) {
                    if (groupSum[i + 1] > groupSum[i]) {
                        code.add(1);
                    } else {
                        code.add(2);
                    }
                } else {
                    code.add(0);
                }

            }
        }
    }

    private boolean isBeetween(int value, int maxIndex, int minIndex) {
        if (maxIndex > minIndex) {
            return inRange(minIndex, maxIndex, value);
        } else {
            return inRange(maxIndex, minIndex, value);
        }

    }

    boolean inRange(double min, double max, double arg) {
        return arg > min && arg < max;
    }

    private double calcAverage(Integer[] t) {
        double av = 0;
        for (int i = 0; i < t.length; ++i) {
            av += t[i];
        }
        return av / t.length;
    }

    private void fillValues(BitmapArray bitmapIn) {
        int length;

        Integer[] newBand;
        Integer[] tmp;
        newBand = getLineValues(bitmapIn, new ArrayList<Integer>(), 0);
        length = newBand.length;
        for (int i = 0; i < bitmapIn.getHeight(); ) {
            if (i + 3 > bitmapIn.getHeight()) {
                continue;
            }
            if (bitmapIn.getPixel(0, i) == Color.BLUE) {
                break;
            }

            newBand = new Integer[length];
            for (int z = 0; z < newBand.length; ++z) {
                newBand[z] = new Integer(0);
            }
            for (int j = 0; j < 3; ++j, ++i) {
                tmp = getLineValues(bitmapIn, new ArrayList<Integer>(), i);
                newBand = sumArrays(tmp, newBand);
            }

            lines.add(newBand);
        }
    }

    private Integer[] getLineValues(BitmapArray bitmapIn, ArrayList<Integer> values, int y) {
        int value, pixelColor;
        for (int x = 0; x < bitmapIn.getWidth(); ) {
            value = 0;
            if (x + 5 > bitmapIn.getWidth()) {
                continue;
            }

            if (bitmapIn.getPixel(x, 0) == Color.BLUE) {
                break;
            }

            for (int i = 0; i < 10; ++i, ++x) {
                pixelColor = bitmapIn.getPixel(x, y);
                value += Color.red(pixelColor);
            }
            values.add(value);
        }
        return (Integer[]) values.toArray(new Integer[0]);
    }

    private Integer[] sumArrays(Integer[] a, Integer[] b) {
        if (a.length != b.length) {
            throw new RuntimeException("sumArrays, arrays have to have the same length");
        }

        Integer[] tmp = new Integer[a.length];
        for (int z = 0; z < tmp.length; ++z) {
            tmp[z] = new Integer(0);
        }
        for (int i = 0; i < tmp.length; ++i) {

            tmp[i] = a[i] + b[i];

        }
        return tmp;
    }


    private void linesToGroups() {

        int size = lines.size();

        for (int x = 0; x < size; ++x) {

            Integer[] line = lines.get(x);
            for (int i = 0; i < line.length; ) {

                if (i + 5 > line.length) {
                    break;
                }
                Integer[] group = new Integer[5];
                for (int j = 0; j < 5; ++j, ++i) {
                    group[j] = line[i];
                }
                groups.add(group);
            }
        }
    }

    private Integer maxValueIndex(Integer[] t) {
        int max = 0;
        for (int i = 0; i < t.length; ++i) {
            if (t[i] > t[max]) {
                max = i;
            }
        }
        return max;
    }

    private Integer minValueIndex(Integer[] t) {
        int min = 0;
        for (int i = 0; i < t.length; ++i) {
            if (t[i] < t[min]) {
                min = i;
            }
        }
        return min;
    }

}
