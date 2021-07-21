/**
 * ShorTermCandleDataのメンバ
 * 日足、週足、月足のCandleDataや残存日数、ATM、IV値幅など
 * 別のタイムフレームの付加情報を保持するクラス
 */

package btj.core.dataset;

import java.util.Iterator;
import java.util.Map;

import btj.core.util.DateFormat;


public class OptionalData extends Data{
	private CandleData candle;
	private Map map;			//<key,value>=<String,Double>
	private OptionalData next;	//未来のOptionalData
	private OptionalData back;	//過去のOptionalData

	public OptionalData(CandleData candle, Map map) {
		super();
		this.candle = candle;
		this.map = map;
	}

	public CandleData getCandle() {
		return candle;
	}

	public void setCandle(CandleData candle) {
		this.candle = candle;
	}

	public double getData(String key){
		Double retBuf = (Double)map.get(key);
		if(retBuf != null){
			return retBuf.doubleValue();
		}else{
			return 0.0;
		}
	}

	public void putData(String key, double value){
		map.put(key, new Double(value));
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
	public OptionalData get(int num){
		OptionalData ret = null;
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

	public OptionalData getNext() {
		return next;
	}

	public void setNext(OptionalData next) {
		this.next = next;
	}

	public OptionalData getBack() {
		return back;
	}

	public void setBack(OptionalData back) {
		this.back = back;
	}

	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append("OptionalData{");
		sb.append(candle.toString());
		sb.append(" next.data=");
		sb.append(next != null ? DateFormat.DF_YMD_SL.format(next.candle.date) : null);
		sb.append(" back.data=");
		sb.append(back != null ? DateFormat.DF_YMD_SL.format(back.candle.date) : null);

		Iterator ite = map.keySet().iterator();
		while(ite.hasNext()){
			String key = (String)ite.next();
			Double value = (Double)map.get(key);
			sb.append(" [" + key + "," + value + "]");
		}
		return sb.toString();
	}

	public String toCSV(){
		StringBuffer sb = new StringBuffer();
		sb.append(candle.toCSV());

		Iterator ite = map.keySet().iterator();
		while(ite.hasNext()){
			String key = (String)ite.next();
			Double value = (Double)map.get(key);
			sb.append(",");
			sb.append(value);
		}
		return sb.toString();
	}
}
