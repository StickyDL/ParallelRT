import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

/**
 * Represents a Sphere in a 3d Scene. 
 *
 * @author  Steve Glazer
 * @author  Sara Jackson
 * @author  Sam Milton
 * @version 16-Feb-2012
 */
public class Sphere extends GraphicObject {
	Point3d center;
	double radius;
	
	/**
	 *  Creates a Sphere object with the given center, radius, ambient color,
	 *  reflectivity, and transparency.
	 *   
     * @param center    Cente of the sphere
     * @param radius    Radius of the sphere
     * @param ambc      Ambient Color
     * @param kr        Reflectivity (0-1). 0=Non-Reflective 1=Reflective
     * @param kt        Transparency (0-1). 0=Non-Transparent 1=Transparent
	 */
	public Sphere(Point3d center, double radius, Color ambc, double kr, double kt){
	    this(center, radius, ambc, 0.2, 0.2, 0.4, 20, kr, kt);
	}
	
	/**
	 *  Constructor
	 *   
	 * @param center    Cente of the sphere
     * @param radius    Radius of the sphere
     * @param ambc      Ambient Color
	 * @param ka        Ambient Lighting (0-1). Amount of background light
	 * @param kd        Diffuse Lighting (0-1). Lambertian Reflection
	 * @param ks        Specular Lighting (0-1). Mirror-like Reflection
	 * @param ke        Exponent (20-100. Needs to be EVEN) - Controls size of Specular Highlight
	 * @param kr        Reflectivity (0-1). 0=Non-Reflective 1=Reflective
	 * @param kt        Transparency (0-1). 0=Non-Transparent 1=Transparent
	 */
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
	
	/**
	 * Determines whether the given ray intersects the sphere.
	 * Assigns the intersection point into the given iPoint
	 * value.
	 *
	 * Used in the pixel-parallelized version
	 *
	 * @param r         Ray to determine intersection
	 * @param iPoint    Point to set point of intersection to
	 *
	 * @return true if ray intersects sphere, false otherwise
	 */
	 public boolean intersect(Ray r, Point3d iPoint){
         double dx=0.0, dy=0.0, dz=0.0, b=0.0, c=0.0, underRoot=0.0, t1=0.0, t2=0.0, t=-1.0;
         double xintersect=0.0, yintersect=0.0, zintersect=0.0;
         
         // Extra Padding
         long p0, p1, p2, p3, p4, p5, p6, p7;
         long p8, p9, pa, pb, pc, pd, pe, pf;

		 dx = r.direction.x;
		 dy = r.direction.y;
		 dz = r.direction.z;

		 b = 2 * ((dx*(r.origin.x - center.x)) + (dy*(r.origin.y - center.y)) + (dz*(r.origin.z - center.z)));
		 c = ((r.origin.x - center.x)*(r.origin.x - center.x)) + ((r.origin.y - center.y)*(r.origin.y - center.y)) + ((r.origin.z - center.z)*(r.origin.z - center.z)) - (radius * radius);

		 underRoot = (b * b) - (4 * c);

		 t = -1;

		 if( underRoot < 0 ) {
			 //no intersection
			 return false;
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
				return false;
			}
		 }

		xintersect = r.origin.x + (dx * t);
		yintersect = r.origin.y + (dy * t);
		zintersect = r.origin.z + (dz * t) - 0.0000001;
		
		iPoint.set(xintersect, yintersect, zintersect);
	
		return true; 
	}
	
	/**
     * Determines whether the given ray intersects the object.
     * If so, the intersection point is returned
     *
     * Used for frame parallelized raytracer
     *
     * @param r         the ray to determine intersection
     *
     * @return the point of intersection, null otherwise
     */
	public Point3d intersect(Ray r){
         double dx=0.0, dy=0.0, dz=0.0, b=0.0, c=0.0, underRoot=0.0, t1=0.0, t2=0.0, t=-1.0;
         double xintersect=0.0, yintersect=0.0, zintersect=0.0;
         Point3d iPoint = new Point3d();
         
         // Extra Padding
         long p0, p1, p2, p3, p4, p5, p6, p7;
         long p8, p9, pa, pb, pc, pd, pe, pf;

		 dx = r.direction.x;
		 dy = r.direction.y;
		 dz = r.direction.z;

		 b = 2 * ((dx*(r.origin.x - center.x)) + (dy*(r.origin.y - center.y)) + (dz*(r.origin.z - center.z)));
		 c = ((r.origin.x - center.x)*(r.origin.x - center.x)) + ((r.origin.y - center.y)*(r.origin.y - center.y)) + ((r.origin.z - center.z)*(r.origin.z - center.z)) - (radius * radius);

		 underRoot = (b * b) - (4 * c);

		 t = -1;

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

		xintersect = r.origin.x + (dx * t);
		yintersect = r.origin.y + (dy * t);
		zintersect = r.origin.z + (dz * t) - 0.0000001;
		
		iPoint.set(xintersect, yintersect, zintersect);
	
		return iPoint; 
	}
	
	/**
     * Returns the surface normal of the object at the given point
     *
     * Used for frame parallelized raytracer
     *
     * @param point     the point at which to determine the normal
     *
     * @return the normal vector from the given point
     */
	public Vector3d getNormal(Point3d point) {
	    //calculate the surface normal at the intersection
	    double xnorm=0.0, ynorm=0.0, znorm=0.0;
	    Vector3d result = new Vector3d();
	    
        // Extra Padding
	    long p0, p1, p2, p3, p4, p5, p6, p7;
        long p8, p9, pa, pb, pc, pd, pe, pf;
        
		xnorm = (point.x - center.x) / radius;
		ynorm = (point.y - center.y) / radius;
		znorm = (point.z - center.z) / radius;
		
		result.set(xnorm, ynorm, znorm);
			
		//Surface normal vector 
		return result;
	}
	
	/**
     * Determines the surface normal of the object at the given point.
     * The surface normal is written into the given vector.
     *
     * Used for pixel parallelized raytracer
     *
     * @param point     the point at which to determine the normal
     * @param norm      the vector the surface normal is written into
     */
	public void getNormal(Point3d point, Vector3d norm) {
		norm.set((point.x - center.x) / radius, (point.y - center.y) / radius, (point.z - center.z) / radius);
		norm.normalize();
	}
	 
	/**
     * Returns the color of the object at the given point
     *
     * @param point     the point at which to determine the color
     *
     * @return the color of the object at the given point
     */
	public Color getColor(Point3d point) {
		return this.ambcolor;
	}

} 
