package peksa.irisr.app;


public class Circle{

    private int x;
    private int y;
    private int r;

    Circle(int X, int Y, int R)
    {
        x = X;
        y = Y;
        r = R;
    }
    Circle(Point center, int r)
    {
        x = center.getX();
        y = center.getY();
        this.r = r;
    }
    Circle()
    {
        x =0;
        y = 0;
        r = 0;
    }
    int getX(){ return x;}
    int getY(){ return y;}
    Point getCenter(){ return new Point(x, y); }
    int getR(){ return r;}
}
