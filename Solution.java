import java.io.*;
import java.util.*;

import static java.lang.Math.*;

public class Solution implements Runnable {
		
	Complex[] fft(Complex a[], boolean inverse) {		
		int n = a.length;		
		int log = 0;
		while(((1<<log) & n) == 0) {
			log++;
		}		
		Complex[] r = new Complex[n];
		for (int i = 0; i < n; i++) {
			int rev = Integer.reverse(i) >>> (32-log);
			r[rev] = a[i];
		}		
		for(int s = 1; s <= log; s++) {
			int len = 1<<s;
			for(int shift = 0; shift < n; shift += len) {
				double angle = 2*PI/len;
				if(inverse) {
					angle *= -1;
				}
				Complex w0 = Complex.WithAngle(angle), w = new Complex(1,0);
				for (int i = 0; i < len/2; i++) {
					Complex t = r[shift+i+len/2].mult(w);
					Complex c = r[shift+i];
					r[shift+i] = c.add(t);
					r[shift+i+len/2] = c.sub(t);
					w = w.mult(w0); 
				}
			}
		}
		return r;
		/*
		if(n == 1) {
			return a;
		}
		Complex[] a1 = new Complex[n/2], a2 = new Complex[n/2];
		for (int i = 0; i < n/2; i++) {
			a1[i] = a[i*2];
			a2[i] = a[i*2+1];
		}
		Complex[] y1 = fft(a1,inverse), y2 = fft(a2, inverse);
		double angle = 2*PI/n;
		if(inverse) {
			angle *= -1;
		}
		Complex w = new Complex(1,0), w0 = new Complex(angle);
		
		Complex r[] = new Complex[n];
		for (int i = 0; i < n/2; i++) {
			Complex t = y2[i].mult(w);
			r[i] = y1[i].add(t);
			r[i+n/2] = y1[i].sub(t);
			w = w.mult(w0);
		}
		return r;/**/
	}
	
	int[] simpleFFTConvolution(int x[], int h[]) {
		int n = x.length, m = h.length;
		int y[] = new int[n+m-1];
		
		int len = 1;
		while(len < y.length) {
			len <<= 1;
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
	
	int[] partitionedFFTConvolution(int x[], int h[]) {		
		int n = x.length, m = h.length;
		//int t = m + rnd.nextInt(max(1,n-m+1));
		int t = 2*m;
		if(t >= n) {
			return simpleFFTConvolution(x, h);
		}
		int[] y = new int[n+m-1];
		
		int[] r = simpleFFTConvolution(Arrays.copyOfRange(x, 0, t), h);
		for (int i = 0; i < r.length-(m-1); i++) {
			y[i] = r[i];
		}
		int sty = r.length-(m-1);
		int stx = t-(m-1);
		while(stx+t-1 < n) {
			r = simpleFFTConvolution(Arrays.copyOfRange(x, stx, stx+t), h);
			for (int i = 0; i < t-m+1; i++) {
				y[sty+i] = r[i+(m-1)];
			}
			sty += t-m+1;
			stx += t-m+1;
		}
		
		r = simpleFFTConvolution(Arrays.copyOfRange(x, n-t, n), h);
		for(int i = 0; i < r.length-(m-1); i++) {
			y[y.length-t+i] = r[m-1+i];
		}
		
		return y;
	}
		
	ArrayList<ConvolutionCalculator> algos = new ArrayList<ConvolutionCalculator>();
	
	void solve() throws Throwable {
	
		algos.add(new StupidConvolutionCalculator());
		algos.add(new PowerOf3ConvolutionCalculator());
		
		//while(test(1+rnd.nextInt(1000),1+rnd.nextInt(1000)));
		
		int m = readInt();
		int h[] = new int[m];
		for (int i = 0; i < m; i++) {
			h[i] = readInt();
		}
		int n = readInt();
		int x[] = new int[n];
		for (int i = 0; i < n; i++) {
			x[i] = readInt();
		}
	
		int algoCnt = algos.size();
		int[][] y = new int[algoCnt][];
		
		for (int i = 0; i < algoCnt; i++) {
			y[i] = algos.get(i).convolution(x, h);
		}
		
		boolean fail = false;		
		
		for (int i = 0; i < algoCnt-1; i++) {
			if(!Arrays.equals(y[i], y[i+1])) {
				fail = true;
				break;
			}
		}
		if(fail) {
			out.println("fail");			
		} else {
			out.println("success");
		}
		for (int i = 0; i < algoCnt; i++) {
			for(int v : y[i]) {
				out.print(v + " ");				
			}
			out.println();
		}
	}
	
	Random rnd = new Random();
	
	/*boolean test(int m, int n) {
		
		int h[] = new int[m];
		for (int i = 0; i < m; i++) {
			h[i] = rnd.nextInt(10);
		}		
		int x[] = new int[n];
		for (int i = 0; i < n; i++) {
			x[i] = rnd.nextInt(10);
		}
		int y1[] = stupidConvolution(x,h);
		int y2[] = simpleFFTConvolution(x,h);
		int y3[] = partitionedFFTConvolution(x, h);
		
		if(Arrays.equals(y1, y2) && Arrays.equals(y2,y3)) {
			debug("test passed");
			return true;
		} else {			
			out.println("h:");
			for(int v : h) {
				out.print(v + " ");
			}
			out.println();
			out.println("x:");
			for(int v : x) {
				out.print(v + " ");
			}
			out.println();
			out.println();
			for(int v : y1) {
				out.print(v + " ");
			}
			out.println();
			
			for(int v : y2) {
				out.print(v + " ");
			}		
			out.println();
			
			for(int v : y3) {
				out.print(v + " ");
			}
			return false;
		}		
	}/**/
	
///////////////////////////////////////
	final boolean ONLINE_JUDGE = System.getProperty("ONLINE_JUDGE") != null;

	BufferedReader in;
	PrintWriter out;
	StringTokenizer tok;

	public static void main(String[] args) {
		new Thread(null, new Solution(), "", 128 * (1L << 20)).start();
	}

	public void run() {
		try {
			long startTime = System.currentTimeMillis();
			Locale.setDefault(Locale.US);
			if (ONLINE_JUDGE) {
				in = new BufferedReader(new InputStreamReader(System.in));
				out = new PrintWriter(System.out);
			}
			 else {
				in = new BufferedReader(new FileReader("input.txt"));
				out = new PrintWriter("output.txt");
			}
			tok = new StringTokenizer("");
			solve();
			in.close();
			out.close();
			long freeMemory = Runtime.getRuntime().freeMemory();
			long totalMemory = Runtime.getRuntime().totalMemory();
			long endTime = System.currentTimeMillis();
			System.err.printf("Time = %.3f ms\n", (endTime - startTime) / 1000.0);
			System.err.printf("Memory = %.3f MB\n", (totalMemory - freeMemory) / (double) (1 << 20));
		} catch (Throwable t) {
			t.printStackTrace(System.err);
			System.exit(-1);
		}
	}

	String readString() throws IOException {
		while (!tok.hasMoreTokens()) {
			String line = in.readLine();
			if (line == null) return null;
			tok = new StringTokenizer(line);
		}
		return tok.nextToken();
	}

	int readInt() throws IOException {
		return Integer.parseInt(readString());
	}

	long readLong() throws IOException {
		return Long.parseLong(readString());
	}

	double readDouble() throws IOException {
		return Double.parseDouble(readString());
	}	

	void debug(Object... o) {
		if (!ONLINE_JUDGE) {
			System.err.println(Arrays.deepToString(o));
		}
	}
}