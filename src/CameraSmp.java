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

public class CameraSmp {

	BufferedImage image;

	private double[] pixelArray;

	World w;
	Point3d position;
	Point3d lookat;
	Vector3d up;
	int xRes, yRes;

	public CameraSmp(Point3d position, Point3d lookat, Vector3d up){

		this.position = position;
		this.lookat = lookat;
		this.up = up;
		this.xRes = 500;
		this.yRes = 500;

		image = new BufferedImage(xRes, yRes, BufferedImage.TYPE_INT_RGB);
		pixelArray = new double[xRes*yRes*3];
	}

	public void render(World w, File outputFile, JProgressBar progress ) throws Exception {
		
		WritableRaster raster = image.getRaster();
		this.w = w;
		final Point3d camPoint = this.position;
		

		new ParallelTeam().execute( new ParallelRegion(){
			public void run() throws Exception{
				execute( 0, yRes - 1, new IntegerForLoop(){
					int pixelNum = 0, renders, row, col;
					double y, x, xMin=-0.5, xMax=0.5, yMin=0.5, yMax=1.5, z=0.5;
					double deltaY = (yMax - yMin) / yRes;
                	double deltaX = (xMax - xMin) / xRes;
					long start;
					Ray rtRay;
					Color pixelColor = new Color(0,0,0);
					
					// Extra padding
					long p0, p1, p2, p3, p4, p5, p6, p7;
                    long p8, p9, pa, pb, pc, pd, pe, pf;
					
                    public IntegerSchedule schedule(){
                        return IntegerSchedule.guided(5);
                    }
					
					public void run( int low, int high ) throws Exception{
						start = System.currentTimeMillis();
						
						pixelNum = low * xRes * 3;
						renders = 0;
						
						y = yMax - ( low * deltaY );
						
						// low + 1 is wrong, should be +0. Helps see the sections on the image
						for( row = low + 0; row <= high; row++ ){
							x = xMin;
							for( col = 0; col < xRes; col++ ) {
                                renders++;

								rtRay = new Ray( camPoint, new Vector3d(x-(camPoint.x), y-(camPoint.y), z-(camPoint.z)));	
							
								//Illuminate the pixel given the ray
							    illuminate(rtRay, 1, pixelColor);
								
                                pixelArray[pixelNum] = pixelColor.r;
                                pixelArray[pixelNum+1] = pixelColor.g;
                                pixelArray[pixelNum+2] = pixelColor.b;								
                                    
                                if(pixelArray[pixelNum] > 255)
                                    pixelArray[pixelNum] = 255;
                                if(pixelArray[pixelNum+1] > 255)
                                    pixelArray[pixelNum+1] = 255;
                                if(pixelArray[pixelNum+2] > 255)
                                    pixelArray[pixelNum+2] = 255;
                                pixelNum += 3;
								
								x += deltaX;
							}
							y -= deltaY;
						}
                        System.out.println( low + " - " + high + " Done.\t" + ( System.currentTimeMillis() - start) + "ms\twith " + renders + " renders");
					}
				});
			}
		} );
		
		raster.setPixels(0, 0, this.xRes, this.yRes, pixelArray);
		
		try{
			ImageIO.write(image, "png", outputFile );
		}catch( Exception e ){
			System.err.println( e.getMessage() );
			e.printStackTrace();
		}

	}
	
	private static double dist( Point3d a, Point3d b ){
		double val = ( a.x - b.x ) + ( a.y - b.y ) + ( a.z - b.z );
		
		// Extra Padding
        long p0, p1, p2, p3, p4, p5, p6, p7;
        long p8, p9, pa, pb, pc, pd, pe, pf;
		
		return val;
	}
	
	public Color illuminate(Ray r, int depth, Color lightF){
    	int maxDepth = 5;
    	double n = 1.0;
		Color light = new Color(0,0,0);
		int objIndex = -1;
		double backgroundRed = 0;
    	double backgroundGreen = 0;
    	double backgroundBlue = 0;
    	boolean intersectFound = false, inShadow = false;
		
		Point3d iPoint = new Point3d(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
		Point3d tmpPoint = new Point3d();
        Point3d shadowInter = new Point3d();
		double minDist = Double.MAX_VALUE, tempDist = 0.0;
		Vector3d surfaceNorm = new Vector3d();
		
		// Extra Padding
        long p0, p1, p2, p3, p4, p5, p6, p7;
        long p8, p9, pa, pb, pc, pd, pe, pf;
		
		//Find the object closest to the Origin of the Ray
        for( int i=0; i<w.objectList.size(); i++) {
            if ( w.objectList.get(i).intersect(r, tmpPoint) ) {
                intersectFound = true;
                tempDist = dist( r.origin, tmpPoint );
                if( tempDist < minDist ) {
                    iPoint.set(tmpPoint.x, tmpPoint.y, tmpPoint.z);
                    minDist = tempDist;
                    objIndex = i;
                }
            }
        }
		
		//If no object was found, set color to background
		if( !intersectFound ) {
		    lightF.r = backgroundRed;
		    lightF.g = backgroundGreen;
		    lightF.b = backgroundBlue;
		    return lightF;
	    }

        // Vector3d N = w.objectList.get(objIndex).getNormal(iPoint);
		w.objectList.get(objIndex).getNormal(iPoint, surfaceNorm);
        // N.normalize();
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
		light = new Color(
				ka * ((w.ambRed * objectColor.r)/255),
				ka * ((w.ambGreen * objectColor.g)/255), 
				ka * ((w.ambBlue * objectColor.b)/255));
				
				
		// Create ray from light source to point of intersection
		for( int lightIndex = 0; lightIndex < w.lightList.size(); lightIndex++ ) {
			Ray shadowRay = new Ray(iPoint, new Vector3d(w.lightList.get(lightIndex).position.x - iPoint.x, 
							w.lightList.get(lightIndex).position.y - iPoint.y, w.lightList.get(lightIndex).position.z - iPoint.z));
				
			// Check to see if shadow ray intersects any object.
			for( int j=0; j<w.objectList.size(); j++ ) {
				if( objIndex != j ) {
                    // Point3d shadowInter = w.objectList.get(j).intersect(shadowRay);
				
					if( w.objectList.get(j).intersect(shadowRay, shadowInter) && (iPoint.distance(w.lightList.get(lightIndex).position) > shadowInter.distance(w.lightList.get(lightIndex).position))){
                        inShadow = true;
                        break;
					}
				}
			}
				
			// If Point is not in shadow, continue finding shading
            if ( !inShadow ) {
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
				double sdotn = S.dot(surfaceNorm);
					
				if( sdotn > 0 ) {
					// Diffuse color
					Color diffuseColor = new Color( (kd * sdotn) * ((w.lightList.get(lightIndex).red * objectColor.r )/255), 
									(kd * sdotn) * ((w.lightList.get(lightIndex).green * objectColor.g)/255), 
									(kd * sdotn) * ((w.lightList.get(lightIndex).blue * objectColor.b)/255));
					
					light.r += diffuseColor.r;
					light.g += diffuseColor.g;
					light.b += diffuseColor.b;
					
					double fraction = (2*sdotn);
				
					Vector3d newN = new Vector3d(surfaceNorm.x * fraction, surfaceNorm.y * fraction, surfaceNorm.z * fraction);
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
			if( depth < maxDepth ){
				// Reflection
				if(kr > 0){
					Vector3d D = new Vector3d(r.origin.x-iPoint.x, 
									r.origin.y-iPoint.y, r.origin.z-iPoint.z);
					D.normalize();
					double ddotn = D.dot(surfaceNorm);
					if(ddotn >= 0){
						Vector3d newN = new Vector3d(surfaceNorm.x * (2*ddotn), surfaceNorm.y * (2*ddotn), surfaceNorm.z * (2*ddotn));
						Vector3d R = new Vector3d( (newN.x - D.x), (newN.y - D.y), (newN.z - D.z));
						Ray reflectiveRay = new Ray(iPoint, R);
						Color reflect = illuminate(reflectiveRay, depth+1, lightF);
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
						nit = n / w.objectList.get(objIndex).n;
					}
					else {
						nit = w.objectList.get(objIndex).n / n;
						surfaceNorm.scale(-1.0);
                        // N = new Vector3d(-N.x, -N.y, -N.z);
					}
					double alpha = nit;
					double underSqRt = 1.0 + ((nit*nit) * ((negD.dot(surfaceNorm) * negD.dot(surfaceNorm)) - 1.0));
					if( underSqRt > 0.0 ) {
						double beta = (nit * (negD.dot(surfaceNorm))) - Math.sqrt(underSqRt);
						Vector3d T = new Vector3d(alpha*D.x+beta*surfaceNorm.x, alpha*D.y + beta*surfaceNorm.y, alpha*D.z + beta*surfaceNorm.z);
						T.normalize();
						Ray transmissionRay = new Ray(iPoint, T);
						Color transmit = illuminate(transmissionRay, depth+1, lightF);
						light.r += kt*transmit.r;
						light.g += kt*transmit.g;
						light.b += kt*transmit.b;
					}
					
					else {
						// Calculate Reflection Ray Instead
						D = new Vector3d(r.origin.x-iPoint.x, r.origin.y-iPoint.y, r.origin.z-iPoint.z);
						D.normalize();
						double ddotn = D.dot(surfaceNorm);
						if(ddotn >= 0){
							Vector3d newN = new Vector3d(surfaceNorm.x * (2*ddotn), surfaceNorm.y * (2*ddotn), surfaceNorm.z * (2*ddotn));
							Ray reflectiveRay = new Ray(iPoint, new Vector3d( (newN.x - D.x), (newN.y - D.y), 
												(newN.z - D.z)));
							Color reflect = illuminate(reflectiveRay, depth+1, lightF);
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
