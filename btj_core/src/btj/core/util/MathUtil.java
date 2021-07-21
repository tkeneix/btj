package btj.core.util;

import java.util.ArrayList;
import java.util.List;

import btj.core.dataset.CandleData;



public class MathUtil {
	public static double max(double a, double b){
		double ret;
		if(a > b){
			ret = a;
		}else{
			ret = b;
		}
		return ret;
	}

	public static double min(double a, double b){
		double ret;
		if(a < b){
			ret = a;
		}else{
			ret = b;
		}
		return ret;
	}

	public static int min(int a, int b){
		int ret;
		if(a < b){
			ret = a;
		}else{
			ret = b;
		}
		return ret;
	}

	//kuriage
	public static double Ceil(double in, double base){
	    double ret;
	    double chousei = 1/base;
	    ret = Math.ceil(in * chousei)*(1/chousei);
	    if(ret < base){
	        ret = base;
	    }
	    return ret;
	}

	//kurisage
	public static double Floor(double in, double base){
	    double ret;
	    double chousei = 1/base;
	    ret = Math.floor(in * chousei)*(1/chousei);
	    return ret;
	}

	public static double max(double[] array, int length){
		double ret = 0.0;
		for(int i=0; i<length; i++){
			if(array[i] > ret){
				ret = array[i];
			}
		}
		return ret;
	}

	public static double min(double[] array, int length){
		double ret = Double.MAX_VALUE;
		for(int i=0; i<length; i++){
			if(array[i] < ret){
				ret = array[i];
			}
		}
		return ret;
	}

	public static double average(double[] array, int length){
		double ret = 0.0;

		if(array != null){
			for(int i=0; i<length; i++){
				ret += array[i];
			}
			ret /= length;
		}

		return ret;
	}

	public static double stdev(double[] array, int length){
		double ret = 0.0;
		if(array != null){
			ret = Math.sqrt(var(array, length));
		}
		return ret;
	}

	public static double var(double[] array, int length){
		double ret = 0.0;
		if(array != null){
			//スパンの平均を求める
			double avg = average(array, length);
			for(int i=0; i<length; i++){
				ret += (array[i] - avg) * (array[i] - avg);
			}
			ret = ret/(length - 1);
		}
		return ret;
	}

	public static double correl(double[] x, double[] y, int length){
        double  xt,yt,x2t,y2t,xyt,xh,yh,xs,ys,ret;
        xt = 0; yt = 0; xyt = 0; x2t = 0; y2t = 0; ret = 0.0;
        double xsd,ysd;

        if(x != null && y != null){
	       for( int  i=0; i<length; i++)  {
	            xt += x[i];   yt += y[i];
	            x2t += x[i]*x[i];    y2t += y[i]*y[i];
	            xyt += x[i]*y[i];
	       }
	       xh = xt/length;
	       yh = yt/length;
	       xsd=x2t/length-xh*xh;
	       ysd=y2t/length-yh*yh;
	       xs = Math.sqrt(xsd);
	       ys = Math.sqrt(ysd);
	       ret = (xyt/length-xh*yh)/(xs*ys);
        }
       return ret;
	}

	public static double rsq(double[] x, double[] y, int length){
		double ret = 0.0;
		if(x != null && y != null){
			double buf = correl(x, y, length);
			ret = buf * buf;
		}

		return ret;
	}
}
