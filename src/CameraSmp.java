import javax.imageio.ImageIO;
import javax.swing.JProgressBar;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import edu.rit.pj.IntegerForLoop;
import edu.rit.pj.IntegerSchedule;
import edu.rit.pj.ParallelForLoop;
import edu.rit.pj.ParallelRegion;
import edu.rit.pj.ParallelTeam;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.util.Random;

public class CameraSmp {

	private final double XMIN = -0.5;
	private final double XMAX = 0.5;
	private final double YMIN = 0.5;
	private final double YMAX = 1.5;
	private double n = 1.0;
	private final int XRES = 500;
	private final int YRES = 500;
	private final double Z = 0.5;

	BufferedImage image;

	private int MAX_DEPTH = 5;
	Random rand = new Random();
	private double deltaY = (YMAX - YMIN) / YRES;
	private double deltaX = (XMAX - XMIN) / XRES;
	private double[] pixelArray;
	private double[][] pixelArray2 = new double[YRES][];
	private double BACKGRD_RED = 220;
	private double BACKGRD_GREEN = 220;
	private double BACKGRD_BLUE = 255;
	World w;
	Point3d position;
	Point3d lookat;
	Vector3d up;
	
	private int xstart = 0;
	private int xfinish = XRES;
	private int ystart = 0;
	private int yfinish = YRES;

	public CameraSmp(Point3d position, Point3d lookat, Vector3d up){

		this.position = position;
		this.lookat = lookat;
		this.up = up;

		image = new BufferedImage(XRES, YRES, BufferedImage.TYPE_INT_RGB);
		pixelArray = new double[XRES*YRES*3];

	}
	
	public CameraSmp( Point3d position, Point3d lookat, Vector3d up, int xmin, int xmax, int ymin, int ymax ){
		xstart = xmin;
		xfinish = xmax;
		ystart = ymin;
		yfinish = ymax;
		
	}

	public void render(World w, File outputFile, JProgressBar progress ) throws Exception {
		if( progress != null ){
			progress.setMaximum( XRES * YRES );
		}
		int pixel_count = 0;
		
		
		WritableRaster raster = image.getRaster();
		this.w = w;
		final Point3d camPoint = this.position;
		

		new ParallelTeam(2).execute( new ParallelRegion(){
			public void run() throws Exception{
				execute( 0, YRES - 1, new IntegerForLoop(){
					int pixelNum = 0, renders, row, col;
					double y, x;
					long start;
					Ray rtRay;
					Color pixelColor;
					double[] pixels;
					
					// Extra padding
					long p0, p1, p2, p3, p4, p5, p6, p7;
                    long p8, p9, pa, pb, pc, pd, pe, pf;
					
//					public IntegerSchedule schedule(){
//						return IntegerSchedule.guided();
//					}
					
					public void run( int low, int high ) throws Exception{
						start = System.currentTimeMillis();
						
						pixelNum = low * XRES * 3;
						renders = 0;
						
						y = YMAX - ( low * deltaY );
						
						// low + 1 is wrong, should be +0. Helps see the sections on the image
						for( row = low + 1; row <= high; row++ ){
							x = XMIN;
							pixels = new double[XRES * 3];
							for( col = 0; col < XRES; col++ ) {
								renders++;

								rtRay = new Ray( camPoint, new Vector3d(x-(camPoint.x), y-(camPoint.y), Z-(camPoint.z)));	
							
								//Illuminate the pixel given the ray
								pixelColor = illuminate(rtRay, 1);
								
//								pixelArray[pixelNum] = pixelColor.r;
//								pixelArray[pixelNum+1] = pixelColor.g;
//								pixelArray[pixelNum+2] = pixelColor.b;
								
								pixels[col] = pixelColor.r;
								pixels[col+1] = pixelColor.g;
								pixels[col+2] = pixelColor.b;
								
									
//								if(pixelArray[pixelNum] > 255)
//									pixelArray[pixelNum] = 255;
//								if(pixelArray[pixelNum+1] > 255)
//									pixelArray[pixelNum+1] = 255;
//								if(pixelArray[pixelNum+2] > 255)
//									pixelArray[pixelNum+2] = 255;
//								pixelNum += 3;
								
								x += deltaX;
								
//								pixel_count++;
							}
//							if( progress != null ){
//								progress.setValue( pixel_count );
//							}
							pixelArray2[row] = pixels;
							y -= deltaY;
						}
						System.out.println( low + " - " + high + " Done.\t" + ( System.currentTimeMillis() - start) + "ms\twith " + renders + " renders");
					}
				});
			}
		} );
		
		raster.setPixels(0, 0, XRES, YRES, pixelArray);
		
		try{
			ImageIO.write(image, "png", outputFile );
		}catch( Exception e ){
			System.err.println( e.getMessage() );
			e.printStackTrace();
		}

	}
	
	private static double dist( Point3d a, Point3d b ){
		double val = ( a.x - b.x ) + ( a.y - b.y ) + ( a.z - b.z );
		return val;
	}
	
	public Color illuminate(Ray r, int depth){
		Color lightF = new Color(0,0,0);
		Color light = new Color(0,0,0);
		int objIndex = -1;
		
		Point3d iPoint = new Point3d(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
		double minDist = Double.MAX_VALUE, tempDist;
		
		//Find the object closest to the Origin of the Ray
		for( int i=0; i<w.objectList.size(); i++) {
			Point3d inter = w.objectList.get(i).intersect(r);
			if( inter != null) {
				tempDist = dist( r.origin, inter );
				if( tempDist < minDist ) {
					iPoint = inter;
					minDist = tempDist;
					objIndex = i;
				}
			}
		}
		
		//If no object was found, set color to background
		if( iPoint.x == Double.MAX_VALUE && iPoint.y == Double.MAX_VALUE && iPoint.z == Double.MAX_VALUE )
			lightF = new Color(BACKGRD_RED, BACKGRD_GREEN, BACKGRD_BLUE);
		//Else determine color for object
		else {
			Vector3d N = w.objectList.get(objIndex).getNormal(iPoint);
//			Vector3d N = w.objectList.get(objIndex).normal;
			N.normalize();
			//Retrieve object constants - Checkpoint 3
			double ka = w.objectList.get(objIndex).ka;
			double kd = w.objectList.get(objIndex).kd;
			double ks = w.objectList.get(objIndex).ks;
			double ke = w.objectList.get(objIndex).ke;
			double kr = w.objectList.get(objIndex).kr;
			double kt = w.objectList.get(objIndex).kt;
					
			//Checkpoint 4 - Get color. If object has a shader, returns color from that.
			Color objectColor = w.objectList.get(objIndex).getColor(iPoint);
				
			//Calculate Ambient Light
			light = new Color(
					ka * ((w.ambRed * objectColor.r)/255),
					ka * ((w.ambGreen * objectColor.g)/255), 
					ka * ((w.ambBlue * objectColor.b)/255));
				
				
			//create ray from light source to point of intersection
			for( int lightIndex = 0; lightIndex < w.lightList.size(); lightIndex++ ) {
			Ray shadowRay = new Ray(iPoint, new Vector3d(w.lightList.get(lightIndex).position.x - iPoint.x, 
							w.lightList.get(lightIndex).position.y - iPoint.y, w.lightList.get(lightIndex).position.z - iPoint.z));
				
			//check to see if shadow ray intersects any object.
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
				
			//If Point is not in shadow, continue finding shading
			if(lightInter == null){
				//calculating V and S
				Vector3d V = new Vector3d((this.position.x - iPoint.x), (this.position.y - iPoint.y),
								(this.position.z - iPoint.z));
				Vector3d S = new Vector3d(w.lightList.get(lightIndex).position.x-(iPoint.x), 
								w.lightList.get(lightIndex).position.y-(iPoint.y), 
								w.lightList.get(lightIndex).position.z-(iPoint.z));
					
				//Normalizing vectors
				V.normalize();
				S.normalize();
					
				//calculating S dot product N
				double sdotn = S.dot(N);
					
				if( sdotn > 0 ) {
					//Diffuse color
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
				
			//Determine Reflection and Transmission (Recursively) - Checkpoint 5 & 6
			if( depth < MAX_DEPTH){
				//Reflection
				if(kr > 0){
					Vector3d D = new Vector3d(r.origin.x-iPoint.x, 
									r.origin.y-iPoint.y, r.origin.z-iPoint.z);
					D.normalize();
					double ddotn = D.dot(N);
					if(ddotn >= 0){
						Vector3d newN = new Vector3d(N.x * (2*ddotn), N.y * (2*ddotn), N.z * (2*ddotn));
						//newN.normalize();
						Vector3d R = new Vector3d( (newN.x - D.x), (newN.y - D.y), (newN.z - D.z));
						//R.normalize();
						//System.out.println("Angle i: " + Math.acos(D.dot(N)));
						//System.out.println("Angle r: " + Math.acos(R.dot(N)));
						Ray reflectiveRay = new Ray(iPoint, R);
						Color reflect = illuminate(reflectiveRay, depth+1);
						light.r += kr*reflect.r;
						light.g += kr*reflect.g;
						light.b += kr*reflect.b;
						
					}	
				}
				//Transmission
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
						//Calculate Reflection Ray Instead
						D = new Vector3d(r.origin.x-iPoint.x, r.origin.y-iPoint.y, r.origin.z-iPoint.z);
						D.normalize();
						double ddotn = D.dot(N);
						if(ddotn >= 0){
							Vector3d newN = new Vector3d(N.x * (2*ddotn), N.y * (2*ddotn), N.z * (2*ddotn));
//							Vector3d R = new Vector3d( (newN.x - D.x), (newN.y - D.y), (newN.z - D.z));
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
		}		
		return lightF;
	}

}
