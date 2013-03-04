import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class Complex {
	double im, re;
	public static Complex WithAngle(double angle) {
		return new Complex(cos(angle),sin(angle));
	}
	Complex(double re, double im) {
		this.im = im;
		this.re = re;
	}
	Complex mult(Complex a) {
		return new Complex(re*a.re - im*a.im, im*a.re + re*a.im);
	}
	Complex add(Complex a) {
		return new Complex(re+a.re, im+a.im);
	}
	Complex sub(Complex a) {
		return new Complex(re-a.re, im-a.im);
	}
	@Override 
	public String toString() {
		return "(" + re + "," + im + ")";
	}
	public Complex sqr() {
		return this.mult(this);
	}
}
