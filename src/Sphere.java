import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

public class Sphere extends GraphicObject {
	Point3d center;
	double radius;
	
	public Sphere(Point3d center, double radius, Color ambc, double kr, double kt){
		this.center = center;
		this.radius = radius;
		this.ambcolor = ambc;
		ka = 0.2; //.5 .6
		kd = 0.2; //.1 .2
		ks = 0.4; //.6 .2
		ke = 20;
		this.kr = kr;
		this.kt = kt;
		//this.kr = 0.2;
		kt = 0.0;
	}
	
	 public Point3d intersect(Ray r){

		 double dx = r.direction.x;
		 double dy = r.direction.y;
		 double dz = r.direction.z;

//		 double a = dx*dx + dy*dy + dz*dz;
		 double b = 2 * ((dx*(r.origin.x - center.x)) + (dy*(r.origin.y - center.y)) + (dz*(r.origin.z - center.z)));
		 double c = ((r.origin.x - center.x)*(r.origin.x - center.x)) + ((r.origin.y - center.y)*(r.origin.y - center.y)) + ((r.origin.z - center.z)*(r.origin.z - center.z)) - (radius * radius);

		 double underRoot = (b * b) - (4 * c);

		 double t1, t2;
		 double t = -1;

		 if( underRoot < 0 ) {
			 //no intersection
			 return null;
		 }
		 else if( underRoot == 0) {
			 //one intersection
			 t = ((-1 * b) + Math.sqrt(underRoot)) / 2;
		 }
		 else {
			//two intersections
			//find least positive
			t1 = ((-1 * b) + Math.sqrt(underRoot)) / 2;
			t2 = ((-1 * b) - Math.sqrt(underRoot)) / 2;

			if( t1 > 0 && t2 > 0) {
				if (t1 < t2)
					t = t1;
				else
					t = t2;
			}
			else if( t < 0 ) {
				return null;
			}
			//System.out.println("t: " + t);
		 }

		double xintersect = r.origin.x + (dx * t);
		double yintersect = r.origin.y + (dy * t);
		double zintersect = r.origin.z + (dz * t) - 0.0000001;

		//calculate the surface normal at the intersection 
		double xnorm = (xintersect - center.x) / radius;
		double ynorm = (yintersect - center.y) / radius;
		double znorm = (zintersect - center.z) / radius;
			
		//Surface normal vector 
		normal = new Vector3d(xnorm, ynorm, znorm);
			
		return new Point3d(xintersect, yintersect, zintersect); 
	}
	 
	public Color getColor(Point3d point) {
		return this.ambcolor;
	}

} 
