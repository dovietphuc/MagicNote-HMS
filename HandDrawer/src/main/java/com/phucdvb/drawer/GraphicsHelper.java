package com.phucdvb.drawer;

import android.graphics.Point;
import android.graphics.PointF;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GraphicsHelper {

    public static boolean isCircle(List<Point> pointList) {
        PointF a = new PointF(pointList.get(0).x, pointList.get(0).y);
        PointF b = new PointF(pointList.get(pointList.size()/3).x, pointList.get(pointList.size()/3).y);
        PointF c = new PointF(pointList.get(pointList.size() * 2/3).x, pointList.get(pointList.size() * 2/3).y);
        PointF center = findCenter(a, b, c);
        if(Math.abs(distance(a, center) - distance(b, center)) < 150 && Math.abs(distance(b, center) - distance(c, center)) < 150){
            return true;
        }
        return false;
    }

    public static boolean isLine(List<Point> pointList) {
        int xChange = pointList.get(1).x - pointList.get(0).x;
        int yChange = pointList.get(1).y - pointList.get(0).y;

        int xChangeN = 0, yChangeN = 0;
        for (int i = 2; i < pointList.size(); i++) {

            xChangeN = pointList.get(i).x - pointList.get(i - 1).x;
            yChangeN = pointList.get(i).y - pointList.get(i - 1).y;
            if (Math.abs(xChange * yChangeN - yChange * xChangeN) > 70) {
                return false;
            }
        }
        return true;
    }

    public static PointF findCenter(PointF a, PointF b, PointF c)
    {
        float k1 = (a.y - b.y) / (a.x - b.x); //Two-point slope equation
        float k2 = (a.y - c.y) / (a.x - c.x); //Same for the (A,C) pair
        PointF midAB = new PointF((a.x + b.x) / 2, (a.y + b.y) / 2); //Midpoint formula
        PointF midAC = new PointF((a.x + c.x) / 2, (a.y + c.y) / 2); //Same for the (A,C) pair
        k1 = -1*k1; //If two lines are perpendicular, then the product of their slopes is -1.
        k2 = -1*k2; //Same for the other slope
        float n1 = midAB.y - k1*midAB.x; //Determining the n element
        float n2 = midAC.y - k2*midAC.y; //Same for (A,C) pair
        //Solve y1=y2 for y1=k1*x1 + n1 and y2=k2*x2 + n2
        float x = (n2-n1) / (k1-k2);
        float y = k1*x + n1;
        return new PointF(x,y);
    }

    public static float distance(PointF a, PointF b){
        return (float) Math.abs(Math.sqrt((b.x - a.x) * (b.x - a.x) + (b.y - a.y) * (b.y - a.y)));
    }
}