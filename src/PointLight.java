import javax.vecmath.Point3d;

public class PointLight {

	double red, green, blue;
	Point3d position;

	public PointLight(Point3d position, Color c) {

		this.position = position;
		this.red = c.r;
		this.green = c.g;
		this.blue = c.b;

	}

} 