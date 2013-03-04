import static java.lang.Math.PI;
import static java.lang.Math.round;


public class PowerOf3ConvolutionCalculator implements ConvolutionCalculator {

	@Override
	public int[] convolution(int x[], int h[]) {
		int n = x.length, m = h.length;
		int y[] = new int[n+m-1];
		
		int len = 1;
		while(len < y.length) {
			len *= 3;
		}
		Complex[] newX = new Complex[len], newH = new Complex[len];
		for (int i = 0; i < n; i++) {
			newX[i] = new Complex(x[i],0);
		}
		for(int i = n; i < len; i++) {
			newX[i] = new Complex(0,0);
		}
		for (int i = 0; i < m; i++) {
			newH[i] = new Complex(h[i],0);			
		}
		for(int i = m; i < len; i++) {
			newH[i] = new Complex(0,0);
		}

		Complex ftx[] = fft(newX,false);
		Complex fth[] = fft(newH,false);
		
		Complex[] r = new Complex[len];
		for (int i = 0; i < len; i++) {
			r[i] = ftx[i].mult(fth[i]);
		}
		r = fft(r,true);
		for (int i = 0; i < y.length; i++) {
			y[i] = (int)round(r[i].re/len);
		}
		return y;
	}
	
	final Complex rotFactor = Complex.WithAngle(2.0*PI/3.0);
	final Complex rotFactor2 = Complex.WithAngle(4.0*PI/3.0);
	
	Complex[] fft(Complex a[], boolean inverse) {
		int n = a.length;
		if(n == 1) {
			return a;
		}
		Complex[] a0 = new Complex[n/3], 
				  a1 = new Complex[n/3],
				  a2 = new Complex[n/3];
		for (int i = 0; i < n/3; i++) {
			a0[i] = a[i*3];
			a1[i] = a[i*3+1];
			a2[i] = a[i*3+2];
		}
		Complex[] y0 = fft(a0, inverse),
				  y1 = fft(a1, inverse),
				  y2 = fft(a2, inverse);
		double angle = 2*PI/n;
		if(inverse) {
			angle *= -1;
		}
		Complex w = new Complex(1,0), w0 = Complex.WithAngle(angle);
		Complex rf1, rf2;
		if(inverse) {
			rf1 = rotFactor2;
			rf2 = rotFactor;
		} else {
			rf2 = rotFactor2;
			rf1 = rotFactor;
		}
		
		Complex r[] = new Complex[n];
		for (int i = 0; i < n/3; i++) {
			r[i]       = y0[i] .add (y1[i].mult(w))                  .add (y2[i].mult(w.sqr()));
			r[i+n/3]   = y0[i] .add (y1[i].mult(w.mult(rf1)))  .add (y2[i].mult(w.mult(rf1).sqr()));
			r[i+2*n/3] = y0[i] .add (y1[i].mult(w.mult(rf2))) .add (y2[i].mult(w.mult(rf2).sqr()));
			/*Complex t = y2[i].mult(w);
			r[i] = y1[i].add(t);
			r[i+n/2] = y1[i].sub(t);/**/
			w = w.mult(w0);
		}
		return r;
	}	
}
