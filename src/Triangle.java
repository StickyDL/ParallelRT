import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import java.util.ArrayList;

public class Triangle extends GraphicObject {

	ArrayList<Point3d> vertices;
	Shader shader = null;

	public Triangle(ArrayList<Point3d> vertices, Vector3d normal, Color ambc) {

		this.vertices = vertices;
		this.normal = normal;
		this.ambcolor = ambc;
		ka = 0.2; //.4
		kd = 0.4; //.3
		ks = 0.6; //.4
		ke = 20;
		kr = 0.0;
		kt = 0.0;
		//Checker Board Shader
		shader = new CheckBoardShad(this.vertices.get(1), new Color(255.0, 0.0, 0.0), 
												new Color(255.0, 255.0, 0.0), 1.5);
		
		//Polka Dot Shader
		//shader = new PolkaDotShad(new Point3d(-2.25, 0.0, 0.0), new Point3d(1.25, 0.0, -10.0), 
		//							new Color(50.0, 100.0, 200.0), new Color(255.0, 255.0, 0.0),
		//							new Color(200.0, 0.0, 0.0), 0.6, 0.25);
		
		//Ripple Shader
		//shader = new RippleShad(new Point3d(0.0, 0.0, -5.0));
}

	public Point3d intersect(Ray r) {

		//http://www.devmaster.net/wiki/Ray-triangle_intersection
		//http://www.lighthouse3d.com/opengl/maths/index.php?raytriint
		//http://www.cs.virginia.edu/~gfx/Courses/2003/ImageSynthesis/papers/Acceleration/Fast%20MinimumStorage%20RayTriangle%20Intersection.pdf

		Vector3d edge1, edge2, tvec;
		Vector3d pvec = new Vector3d();
		Vector3d qvec = new Vector3d();

		double det, inv_det, u, v, t;
		final double EPSILON = 0.000001;

		//find vectors for two edges sharing vertex 0

		edge1 = new Vector3d(vertices.get(1).x - vertices.get(0).x, vertices.get(1).y - vertices.get(0).y,
				vertices.get(1).z - vertices.get(0).z);
		edge2 = new Vector3d(vertices.get(2).x - vertices.get(0).x, vertices.get(2).y - vertices.get(0).y,
				vertices.get(2).z - vertices.get(0).z);

		//begin calculation determinant

		pvec.cross(r.direction, edge2);

		//if determinant is near zero, ray lies in plane of triangle

		det = pvec.dot(edge1);

		if(det > (-1*EPSILON) && det < EPSILON){
			return null;
		}
		inv_det = 1.0/det;

		//calculate the distance from vertex 0 to ray origin

		tvec = new Vector3d(r.origin.x - vertices.get(0).x, r.origin.y - vertices.get(0).y,
				r.origin.z - vertices.get(0).z);

		//calculate u and test bounds

		u = pvec.dot(tvec) * inv_det;

		if( u < 0.0 || u > 1.0 ){

			return null;
		}
		
		//prepare to test v
		qvec.cross(tvec, edge1);

		//calculate v and test bounds
		v = qvec.dot(r.direction) * inv_det;

		if( v < 0.0 || (u + v) > 1.0){
			return null;
		}

		//calculate t, ray intersects triangle
		t = qvec.dot(edge2) * inv_det;

		double xintersect = r.origin.x + (r.direction.x * t);
		double yintersect = r.origin.y + (r.direction.y * t);
		double zintersect = r.origin.z + (r.direction.z * t);

		return new Point3d(xintersect, yintersect, zintersect);

	}
	
	public Color getColor(Point3d point) {
		Color result;
		if(shader != null) {
			result = shader.shade(point);
		}
		else
			result = this.ambcolor;
		return result;
	}

} 