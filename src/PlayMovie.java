import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Arrays;

import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;


public class PlayMovie {
	
	// Determines if a file selection dialog pops up or not
	public static final boolean PICK_FOLDER = false;

	public static void main( String args[] ){
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		frame.setBounds(0, 0, 550, 550);
		frame.setLayout( new BorderLayout() );
		
		File dir;
		if( PICK_FOLDER ){
			// Display folder selector
			Display d = new Display();
			Shell s = new Shell( d );
			DirectoryDialog dialog = new DirectoryDialog( s, SWT.OPEN );
			String dirName = dialog.open();
			s.close();
			d.dispose();
			
			if( dirName == null ){
				System.exit(1);
			}
			
			dir = new File( dirName );
		}else{
			// Use current directory
			dir = new File(".");
		}
		
		// Read in all files and construct images
		File[] files = dir.listFiles();
		Arrays.sort( files );
		
		ImageIcon[] images = new ImageIcon[files.length];
		
		int index = 0;
		try{
			for( int i = 0; i < files.length; i++ ){
				if( files[i].getName().endsWith(".png") ){
					images[index++] = new ImageIcon( ImageIO.read( files[i] ) );
					System.out.println( files[i].getName() );
				}
			}
		}catch( Exception e ){
			System.exit(1);
		}
		
		
		JLabel label = new JLabel(images[0]);
		final JToggleButton play = new JToggleButton("Play");
		
		final Player p = new Player( label, images, index, play );
		
		play.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae) {
				p.playPause();
			}
		});
		
		JPanel bottom = new JPanel();
		bottom.setLayout( new FlowLayout() );
		bottom.add( play );
		
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
		int curframe = 0;
		boolean play = false;
		
		public Player( JLabel display, ImageIcon[] images, int frames, JToggleButton button ){
			screen = display;
			this.images = images;
			this.frames = frames;
			this.button = button;
			start();
		}
		
		/**
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
		 * Thread for playing the frames of the movie
		 */
		public void run(){
			while( true ){
				
				for( ; curframe < frames && play; curframe++ ){
					screen.setIcon( images[curframe] );
					screen.validate();
					try{
						Thread.sleep(1000/24);
					}catch( Exception e ){}
				}
				
				if( curframe >= frames ){
					curframe = 0;
					button.setText("Play");
					button.setSelected(false);
					play = false;
				}
				
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {}
			}
		}
	}
}
