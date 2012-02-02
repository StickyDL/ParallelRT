import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;


public class WorldAntimatter extends WorldGenerator{
	private World[] worlds;
    
	
	final static double back = -10;
	final static double front = 0;
	final static double left = -2.25;
	final static double right = 2.25;
	final static double bottom = 0.0;
	final static double top = 3.0;
	
	public WorldAntimatter( int seconds, int particles ){
		this( seconds, particles, new Random().nextInt() );
	}
	
	public WorldAntimatter( int frames, int particles, int seed ){
		LinkedList<World> worldList = new LinkedList<World>();
		System.out.println( "WorldAntimatter( " + frames + ", " + particles + ", " + seed + " )" );
		
		double centerx = 0;
		double centerz = -2;
		double centery = 0.2;
		
		double radius = 0.1;
		
		Color[] colors = new Color[particles*particles];
		double ka = 0.2;
		double kd = 0.2;
		double ks = 0.4;
		double kr = 0.3;
		double kt = 0.5;
		int ke = 50;
		
		for( int i = 0; i < particles; i++ ){
			if( i % 2 == 0 ){
				colors[i] = new Color(255,20,147); // Pink
			}else{
				colors[i] = new Color(0,50,220); // Blue
			}
		}
		
		// Set up floor data
		Point3d PLANEVERTLF = new Point3d( left*2, bottom, front );
		Point3d PLANEVERTRF = new Point3d( right*2, bottom, front );
		Point3d PLANEVERTRR = new Point3d( right*2, bottom, back );
		Point3d PLANEVERTLR = new Point3d( left*2, bottom, back );

		ArrayList<Point3d> triAVertices = new ArrayList<Point3d>();
		ArrayList<Point3d> triBVertices = new ArrayList<Point3d>();

		triAVertices.add( PLANEVERTLF );
		triAVertices.add( PLANEVERTRF );
		triAVertices.add( PLANEVERTRR );

		triBVertices.add( PLANEVERTLF );
		triBVertices.add( PLANEVERTRR );
		triBVertices.add( PLANEVERTLR );

		AntiprotonSeq.simulate( seed, frames, particles );
		double[][] points = AntiprotonSeq.positions;
		
		for( int i = 0; i < frames; i++ ){
				World world = new World();
				worldList.add( world );
				
				// Set up spheres
				Point3d centers[] = new Point3d[particles];
				for( int k = 0; k < centers.length; k++ ){
					centers[k] = new Point3d( ( points[i*particles+k][0] - 5 ) * .75, 0.25, -points[i*particles+k][1] * .75 );
				}
				
				// Set up light
				Point3d LIGHTCENTER = new Point3d( 0.3, 2.75, 1.0 );
		
				// Add objects to world
				for( int k = 0; k < centers.length; k++ ){
					world.add( new Sphere( centers[k], radius, colors[k], ka, kd, ks, ke, kr, kt ) );
				}
                world.add( new Triangle( triAVertices, new Color( 255, 255, 255 ), 0.2, 0.4, 0.6, 20, 0.6, 0.0, 1 ) );
                world.add( new Triangle( triBVertices, new Color( 255, 255, 255 ), 0.2, 0.4, 0.6, 20, 0.6, 0.0, 1 ) );
		
				world.add( new PointLight( LIGHTCENTER, new Color( 255.0, 255.0, 255.0 ) ) );
			}

		worlds = worldList.toArray( new World[0] );
	}

	public World[] getWorlds() {
		return worlds;
	}

}
