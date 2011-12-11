import javax.vecmath.Point3d;
import java.util.Random;
import java.util.ArrayList;

public class PolkaDotShad extends Shader {
	ArrayList<Color> colors;
	double polkaDist, polkaRad;
	Point3d bound;
	Random rand;
	
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