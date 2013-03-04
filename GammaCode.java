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
		x = -z.re + 1.0/sqrt(3)*z.im;
		y = -z.im - 1.0/sqrt(3)*z.im;
	}
	GammaCode mult(GammaCode a) {
		double u = a.x, v = a.y;
		return new GammaCode((y-x)*(v-u)-x*u, (y-x)*(v-u)-y*v);
	}
	GammaCode add(GammaCode a) {
		return new GammaCode(x+a.y, x+a.y);
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
}