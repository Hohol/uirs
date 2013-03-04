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
		GammaCode[] newX = new GammaCode[len], newH = new GammaCode[len];
		for (int i = 0; i < n; i++) {
			newX[i] = new GammaCode(new Complex(x[i],0));
		}
		for(int i = n; i < len; i++) {
			newX[i] = new GammaCode(0,0);
		}
		for (int i = 0; i < m; i++) {
			newH[i] = new GammaCode(new Complex(h[i],0));			
		}
		for(int i = m; i < len; i++) {
			newH[i] = new GammaCode(0,0);
		}

		GammaCode ftx[] = fft(newX);
		GammaCode fth[] = fft(newH);
		
		GammaCode[] r = new GammaCode[len];
		for (int i = 0; i < len; i++) {
			r[i] = ftx[i].mult(fth[i]);
		}
		r = fft(r);
		for (int i = 0; i < (len-1)/2; i++) {
			GammaCode tmp = r[1+i];
			r[1+i] = r[len-1-i];
			r[len-1-i] = tmp;
		}
		
		for (int i = 0; i < y.length; i++) {
			y[i] = (int)round(-(r[i].x+r[i].y)/(2*len));
		}
		return y;
	}
		
	GammaCode[] fft(GammaCode a[]) {
		int n = a.length;
		if(n == 1) {
			return a;
		}
		GammaCode[] a0 = new GammaCode[n/3], 
				  a1 = new GammaCode[n/3],
				  a2 = new GammaCode[n/3];
		for (int i = 0; i < n/3; i++) {
			a0[i] = a[i*3];
			a1[i] = a[i*3+1];
			a2[i] = a[i*3+2];
		}
		GammaCode[] y0 = fft(a0),
				  y1 = fft(a1),
				  y2 = fft(a2);
		double angle = 2*PI/n;		
		GammaCode w = new GammaCode(new Complex(1,0)), w0 = new GammaCode(Complex.WithAngle(angle));
		
		
		
		GammaCode r[] = new GammaCode[n];
		for (int i = 0; i < n/3; i++) {
			r[i]       = y0[i] .add (y1[i].mult(w))                  .add (y2[i].mult(w.sqr()));
			r[i+n/3]   = y0[i] .add (y1[i].mult(w.rotateLeft()))  .add (y2[i].mult(w.rotateLeft().sqr()));
			r[i+2*n/3] = y0[i] .add (y1[i].mult(w.rotateRight())) .add (y2[i].mult(w.rotateRight().sqr()));
			/*Complex t = y2[i].mult(w);
			r[i] = y1[i].add(t);
			r[i+n/2] = y1[i].sub(t);/**/
			w = w.mult(w0);
		}
		return r;
	}	
}
