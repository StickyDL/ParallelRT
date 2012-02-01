import java.util.ArrayList;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

/**
 * Simple world generator. Has two balls, one bounces.
 */
public class WorldTest extends WorldGenerator{

	private World[] worlds;

	public World[] getWorlds(){
		return worlds;
	}

	public WorldTest(){
		this( 10 );
	}

	public WorldTest( int seconds ){
		worlds = new World[24 * seconds];

		double lx = -0.4;
		double ly = 1.2;
		double lz = -1.5;
		double lspdy = 0;

		double rx = 0.25;
		double ry = 0.95;
		double rz = -1.0;

		double LSPHERERADIUS = 0.40;
		double RSPHERERADIUS = 0.466;

		for( int i = 0; i < worlds.length; i++ ){
			World world = new World();
			worlds[i] = world;

			Point3d LSPHERECENTER = new Point3d( lx, ly, lz );
			Point3d RSPHERECENTER = new Point3d( rx, ry, rz );
			ArrayList<Point3d> triAVertices = new ArrayList<Point3d>();
			ArrayList<Point3d> triBVertices = new ArrayList<Point3d>();
			Point3d LIGHTCENTER = new Point3d( 0.3, 2.75, 1.0 );

			Point3d PLANEVERTLF = new Point3d( -2.25, 0.0, 0.0 );
			Point3d PLANEVERTRF = new Point3d( 1.25, 0.0, 0.0 );
			Point3d PLANEVERTRR = new Point3d( 1.25, 0.0, -10.0 );
			Point3d PLANEVERTLR = new Point3d( -2.25, 0.0, -10.0 );

			triAVertices.add( PLANEVERTLF );
			triAVertices.add( PLANEVERTRF );
			triAVertices.add( PLANEVERTRR );

			triBVertices.add( PLANEVERTLR );
			triBVertices.add( PLANEVERTRR );
			triBVertices.add( PLANEVERTLF );

			world.add( new Sphere( LSPHERECENTER, LSPHERERADIUS, new Color( 178.5, 178.5, 178.5 ), 1.0, 0.0 ) ); // 0, 255, 0
			world.add( new Sphere( RSPHERECENTER, RSPHERERADIUS, new Color( 255.0, 255.0, 255.0 ), 0.0, 0.85 ) ); // 200, 10, 10
			world.add( new Triangle( triAVertices, new Color( 200, 200, 10 ) ) );
			world.add( new Triangle( triBVertices, new Color( 200, 200, 10 ) ) );

			world.add( new PointLight( LIGHTCENTER, new Color( 255.0, 255.0, 255.0 ) ) );

			lspdy -= .98 * (1.0 / 24.0);
			ly += lspdy * (1.0 / 24.0);
			if( ly <= LSPHERERADIUS ){
				lspdy = -lspdy * .9;
				ly = Math.abs( LSPHERERADIUS - ly ) + LSPHERERADIUS;
			}

		}
	}
}
