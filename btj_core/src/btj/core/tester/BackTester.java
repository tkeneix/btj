package btj.core.tester;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import btj.core.dataset.DataSetManager;
import btj.core.strategy.Strategy;
import btj.core.tally.Tally;


public class BackTester {
	public final SimpleDateFormat DF_YMDHMS_SL
	= new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

	private ArrayList _strategyList;
	private ArrayList _tallyList;
	private DataSetManager _dsMng;
	private boolean isDump;
	private NumberManager numberMng;
	private FileManager fileMng;

	public BackTester(ArrayList list, ArrayList list2, DataSetManager mng,
			boolean isDump) {
		super();
		_strategyList = list;
		_tallyList = list2;
		_dsMng = mng;
		this.isDump = isDump;
		this.numberMng = NumberManager.getInstance();
	}

	public BackTester(ArrayList strategyList, ArrayList tallyList, DataSetManager dsMng){
		this(strategyList, tallyList, dsMng, true);
	}

	public BackTester(DataSetManager dsMng){
		this(new ArrayList(), new ArrayList(), dsMng);
	}

	public BackTester(DataSetManager dsMng, boolean isDump){
		this(new ArrayList(), new ArrayList(), dsMng, isDump);
	}

	public void addStrategy(Strategy stg){
		_strategyList.add(stg);
	}

	public void addTally(Tally tal){
		_tallyList.add(tal);
	}

	public void setDataSetManager(DataSetManager dsMng){
		_dsMng = dsMng;
	}

	public FileManager getFileManager(){
		if(fileMng != null){
			fileMng = new FileManager("BTJ_LOG", this.numberMng.next());
		}
		return fileMng;
	}

	public void start(){
		//try{
			IOrderManager oMng = new OrderManager(fileMng, _dsMng);
			Iterator stIte = null;

			//ストラテジの初期化
			//init
			stIte = _strategyList.iterator();
			while(stIte.hasNext()){
				((Strategy)stIte.next()).init(_dsMng);
			}

			for(int num=0; true; num++){
				//System.out.println(num);
				if(!_dsMng.hasNum(num)){
					//DataSetManagerが保持するDataSetのうち
					//lengthが最小のDataSetの数に到達した場合は
					//検証を終了する
					break;
				}

				//ストラテジの実行
				//ready
				//寄前注文などはOrderManagerのTypeで識別できるので
				//ready実装を省略する

				//start
				stIte = _strategyList.iterator();
				while(stIte.hasNext()){
					//Strategyにも当日のデータ(未来のデータ)を渡す。
					//Strategy側で未来のデータは使用しないように考慮する。
					//将来的にはワーニングを出力できるとよい。
					((Strategy)stIte.next()).start(_dsMng, oMng, num);
				}

				//注文管理
				OrderCheckStatus status = oMng.nextCheck( num);
				if(status.getValue() == OrderCheckStatus.FINISHED){
					//DataSetの終端に到着した場合はBackTesterを終了する
					break;
				}

				//集計処理
				Iterator taIte = _tallyList.iterator();
				while(taIte.hasNext()){
					((Tally)taIte.next()).result(status.getClosedList());
				}
			}

			//ストラテジの終了処理
			//deinit
			stIte = _strategyList.iterator();
			while(stIte.hasNext()){
				((Strategy)stIte.next()).deinit(_dsMng, oMng);
			}

			if(isDump){
				oMng.dumpHistoricalTicketTable();
			}

			if(fileMng != null) dump(fileMng);
		//}catch(Exception ex){
		//	ex.printStackTrace();
		//}
	}

	private void dump(FileManager fileMng){
		try{
			PrintWriter pw = fileMng.getRootDir("backtesterlog.csv", true);
			StringBuffer sb = new StringBuffer();
			sb.append(DF_YMDHMS_SL.format(new Date()));
			sb.append(",");
			sb.append(numberMng.getCurrentNumber());
			sb.append(",");

			Iterator stIte = _strategyList.iterator();
			while(stIte.hasNext()){
				sb.append(((Strategy)stIte.next()).getName());
				sb.append(";");
			}

			sb.append(",");

			Iterator dsmIte = _dsMng.getMap().values().iterator();
			while(dsmIte.hasNext()){
				sb.append(getFullPackToClass(dsmIte.next()));
				sb.append(";");
			}

			sb.append(",");

			Iterator taIte = _tallyList.iterator();
			while(taIte.hasNext()){
				sb.append(getFullPackToClass(taIte.next()));
				sb.append(";");
			}

			pw.println(sb.toString());
			pw.flush();
			pw.close();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	private String getFullPackToClass(Object obj){
		String clsName = obj.getClass().getName();
		return clsName.substring(clsName.lastIndexOf(".") + 1);
	}
}
