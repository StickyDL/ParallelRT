import javax.vecmath.Point3d;
import java.util.Random;
import java.util.ArrayList;

/**
 * PolkaDotShad class is a type of shader that can be applied to graphic objects.
 * It gives the appearance of a tri-color polka-dotting on the object.
 * This class is only accurately used on horizontal triangles at the moment.
 *
 * @author  Steve Glazer
 * @author  Sara Jackson
 * @author  Sam Milton
 * @version 16-Feb-2012
 */
public class PolkaDotShad extends Shader {
	ArrayList<Color> colors;
	double polkaDist, polkaRad;
	Point3d bound;
	Random rand;
	
	/**
     * Constructor
     *
     * @param origin            the origin of the shader
     * @param bound             bound of the shader
     * @param color1            the primary color of the shader
     * @param color2            color for half the polka-dots
     * @param color3            color for the other half of the polka-dots
     * @param polkaDist         distance between the polka-dots
     * @param polkaRad          radius of the polka-dots
     */
	public PolkaDotShad(Point3d origin, Point3d bound, Color color1, Color color2, Color color3, double polkaDist, double polkaRad) {
		super(origin);
		this.bound = bound;
		colors = new ArrayList<Color>(3);
		colors.add(color1);
		colors.add(color2);
		colors.add(color3);
		this.polkaDist = polkaDist;
		this.polkaRad = polkaRad;
		this.rand = new Random();
	}
	
	/**
     * Retrieves the color of the shader at the given point.
     * Determines the placement of the point on the shader to determine
     * the color.
     *
     * @param point     the point to get that color of the shader at
     *
     * @return the color at the given point
     */
	public Color shade(Point3d point) {
		Color result = null;
		boolean even = true;
		
		for(double x=this.origin.x; x < (this.bound.x+polkaDist); x += this.polkaDist) {
			if(even) {
				for(double z=this.origin.z; z > (this.bound.z-polkaDist); z -= this.polkaDist) {
					if(point.distance(new Point3d(x, ((this.origin.y + this.bound.y) / 2), z)) < polkaRad) {
						result = colors.get(0);
						break;
					}
				}
				even = false;
			}
			else {
				for(double z=(this.origin.z-polkaRad); z > (this.bound.z-polkaDist); z -= this.polkaDist) {
					if(point.distance(new Point3d(x, ((this.origin.y + this.bound.y) / 2), z)) < polkaRad) {
						result = colors.get(1);
						break;
					}
				}
				even = true;
			}
			if(result != null)
				break;
		}
	
		if(result == null) {
			result = colors.get(2);
		}
		return result;

	}
}