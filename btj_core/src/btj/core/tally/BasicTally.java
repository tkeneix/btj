package btj.core.tally;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import btj.core.tally.DealTally.DealData;
import btj.core.tester.FileManager;
import btj.core.tester.Ticket;
import btj.core.util.DoubleArray;
import btj.core.util.MathUtil;
import btj.core.util.TimeUtil;


public class BasicTally implements Tally {
	/**
	 * SDFはスレッドアンセーフのためインスタンスごとに実体化する必要がある
	 * また、Serializableの転送対象にするとvmcid:201エラーが発生するので除外する。
	 * 上記を踏まえ、readObject()およびclone(実行の位置・順番も配慮)での明示的な初期化が必要。
	 */
	public transient SimpleDateFormat DF_YMDHMS_SL
	= new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

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
	private String targetStrategyName;
	/**
	 * ストラテジの名前
	 */
	private String strategyName;
	/**
	 * 集計対象のDataSet名(銘柄コード、null指定で銘柄を区別せず集計)
	 */
	private String dsName;
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
	 * 集計ファイル名のプリフィクス
	 */
	private String prefix;
	/**
	 * 検証日数
	 */
	private long period;


	//内部パラメータ
	/**
	 * このクラスのバージョン情報
	 */
	private static final String version = "BasicTally Version 0100";
	/**
	 * 検証日時
	 */
	private String today;
	/**
	 * 勝ちトレード数
	 */
	private double winTradeCount;
	/**
	 * 負けトレード数
	 */
	private double loseTradeCount;
	/**
	 * 勝ちトレード総損益
	 */
	private double winTradeValue;
	/**
	 * 負けトレード総損益
	 */
	private double loseTradeValue;
	/**
	 * 最大勝ちトレード損益
	 */
	private double maxWinTradeValue;
	/**
	 * 最大負けトレード損益
	 */
	private double maxLoseTradeValue;
	/**
	 * 最大連続勝ちトレード数
	 */
	private double maxWinContCount;
	/**
	 * 現在の連続勝ちトレード数
	 */
	private double winContCount;
	/**
	 * 最大連続負けトレード数
	 */
	private double maxLoseContCount;
	/**
	 * 現在の連続負けトレード数
	 */
	private double loseContCount;
	/**
	 * 最大総損益
	 */
	private double maxNetProfit;
	/**
	 * 現在の総損益
	 */
	private double netProfit;
	/**
	 * 最大ドローダウン
	 */
	private double maxDrawDown;
	/**
	 * 現在のドローダウン
	 */
	private double drawDown;
	/**
	 * トレード率(1日あたりのトレード数比率)
	 */
	private double tradeRatio;
	/**
	 * 勝率
	 */
	private double shouritsu;
	/**
	 * 期限切れカウント
	 */
	private long expiredCount;

	/**
	 * 総トレード数
	 * (設計注)
	 * Ticket対TradeResultを1対n化する場合は、総トレード数を
	 * チケット単位とするのか、ポジション単位とするのか検討すること。
	 */
	private double totalTradeCount;
	private double profitFactor;
	private double winAverageValue;
	private double loseAverageValue;
	private double expectedRatio;

	private boolean isCalc;

	/**
	 * 損益リスト
	 */
	private DoubleArray profitList;
	private DoubleArray winTradeList;
	private DoubleArray loseTradeList;
	private DoubleArray netProfitList;
	private Map<String, SliceInfo> yearsListMap;			//key=年文字列, value=DoubleArray
	private Map<String, SliceInfo> monthListMap;			//key=月文字列, value=DoubleArray
	private Map<String, SliceInfo> weekdayListMap;			//key=曜日文字列, value=DoubleArray

	public BasicTally(double slippage, double leverage, double cost,
			String strategyName, String dsName, String startDate,
			String endDate, String detail) {
		this(strategyName, slippage, leverage, cost, strategyName, strategyName, dsName, startDate, endDate, 0, detail);
	}

	public BasicTally(String prefix, double slippage, double leverage, double cost,
			String targetStrategyName, String strategyName, String dsName, String startDate,
			String endDate, long period, String detail) {
		super();
		this.prefix = prefix;
		this.slippage = slippage;
		this.leverage = leverage;
		this.cost = cost;
		this.targetStrategyName = targetStrategyName;
		this.dsName = dsName;
		this.startDate = startDate;
		this.endDate = endDate;
		this.period = period;
		this.detail = detail;
		this.strategyName = strategyName;

		int initialCapacity = 50;
		if(period != 0){
			initialCapacity = (int)period;
		}
		this.profitList = new DoubleArray(initialCapacity);
		this.winTradeList = new DoubleArray(initialCapacity);
		this.loseTradeList = new DoubleArray(initialCapacity);
		this.netProfitList = new DoubleArray(initialCapacity);

		this.yearsListMap = new TreeMap(new IntegerComparator());
		this.monthListMap = new TreeMap(new IntegerComparator());
		this.weekdayListMap = new TreeMap(new WeekdayComparator());
		parametersInit();
	}

	private void parametersInit(){
		today = DF_YMDHMS_SL.format(new Date());
		winTradeCount = 0.0;
		loseTradeCount = 0.0;
		winTradeValue = 0.0;
		loseTradeValue = 0.0;
		maxWinTradeValue = 0.0;
		maxLoseTradeValue = 0.0;
		maxWinContCount = 0.0;
		winContCount = 0.0;
		maxLoseContCount = 0.0;
		loseContCount = 0.0;
		maxNetProfit = 0.0;
		netProfit = 0.0;
		maxDrawDown = 0.0;
		drawDown = 0.0;
		expiredCount = 0;
	}

	public void result(Ticket[] ticketList) {
		if(ticketList != null){
			for(int i=0; i<ticketList.length; i++){
				if((targetStrategyName == null ||
					targetStrategyName.equals(ticketList[i].getStrategyName()))
					&&
					(dsName == null ||
					dsName.equals(ticketList[i].getDsName()))){
					tally(ticketList[i]);
				}
			}
		}
	}

	private void tally(Ticket ticket){
		//有効期限切れは集計対象外とする。
		if(ticket.result.isExpired){
			expiredCount++;
			return;
		}

		double profit = ticket.getProfit();

		if(leverage > 0.0){
			profit *= leverage;
		}

		if(cost > 0.0){
			profit -= cost;
		}
		//System.out.println(DT_FORMAT.format(ticket.result.openTime) + "," + profit);

		if(profit > 0){
			//勝ちトレードの場合
			winTradeCount++;
			winTradeValue += profit;
			maxWinTradeValue = MathUtil.max(profit, maxWinTradeValue);
			winContCount++;
			winTradeList.add(profit);

			//連続負けトレード数をリセットする
			loseContCount = 0;
		}else{
			//負けトレードの場合
			loseTradeCount++;
			loseTradeValue += profit;
			maxLoseTradeValue = MathUtil.min(profit, maxLoseTradeValue);
			loseContCount++;
			loseTradeList.add(profit);

			//連続勝ちトレード数をリセットする
			winContCount = 0;
		}

		//勝ち負けに関係なく集計する
		netProfit += profit;
		maxNetProfit = MathUtil.max(netProfit, maxNetProfit);
		drawDown =  netProfit - maxNetProfit;
		maxDrawDown = MathUtil.min(drawDown, maxDrawDown);
		maxWinContCount = MathUtil.max(winContCount, maxWinContCount);
		maxLoseContCount = MathUtil.max(loseContCount, maxLoseContCount);
		profitList.add(profit);
		netProfitList.add(netProfit);
		calcPerYears(ticket.result.orderTime, profit);
		calcPerMonth(ticket.result.orderTime, profit);
		calcPerWeekday(ticket.result.orderTime, profit);
	}

	/**
	 * 年単位の損益リストを返す
	 * @param targetDate
	 * @return DoubleArrayクラス（nullは返さない）
	 */
	private void calcPerYears(Date targetDate, double profit){
		SliceInfo ret = null;
		String key = String.valueOf(TimeUtil.calendarGet(targetDate, Calendar.YEAR));
		//System.out.println(targetDate + " " + key);
		ret = (SliceInfo)yearsListMap.get(key);
		if(ret == null){
			ret = new SliceInfo(key);
			yearsListMap.put(key, ret);
			//System.out.println(targetDate + " " + key + " " + yearsListMap.size());
		}
		ret.profitList.add(profit);
		double currentNetProfit;
		if(ret.netProfitList.length() > 0){
			//2回目以降
			currentNetProfit = ret.netProfitList.get(ret.netProfitList.length() - 1) + profit;
		}else{
			//初回
			currentNetProfit = profit;
		}
		ret.netProfitList.add(currentNetProfit);
	}

	private void calcPerMonth(Date targetDate, double profit){
		SliceInfo ret = null;
		//Januaryで0なので+1する
		String key = String.valueOf(TimeUtil.calendarGet(targetDate, Calendar.MONTH) + 1);
		//System.out.println(targetDate + " " + key);
		ret = (SliceInfo)monthListMap.get(key);
		if(ret == null){
			ret = new SliceInfo(key);
			monthListMap.put(key, ret);
		}
		ret.profitList.add(profit);
		double currentNetProfit;
		if(ret.netProfitList.length() > 0){
			//2回目以降
			currentNetProfit = ret.netProfitList.get(ret.netProfitList.length() - 1) + profit;
		}else{
			//初回
			currentNetProfit = profit;
		}
		ret.netProfitList.add(currentNetProfit);
	}

	private void calcPerWeekday(Date targetDate, double profit){
		SliceInfo ret = null;
		//Mondayは2
		String key = TimeUtil.getWeekdayString(TimeUtil.calendarGet(targetDate, Calendar.DAY_OF_WEEK));
		//System.out.println(targetDate + " " + key);
		ret = (SliceInfo)weekdayListMap.get(key);
		if(ret == null){
			ret = new SliceInfo(key);
			weekdayListMap.put(key, ret);
		}
		ret.profitList.add(profit);
		double currentNetProfit;
		if(ret.netProfitList.length() > 0){
			//2回目以降
			currentNetProfit = ret.netProfitList.get(ret.netProfitList.length() - 1) + profit;
		}else{
			//初回
			currentNetProfit = profit;
		}
		ret.netProfitList.add(currentNetProfit);
	}

	public Tally clone(){
		BasicTally ret = null;
		try {
			ret = (BasicTally)super.clone();
			ret.DF_YMDHMS_SL
			= new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

			ret.profitList = new DoubleArray(50);
			ret.winTradeList = new DoubleArray(50);
			ret.loseTradeList = new DoubleArray(50);
			ret.netProfitList = new DoubleArray(50);

			ret.yearsListMap = new TreeMap(new IntegerComparator());
			ret.monthListMap = new TreeMap(new IntegerComparator());
			ret.weekdayListMap = new TreeMap(new WeekdayComparator());

			ret.today = ret.DF_YMDHMS_SL.format(new Date());
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return ret;
	}

	public void setStrategyName(String name){
		this.strategyName = name;
	}

	private void writeObject(ObjectOutputStream out) throws IOException{
		out.defaultWriteObject();
	}

	private void readObject(ObjectInputStream in)
            throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		DF_YMDHMS_SL
		= new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	}

	private class SliceInfo implements Serializable{
		String name;
		DoubleArray profitList;
		DoubleArray netProfitList;

		public SliceInfo(String name){
			this.name = name;
			this.profitList = new DoubleArray(250);
			this.netProfitList = new DoubleArray(250);
		}
	}

	private class IntegerComparator implements Comparator, Serializable{

		public int compare(Object o1, Object o2) {
			int i1 = Integer.parseInt((String)o1);
			int i2 = Integer.parseInt((String)o2);

			//逆順にソートする
			int ret = 0;
			if(i1 < i2){
				ret = -1;
			}else if(i1 > i2){
				ret = 1;
			}
			return ret;
		}
	}


	private class WeekdayComparator implements Comparator, Serializable{
		Map<String, Integer> weekdayNumMap;

		public WeekdayComparator(){
			weekdayNumMap = new HashMap();
			weekdayNumMap.put("Mon", 1);
			weekdayNumMap.put("Tue", 2);
			weekdayNumMap.put("Wed", 3);
			weekdayNumMap.put("Thu", 4);
			weekdayNumMap.put("Fri", 5);
			weekdayNumMap.put("Sat", 6);
			weekdayNumMap.put("Sun", 7);
		}

		public int compare(Object o1, Object o2) {
			/*
			if(o1 == null || o2 == null
					||!( o1 instanceof String)
					||!( o2 instanceof String)){
				System.err.println(o1 + " " + o2);
				return -1;
			}
			*/

			String str1 = (String)o1;
			String str2 = (String)o2;
			int i1 = weekdayNumMap.get(str1);
			int i2 = weekdayNumMap.get(str2);

			//逆順にソートする
			int ret = 0;
			if(i1 < i2){
				ret = -1;
			}else if(i1 > i2){
				ret = 1;
			}
			return ret;
		}

	}

	private void calc(){
		if(isCalc) return;
		totalTradeCount = winTradeCount + loseTradeCount;
		shouritsu = (totalTradeCount != 0.0) ? winTradeCount/totalTradeCount : 0.0;
		profitFactor = (loseTradeValue != 0.0) ? Math.abs(winTradeValue/loseTradeValue) : 0.0;
		winAverageValue = (winTradeCount != 0.0) ? winTradeValue/winTradeCount: 0.0;
		loseAverageValue = (loseTradeCount != 0.0) ? loseTradeValue/loseTradeCount: 0.0;
		expectedRatio = (loseAverageValue != 0.0) ? (shouritsu * winAverageValue + (1-shouritsu)*loseAverageValue) : 0.0;
		tradeRatio = (period != 0.0) ? totalTradeCount/period : 0.0;
		isCalc = true;
	}

	private String verificationInfo(){
		StringBuffer sb = new StringBuffer();
		sb.append("Verification Name,");
		sb.append(prefix);
		sb.append(",DSname,");
		sb.append(dsName);
		sb.append(",StartDate,");
		sb.append(startDate);
		sb.append(",EndDate,");
		sb.append(endDate);
		sb.append(",DayPeriod,");
		sb.append(period);
		sb.append(",Today,");
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

	public String toCSVHeader(){
		StringBuffer sb = new StringBuffer();
		sb.append(verificationInfo());
		sb.append("\n");
		sb.append("ストラテジ名");
		sb.append(",");
		sb.append("勝率");
		sb.append(",");
		sb.append("トレード総数");
		sb.append(",");
		sb.append("損益平均");
		sb.append(",");
		sb.append("損益偏差");
		sb.append(",");
		sb.append("平均／偏差");
		sb.append(",");
		sb.append("累積CORREL");
		sb.append(",");
		sb.append("累積STEYX");
		sb.append(",");
		sb.append("トレード率");
		sb.append(",");
		sb.append("勝ちトレード数");
		sb.append(",");
		sb.append("負けトレード数");
		sb.append(",");
		sb.append("利益");
		sb.append(",");
		sb.append("損失");
		sb.append(",");
		sb.append("総損益");
		sb.append(",");
		sb.append("最大損益");
		sb.append(",");
		sb.append("最大ドローダウン");
		sb.append(",");
		sb.append("プロフィットファクター");
		sb.append(",");
		sb.append("利益平均");
		sb.append(",");
		sb.append("損失平均");
		sb.append(",");
		sb.append("利益偏差");
		sb.append(",");
		sb.append("損失偏差");
		sb.append(",");
		sb.append("期待値");
		sb.append(",");
		sb.append("最大利益");
		sb.append(",");
		sb.append("最大損失");
		sb.append(",");
		sb.append("最大連続勝ち数");
		sb.append(",");
		sb.append("最大連続負け数");
		sb.append(",");
		sb.append("期限切れカウント");
		sb.append(",");

		//期待値
		Iterator<String> ite = yearsListMap.keySet().iterator();
		while(ite.hasNext()){
			String key = ite.next();
			sb.append(key + "損益平均");
			sb.append(",");
		}
		//CORREL
		ite = yearsListMap.keySet().iterator();
		while(ite.hasNext()){
			String key = ite.next();
			sb.append(key + "CORREL");
			sb.append(",");
		}

		//期待値
		ite = monthListMap.keySet().iterator();
		while(ite.hasNext()){
			String key = ite.next();
			sb.append(key + "月損益平均");
			sb.append(",");
		}
		//CORREL
		ite = monthListMap.keySet().iterator();
		while(ite.hasNext()){
			String key = ite.next();
			sb.append(key + "月CORREL");
			sb.append(",");
		}

		//期待値
		ite = weekdayListMap.keySet().iterator();
		while(ite.hasNext()){
			String key = ite.next();
			sb.append(key + "損益平均");
			sb.append(",");
		}
		//CORREL
		ite = weekdayListMap.keySet().iterator();
		while(ite.hasNext()){
			String key = ite.next();
			sb.append(key + "CORREL");
			sb.append(",");
		}

		sb.append("プラスカウント");
		sb.append(",");
		sb.append("マイナスカウント");
		sb.append(",");

		return sb.toString();
	}

	public String toCSV(){
		calc();
		StringBuffer sb = new StringBuffer();
		sb.append(strategyName);
		sb.append(",");
		sb.append(shouritsu);
		sb.append(",");
		sb.append(totalTradeCount);
		sb.append(",");
		double average, stdev, av_st, correl, steyx;
		average = MathUtil.average(profitList.getArray(), profitList.length());
		sb.append(average);
		sb.append(",");
		stdev = MathUtil.stdev(profitList.getArray(), profitList.length());
		sb.append(stdev);
		sb.append(",");
		av_st = 0.0;
		if(average != 0.0){
			av_st = average/stdev;
		}
		sb.append(av_st);
		sb.append(",");
		correl = MathUtil.correl(netProfitList.getArraySequence(),
				netProfitList.getArray(), netProfitList.length());
		sb.append(correl);
		sb.append(",");
		steyx = 0.0;
		sb.append(steyx);
		sb.append(",");
		sb.append(tradeRatio);
		sb.append(",");
		sb.append(winTradeCount);
		sb.append(",");
		sb.append(loseTradeCount);
		sb.append(",");
		sb.append(winTradeValue);
		sb.append(",");
		sb.append(loseTradeValue);
		sb.append(",");
		sb.append(netProfit);
		sb.append(",");
		sb.append(maxNetProfit);
		sb.append(",");
		sb.append(maxDrawDown);
		sb.append(",");
		sb.append(profitFactor);
		sb.append(",");
		sb.append(winAverageValue);
		sb.append(",");
		sb.append(loseAverageValue);
		sb.append(",");
		double winStdevValue, loseStdevValue;
		winStdevValue = MathUtil.stdev(winTradeList.getArray(), winTradeList.length());
		sb.append(winStdevValue);
		sb.append(",");
		loseStdevValue = MathUtil.stdev(loseTradeList.getArray(), loseTradeList.length());
		sb.append(loseStdevValue);
		sb.append(",");
		sb.append(expectedRatio);
		sb.append(",");
		sb.append(maxWinTradeValue);
		sb.append(",");
		sb.append(maxLoseTradeValue);
		sb.append(",");
		sb.append(maxWinContCount);
		sb.append(",");
		sb.append(maxLoseContCount);
		sb.append(",");
		sb.append(expiredCount);
		sb.append(",");

		int plusCount = 0;
		int minusCount = 0;

		//年損益平均
		Iterator<String> ite = yearsListMap.keySet().iterator();
		while(ite.hasNext()){
			String key = ite.next();
			SliceInfo value = yearsListMap.get(key);
			double calcValue = MathUtil.average(value.profitList.getArray(), value.profitList.length());
			sb.append(calcValue);
			sb.append(",");
			if(calcValue > 0){
				plusCount++;
			}else{
				minusCount++;
			}
		}
		//年CORREL
		ite = yearsListMap.keySet().iterator();
		while(ite.hasNext()){
			String key = ite.next();
			SliceInfo value = yearsListMap.get(key);
			double calcValue = MathUtil.correl(value.netProfitList.getArraySequence(),
							value.netProfitList.getArray(), value.netProfitList.length());
			sb.append(calcValue);
			sb.append(",");
			if(calcValue > 0){
				plusCount++;
			}else{
				minusCount++;
			}
		}

		//月損益平均
		ite = monthListMap.keySet().iterator();
		while(ite.hasNext()){
			String key = ite.next();
			SliceInfo value = monthListMap.get(key);
			double calcValue = MathUtil.average(value.profitList.getArray(), value.profitList.length());
			sb.append(calcValue);
			sb.append(",");
			if(calcValue > 0){
				plusCount++;
			}else{
				minusCount++;
			}
		}
		//月CORREL
		ite = monthListMap.keySet().iterator();
		while(ite.hasNext()){
			String key = ite.next();
			SliceInfo value = monthListMap.get(key);
			double calcValue = MathUtil.correl(value.netProfitList.getArraySequence(),
					value.netProfitList.getArray(), value.netProfitList.length());
			sb.append(calcValue);
			sb.append(",");
			if(calcValue > 0){
				plusCount++;
			}else{
				minusCount++;
			}
		}

		//曜日損益平均
		ite = weekdayListMap.keySet().iterator();
		while(ite.hasNext()){
			String key = ite.next();
			SliceInfo value = weekdayListMap.get(key);
			double calcValue = MathUtil.average(value.profitList.getArray(), value.profitList.length());
			sb.append(calcValue);
			sb.append(",");
			if(calcValue > 0){
				plusCount++;
			}else{
				minusCount++;
			}
		}
		//曜日CORREL
		ite = weekdayListMap.keySet().iterator();
		while(ite.hasNext()){
			String key = ite.next();
			SliceInfo value = weekdayListMap.get(key);
			double calcValue = MathUtil.correl(value.netProfitList.getArraySequence(),
					value.netProfitList.getArray(), value.netProfitList.length());
			sb.append(calcValue);
			sb.append(",");
			if(calcValue > 0){
				plusCount++;
			}else{
				minusCount++;
			}
		}

		sb.append(plusCount);
		sb.append(",");
		sb.append(minusCount);
		sb.append(",");

		return sb.toString();
	}

	public void init(FileManager fileMng) throws IOException{
		String name = prefix + "_basictally.csv";
		PrintWriter pw = fileMng.getSubDir(name, true);
		pw.println(toCSVHeader());
		pw.flush();
	}

	public void dump(FileManager fileMng) throws IOException{
		String name = prefix + "_basictally.csv";

		if(!fileMng.isContained(name)){
			init(fileMng);
		}
		PrintWriter pw = fileMng.getSubDir(name, true);
		pw.println(toCSV());
		pw.flush();
		//closeはFileManager#shutdown()で実行する。
	}

	public double getExpectedRatio(){
		calc();
		return  expectedRatio;
	}
}
