import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.File;

public class RayTracerSeq {
	final static Point3d CAMERACENTER = new Point3d(0.0, 1.0, 2.0);
	final static Point3d CAMERALOOKAT = new Point3d(0.0, 0.0, -1.0); //Direction
	final static Vector3d CAMERAUP = new Vector3d(0.0, 1.0, 0.0);
	
	public static void main(String[] args) throws Exception {
		//Camera
		final Camera camera = new Camera(CAMERACENTER, CAMERALOOKAT, CAMERAUP);
		
		
		//Testing Camera
		//Camera camera = new Camera(TEST_CAMERA, CAMERALOOKAT, CAMERAUP);
		
		
		// Setup worlds to render
		//World[] worlds = new World[]{ world, world, world, world }; // Array or worlds to render
		WorldMarbles genWorld = new WorldMarbles(5);
		final World[] worlds = genWorld.getWorlds();
		
		
		// Setup progress window
		JFrame frame = new JFrame("Rendering...");
		frame.setBounds(0, 0, 300, 100);
		frame.setLayout( new BorderLayout() );
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// Create progress bars
		final JProgressBar main = new JProgressBar(0, worlds.length);
		final JProgressBar bar = new JProgressBar();
		main.setValue(0);
		bar.setValue(0);
		main.setStringPainted(true);
		bar.setStringPainted(true);
		main.setString("Rendering");
		
		// Setup left panel, with progress bar labels
		JPanel left = new JPanel();
		left.setLayout( new GridLayout(2,1) );
		left.add( new JLabel("Total:" ) );
		left.add( new JLabel("Frame:" ) );
		
		// Setup right panel, with progress bars
		JPanel right = new JPanel();
		right.setLayout( new GridLayout(2,1) );
		right.add( main );
		right.add( bar );
		
		// Finalize layout and display
		frame.add( left, BorderLayout.WEST );
		frame.add( right, BorderLayout.CENTER );
		frame.setVisible(true);
		
		// Render all worlds and save to file
		for( int i = 0; i < worlds.length; i++ ){
			bar.setString( "Frame " + i );
			bar.setValue(0);
			camera.render( worlds[i], new File( "render_" + String.format( "%1$04d" , i) + ".png" ), bar );
			main.setValue( i+1 );
		}

		bar.setString("Done!");
		main.setString("Done!");

	}

} 
