package btj.core.dataset;

import java.text.SimpleDateFormat;
import java.util.Date;

import btj.core.tester.Ticket;
import btj.core.util.DateFormat;


public class CandleData extends Data{
	public static final short OPEN  = 1;
	public static final short HIGH  = 2;
	public static final short LOW   = 3;
	public static final short CLOSE = 4;

	public Date date;
	public double open;
	public double high;
	public double low;
	public double close;
	public double volume;

	/**
	 * CandleDataの双方向リストの後方を表す。
	 * nullの場合は、startDate。
	 */
	public CandleData back;

	/**
	 * CandleDataの双方向リストの前方を表す。
	 * nullの場合は、endDate。
	 */
	public CandleData next;


	public CandleData(){
		this.date = null;
		this.open = 0.0;
		this.high = 0.0;
		this.low = 0.0;
		this.close = 0.0;
		this.volume = 0.0;
	}

	public CandleData(Date date, double open, double high,
			double low, double close, double volume){
		this.date = date;
		this.open = open;
		this.high = high;
		this.low = low;
		this.close = close;
		this.volume = volume;
	}


	/**
	 * 0が当日
	 * 1が前日
	 * -1が明日
	 * (設計注)
	 * データがない場合はnullを返す。
	 * 中身が空のオブジェクト(isAvailable=false)を返すのと
	 * どちらがよいか検討が必要。
	 * @param num
	 * @return
	 */
	public CandleData get(int num){
		CandleData ret = null;
		if(num == 0){
			ret = this;
		}else if(num > 0){
			ret = this;
			for(int i=1; i<=num; i++){
				if(ret != null){
					ret = ret.back;
				}
			}
		}else if(num < 0){
			ret = this;
			num = num * -1;
			for(int i=1; i<=num; i++){
				if(ret != null){
					ret = ret.next;
				}
			}
		}

		return ret;
	}

	public CandleData getNext() {
		return next;
	}

	public void setNext(CandleData next) {
		this.next = next;
	}

	public CandleData getBack() {
		return back;
	}

	public void setBack(CandleData back) {
		this.back = back;
	}

	public String toString(){
		return "date:" + DateFormat.DF_YMDHMS_SL.format(date) + " open:" + open + " high:" + high
				+ " low:" + low + " close:" + close + " volume:" + volume;
	}

	public String toCSV(){
		StringBuffer sb = new StringBuffer();
		sb.append(DateFormat.DF_YMDHMS_SL.format(date));
		sb.append(",");
		sb.append(open);
		sb.append(",");
		sb.append(high);
		sb.append(",");
		sb.append(low);
		sb.append(",");
		sb.append(close);
		sb.append(",");
		sb.append(volume);

		return sb.toString();
	}
}
