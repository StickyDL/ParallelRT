import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

public class Ray {

	Point3d origin;
	Vector3d direction;
	
	public Ray(Point3d origin, Vector3d direction){
		this.origin = origin;
		this.direction = direction;
		this.direction.normalize();
	}
}
