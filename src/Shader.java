import javax.vecmath.Point3d;

public abstract class Shader {
	public Point3d origin;
	
	public Shader(Point3d origin) {
		this.origin = origin;
	}
	
	abstract public Color shade(Point3d point);
}