import javax.imageio.ImageIO;
import javax.swing.JProgressBar;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.util.Random;

/**
 * The Camera class represents and camera in a virtual 3d
 * scene. The camera is an object that can render the view
 * it has of the 3d scene. This rendered image is then 
 * written to an image file.
 *
 * @author  Steve Glazer
 * @author  Sara Jackson
 * @author  Sam Milton
 * @version 16-Feb-2012
 */
public class Camera {

	private final double XMIN = -0.5;
	private final double XMAX = 0.5;
	private final double YMIN = 0.5;
	private final double YMAX = 1.5;
	private double n = 1.0;
	private final int XRES = 500;
	private final int YRES = 500;
	private final double Z = 0.5;

	BufferedImage image;

	private int pixelNum = 0;
	private int MAX_DEPTH = 5;
	Random rand = new Random();
	private double deltaY = (YMAX - YMIN) / YRES;
	private double deltaX = (XMAX - XMIN) / XRES;
	private double[] pixelArray;
	private double BACKGRD_RED = 0;
	private double BACKGRD_GREEN = 0;
	private double BACKGRD_BLUE = 0;
	World w;
	Point3d position;
	Point3d lookat;
	Vector3d up;

    /**
     * Constructor
     *
     * @param position   the center point of the camera
     * @param lookat     the point at which the camera is facing
     * @param up         the vector that determines which way is up
     */
	public Camera(Point3d position, Point3d lookat, Vector3d up){

		this.position = position;
		this.lookat = lookat;
		this.up = up;

		image = new BufferedImage(XRES, YRES, BufferedImage.TYPE_INT_RGB);
		pixelArray = new double[XRES*YRES*3];

	}

    /**
     * Renders the 3d scene from the point of view of the camera.
     * Writes the rendered image to file.
     *
     * @param w             world which contains the lights/objects to render
     * @param outputFile    file to write the file to
     * @param progress      progress bar to update with current status
     *
     * @return an array for the timings of rendering each frame
     */
	public long[] render(World w, File outputFile, JProgressBar progress ) {
		long t1 = System.currentTimeMillis();
		if( progress != null ){
			progress.setMaximum( XRES * YRES );
		}
		int pixel_count = 0;
		
		WritableRaster raster = image.getRaster();
		this.w = w;
		Point3d camPoint = this.position;
		pixelNum = 0;

        // For every row in the image
		for(double y = YMAX; y > YMIN; y = y - deltaY) {
            
            // For every column in the image
			for( double x = XMIN; x < XMAX; x = x + deltaX) {

				if(pixelNum < pixelArray.length) {
					// Super Sample
					// Multiple iterations performs anti-aliasing
					// Turned off for this project
					for(int supeSamp = 0; supeSamp < 1; supeSamp++) {
						if(supeSamp != 0) {
							double alterX = (rand.nextDouble() * 0.0026) - 0.0013;
							double alterY = (rand.nextDouble() * 0.0026) - 0.0013;
							camPoint = new Point3d(position.x + alterX, position.y + alterY, position.z);
						}

                        // Shoot ray into the scene
						Ray rtRay = new Ray( camPoint, new Vector3d(x-(camPoint.x), y-(camPoint.y), Z-(camPoint.z)));	
					
						// Illuminate the pixel given the ray
						Color pixelColor = illuminate(rtRay, 1);
						if(supeSamp == 0) {
							pixelArray[pixelNum] = pixelColor.r;
							pixelArray[pixelNum+1] = pixelColor.g;
							pixelArray[pixelNum+2] = pixelColor.b;
						}
						else {
							pixelArray[pixelNum] = (pixelArray[pixelNum] + pixelColor.r) / 2;
							pixelArray[pixelNum+1] = (pixelArray[pixelNum+1] + pixelColor.g) / 2;
							pixelArray[pixelNum+2] = (pixelArray[pixelNum+2] + pixelColor.b) / 2;
						}
					}
					if(pixelArray[pixelNum] > 255)
						pixelArray[pixelNum] = 255;
					if(pixelArray[pixelNum+1] > 255)
						pixelArray[pixelNum+1] = 255;
					if(pixelArray[pixelNum+2] > 255)
						pixelArray[pixelNum+2] = 255;
					pixelNum += 3;


				}
				pixel_count++;
			}
			if( progress != null ){
				progress.setValue( pixel_count );
			}
		}
		
		raster.setPixels(0, 0, XRES, YRES, pixelArray);
		
		long t2 = System.currentTimeMillis();
		
		// Write out the imag file to disk
		try{
			ImageIO.write(image, "png", outputFile );
		}catch( Exception e ){
			System.err.println( e.getMessage() );
			e.printStackTrace();
		}
		long t3 = System.currentTimeMillis();
		return new long[]{t2-t1,t3-t2};
	}
	
	/**
     * A recursive method that returns the color for the pixel
     * depending on the objects in the scene based on the given
     * ray.
     *
     * @param r         ray into the scene
     * @param depth     depth of the current recursive call
     *
     * @return the color of the pixel
     */
	public Color illuminate(Ray r, int depth){
		Color lightF = new Color(0,0,0);
		Color light = new Color(0,0,0);
		int objIndex = -1;
		Point3d iPoint = new Point3d(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
		
		//Find the object closest to the Origin of the Ray
		for( int i=0; i<w.objectList.size(); i++) {
			Point3d inter = w.objectList.get(i).intersect(r);
			if( inter != null) {
				if( r.origin.distance(inter) < r.origin.distance(iPoint)) {
					//System.out.println("(" + inter.x + ", " + inter.y + ", " + inter.z + ")");
					iPoint = inter;
					objIndex = i;
				}
			}
		}
		//If no object was found, set color to background
		if( iPoint.x == Double.MAX_VALUE && iPoint.y == Double.MAX_VALUE && iPoint.z == Double.MAX_VALUE ) {
			lightF = new Color(BACKGRD_RED, BACKGRD_GREEN, BACKGRD_BLUE);
			return lightF;
		}
		
		Vector3d N = w.objectList.get(objIndex).getNormal(iPoint);
		N.normalize();
		
		// Retrieve object constants
		double ka = w.objectList.get(objIndex).ka;
		double kd = w.objectList.get(objIndex).kd;
		double ks = w.objectList.get(objIndex).ks;
		double ke = w.objectList.get(objIndex).ke;
		double kr = w.objectList.get(objIndex).kr;
		double kt = w.objectList.get(objIndex).kt;
				
		// Get color. If object has a shader, returns color from that.
		Color objectColor = w.objectList.get(objIndex).getColor(iPoint);
			
		// Calculate Ambient Light
		light = new Color(ka * ((w.ambRed * objectColor.r)/255), ka * ((w.ambGreen * objectColor.g)/255), 
				ka * ((w.ambBlue * objectColor.b)/255));
				
				
		// Create ray from light source to point of intersection
		for( int lightIndex = 0; lightIndex < w.lightList.size(); lightIndex++ ) {
			Ray shadowRay = new Ray(iPoint, new Vector3d(w.lightList.get(lightIndex).position.x - iPoint.x, 
							w.lightList.get(lightIndex).position.y - iPoint.y, w.lightList.get(lightIndex).position.z - iPoint.z));
				
			// Check to see if shadow ray intersects any object.
			Point3d lightInter = null;
				
			for( int j=0; j<w.objectList.size(); j++) {
				if( objIndex != j ) {
					Point3d shadowInter = w.objectList.get(j).intersect(shadowRay);
				
					if( shadowInter != null && (iPoint.distance(w.lightList.get(lightIndex).position) > shadowInter.distance(w.lightList.get(lightIndex).position))){
						if(lightInter == null || (lightInter.distance(w.lightList.get(lightIndex).position) > shadowInter.distance(w.lightList.get(lightIndex).position))){
							lightInter = shadowInter;
						}
					}
				}
			}
				
			// If Point is not in shadow, continue finding shading
			if(lightInter == null){
				// Calculating V and S
				Vector3d V = new Vector3d((this.position.x - iPoint.x), (this.position.y - iPoint.y),
								(this.position.z - iPoint.z));
				Vector3d S = new Vector3d(w.lightList.get(lightIndex).position.x-(iPoint.x), 
								w.lightList.get(lightIndex).position.y-(iPoint.y), 
								w.lightList.get(lightIndex).position.z-(iPoint.z));
					
				// Normalizing vectors
				V.normalize();
				S.normalize();
					
				// Calculating S dot product N
				double sdotn = S.dot(N);
					
				if( sdotn > 0 ) {
					// Diffuse color
					Color diffuseColor = new Color( (kd * sdotn) * ((w.lightList.get(lightIndex).red * objectColor.r )/255), 
									(kd * sdotn) * ((w.lightList.get(lightIndex).green * objectColor.g)/255), 
									(kd * sdotn) * ((w.lightList.get(lightIndex).blue * objectColor.b)/255));
					
					light.r += diffuseColor.r;
					light.g += diffuseColor.g;
					light.b += diffuseColor.b;
					
					double fraction = (2*sdotn);
				
					Vector3d newN = new Vector3d(N.x * fraction, N.y * fraction, N.z * fraction);
					Vector3d R = new Vector3d(S.x - newN.x, S.y - newN.y, S.z - newN.z);
					R.normalize();
					
					double sdotr = V.dot(R);
						
					Color specColor = new Color( (ks * Math.pow(sdotr, ke)) * ((w.lightList.get(lightIndex).red * w.objectList.get(objIndex).speccolor.r)/255), (ks * Math.pow(sdotr, ke)) *  ((w.lightList.get(lightIndex).green * w.objectList.get(objIndex).speccolor.g)/255), 
										(ks * Math.pow(sdotr, ke)) * ((w.lightList.get(lightIndex).blue * w.objectList.get(objIndex).speccolor.b)/255));
					
					light.r += specColor.r;
					light.g += specColor.g;
					light.b += specColor.b;
				}
			}
				
			// Determine Reflection and Transmission (Recursively)
			if( depth < MAX_DEPTH){
				// Reflection
				if(kr > 0){
					Vector3d D = new Vector3d(r.origin.x-iPoint.x, 
									r.origin.y-iPoint.y, r.origin.z-iPoint.z);
					D.normalize();
					double ddotn = D.dot(N);
					if(ddotn >= 0){
						Vector3d newN = new Vector3d(N.x * (2*ddotn), N.y * (2*ddotn), N.z * (2*ddotn));
						Vector3d R = new Vector3d( (newN.x - D.x), (newN.y - D.y), (newN.z - D.z));
						Ray reflectiveRay = new Ray(iPoint, R);
						Color reflect = illuminate(reflectiveRay, depth+1);
						light.r += kr*reflect.r;
						light.g += kr*reflect.g;
						light.b += kr*reflect.b;
					}	
				}
				// Transmission
				if(kt > 0.0) {
					Vector3d D = new Vector3d(iPoint.x - r.origin.x, iPoint.y - r.origin.y, iPoint.z - r.origin.z);
					D.normalize();
					Vector3d negD = new Vector3d(-D.x, -D.y, -D.z);
					double nit;
					if( depth % 2 == 1 ) {
						nit = this.n / w.objectList.get(objIndex).n;
					}
					else {
						nit = w.objectList.get(objIndex).n / this.n;
						N = new Vector3d(-N.x, -N.y, -N.z);
					}
					double alpha = nit;
					double underSqRt = 1.0 + ((nit*nit) * ((negD.dot(N) * negD.dot(N)) - 1.0));
					if( underSqRt > 0.0 ) {
						double beta = (nit * (negD.dot(N))) - Math.sqrt(underSqRt);
						Vector3d T = new Vector3d(alpha*D.x+beta*N.x, alpha*D.y + beta*N.y, alpha*D.z + beta*N.z);
						T.normalize();
						Ray transmissionRay = new Ray(iPoint, T);
						Color transmit = illuminate(transmissionRay, depth+1);
						light.r += kt*transmit.r;
						light.g += kt*transmit.g;
						light.b += kt*transmit.b;
					}
					
					else {
						// Calculate Reflection Ray Instead
						D = new Vector3d(r.origin.x-iPoint.x, r.origin.y-iPoint.y, r.origin.z-iPoint.z);
						D.normalize();
						double ddotn = D.dot(N);
						if(ddotn >= 0){
							Vector3d newN = new Vector3d(N.x * (2*ddotn), N.y * (2*ddotn), N.z * (2*ddotn));
							Ray reflectiveRay = new Ray(iPoint, new Vector3d( (newN.x - D.x), (newN.y - D.y), 
												(newN.z - D.z)));
							Color reflect = illuminate(reflectiveRay, depth+1);
							light.r += kt*reflect.r;
							light.g += kt*reflect.g;
							light.b += kt*reflect.b;
						
						}
						
					}
					
					
				}
			}
			if( lightIndex > 0 ) {
				lightF.r = (lightF.r + light.r) / 2;
				lightF.g = (lightF.g + light.g) / 2;
				lightF.b = (lightF.b + light.b) / 2;
			}
			else {
				lightF.r = light.r;
				lightF.g = light.g;
				lightF.b = light.b;
			}
		}		
		return lightF;
	}

}
