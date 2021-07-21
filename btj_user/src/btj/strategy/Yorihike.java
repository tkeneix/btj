/**
 */
package btj.strategy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.Serializable;
import java.net.URL;
import java.util.Date;
import java.util.Hashtable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.naming.InitialContext;

import btj.core.log.DailySimpleLogger;

import btj.core.tester.FileManager;
import btj.core.tester.IOrderManager;
import btj.core.tester.NewOrder;
import btj.core.tester.Order;
import btj.core.tester.OrderManager;
import btj.core.dataset.CandleData;
import btj.core.dataset.CandleDataSet;
import btj.core.dataset.DataSetManager;
import btj.core.strategy.Strategy;
import btj.core.tally.BasicTally;
import btj.core.tally.DealTally;
import btj.core.util.DateFormat;
import btj.core.util.DoubleArray;
import btj.core.util.MathUtil;
import btj.core.util.Parameters;
import btj.core.util.RollingArray;
import btj.core.util.StrUtil;
import btj.common.Job;
import btj.common.JobPack;
import btj.service.BackTestService;
import btj.core.tester.Ticket;

public class Yorihike implements Strategy{
	private static final String verificationName = btj.strategy.Yorihike.class.getName();
	/**
	 * 戦略パラメータ
	 */
	public int maPeriod = 25;//for ma
	public double lots = 1.0;
	public String dsName = "nk225_d1";

	//システム用
	Map<String, Ticket> ticketMap = new HashMap<String, Ticket>();
	RollingArray maArray;
	double beforeMAValue = Double.MIN_VALUE;
	double beforeClose = Double.MIN_VALUE;
	Ticket ticket = null;

	//ログ
	transient DailySimpleLogger csvLogger;
	public Yorihike(DailySimpleLogger csvLogger){
		this.csvLogger = csvLogger;
	}

	public Yorihike(){
	}

	public void deinit(DataSetManager arg0, IOrderManager arg1) {
	}

	public String getName() {
		StringBuilder sb = new StringBuilder();
		sb.append(verificationName);
		sb.append("_");
		sb.append("mp-").append(StrUtil.paddingZeroFront(maPeriod,2));

		return sb.toString();
	}

	public void init(DataSetManager arg0) {
		maArray = new RollingArray(this.maPeriod);
	}

	public void ready(DataSetManager arg0, IOrderManager arg1, int arg2) {
	}

	public void start(DataSetManager dsMng, IOrderManager ordMng, int num) {

		//MA用データの蓄積
		CandleDataSet dataset = (CandleDataSet)dsMng.getLabelDataset(this.dsName);
		CandleData data = (CandleData)dataset.get(num);
		CandleData beforeData = data.get(2);
		if(beforeData != null){
			maArray.add(beforeData.close);
		}

		//バッファのチェック
		if(!maArray.isLap()){
			return;
		}

		//エントリー条件算出
		//買い：前日終値 > MA
		//売り：前日終値 < MA
		//*MAに前日終値は入れない
		double maValue = maArray.ave();

		if(this.beforeMAValue != Double.MIN_VALUE){
			if(beforeData.close > maValue
					&& this.beforeClose <= this.beforeMAValue){
				//買い
				if(ticket != null){
					ordMng.requestRepayOrder(ticket, Order.TP_NARI, 0);
				}
				ticket = ordMng.requestNewOrder(getName(), dsName, Order.TP_NARI,
						NewOrder.DT_BUY, lots, 0, Order.TP_IGNORE);
			}else if(beforeData.close < maValue
					&& this.beforeClose >= this.beforeMAValue){
				if(ticket != null){
					ordMng.requestRepayOrder(ticket, Order.TP_NARI, 0);
				}
				ticket = ordMng.requestNewOrder(getName(), dsName, Order.TP_NARI,
						NewOrder.DT_SELL, lots, 0, Order.TP_IGNORE);
			}
		}
		this.beforeMAValue = maValue;
		this.beforeClose = beforeData.close;
	}


	public static void main(String[] args){
		try{
			String startDate = "1988/09/03 00:00:00";
			String endDate = "2012/11/01 00:00:00";
			String serverName = args[0];

			Parameters params = new Parameters("btjclient.properties");
			String providerUrl = params.getString("providerUrl", "iiop://localhost:900");

			Hashtable env = new Hashtable();
			env.put("java.naming.factory.initial", "com.sun.jndi.cosnaming.CNCtxFactory");
			env.put("java.naming.provider.url", providerUrl);

			InitialContext initialNamingContext = new InitialContext(env);
			BackTestService service = (BackTestService)initialNamingContext.lookup(serverName);

			//集計用ディレクトリの作成
			FileManager fMng = new FileManager("./TALLY_LOG",
					verificationName + "_" + DateFormat.DF_YMDHMS_NO.format(new Date()));

			//総合設定
			DealTally tdt = new DealTally(null, 0.0, 0.0, 0.0, null, null,
					startDate, endDate, null);

			//サマリー集計設定
			BasicTally btt = new BasicTally(verificationName,
				0.0, 0, 0, null, null, null,startDate, endDate,
				0, null);

			JobPack pack = new JobPack();
			pack.setFileManager(fMng);
			pack.setTally(tdt);
			pack.setTally(btt);
			//pack.setPriority(JobPack.PRIORITY_LOW);
			pack.setPriority(JobPack.PRIORITY_HIGH);
			pack.setDsMngName("candle");

			String jarUrl = "file:./lib/btj_user.jar";
			String className = verificationName;
			for(int ma=2; ma<=60; ma+=1){
				//ストラテジの設定
				Map fieldMap = new HashMap();
				fieldMap.put("maPeriod", ma);
				Job job = new Job(jarUrl, className, fieldMap);
				pack.setJob(job);
			}

			Serializable ret = service.execute(pack);
			if(ret != null){
				System.out.println("OK");
			}

		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

}
