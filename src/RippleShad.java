import javax.vecmath.Point3d;
import java.util.ArrayList;

public class RippleShad extends Shader {
	ArrayList<Color> colors;
	
	public RippleShad(Point3d origin) {
		super(origin);
		this.colors = new ArrayList<Color>(10);
		colors.add(new Color(156.6686932398956, 28.568113368400432, 141.63839555005856));
		colors.add(new Color(99.70635987934406, 199.1531151475586, 247.91370794679878));
		colors.add(new Color(220.06454640483702, 75.20719461320054, 44.68806538457837));
		colors.add(new Color(72.84652576687928, 38.46796760678615, 200.42242524201257));
		colors.add(new Color(234.2085113813471, 198.461009037022, 92.14850348930814));
		colors.add(new Color(61.5774726146031, 29.23721178235078, 63.99156607021947));
		colors.add(new Color(102.43867171570928, 224.31882163943126, 141.04385682247653));
		colors.add(new Color(143.99683834958796, 91.83670870903593, 107.3125890715678));
		colors.add(new Color(105.42679542558923, 213.1216103390557, 200.35282851768633));
		colors.add(new Color(53.806758239983665, 44.31825234013605, 232.5117080791454));
	}
	
	public Color shade(Point3d point) {
		return colors.get((int)point.distance(origin));
	}

}
