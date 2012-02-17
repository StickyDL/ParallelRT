import java.util.*;

/**
 * The World holds all the objects for a 3d scene
 *
 * @author  Steve Glazer
 * @author  Sara Jackson
 * @author  Sam Milton
 * @version 16-Feb-2012
 */
public class World {
	
	ArrayList<GraphicObject> objectList;
	ArrayList<PointLight> lightList;
	double ambRed, ambGreen, ambBlue;
	
    /**
     * Constructor
     *
     * @param objectList    list of the objects in the scene
     * @param ambient       ambient color of the scene
     */
	public World(ArrayList<GraphicObject> objectList, Color ambient){
		this.objectList = objectList;
		this.ambRed = ambient.r;
		this.ambGreen = ambient.g;
		this.ambBlue = ambient.b;
	}

    /**
     * Constructor
     */
	public World() {
		this.objectList = new ArrayList<GraphicObject>();
		this.lightList = new ArrayList<PointLight>();
		this.ambRed = 255;
		this.ambGreen = 255;
		this.ambBlue = 255;
	}
	
	/**
	 * Adds a graphic object to the scene
	 *
	 * @param o     object to be added
	 */
	public void add(GraphicObject o){
		objectList.add(o);	
	}
	
	/**
	* Adds a point light to the scene
	*
	* @param e      light to be added
	*/
	public void add(PointLight e){
		lightList.add(e);
	}
}
