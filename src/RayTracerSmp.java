import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import edu.rit.pj.BarrierAction;
import edu.rit.pj.IntegerForLoop;
import edu.rit.pj.IntegerSchedule;
import edu.rit.pj.ParallelRegion;
import edu.rit.pj.ParallelTeam;
import edu.rit.pj.reduction.ObjectOp;
import edu.rit.pj.reduction.SharedObject;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.File;

public class RayTracerSmp{
	final static Point3d CAMERACENTER = new Point3d( 0.0, 1.0, 2.0 );
	final static Point3d CAMERALOOKAT = new Point3d( 0.0, 0.0, -1.0 ); // Direction
	final static Vector3d CAMERAUP = new Vector3d( 0.0, 1.0, 0.0 );
	
	final static int THREADS = ParallelTeam.getDefaultThreadCount();
	
	private WorldGenerator genWorlds;

	public static void main( String[] args ) throws Exception{
        RayTracerSmp rt = new RayTracerSmp( new WorldMarbles( 5, 50, true ) );
        // RayTracerSmp rt = new RayTracerSmp( new WorldMarbleGrid( 10, WorldMarbleGrid.DIAGONAL ) );
        rt.cleanup();
        long startTime = System.currentTimeMillis();
		rt.render();
		System.out.println("Time: " + (System.currentTimeMillis() - startTime) + "msec");

		PlayMovie.main( new String[]{} );
	}

	/**
	 * Create a ray tracer using the given world generator
	 * 
	 * @param genWorlds World generator
	 */
	public RayTracerSmp( WorldGenerator genWorlds ){
		this.genWorlds = genWorlds;
	}
	
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

		// Setup progress window
		JFrame frame = new JFrame( "Rendering..." );
		frame.setBounds( 0, 0, 300, 50 * THREADS + 50 );
		frame.setLayout( new BorderLayout() );
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

		// Create progress bars
		final JProgressBar main = new JProgressBar( 0, worlds.length );
		final JProgressBar[] bars = new JProgressBar[THREADS];
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

		final SharedObject<JProgressBar> sharedMain = new SharedObject<JProgressBar>( main );

		new ParallelTeam().execute( new ParallelRegion(){
			public void run() throws Exception{
				final ObjectOp<JProgressBar> op = new ObjectOp<JProgressBar>(){
					public JProgressBar op( JProgressBar arg0, JProgressBar arg1 ){
						arg0.setValue( arg0.getValue() + 1 );
						return arg0;
					}
				};

				execute( 0, worlds.length - 1, new IntegerForLoop(){

					public IntegerSchedule schedule()
					{
						return IntegerSchedule.guided();
					}

					public void run( int low, int high ) throws Exception{
						Camera camera = new Camera( CAMERACENTER, CAMERALOOKAT, CAMERAUP );
						int index = getThreadIndex();

						// Render all worlds and save to file
						for( int i = low; i <= high; i++ ){
							bars[index].setString( "Frame " + i );
							bars[index].setValue( 0 );
							camera.render( worlds[i], new File( "render_" + String.format( "%1$04d", i ) + ".png" ), bars[index] );
							sharedMain.reduce( null, op );
						}

						bars[index].setString( "Done!" );
					}
				},
						BarrierAction.NO_WAIT );
			}
		} );

		main.setString( "Done!" );
	}

}
