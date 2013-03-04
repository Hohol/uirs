import static java.lang.Math.PI;
import static java.lang.Math.round;

import java.util.Arrays;


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
	
	void digitReversePermutation(Object a[], int base) {
		int n = a.length;
		int log = 0, t = 1;
		while(t < n) {
			t *= base;
			log++;
		}
		
		int[] d = new int[log];
		int[] pow = new int[log];
		pow[0] = 1;
		for(int i = 1; i < log; i++) {
			pow[i] = pow[i-1]*base;
		}
		int rev = 0;
		for(int i = 0; i < n; i++) {
			if(i < rev) {
				Object tmp = a[i];
				a[i] = a[rev];
				a[rev] = tmp;
			}
			d[0]++;
			rev += pow[log-1];
			int j = 0;
			if(i != n-1) {
				while(d[j] == base) {
					d[j] = 0;
					rev -= base*pow[log-j-1];
					j++;
					d[j]++;
					rev += pow[log-j-1];				
				}
			}
		}
	}
	
	GammaCode[] fft(GammaCode a[]) {
		digitReversePermutation(a,3);
		int n = a.length;
		
		for(int len = 3; len <= n; len *= 3) {
			double angle = 2*PI/len;
			GammaCode w0 = new GammaCode(Complex.WithAngle(angle));
			for(int shift = 0; shift < n; shift += len) {
				GammaCode w = new GammaCode(new Complex(1,0));
				for(int i = 0; i < len/3; i++) {
					GammaCode y0 = a[shift+i], y1 = a[shift+i+len/3], y2 = a[shift+i+2*len/3];
					a[shift+i]         = y0    .add(y1.mult(w))                    .add(y2.mult(w.sqr()));
					a[shift+i+len/3]   = y0    .add(y1.mult(w.rotateLeft()))       .add(y2.mult(w.rotateLeft().sqr()));
					a[shift+i+2*len/3] = y0    .add(y1.mult(w.rotateRight()))      .add(y2.mult(w.rotateRight().sqr()));
					w = w.mult(w0);
				}
			}
		}
		
		return a;
		
		/*int n = a.length;
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
			w = w.mult(w0);
		}
		return r;/**/
	}	
}
