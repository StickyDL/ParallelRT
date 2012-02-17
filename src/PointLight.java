import javax.vecmath.Point3d;

/**
 * The PointLight class represents a point light in
 * a 3d scene. A point light has a position and color
 * which will light up the objects in the scene
 *
 * @author  Steve Glazer
 * @author  Sara Jackson
 * @author  Sam Milton
 * @version 16-Feb-2012
 */
public class PointLight {

	double red, green, blue;
	Point3d position;

    /**
     * Constructor
     *
     * @param position      position of the point light
     * @param c             color of the point light
     */
	public PointLight(Point3d position, Color c) {

		this.position = position;
		this.red = c.r;
		this.green = c.g;
		this.blue = c.b;

	}

} 