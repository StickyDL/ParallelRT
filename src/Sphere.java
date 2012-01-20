import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

public class Sphere extends GraphicObject {
	Point3d center;
	double radius;
	
	/*
	    Creates a Sphere object with the given center, radius, ambient color,
	    reflectivity, and transparency.
	    
	    ka - Ambient Lighting (0-1). Amount of background light
	    kd - Diffuse Lighting (0-1). Lambertian Reflection
	    ks - Specular Lighting (0-1). Mirror-like Reflection
	    ke - Exponent (20-100. Needs to be EVEN) - Controls size of Specular Highlight
	    kr - Reflectivity (0-1). 0=Non-Reflective 1=Reflective
	    kt - Transparency (0-1). 0=Non-Transparent 1=Transparent
	*/
	public Sphere(Point3d center, double radius, Color ambc, double kr, double kt){
	    this(center, radius, ambc, 0.2, 0.2, 0.4, 20, kr, kt);
	}
	
	public Sphere(Point3d center, double radius, Color ambc, double ka, double kd,
	                double ks, int ke, double kr, double kt) {
    	this.center = center;
    	this.radius = radius;
    	this.ambcolor = ambc;
    	this.ka = ka;
        this.kd = kd;
        this.ks = ks;
        if ( (ke & 1) == 0 ) {
            this.ke = (double)ke;
        } else {
            this.ke = (double)ke+1.0;
        } 
    	this.kr = kr;
    	this.kt = kt;   
	}
	
	 public Point3d intersect(Ray r){

		 double dx = r.direction.x;
		 double dy = r.direction.y;
		 double dz = r.direction.z;

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
