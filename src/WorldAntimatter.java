import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

import javax.vecmath.Point3d;


public class WorldAntimatter extends WorldGenerator{
	private World[] worlds;
    
	// Bound area for marbles
	final static double back = -10;
	final static double front = 0;
	final static double left = -2.25;
	final static double right = 2.25;
	final static double bottom = 0.0;
	final static double top = 3.0;
	
	/**
	 * Generates world of marbles simulating anti-protons in a magnetic field
	 * 
	 * @param frames    Number of frames to render
	 * @param particles Number of anti-protons to simulate
	 */
	public WorldAntimatter( int frames, int particles ){
		this( frames, particles, new Random().nextInt() );
	}
	
	/**
	 * Generates world of marbles simulating anti-protons in a magnetic field
	 * 
	 * @param frames    Number of frames to render
	 * @param particles Number of anti-protons to simulate
	 * @param seed      Seed value for a random number generator
	 */
	public WorldAntimatter( int frames, int particles, int seed ){
		LinkedList<World> worldList = new LinkedList<World>();
		System.out.println( "WorldAntimatter( " + frames + ", " + particles + ", " + seed + " )" );
		System.out.println( "\tGenerating Worlds" );
		
		// Point to center anti-protons around
		double centerx = 0;
		double centerz = -4;
		double centery = 0.25;
		
		// Field size to scale result into
		double widthx = 6;
		double widthz = 7;
		
		// Anti-proton radius
		double radius = 0.1;
		
		// Sphere proteries
		Color[] colors = new Color[particles*particles];
		double ka = 0.2;
		double kd = 0.2;
		double ks = 0.4;
		double kr = 0.3;
		double kt = 0.5;
		int ke = 50;
		
		// Alternate anti-proton colors to make it interesting looking
		for( int i = 0; i < particles; i++ ){
			if( (i&1) == 0 ){
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

		// Use PJ AntiprotonSeq to simulate anti-protons and get coordinate locations
		AntiprotonSeq.simulate( seed, frames, particles );
		double[][] points = AntiprotonSeq.positions;
		
		// Set up each world (each frame)
		for( int i = 0; i < frames; i++ ){
				World world = new World();
				worldList.add( world );
				
				// Set up spheres
				Point3d centers[] = new Point3d[particles];
				for( int k = 0; k < centers.length; k++ ){
					centers[k] = new Point3d(
							( points[i*particles+k][0] - 5 ) * ( widthx / 10.0 ) + centerx,
							centery,
							( points[i*particles+k][1] - 5 ) * ( widthz / 10.0 ) + centerz );
				}
				
				// Set up light
				Point3d LIGHTCENTER = new Point3d( 0.3, 2.75, 1.0 );
		
				// Add objects to world
				for( int k = 0; k < centers.length; k++ ){
					world.add( new Sphere( centers[k], radius, colors[k], ka, kd, ks, ke, kr, kt ) );
				}
				
				// Add floor to world
                world.add( new Triangle( triAVertices, new Color( 255, 255, 255 ), 0.2, 0.4, 0.6, 20, 0.6, 0.0, 1 ) );
                world.add( new Triangle( triBVertices, new Color( 255, 255, 255 ), 0.2, 0.4, 0.6, 20, 0.6, 0.0, 1 ) );
		
                // Add light source
				world.add( new PointLight( LIGHTCENTER, new Color( 255.0, 255.0, 255.0 ) ) );
			}

		worlds = worldList.toArray( new World[0] );
		System.out.println( "\tGeneration Finished" );
	}

	/**
	 * Gets the worlds produced by this world generator
	 */
	public World[] getWorlds() {
		return worlds;
	}

}
