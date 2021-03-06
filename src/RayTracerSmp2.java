import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import edu.rit.pj.Comm;
import edu.rit.pj.ParallelTeam;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.File;
import java.util.Random;

/**
 * Runs a ray tracer as an Smp
 * 
 * Parallelizes the pixels.  So each thread computes a subset of pixels from
 * a single frame
 *
 */
public class RayTracerSmp2{
	final static Point3d CAMERACENTER = new Point3d( 0.0, 1.0, 2.0 );
	final static Point3d CAMERALOOKAT = new Point3d( 0.0, 0.0, -1.0 ); // Direction
	final static Vector3d CAMERAUP = new Vector3d( 0.0, 1.0, 0.0 );
	
	final static int THREADS = ParallelTeam.getDefaultThreadCount();
	
	private static boolean GUI = false;   // Show rendering gui
	private static boolean PLAYER = true; // Show player after rendering
	
	private WorldGenerator genWorlds;

	public static void main( String[] args ) throws Exception{
		Comm.init(args);
		
		int seed = new Random().nextInt();
		WorldGenerator wg = null;
		int frames = 5*24;
		int marbles = 5;
		String world = "marbles";
		
		// Display help
		if( args.length == 1 && ( args[0].contains( "help" ) || args[0].equals( "-h" ) ) ){
			usage();
		}
		
		// Check commandline arguments
		for( int i = 0; i < args.length; i++ ){
			args[i] = args[i].toLowerCase();
			
			try{
				if( args[i].startsWith( "seed" ) ){
					seed = Integer.parseInt( args[i].substring( 5 ) );
					
				}else if( args[i].startsWith( "frames" ) ){
					frames = Integer.parseInt( args[i].substring( 7 ) );
					
				}else if( args[i].startsWith( "seconds" ) ){
					frames = Integer.parseInt( args[i].substring( 8 ) ) * 24;
					
				}else if( args[i].startsWith( "world" ) ){
					world = args[i].substring( 6 );
					if( world.startsWith( "world" ) ){
						world = world.substring( 5 );
					}
					
				}else if( args[i].startsWith( "marbles" ) ){
					marbles = Integer.parseInt( args[i].substring( 8 ) );
					
				}else if( args[i].startsWith( "gui" ) ){
					try{
						GUI = Integer.parseInt( args[i].substring( 4 ) ) != 0;
					}catch( Exception e ){
						try{
							GUI = Boolean.parseBoolean( args[i].substring( 4 ) );
						}catch( Exception ex ){
							System.err.println( "Argument: \"" + args[i].substring( 4 ) + "\" for 'gui' unknown. Ignored." );
						}
					}
					
				}else if( args[i].startsWith( "player" ) ){
					try{
						PLAYER = Integer.parseInt( args[i].substring( 7 ) ) != 0;
					}catch( Exception e ){
						try{
							PLAYER = Boolean.parseBoolean( args[i].substring( 7 ) );
						}catch( Exception ex ){
							System.err.println( "Value: \"" + args[i].substring( 7 ) + "\" for 'player' unknown. Ignored." );
						}
					}
				}else{
					System.err.println( "Argument: \"" + args[i] + "\" unknown. Ignored." );
				}
			}catch( Exception e ){
				System.err.println( "Argument: " + args[i] + " not understood. Ignored." );
			}
		}
		
		// Set up world generator
		if( world.equals( "marbles" ) ){
			wg = new WorldMarbles( frames, marbles, true, seed );
		}else if( world.equals( "antimatter" ) ){
			wg = new WorldAntimatter( frames, marbles, seed );
		}else if( world.equals( "collide" ) ){
			wg = new WorldCollide( frames, marbles, true, seed );
		}else if( world.equals( "marblegrid" ) ){
			wg = new WorldMarbleGrid( marbles, WorldMarbleGrid.DIAGONAL, frames );
		}else{
			usage();
		}
		
		// Create ray tracer
		RayTracerSmp2 rt = new RayTracerSmp2( wg );
		
		// Cleanup existing render frames and run new render
        rt.cleanup();
        long startTime = System.currentTimeMillis();
		rt.render();
		long runTime = System.currentTimeMillis() - startTime;
		System.out.println("Time: " + runTime + "msec");
		System.out.println( "Time / frame: " + ( runTime / frames ) );

		// Show movie player
		if( PLAYER ){
			PlayMovie.main( new String[]{} );
		}
	}
	
	/**
	 * Prints usage information for the main method and exits
	 */
	private static void usage(){
		System.err.println( "Usage: java RayTracerSmp2 [seed=<int>] [world=<generator>] [frames=<int>] [seconds=<int>] [marbles=<int>] [player=<boolean>] [gui=<boolean>]" );
		System.exit( 1 );
	}

	/**
	 * Create a ray tracer using the given world generator
	 * 
	 * @param genWorlds World generator
	 */
	public RayTracerSmp2( WorldGenerator genWorlds ){
		this.genWorlds = genWorlds;
	}
	
	/**
	 * Removes any existing rendered frames from current directory
	 */
	public void cleanup(){
		// Erase previous render
		File[] files = new File( "." ).listFiles();
		try{
			for( int i = 0; i < files.length; i++ ){
				if( files[i].getName().endsWith( ".png" ) ){
					files[i].delete();
				}
			}
		}catch( Exception e ){
			System.exit( 1 );
		}
	}
	
	/**
	 * Renders the Worlds produced by the world Generator
	 * 
	 * @throws Exception
	 */
	public void render() throws Exception{
		// Setup worlds to render
		final World[] worlds = genWorlds.getWorlds();

		final JProgressBar main;
		final JProgressBar[] bars;
		if( GUI ){
			// Show a rendering GUI
			
			// Setup progress window
			JFrame frame = new JFrame( "Rendering..." );
			frame.setBounds( 0, 0, 300, 50 * THREADS + 50 );
			frame.setLayout( new BorderLayout() );
			frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
	
			// Create progress bars
			main = new JProgressBar( 0, worlds.length );
			bars = new JProgressBar[THREADS];
			for( int i = 0; i < bars.length; i++ ){
				bars[i] = new JProgressBar();
				bars[i].setValue( 0 );
				bars[i].setStringPainted( true );
			}
			main.setValue( 0 );
			main.setStringPainted( true );
			main.setString( "Rendering" );
	
			// Setup left panel, with progress bar labels
			JPanel left = new JPanel();
			left.setLayout( new GridLayout( THREADS + 1, 1 ) );
			left.add( new JLabel( "Total:" ) );
			for( int i = 0; i < bars.length; i++ ){
				left.add( new JLabel( "Frame:" ) );
			}
	
			// Setup right panel, with progress bars
			JPanel right = new JPanel();
			right.setLayout( new GridLayout( THREADS + 1, 1 ) );
			right.add( main );
			for( int i = 0; i < bars.length; i++ ){
				right.add( bars[i] );
			}
	
			// Finalize layout and display
			frame.add( left, BorderLayout.WEST );
			frame.add( right, BorderLayout.CENTER );
			frame.setVisible( true );
		}else{
			main = null;
			bars = null;
		}

		final long[][] times = new long[worlds.length][];
		
		// Render all worlds and save to file
		for( int i = 0; i < worlds.length; i++ ){
			CameraSmp camera = new CameraSmp( CAMERACENTER, CAMERALOOKAT, CAMERAUP );
			times[i] = camera.render( worlds[i], new File( "render_" + String.format( "%1$04d" , i) + ".png" ), null );
		}

		if( GUI ){
			main.setString( "Done!" );
		}
		
		// Compute some timing metrics
		int renderTime = 0;
		int ioTime = 0;
		int ttlTime = 0;
		for( int i = 0; i < times.length; i++ ){
			renderTime += times[i][0];
			ioTime += times[i][1];
			ttlTime += times[i][0] + times[i][1];
		}
		
		renderTime /= times.length;
		ioTime /= times.length;
		ttlTime /= times.length;
		
		System.out.println( "Average Frame Render Time: " + renderTime );
		System.out.println( "Average Frame I/O Time   : " + ioTime );
		System.out.println( "Average Frame Total Time : " + ttlTime );
	}

}
