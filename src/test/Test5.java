package test;

import org.apache.log4j.chainsaw.Main;
import org.apache.poi.hslf.record.PPDrawing;

class Point{
	double x,y;
	public Point(double _x,double _y){
		x = _x;
		y = _y;
	}
	public double getDistance(Point p){
		return Math.sqrt((x - p.x)*(x - p.x) + (y - p.y)*(y - p.y));
	}
}
public class Test5 {
	public static void main(String[] args) {
		Point p = new Point(3.0, 4.0);
		Point origin = new Point(0.0, 0.0);
		System.out.println(p.getDistance(origin));
	}
}
