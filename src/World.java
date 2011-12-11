import java.util.*;

public class World {
	
	ArrayList<GraphicObject> objectList;
	ArrayList<PointLight> lightList;
	//PointLight light;
	double ambRed, ambGreen, ambBlue; 

	public World(ArrayList<GraphicObject> objectList, Color ambient){
		this.objectList = objectList;
		this.ambRed = ambient.r;
		this.ambGreen = ambient.g;
		this.ambBlue = ambient.b;
	}

	public World() {
		this.objectList = new ArrayList<GraphicObject>();
		this.lightList = new ArrayList<PointLight>();
		this.ambRed = 255;
		this.ambGreen = 255;
		this.ambBlue = 255;
	}
	
	public void add(GraphicObject o){
		objectList.add(o);	
	}
	public void add(PointLight e){
		lightList.add(e);//light = e;	
	}
	public void transform(GraphicObject o){
		
	}
}
