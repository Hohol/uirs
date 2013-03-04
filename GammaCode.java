import static java.lang.Math.*;

public class GammaCode {
	double x, y;
//	public static Complex WithAngle(double angle) {
//		return new Complex(cos(angle),sin(angle));
//	}
	GammaCode(double x, double y) {
		this.x = x;
		this.y = y;
	}
	GammaCode(Complex z) {
		x = -z.re + z.im/sqrt(3);
		y = -z.re - z.im/sqrt(3);
	}
	GammaCode mult(GammaCode a) {
		double u = a.x, v = a.y;
		return new GammaCode((y-x)*(v-u)-x*u, (y-x)*(v-u)-y*v);
	}
	GammaCode add(GammaCode a) {
		return new GammaCode(x+a.x, y+a.y);
	}
	GammaCode sub(GammaCode a) {
		return new GammaCode(x-a.y, x-a.y);
	}
	@Override 
	public String toString() {
		return "(" + x + "," + y + ")";
	}
	public GammaCode sqr() {
		return this.mult(this);
	}
	public GammaCode rotateLeft() {
		return new GammaCode(-y,x-y);
	}
	public GammaCode rotateRight() {
		return new GammaCode(y-x,-x);
	}
}