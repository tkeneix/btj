/**
 * 配当が0以外の場合は修正株価を考慮する必要あり
 */

package btj.core.util;

import java.util.Date;

public class BlackScholes {
	public final static short PUT = (short)1;
	public final static short CALL = (short)2;

	public static boolean debug = false;
	public static double IV_MAX = 500.0;
	public static double IV_MIN = 0.0;
	public static double IV_LOOPCOUNT = 10000;

	public static double theoreticalPrice(
									short kind,		//call or put
									double spotPrice, // spot price(現在価格)
									double strikePrice, // exercise price(行使価格)
									double timeToMaturity, // time to maturity
									double interestRate, // interest rate(金利)
									double dividendYield, // dividend yield(配当)
									double impliedVolatility // volatility
									){
		checkArgs(kind, timeToMaturity, impliedVolatility, interestRate);
		double daysToExpiration = timeToMaturity / 365.0;
		double volRate = impliedVolatility / 100.0;
		double irRate = interestRate / 100.0;
		if(kind == CALL){
			if(timeToMaturity <= 0.0){
				return Math.max(0.0,spotPrice-strikePrice);
			}else{
				return spotPrice*Math.exp(-dividendYield*daysToExpiration)*_normsdist(_d1(spotPrice,strikePrice,volRate,daysToExpiration,irRate,dividendYield),1)
						- strikePrice*Math.exp(-irRate*daysToExpiration)*_normsdist(_d2(spotPrice,strikePrice,volRate,daysToExpiration,irRate,dividendYield),1);
	//			return callputFlag * (spotPrice * _normsdist(callputFlag * _d1(spotPrice,strikePrice,volRate,daysToExpiration,irRate,dividendYield)))
	//					- strikePrice * _normsdist(callputFlag * _d2(spotPrice,strikePrice,volRate,daysToExpiration,irRate,dividendYield));
			}
		}else if(kind == PUT){
			if(timeToMaturity <= 0.0){
				return Math.max(0.0,strikePrice-spotPrice);
			}else{
				return strikePrice*Math.exp(-irRate*daysToExpiration)*_normsdist(_d2(spotPrice,strikePrice,volRate,daysToExpiration,irRate,dividendYield),-1)
						- spotPrice*Math.exp(-dividendYield*daysToExpiration)*_normsdist(_d1(spotPrice,strikePrice,volRate,daysToExpiration,irRate,dividendYield),-1);
			}
		}else{
			//通らないパス
			return 0;
		}
	}

	public static double delta(
			short kind,		//call or put
			double spotPrice, // spot price(現在価格)
			double strikePrice, // exercise price(行使価格)
			double timeToMaturity, // time to maturity
			double interestRate, // interest rate(金利)
			double dividendYield, // dividend yield(配当)
			double impliedVolatility // volatility
			){
		checkArgs(kind, timeToMaturity, impliedVolatility, interestRate);
		double daysToExpiration = timeToMaturity / 365.0;
		double volRate = impliedVolatility / 100.0;
		double irRate = interestRate / 100.0;
		double callputFlag = 0;
		if(kind == CALL){
			callputFlag = 1;
		}else if(kind == PUT){
			callputFlag = -1;
		}

		return callputFlag * _normsdist(_d1(spotPrice,strikePrice,volRate,daysToExpiration,irRate,dividendYield), callputFlag);
	}

	public static double gamma(
			short kind,		//call or put
			double spotPrice, // spot price(現在価格)
			double strikePrice, // exercise price(行使価格)
			double timeToMaturity, // time to maturity
			double interestRate, // interest rate(金利)
			double dividendYield, // dividend yield(配当)
			double impliedVolatility // volatility
			){
		checkArgs(kind, timeToMaturity, impliedVolatility, interestRate);
		double daysToExpiration = timeToMaturity / 365.0;
		double volRate = impliedVolatility / 100.0;
		double irRate = interestRate / 100.0;
		double callputFlag = 0;
		if(kind == CALL){
			callputFlag = 1;
		}else if(kind == PUT){
			callputFlag = -1;
		}

		double d1 = _d1(spotPrice,strikePrice,volRate,daysToExpiration,irRate,dividendYield);
		return Math.exp((-1)*d1*d1/2)
				/ ((Math.sqrt(2 * Math.PI)) * spotPrice * _a(volRate, daysToExpiration));
	}

	public static double vega(
			short kind,		//call or put
			double spotPrice, // spot price(現在価格)
			double strikePrice, // exercise price(行使価格)
			double timeToMaturity, // time to maturity
			double interestRate, // interest rate(金利)
			double dividendYield, // dividend yield(配当)
			double impliedVolatility // volatility
			){
		checkArgs(kind, timeToMaturity, impliedVolatility, interestRate);
		double daysToExpiration = timeToMaturity / 365.0;
		double volRate = impliedVolatility / 100.0;
		double irRate = interestRate / 100.0;
		double callputFlag = 0;
		if(kind == CALL){
			callputFlag = 1;
		}else if(kind == PUT){
			callputFlag = -1;
		}

		double d1 = _d1(spotPrice,strikePrice,volRate,daysToExpiration,irRate,dividendYield);
		return (spotPrice * Math.sqrt(daysToExpiration) * Math.exp((-1)*d1*d1/2)) / ((Math.sqrt(2 * Math.PI)));
	}

	public static double percentVega(
			short kind,		//call or put
			double spotPrice, // spot price(現在価格)
			double strikePrice, // exercise price(行使価格)
			double timeToMaturity, // time to maturity
			double interestRate, // interest rate(金利)
			double dividendYield, // dividend yield(配当)
			double impliedVolatility // volatility
			){
		return vega(kind, spotPrice, strikePrice, timeToMaturity, interestRate, dividendYield, impliedVolatility)/100.0;

	}

	public static double theta(
			short kind,		//call or put
			double spotPrice, // spot price(現在価格)
			double strikePrice, // exercise price(行使価格)
			double timeToMaturity, // time to maturity
			double interestRate, // interest rate(金利)
			double dividendYield, // dividend yield(配当)
			double impliedVolatility // volatility
			){
		checkArgs(kind, timeToMaturity, impliedVolatility, interestRate);
		double daysToExpiration = timeToMaturity / 365.0;
		double volRate = impliedVolatility / 100.0;
		double irRate = interestRate / 100.0;
		double callputFlag = 0;
		if(kind == CALL){
			callputFlag = 1;
		}else if(kind == PUT){
			callputFlag = -1;
		}

		double d1 = _d1(spotPrice,strikePrice,volRate,daysToExpiration,irRate,dividendYield);
		double d2 = _d2(spotPrice,strikePrice,volRate,daysToExpiration,irRate,dividendYield);
		return -spotPrice * volRate * Math.exp((-1)*d1*d1/2) / (2 * Math.sqrt(2 * Math.PI) * Math.sqrt(daysToExpiration))
				- callputFlag * irRate * strikePrice * _normsdist(d2, callputFlag) * Math.exp(-irRate * daysToExpiration);
	}

	public static double timeDecay(
			short kind,		//call or put
			double spotPrice, // spot price(現在価格)
			double strikePrice, // exercise price(行使価格)
			double timeToMaturity, // time to maturity
			double interestRate, // interest rate(金利)
			double dividendYield, // dividend yield(配当)
			double impliedVolatility // volatility
			){
		return theta(kind, spotPrice, strikePrice, timeToMaturity, interestRate, dividendYield, impliedVolatility)/365.0;

	}

	public static double inpliedVolatility(
									short kind,		//call or put
									double spotPrice, // spot price(現在価格)
									double strikePrice, // exercise price(行使価格)
									double timeToMaturity, // time to maturity
									double interestRate, // interest rate(金利)
									double dividendYield, // dividend yield(配当)
									double premium // premium
									){
		/**
		 * premiumは整数値を前提とする。
		 * 小数点がある場合は、roundの処理を小数点を考慮して丸める必要がある。
		 */
		double mid = 0.0;
		double estimate = 0.0;
		double high = IV_MAX;
		double low = IV_MIN;

		for(int i=0; i<IV_LOOPCOUNT; i++){
			mid = (high + low) / 2.0;
			estimate = theoreticalPrice(kind, spotPrice, strikePrice, timeToMaturity, interestRate, dividendYield, mid);
			//小数点第２位まで一致するまでループする
			if(Math.round(estimate*100) == premium*100){
				break;
			}else if(estimate > premium){
				high = mid;
			}else if(estimate < premium){
				low = mid;
			}
		}
		return mid;
	}

	public static double detailTimeToMaturity(Date current, Date SQday){
		double currentTime = (double)current.getTime();
		double SQDayTime = (double)SQday.getTime();
		return (SQDayTime - currentTime)/(1000*60*60*24);
	}

	private static void checkArgs(short kind, double timeToMaturity, double impliedVolatility, double interestRate){
		if((kind != CALL && kind != PUT) || timeToMaturity == 0.0 || impliedVolatility == 0.0 || interestRate == 0.0){
			throw new RuntimeException("error timeToMaturity=" + timeToMaturity
										+ " impliedVolatility=" + impliedVolatility
										+ " interestRate=" + interestRate);
		}
	}

	private static double _a(double volRate, double daysToExpiration){
		return volRate * Math.sqrt(daysToExpiration);
	}

	private static double _d1(
							double spotPrice,
							double strikePrice,
							double volRate,
							double daysToExpiration,
							double irRate,
							double dividendYield)
	{
			double ret = (Math.log(spotPrice/strikePrice)+(irRate-dividendYield+(volRate*volRate)/2)*daysToExpiration)/_a(volRate, daysToExpiration);
			if(debug) System.out.println("_d1=" + ret);
			return ret;
	}

	private static double _d2(
							double spotPrice,
							double strikePrice,
							double volRate,
							double daysToExpiration,
							double r,
							double dividendYield)
	{
			double ret = _d1(spotPrice,strikePrice,volRate,daysToExpiration,r,dividendYield)-_a(volRate, daysToExpiration);
			if(debug) System.out.println("_d2=" + ret);
			return ret;
	}





	private static double _normsdist(double value, double hugou)
	{
		double ret = 0.0;
		double z2 = value*value,
		t = hugou * value*Math.exp(-0.5*z2)/Math.sqrt(2*Math.PI),
		p = t;

		for(int i=3;i<200;i+=2)
		{
			double prev = p; t *= z2/i; p += t;
			if(p == prev)
			{
				return 0.5+p;
			}
		}

		//if(debug) System.out.println("_normsdist=" + value);
		if(value>0){
			ret = 1.0;
		}else{
			ret = 0.0;
		}
		return ret;
	}

	// Private variables used for Black-Scholes formula
//	static final double beta = 0.2316419;
//	static final double a1 = 0.319381530;
//	static final double a2 = -0.356563782;
//	static final double a3 = 1.781477937;
//	static final double a4 = -1.821255978;
//	static final double a5 = 1.330274429;
//
//	private static double N(double x) {
//		double n_dash;
//		double k = 1 / (1 + beta * x);
//		n_dash = 1 / Math.sqrt(2 * Math.PI) * Math.exp(- x * x /2);
//		if (x >= 0) {
//			return 1 - n_dash * (a1*k + a2*Math.pow(k, 2) + a3*Math.pow(k, 3) + a4*Math.pow(k, 4) + a5*Math.pow(k, 5));
//		} else {
//			return 1 - N(-x);
//		}
//	}
//	private static double _normsdist(double value, double hugou){
//		return N(hugou * value);
//	}

}
