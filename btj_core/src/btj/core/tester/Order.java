/**
 * 設計注(20090405)
 * 建玉1枚につきOrderインスタンスを1つ生成する。
 * Orderに建玉数を付与してTradeResultインスタンスを複数管理する方法も
 * 考えられるが、現在の1対1の方式でも複数建玉は複数注文を出せば良いだけである。
 */

package btj.core.tester;

import java.util.HashMap;
import java.util.Map;

public class Order {
	public static final short KD_NEW			= 1;
	public static final short KD_REPAY			= 2;

	public static final short TP_IGNORE			= -1;
	public static final short TP_YORI			= 1;
	public static final short TP_HIKE			= 2;
	public static final short TP_ZENBAHIKE		= 3;
	public static final short TP_GOBAYORI		= 4;
	public static final short TP_NARI			= 5;
	public static final short TP_SASHINE		= 6;
	public static final short TP_GYAKUSASHINE	= 7;
	public static final short TP_YUUBAYORI		= 8;
	public static final short TP_YUUBAHIKE		= 9;

	public static final Map<Short, String> orderToStr;

	static{
		orderToStr = new HashMap<Short, String>();
		orderToStr.put(TP_YORI, "寄付");
		orderToStr.put(TP_NARI, "成行");
		orderToStr.put(TP_SASHINE, "指値");
	}

	/**
	 * DataSet名(銘柄コード)
	 */
	private String dsName;

	/**
	 * 新規か返済かを表す。
	 */
	private short kind;		//KD_NEW,KD_REPAY

	/**
	 * 寄成、引成、成行、指値、逆指値などの注文タイプを表す。
	 */
	private short type;		//TP_YORI,TP_HIKE,TP_NARI,
	                        //TP_SASHINE,TP_GYAKUSASHINE

	/**
	 * 注文に対するチケット。
	 * 新規注文または返済注文に対するチケットは一つ。
	 * チケットに対する新規注文は一つ。
	 * チケットに対する返済注文は複数。
	 */
	private Ticket ticket;

	/**
	 * Judgementクラスによるチェックが行われたかどうかを表すフラグ
	 */
	private boolean isChecked;

	public Order(String dsName, short kind, short type){
		this.dsName = dsName;
		this.kind = kind;
		this.type = type;
		this.ticket = null;
		this.isChecked = false;
	}

	public Ticket getTicket() {
		return ticket;
	}

	public void setTicket(Ticket ticket) {
		this.ticket = ticket;
	}

	public short getKind() {
		return kind;
	}

	public short getType() {
		return type;
	}

	public String getDsName() {
		return dsName;
	}

	public void setCheck(boolean value){
		isChecked = value;
	}

	public boolean getCheck(){
		return isChecked;
	}

}
