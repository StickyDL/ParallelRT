import java.util.ArrayList;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

public class Rectangle extends GraphicObject {

	ArrayList<Point3d> vertices;
	Shader shader = null;
	

	public Rectangle(ArrayList<Point3d> vertices, Vector3d normal, Color ambc) {

		this.vertices = vertices;
		this.normal = normal;
		//this.normal.normalize();
		this.ambcolor = ambc;
		ka = 0.4;
		kd = 0.3;
		ks = 0.2;
		ke = 20;
		//Checker Board Shader
		//shader = new CheckBoardShad(this.vertices.get(0), new Color(255.0, 0.0, 0.0), 
		//										new Color(255.0, 255.0, 0.0), 1.5);
		
		//Polka Dot Shader
		//shader = new PolkaDotShad(this.vertices.get(0), new Color(255.0, 0.0, 0.0), 
		//										new Color(255.0, 255.0, 0.0), 1.5, .25);
		
		//Ripple Shader
		//shader = new RippleShad(vertices, 2.0);
	}

	public Point3d intersect(Ray r) {

		//D = 1 or F =1 if you're looking at Bailey's slides, possibly -1 depending on wording.
		//Distance to Rectangle
		double f = 1;

		double bottomT = normal.dot(r.direction);
		
		if(bottomT == 0){
			//no intersection
			return null;
		}else{
			//*Option 2*
			Vector3d r0 = new Vector3d(r.origin.x, r.origin.y, r.origin.z);
			double topT = -(normal.dot(r0) + f);
			double t = topT/bottomT;
			if(t < 0){
				//intersects behind origin, ignore
				return null;
			}else{
				//Calculate Intersection
				double xintersect = (r.origin.x + r.direction.x) * t;
				double yintersect = (r.origin.y + r.direction.y) * t;
				double zintersect = -((r.origin.z + r.direction.z) * t);
				//System.out.println("X: " + xintersect + "; Z: " + zintersect);
				
				Point3d iPoint = null;
				//iPoint = new Point3d(xintersect, yintersect, zintersect);
				
				double xmin = vertices.get(0).x;
				double xmax = vertices.get(0).x;
				double zmax = vertices.get(0).z;
				double zmin = vertices.get(0).z;
				
				for(int i = 1; i < vertices.size(); i++){
					if(vertices.get(i).x < xmin){
						xmin = vertices.get(i).x;
					}else if( vertices.get(i).x > xmax ){
						xmax = vertices.get(i).x;
					}
					if(vertices.get(i).z < zmin){
						zmin = vertices.get(i).z;
					}else if( vertices.get(i).z > zmax ){
						zmax = vertices.get(i).z;
					}
					
				}
				
				if(xintersect <= xmax && xintersect >= xmin && zintersect >= zmin && zintersect <= zmax){
					iPoint = new Point3d(xintersect, yintersect, zintersect);
				}
				
				return iPoint;
			}
			
		}
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