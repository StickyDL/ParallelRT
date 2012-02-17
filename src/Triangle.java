import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import java.util.ArrayList;

/**
 * Represents a Triangle object in the 3d scene
 *
 * @author  Steve Glazer
 * @author  Sara Jackson
 * @author  Sam Milton
 * @version 16-Feb-2012
 */
public class Triangle extends GraphicObject {

	ArrayList<Point3d> vertices;
	Shader shader = null;

    /**
     * Constructor
     *
     * @param vertices      vertices of the triangle
     * @param ambc          ambient color of the triangle
     */
	public Triangle(ArrayList<Point3d> vertices, Color ambc)
	{
        this(vertices, ambc, 0.2, 0.4, 0.6, 20, 0.0, 0.0, 0);
    }
    
    /**
     * Constructor
     *
     * @param vertices      vertices of the triangle
     * @param ambc          ambient color of the triangle
     * @param shaderIndex   index of the shader to use
     */
    public Triangle(ArrayList<Point3d> vertices, Color ambc, int shaderIndex)
    {
        this(vertices, ambc, 0.2, 0.4, 0.6, 20, 0.0, 0.0, shaderIndex);
    }

    /**
	 *  Creates a Triangle object with the given vertices, surface normal, ambient color,
	 *  ambient light, diffuse light, specular light, the exponent, reflectivity,
	 *  transparency, and a shaderIndex.
	 *   
	 * @param vertices      Vertices of the triangle
     * @param ambc          Ambient color of the triangle
	 * @param ka            Ambient Lighting (0-1). Amount of background light
	 * @param kd            Diffuse Lighting (0-1). Lambertian Reflection
	 * @param ks            Specular Lighting (0-1). Mirror-like Reflection
	 * @param ke            Exponent (20-100. Needs to be EVEN) - Controls size of Specular Highlight
	 * @param kr            Reflectivity (0-1). 0=Non-Reflective 1=Reflective
	 * @param kt            Transparency (0-1). 0=Non-Transparent 1=Transparent
	 * @param shaderIndex   0=No Shader 1=Checkerboard 2=PolkaDot 3=Ripple
	 */
    public Triangle(ArrayList<Point3d> vertices, Color ambc, double ka, double kd, double ks, int ke, double kr, double kt, int shaderIndex)
	{
		this.vertices = vertices;
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
		
        Vector3d p3p2 = new Vector3d(vertices.get(2).x-vertices.get(1).x, vertices.get(2).y-vertices.get(1).y, vertices.get(2).z-vertices.get(1).z);
        Vector3d p1p2 = new Vector3d(vertices.get(0).x-vertices.get(1).x, vertices.get(0).y-vertices.get(1).y, vertices.get(0).z-vertices.get(1).z);
        this.normal = new Vector3d();
		this.normal.cross(p3p2,p1p2);
		this.normal.normalize();
		
		if ( shaderIndex == 1 ) {
    		//Checker Board Shader
            this.shader = new CheckBoardShad(this.vertices.get(1), new Color(0.0, 0.0, 0.0), 
                                                    new Color(500.0, 500.0, 500.0), 1.5);
		} else if ( shaderIndex == 2 ) {
    		//Polka Dot Shader
            this.shader = new PolkaDotShad(new Point3d(-2.25, 0.0, 0.0), new Point3d(1.25, 0.0, -10.0), 
                                        new Color(50.0, 100.0, 200.0), new Color(255.0, 255.0, 0.0),
                                        new Color(200.0, 0.0, 0.0), 0.6, 0.25);
        } else if ( shaderIndex == 3 ) {
    		//Ripple Shader
            this.shader = new RippleShad(new Point3d(0.0, 0.0, -5.0));
        }
    }

    /**
     * Determines whether the given ray intersects the object.
     * If so, the given iPoint value is set to the intersection
     * point and true is return.
     *
     * Used for pixel parallelized raytracer
     *
     * @param r         the ray to determine intersection
     * @param iPoint    the point to write the intersection point into
     *
     * @return true if the ray intersects the object, false otherwise
     */
	public boolean intersect(Ray r, Point3d iPoint) 
	{
	    double ntd=0.0, t=0.0, total=0.0, absPiTotal=0.0;
	    Vector3d rOriginVec = new Vector3d();
	    Vector3d p1 = new Vector3d();
	    Vector3d l1 = new Vector3d();
	    Vector3d l2 = new Vector3d();
	    Vector3d l3 = new Vector3d();
	    Point3d poi = new Point3d();
	    
	    // Extra Padding
        long z0, z1, z2, z3, z4, z5, z6, z7;
        long z8, z9, za, zb, zc, zd, ze, zf;
	    
	    ntd = normal.dot(r.direction);
        if ( ntd > 0 ) {
            return false;
        } else {
            rOriginVec.set(r.origin.x, r.origin.y, r.origin.z);
            p1.set(vertices.get(0).x, vertices.get(0).y, vertices.get(0).z);
            t = -(normal.dot(rOriginVec) - normal.dot(p1)) / ntd;
            poi.set(r.origin.x+t*r.direction.x, r.origin.y+t*r.direction.y, r.origin.z+t*r.direction.z);
            l1.set(vertices.get(0).x-poi.x, vertices.get(0).y-poi.y, vertices.get(0).z-poi.z);
            l1.normalize();
            l2.set(vertices.get(1).x-poi.x, vertices.get(1).y-poi.y, vertices.get(1).z-poi.z);
            l2.normalize();
            l3.set(vertices.get(2).x-poi.x, vertices.get(2).y-poi.y, vertices.get(2).z-poi.z);
            l3.normalize();
            total = Math.acos(l1.dot(l2)) + Math.acos(l2.dot(l3)) + Math.acos(l3.dot(l1));
            absPiTotal = 2*Math.PI - total;
            if (absPiTotal < 0) {
                absPiTotal *= -1;
            }
            if(absPiTotal < .0001){ //point is inside plane
                iPoint.set(poi.x, poi.y, poi.z);
                return true;
            } else { //point is outside plane
                return false;
            }
        }
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
	public Point3d intersect(Ray r) 
	{
	    double ntd=0.0, t=0.0, total=0.0, absPiTotal=0.0;
	    Vector3d rOriginVec = new Vector3d();
	    Vector3d p1 = new Vector3d();
	    Vector3d l1 = new Vector3d();
	    Vector3d l2 = new Vector3d();
	    Vector3d l3 = new Vector3d();
	    Point3d poi = new Point3d();
	    
	    // Extra Padding
        long z0, z1, z2, z3, z4, z5, z6, z7;
        long z8, z9, za, zb, zc, zd, ze, zf;
	    
	    ntd = normal.dot(r.direction);
        if ( ntd > 0 ) {
            return null;
        } else {
            rOriginVec.set(r.origin.x, r.origin.y, r.origin.z);
            p1.set(vertices.get(0).x, vertices.get(0).y, vertices.get(0).z);
            t = -(normal.dot(rOriginVec) - normal.dot(p1)) / ntd;
            poi.set(r.origin.x+t*r.direction.x, r.origin.y+t*r.direction.y, r.origin.z+t*r.direction.z);
            l1.set(vertices.get(0).x-poi.x, vertices.get(0).y-poi.y, vertices.get(0).z-poi.z);
            l1.normalize();
            l2.set(vertices.get(1).x-poi.x, vertices.get(1).y-poi.y, vertices.get(1).z-poi.z);
            l2.normalize();
            l3.set(vertices.get(2).x-poi.x, vertices.get(2).y-poi.y, vertices.get(2).z-poi.z);
            l3.normalize();
            total = Math.acos(l1.dot(l2)) + Math.acos(l2.dot(l3)) + Math.acos(l3.dot(l1));
            absPiTotal = 2*Math.PI - total;
            if (absPiTotal < 0) {
                absPiTotal *= -1;
            }
            if(absPiTotal < .0001){ //point is inside plane
                return poi;
            } else { //point is outside plane
                return null;
            }
        }
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
	public Vector3d getNormal(Point3d point) 
	{
	    return this.normal;
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
	public void getNormal(Point3d point, Vector3d norm) 
	{
	    norm.x = this.normal.x;
	    norm.y = this.normal.y;
	    norm.z = this.normal.z;
	}
	
	/**
     * Returns the color of the object at the given point
     *
     * @param point     the point at which to determine the color
     *
     * @return the color of the object at the given point
     */
	public Color getColor(Point3d point) {
		Color result = new Color(0,0,0);
		
		// Extra Padding
        long p0, p1, p2, p3, p4, p5, p6, p7;
        long p8, p9, pa, pb, pc, pd, pe, pf;
		
		if(shader != null) {
			result = shader.shade(point);
		}
		else
			result = this.ambcolor;
		return result;
	}

} 