package btj.core.tester;

import java.util.HashMap;
import java.util.Map;

public class NewOrder extends Order{
	public static final short DT_NONE			= 0;
	public static final short DT_BUY			= 1;
	public static final short DT_SELL			= 2;

	public static final Map<Short, String> kindToStr;

	static{
		kindToStr = new HashMap<Short, String>();
		kindToStr.put(DT_BUY, "買");
		kindToStr.put(DT_SELL, "売");
	}


	/**
	 * 買か売かを表す。
	 */
	private short delta;	//OP_BUY,OP_SELL
	/**
	 * ポジション枚数
	 * (設計注)
	 * 分割返済はできない
	 */
	private double lots;
	/**
	 * 注文価格
	 */
	private double value;
	/**
	 * 注文の有効期限を表す。
	 * 有効期限かどうかの判定はJudgementで行う
	 */
	private short expired;

	public NewOrder(String dsName, short type, short delta, double lots, double value, short expired){
		super(dsName, Order.KD_NEW, type);
		this.delta = delta;
		this.lots = lots;
		this.value = value;
		this.expired = expired;
	}

	public short getDelta() {
		return delta;
	}

	public double getValue() {
		return value;
	}

	public short getExpired() {
		return expired;
	}

	public double getLots() {
		return lots;
	}

}
