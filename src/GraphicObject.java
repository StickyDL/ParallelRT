import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;



public abstract class GraphicObject {

	Color ambcolor;
	Color speccolor = new Color(255, 255, 255);
    Vector3d normal = null;
	double n = 0.95;
	double ka;
	double ke;
	double kd;
	double ks;
	double kr;
	double kt;
	
	abstract boolean intersect(Ray r, Point3d iPoint);
	abstract Point3d intersect(Ray r);
	abstract Color getColor(Point3d point);
	abstract Vector3d getNormal(Point3d point);
	abstract void getNormal(Point3d point, Vector3d norm);

}

