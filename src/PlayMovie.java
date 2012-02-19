import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

/**
 * Reads png files from a directory and plays them like a movie
 *
 */
public class PlayMovie {
	
	// Determines if a file selection dialog pops up or not
	public static final boolean PICK_FOLDER = false;

	
	public static void main( String args[] ){
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		frame.setBounds(0, 0, 550, 550);
		frame.setLayout( new BorderLayout() );
		
		File dir = new File(".");
		
		System.out.println( "PlayMovie: Loading movie from current directory" );
		
		// Read in all files and construct images
		File[] files = dir.listFiles();
		Arrays.sort( files );
		
		ImageIcon[] images = new ImageIcon[files.length];
		
		int index = 0;
		try{
			for( int i = 0; i < files.length; i++ ){
				if( files[i].getName().endsWith(".png") ){
					images[index++] = new ImageIcon( ImageIO.read( files[i] ) );
				}
			}
		}catch( Exception e ){
			System.exit(1);
		}
		
		System.out.println( "PlayMovie: Loaded " + index + " frames (" + (index / 24.0) + "s)" );
		
		
		// Set up GUI
		JLabel label = new JLabel(images[0]);
		final JToggleButton play = new JToggleButton("Play");
		final JButton reset = new JButton( "Reset" );
		final JCheckBox repeat = new JCheckBox( "Repeat" );
		
		final Player p = new Player( label, images, index, play, repeat );
		
		play.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae) {
				p.playPause();
			}
		});
		
		reset.addActionListener( new ActionListener(){
			public void actionPerformed( ActionEvent arg0 ){
				p.reset();
			}
		});
		
		JPanel bottom = new JPanel();
		bottom.setLayout( new FlowLayout() );
		bottom.add( play );
		bottom.add( reset );
		bottom.add( repeat );
		
		frame.add( label, BorderLayout.CENTER );
		frame.add( bottom, BorderLayout.SOUTH );
		frame.setVisible( true );
		frame.pack();
	}
	
	/**
	 * Handles actually displaying the frames
	 */
	private static class Player extends Thread{
		
		JLabel screen;
		ImageIcon[] images;
		int frames;
		JToggleButton button;
		JCheckBox repeat;
		int curframe = 0;
		boolean play = false;
		boolean reset = false;
		
		/**
		 * Creates a player
		 * @param display JLabel which images will be displayed on
		 * @param images  Array of images to be played, in order
		 * @param frames  Number of frames to play
		 * @param button  Play button
		 * @param repeat  Repeat checkbox
		 */
		public Player( JLabel display, ImageIcon[] images, int frames, JToggleButton button, JCheckBox repeat ){
			screen = display;
			this.images = images;
			this.frames = frames;
			this.button = button;
			this.repeat = repeat;
			start();
		}
		
		/**
		 * If paused, plays the movie.
		 * If playing, pauses the movie
		 * 
		 * @return True if it plays, false if it pauses
		 */
		public boolean playPause(){
			play = !play;
			if( play ){
				this.interrupt();
				button.setText( "Pause" );
			}else{
				button.setText( "Play" );
			}
			return play;
		}
		
		/**
		 * Sets the player to the first frame
		 */
		public void reset(){
			reset = true;
			if( !play ){
				this.interrupt();
			}
		}
		
		/**
		 * Thread for playing the frames of the movie
		 */
		public void run(){
			
			// Loop until closed
			while( true ){
				
				// Play frames
				for( ; curframe < frames && play && !reset; curframe++ ){
					screen.setIcon( images[curframe] );
//					screen.validate();
					try{
						Thread.sleep(1000L/24L);
					}catch( Exception e ){}
				}
				
				// Check if playing stopped because last frame was hit
				if( curframe >= frames ){
					if( repeat.isSelected() ){
						// If repeat is set, set flag to repeat video
						reset = true;
					}else{
						// If repeat is not set, set gui to show it ended
						curframe = 0;
						button.setText("Replay");
						button.setSelected(false);
						play = false;
					}
				}
				
				// If reset flag is set, reset frame back to first
				if( reset ){
					reset = false;
					curframe = 0;
					screen.setIcon( images[curframe] );
//					screen.validate();
				}else{
					// Not set to repeat, sleep
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {}
				}
			}
		}
	}
}
