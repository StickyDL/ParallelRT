import javax.vecmath.Point3d;

/**
 * CheckBoardShad class is a type of shader that can be applied to graphic objects.
 * This class is only accurately used on horizontal triangles at the moment.
 *
 * @author  Steve Glazer
 * @author  Sara Jackson
 * @author  Sam Milton
 * @version 16-Feb-2012
 */
public class CheckBoardShad extends Shader {
	Color color1, color2;
	private double checkBoardSize;
	
	/**
     * Constructor
     * 
     * Creates a CheckerBoardShader object
     *
     * @param origin            the origin of the shader
     * @param color1            the primary color of the shader
     * @param color2            the alternate color for the odd squares
     * @param checkBoardSize    the size of the checkerboard squares
     */
	public CheckBoardShad(Point3d origin, Color color1, Color color2, double checkBoardSize) {
		super(origin);
		this.color1 = color1;
		this.color2 = color2;
		this.checkBoardSize = checkBoardSize;
	}
	
	/**
     * Retrieves the color of the shader at the given point.
     * Determines the placement of the point on the shader to determine
     * the color.
     *
     * @param point     the point to get that color of the shader at
     *
     * @return the color at the given point
     */
	public Color shade(Point3d point) {
		Color result = new Color(0,0,0);
		double row = Math.floor(origin.z - point.z / checkBoardSize) + 2;
		double col = Math.floor(point.x - origin.x / checkBoardSize) + 2;
		if ( (point.x - origin.x) == 0 ) {
		    col = Math.floor(point.y - origin.y / checkBoardSize) + 2;
		}
		if( row < 0 )
			row *= -1;
		if( col < 0 )
			col *= -1;
		//if both row and column are either even or odd. Make color red
		if (row % 2 == 0 && col % 2 == 0 || row % 2 == 1 && col % 2 == 1)
			result = color1;
		//else either the row or column are odd with the other even. Make color yellow
		else if(row % 2 == 0 && col % 2 == 1 || row % 2 == 1 && col % 2 == 0) 
			result = color2;
		return result;
	}
}