package peksa.irisr.app;

import java.util.ArrayList;

/**
 * Created by pawel on 28.06.14.
 */
public class Verification {

    private ArrayList<Integer> code1;
    private ArrayList<Integer> code2;

    public Verification(ArrayList<Integer> c1, ArrayList<Integer> c2) {
        code1 = c1;
        code2 = c2;
    }

    private double calcHammingDistance() {
        double distance = 0;
        int size = code1.size() < code2.size() ? code1.size() : code2.size();
        for (int i = 0; i < size; ++i) {
            if (code1.get(i) == 0 || code2.get(i) == 0) {
                continue;
            }
            if(code1.get(i)!=code2.get(i)){
                ++distance;
            }
        }
        if(distance==0.0)
            return 0.0;
        distance = distance / (1/(2*size));
        return distance;
    }

    public boolean process() {
        double distance = calcHammingDistance();
        System.err.println(distance);
        return false;
    }
}
