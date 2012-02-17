import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

/**
 * Abstract class for all graphic objects
 * Currently only triangles and spheres
 *
 * @author  Steve Glazer
 * @author  Sara Jackson
 * @author  Sam Milton
 * @version 16-Feb-2012
 */
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
	abstract boolean intersect(Ray r, Point3d iPoint);
	
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
	abstract Point3d intersect(Ray r);
	
	/**
     * Returns the color of the object at the given point
     *
     * @param point     the point at which to determine the color
     *
     * @return the color of the object at the given point
     */
	abstract Color getColor(Point3d point);
	
	/**
     * Returns the surface normal of the object at the given point
     *
     * Used for frame parallelized raytracer
     *
     * @param point     the point at which to determine the normal
     *
     * @return the normal vector from the given point
     */
	abstract Vector3d getNormal(Point3d point);
	
	/**
     * Determines the surface normal of the object at the given point.
     * The surface normal is written into the given vector.
     *
     * Used for pixel parallelized raytracer
     *
     * @param point     the point at which to determine the normal
     * @param norm      the vector the surface normal is written into
     */
	abstract void getNormal(Point3d point, Vector3d norm);

}

