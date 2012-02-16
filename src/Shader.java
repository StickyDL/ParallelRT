import javax.vecmath.Point3d;

/**
 * Abstract class for all shaders that can be applied to
 * objects
 *
 * @author  Steve Glazer
 * @author  Sara Jackson
 * @author  Sam Milton
 * @version 16-Feb-2012
 */
public abstract class Shader {
	public Point3d origin;
	
	// Constructor
	public Shader(Point3d origin) {
		this.origin = origin;
	}
	
	// Return the color at the given point
	abstract public Color shade(Point3d point);
}