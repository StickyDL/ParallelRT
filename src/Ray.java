import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

/**
 * The ray class represents a ray through a 3d scene.
 * It consists of an origin of the ray and a vector
 * direction.
 *
 * @author  Steve Glazer
 * @author  Sara Jackson
 * @author  Sam Milton
 * @version 16-Feb-2012
 */
public class Ray {

	Point3d origin;
	Vector3d direction;
	
	/**
     * Constructor
     *
     * @param origin        origin of the ray
     * @param direction     direction of the ray
     */
	public Ray(Point3d origin, Vector3d direction){
		this.origin = origin;
		this.direction = direction;
		this.direction.normalize();
	}
}
