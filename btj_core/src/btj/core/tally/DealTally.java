/**
 * 修正履歴
 *   Version 0100 2009/xx/xx 初版リリース
 *   Version 0101 2009/08/03 PerDay集計を追加
 */

package btj.core.tally;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import btj.core.tester.FileManager;
import btj.core.tester.Ticket;
import btj.core.tester.TradeResult;
import btj.core.util.MathUtil;


public class DealTally implements Tally {
	/**
	 * SDFはスレッドアンセーフのためインスタンスごとに実体化する必要がある
	 * また、Serializableの転送対象にするとvmcid:201エラーが発生するので除外する。
	 * 上記を踏まえ、readObject()およびclone(実行の位置・順番も配慮)での明示的な初期化が必要。
	 */

	//外部パラメータ
	/**
	 * スリッページ
	 */
	private double slippage;
	/**
	 * レバレッジ 綴りを確認すること
	 */
	private double leverage;
	/**
	 * 手数料
	 */
	private double cost;
	/**
	 * 集計対象のStrategy名(null指定でストラテジを区別せず集計)
	 */
	private String strategyName;
	/**
	 * 集計対象のDataSet名(銘柄コード、null指定で銘柄を区別せず集計)
	 */
	private String dsName;
	/**
	 * 集計ファイル名のプリフィクス
	 */
	private String prefix;
	/**
	 * 集計開始日
	 */
	private String startDate;
	/**
	 * 集計終了日
	 */
	private String endDate;
	/**
	 * 集計の補足説明
	 */
	private String detail;
	/**
	 * このクラスのバージョン情報
	 */
	private static final String version = "DealTally Version 0101";

	private double netProfit;
	private double maxNetProfit;
	private double drawDown;

	/**
	 * トレードごとのDealDataを格納するリスト
	 */
	private List<DealData> dealList;

	/**
	 * 1日ごとの集計結果を格納するリスト
	 */
	private DayQueue queue;

	/**
	 * 現在の集計日付
	 */
	private DateWrapper dateOffset;



	/**
	 * 初期コードのためのコンストラクタ
	 * @param slippage
	 * @param leverage
	 * @param cost
	 * @param strategyName
	 * @param dsName
	 */
	public DealTally(double slippage, double leverage, double cost,
			String strategyName, String dsName) throws ParseException{
		this(strategyName, slippage, leverage, cost, strategyName, dsName, null, null, null);
	}

	/**
	 * 通常はこちらのコンストラクタを使用する
	 * @param prefix
	 * @param slippage
	 * @param leverage
	 * @param cost
	 * @param strategyName
	 * @param dsName
	 * @param startDate
	 * @param endDate
	 * @param detail
	 * @throws ParseException
	 */
	public DealTally(String prefix, double slippage, double leverage, double cost,
			String strategyName, String dsName, String startDate,
			String endDate, String detail) throws ParseException {
		super();
		this.prefix = prefix;
		this.slippage = slippage;
		this.leverage = leverage;
		this.cost = cost;
		this.strategyName = strategyName;
		this.dsName = dsName;
		this.dealList = new ArrayList();
		this.startDate = startDate;
		this.endDate = endDate;
		this.detail = detail;
		this.dateOffset = new DateWrapper(startDate);
		this.queue = new DayQueue(dateOffset);
	}


	public void result(Ticket[] ticketList) {
		if(ticketList != null){
			for(int i=0; i<ticketList.length; i++){
				if((strategyName == null ||
					strategyName.equals(ticketList[i].getStrategyName()))
					&&
					(dsName == null ||
					dsName.equals(ticketList[i].getDsName()))){
					tally(ticketList[i]);
				}
			}
		}
	}

	private void tally(Ticket ticket){
		double profit;

		if(ticket.result.isExpired){
			profit = 0.0;
		}else{
			profit = ticket.getProfit();
			//System.out.println(profit);

			if(leverage > 0.0){
				profit *= leverage;
			}

			if(cost > 0.0){
				profit -= cost;
			}

			//isExpiredは1日単位集計の対象外
			queue.push(ticket);
		}

		netProfit += profit;
		maxNetProfit = MathUtil.max(netProfit, maxNetProfit);
		drawDown =  netProfit - maxNetProfit;

		DealData dd = new DealData();
		dd.result = ticket.result;
		dd.profit = profit;
		dd.netProfit = netProfit;
		dd.maxNetProfit = maxNetProfit;
		dd.drawDown = drawDown;
		dealList.add(dd);
	}

	public void dump(FileManager fileMng) throws IOException{
		String name = prefix + "_dealtally.csv";
		PrintWriter pw = fileMng.getSubDir(name);
		pw.println(version);
		pw.println(verificationInfo());
		pw.println("");
		pw.println(toCSVHeader());

		//queueにはまだ最終日付の集計情報がバッファに残っている
		queue.compaction();

		Iterator ite = dealList.iterator();
		Iterator queIte = queue.getList().iterator();
		//1日単位集計よりもトレード単位集計の方が多い
		StringBuffer sb = new StringBuffer();
		while(ite.hasNext()){
			sb.append(((DealData)ite.next()).toCSV());

			if(queIte.hasNext()){
				sb.append(",");
				sb.append(((DayData)queIte.next()).toCSV());
			}

			pw.println(sb.toString());
			sb.delete(0, sb.length());
		}
		pw.flush();
		pw.close();
	}

	private String verificationInfo(){
		StringBuffer sb = new StringBuffer();
		sb.append("Verification Name,");
		sb.append(prefix);
		sb.append(",StartDate,");
		sb.append(startDate);
		sb.append(",EndDate,");
		sb.append(endDate);
		sb.append(",Today,");
		SimpleDateFormat DF_YMDHMS_SL
		= new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		sb.append(DF_YMDHMS_SL.format(new Date()));
		sb.append(",Slippage,");
		sb.append(slippage);
		sb.append(",Leverage,");
		sb.append(leverage);
		sb.append(",Cost,");
		sb.append(cost);
		sb.append(",Detail,");
		sb.append(detail != null ? detail : "");
		return sb.toString();
	}

	public static String toCSVHeader(){
		StringBuffer sb = new StringBuffer();
		sb.append(TradeResult.toCSVHeader());
		sb.append(",");
		sb.append("profit");
		sb.append(",");
		sb.append("netProfit");
		sb.append(",");
		sb.append("drawDown");
		sb.append(",");
		sb.append("maxNetProfit");
		sb.append(",");
		sb.append("perDay");
		sb.append(",");
		sb.append("perDayLots");
		sb.append(",");
		sb.append("perDayProfit");
		sb.append(",");
		sb.append("perDayNetProfit");
		sb.append(",");
		sb.append("perDayDrawDown");
		sb.append(",");
		sb.append("perDayMaxNetProfit");
		return sb.toString();
	}

	public Tally clone(){
		DealTally ret = null;
		try {
			ret = (DealTally)super.clone();
			ret.dealList = new ArrayList();
			ret.dateOffset = new DateWrapper(ret.startDate);
			ret.queue = new DayQueue(ret.dateOffset);
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return ret;
	}

	public void setStrategyName(String name){
		this.prefix = name;
	}

	private void writeObject(ObjectOutputStream out) throws IOException{
		out.defaultWriteObject();
	}

	private void readObject(ObjectInputStream in)
            throws IOException, ClassNotFoundException {
		in.defaultReadObject();
	}

	/**
	 * TradeResultのラッパークラス
	 */
	class DealData implements Serializable{
		TradeResult result;
		double profit;
		double netProfit;
		double maxNetProfit;
		double drawDown;

		public String toCSV(){
			StringBuffer sb = new StringBuffer();
			sb.append(result.toCSV());
			sb.append(",");
			sb.append(profit);
			sb.append(",");
			sb.append(netProfit);
			sb.append(",");
			sb.append(drawDown);
			sb.append(",");
			sb.append(maxNetProfit);
			return sb.toString();
		}
	}

	/**
	 * 1日単位で集計するクラス
	 * 新しい日のTicketをpushされるごとに1エントリ追加する。
	 */
	class DayQueue implements Serializable, Cloneable{
		List queueList;
		Date currentDate;
		double profit;
		double lots;
		double netProfit;
		double maxNetProfit;
		double drawDown;
		DateWrapper dateOffset;

		DayQueue(DateWrapper dateOffset){
			this.queueList = new ArrayList();
			this.netProfit = 0.0;
			this.maxNetProfit = 0.0;
			this.profit = 0.0;
			this.lots = 0.0;
			this.dateOffset = dateOffset;
			this.currentDate = dateOffset.getDate();
		}

		public void push(Ticket ticket){
//			if(currentDate == null){
//				//currentの状態を初期化
//				profit = 0.0;
//				lots = 0.0;
//				currentDate = ticket.result.closeTime;
			long counter = dateOffset.getTime();
			for(;counter < ticket.result.closeTime.getTime();
							counter += DateWrapper.ONE_DAY_MSEC){
				padding();
				currentDate = dateOffset.nextDate();
			}

			//System.out.println("currentDate=" + currentDate + " closeTime=" + ticket.result.closeTime);
			SimpleDateFormat DF_YMD_SL
			= new SimpleDateFormat("yyyy/MM/dd");
			if(DF_YMD_SL.format(currentDate).equals(
						DF_YMD_SL.format(ticket.result.closeTime))){
				//ignore
			}else{
				//日付が更新された場合
				//更新前の状態でNetProfitとドローダウンを算出する
				maxNetProfit = MathUtil.max(netProfit, maxNetProfit);
				drawDown =  netProfit - maxNetProfit;

				DayData dd = new DayData();
				dd.date = DF_YMD_SL.format(currentDate);
				dd.lots = lots;
				dd.profit = profit;
				dd.netProfit = netProfit;
				dd.maxNetProfit = maxNetProfit;
				dd.drawDown = drawDown;
				queueList.add(dd);

				//currentの状態を初期化
				profit = 0.0;
				lots = 0.0;
				currentDate = ticket.result.closeTime;
			}

			double buf = ticket.getProfit();

			if(leverage > 0.0){
				buf *= leverage;
			}

			if(cost > 0.0){
				buf -= cost;
			}

			profit += buf;
			netProfit += buf;
			lots += ticket.result.lots;

		}

		/**
		 * 最後のエントリをリストに詰める
		 */
		public void padding(){
			DayData dd = new DayData();
			SimpleDateFormat DF_YMD_SL
			= new SimpleDateFormat("yyyy/MM/dd");
			dd.date = DF_YMD_SL.format(currentDate);
			dd.lots = 0;
			dd.profit = 0;
			dd.netProfit = netProfit;
			dd.maxNetProfit = maxNetProfit;
			dd.drawDown = drawDown;
			queueList.add(dd);
		}

		/**
		 * 最後のエントリをリストに詰める
		 */
		public void compaction(){
			DayData dd = new DayData();
			SimpleDateFormat DF_YMD_SL
			= new SimpleDateFormat("yyyy/MM/dd");

			dd.date = DF_YMD_SL.format(currentDate);
			dd.lots = lots;
			dd.profit = profit;
			dd.netProfit = netProfit;
			dd.maxNetProfit = maxNetProfit;
			dd.drawDown = drawDown;
			queueList.add(dd);
		}

		public List getList(){
			return queueList;
		}

		public DayQueue clone(){
			DayQueue ret = null;
			try {
				ret = (DayQueue)super.clone();
				ret.queueList = new ArrayList();
				ret.currentDate = ret.dateOffset.getDate();
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
			return ret;
		}
	}

	class DayData implements Serializable{
		String date;
		double profit;
		double lots;
		double netProfit;
		double maxNetProfit;
		double drawDown;

		public String toCSV(){
			StringBuffer sb = new StringBuffer();
			sb.append(date);
			sb.append(",");
			sb.append(lots);
			sb.append(",");
			sb.append(profit);
			sb.append(",");
			sb.append(netProfit);
			sb.append(",");
			sb.append(drawDown);
			sb.append(",");
			sb.append(maxNetProfit);
			return sb.toString();
		}
	}

	/**
	 * 現在の集計日付を表すクラス
	 * @author kenei
	 *
	 */
	class DateWrapper implements Serializable{
		private long currentDate;
		public static final long ONE_DAY_MSEC = 1000 * 60 * 60 * 24;;

		DateWrapper(String dateStr) throws ParseException{
			SimpleDateFormat DF_YMDHMS_SL
			= new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			currentDate = DF_YMDHMS_SL.parse(dateStr).getTime();
		}

		/**
		 * 日付を設定する
		 * @param value 日付
		 */
		void setDate(Date value){
			currentDate = value.getTime();
		}

		/**
		 * 翌日をリターンする
		 * @return 翌日
		 */
		Date nextDate(){
			currentDate += ONE_DAY_MSEC;
			return new Date(currentDate);
		}

		/**
		 *
		 * @return 現在の日付
		 */
		Date getDate(){
			return new Date(currentDate);
		}

		/**
		 *
		 * @return 日付を表すミリ秒値
		 */
		long getTime(){
			return currentDate;
		}

	}

}
