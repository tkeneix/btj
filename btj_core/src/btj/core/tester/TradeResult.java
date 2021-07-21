package btj.core.tester;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;


public class TradeResult implements Serializable{
	public final transient SimpleDateFormat DF_YMDHMS_SL
	= new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	public final transient String DF_YMDHMS_DEFAULT =
			"0000/00/00 00:00:00";

	/**
	 * ストラテジーの名前。
	 */
	public String strategyName;
	/**
	 * トレードと対応付けられる通番を表す。
	 */
	public long ticketNumber;
	/**
	 * トレード対象の銘柄の名前
	 */
	public String securityCode;
	/**
	 * トレードが売または買のどちらの注文だったかを表す。
	 */
	public short delta;
	/**
	 * トレードのポジション枚数
	 */
	public double lots;
	/**
	 * ポジションをオープンした値。
	 */
	public double openValue;
	/**
	 * ポジションをクローズした値。
	 */
	public double closeValue;
	/**
	 * ポジションをオープンしてからの高値。
	 */
	public double highValue;
	/**
	 * ポジションをオープンしてからの安値。
	 */
	public double lowValue;
	/**
	 * 注文した時刻。
	 */
	public Date orderTime;
	/**
	 * ポジションをオープンした時刻。
	 */
	public Date openTime;
	/**
	 * ポジションをクローズした時刻。
	 */
	public Date closeTime;
	/**
	 * ポジションをオープンしてから高値を付けた時刻。
	 */
	public Date highTime;
	/**
	 * ポジションをオープンしてから安値を付けた時刻。
	 */
	public Date lowTime;
	/**
	 * オープンしたこを示すフラグ。
	 */
	public boolean isOpened;
	/**
	 * クローズしたことを示すフラグ。
	 */
	public boolean isClosed;
	/**
	 * 有効期限切れになったことを示すフラグ。
	 */
	public boolean isExpired;

	/**
	 * 注文がキャンセルされたかどうかを表すフラグ。
	 */
	public boolean isCanceled;

	/**
	 * 各Order発行時のコメント。
	 */
	public StringBuffer comment;

	public double leverage;

	public TradeResult() {
		super();
		this.openValue = 0.0;
		this.closeValue = 0.0;
		this.highValue = 0.0;
		this.lowValue = 0.0;
		this.lots = 0.0;
		this.orderTime = null;
		this.openTime = null;
		this.closeTime = null;
		this.highTime = null;
		this.lowTime = null;
		this.isOpened = false;
		this.isClosed = false;
		this.isExpired = false;
		this.isCanceled = false;
		this.comment = new StringBuffer();
		this.leverage = 1.0;
	}

	public double getProfit(){
		double ret = 0.0;

		if(isClosed){
			if(delta == NewOrder.DT_BUY){
				ret = (closeValue - openValue)*lots*leverage;
			}else if(delta == NewOrder.DT_SELL){
				ret = (openValue - closeValue)*lots*leverage;
			}else{
				throw new RuntimeException("予期しないデルタです。delta="
						+ delta);
			}
		}else{
			throw new RuntimeException("未約定のチケットを集計しようとしました。isClosed="
					+ isClosed);
		}
		return ret;
	}

	public String toCSV(){
		StringBuffer sb = new StringBuffer();
		sb.append(strategyName);
		sb.append(",");
		sb.append(ticketNumber);
		sb.append(",");
		sb.append(securityCode);
		sb.append(",");
		String deltaStr = (delta == NewOrder.DT_BUY ) ? "buy" : "sell";
		sb.append(deltaStr);
		sb.append(",");
		sb.append(lots);
		sb.append(",");
		sb.append(orderTime != null ? DF_YMDHMS_SL.format(orderTime)
				: DF_YMDHMS_DEFAULT);
		sb.append(",");
		sb.append(openTime != null ? DF_YMDHMS_SL.format(openTime)
				: DF_YMDHMS_DEFAULT);
		sb.append(",");
		sb.append(highTime != null ? DF_YMDHMS_SL.format(highTime)
				: DF_YMDHMS_DEFAULT);
		sb.append(",");
		sb.append(lowTime != null ? DF_YMDHMS_SL.format(lowTime)
				: DF_YMDHMS_DEFAULT);
		sb.append(",");
		sb.append(closeTime != null ? DF_YMDHMS_SL.format(closeTime)
				: DF_YMDHMS_DEFAULT);
		sb.append(",");
		sb.append(openValue);
		sb.append(",");
		sb.append(highValue);
		sb.append(",");
		sb.append(lowValue);
		sb.append(",");
		sb.append(closeValue);
		sb.append(",");
		sb.append(isOpened);
		sb.append(",");
		sb.append(isClosed);
		sb.append(",");
		sb.append(isExpired);
		sb.append(",");
		sb.append(isCanceled);
		sb.append(",");
		sb.append(comment.toString());
		return sb.toString();
	}

	public static String toCSVHeader(){
		StringBuffer sb = new StringBuffer();
		sb.append("strategyName");
		sb.append(",");
		sb.append("ticketNumber");
		sb.append(",");
		sb.append("securityCode");
		sb.append(",");
		sb.append("delta");
		sb.append(",");
		sb.append("lots");
		sb.append(",");
		sb.append("orderTime");
		sb.append(",");
		sb.append("openTime");
		sb.append(",");
		sb.append("highTime");
		sb.append(",");
		sb.append("lowTime");
		sb.append(",");
		sb.append("closeTime");
		sb.append(",");
		sb.append("openValue");
		sb.append(",");
		sb.append("highValue");
		sb.append(",");
		sb.append("lowValue");
		sb.append(",");
		sb.append("closeValue");
		sb.append(",");
		sb.append("isOpened");
		sb.append(",");
		sb.append("isClosed");
		sb.append(",");
		sb.append("isExpired");
		sb.append(",");
		sb.append("isCanceled");
		sb.append(",");
		sb.append("comment");

		return sb.toString();
	}

	/**
	 * トレードのコメントを追加する
	 *
	 * @param value コメント
	 */
	public void addComment(String value){
		comment.append(value);
		//区切り文字は半角スペース
		comment.append(" ");
	}
}
