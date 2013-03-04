import static java.lang.Math.max;
import static java.lang.Math.min;

public class StupidConvolutionCalculator implements ConvolutionCalculator {

	@Override
	public int[] convolution(int[] x, int[] h) {	
		int n = x.length, m = h.length;
		int y[] = new int[n+m-1];
		for (int i = 0; i < y.length; i++) {
			for (int j = max(0,i-n+1); j <= min(i,m-1); j++) {
				y[i] += h[j]*x[i-j];
			}
		}
		return y;
	}

}
