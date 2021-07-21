/**
 * OptinalDataのマップを生成するクラス
 */

package btj.core.dataset;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import btj.core.util.DateFormat;
import btj.core.util.TimeUtil;



public class MapDataSetFactory implements DataSetFactory {
	private static final short HIT  = 0;
	private static final short MORE = 1;
	private static final short LESS = 2;
	private MapDataTarget[] target;
	private boolean isFirstEntry = true;
	private int skipEntryCount = 0;

	public MapDataSetFactory(int skipEntryCount, MapDataTarget[] target){
		this.target = target;
		this.skipEntryCount =skipEntryCount;
	}

	public DataSet create(DataSetFactoryArgs args) throws FileNotFoundException, IOException,
			ParseException {

		OptionalDataSetFactoryArgs opArgs = (OptionalDataSetFactoryArgs)args;
		String dsName = opArgs.getDsName();
		String fileName = opArgs.getMapDataFileName();
		Date startDate = opArgs.getStartDate();
		Date endDate = opArgs.getEndDate();
		Judgement judgement = opArgs.getJudgement();

		DataStream ds = new DataStream(fileName, startDate, endDate);
		ArrayList list = new ArrayList();
		while(true){
			Data d = ds.next();
			if(d != null){
				list.add(d);
			}else{
				break;
			}
		}

		return new MapDataSet(dsName, fileName, startDate, endDate,
					judgement, list);
	}

	class DataStream{
		BufferedReader br;
		boolean isAlwaysRanged;
		Date startDate;
		Date endDate;
		long count;
		MapData backSS;
		MapData beforeSS;

		public DataStream(String fileName) throws FileNotFoundException{
			this(fileName, null, null);
		}

		public DataStream(String fileName, Date startDate, Date endDate) throws FileNotFoundException{
			br = new BufferedReader(new FileReader(fileName));

			if(startDate == null && endDate == null){
				isAlwaysRanged = true;
			}else{
				this.startDate = startDate;
				this.endDate = endDate;
				isAlwaysRanged = false;
			}
			count = 1;
			beforeSS = null;
		}

		/**
		 * ,区切りのリストからMapDataインスタンスを作成する
		 * @param list
		 * @return
		 */
		public MapData create(String line) throws ParseException{
			Map table = new HashMap(target.length);
			boolean isAvailable = true;
			String[] splits = line.split(",");
			//1列目は日付情報
			Date date = DateFormat.DF_YMD_SL.parse(splits[0]);
			for(int i=0; i<target.length; i++){
				String key = target[i].getKeyName();
				double value = 0.0;
				try{
					value = Double.parseDouble(splits[target[i].getColumn()-1]);
				}catch(Exception ex){
					//予期しないデータが含まれてる場合
					isAvailable = false;
				}
				table.put(key, value);
			}
			MapData ret = new MapData(target, date, table);
			ret.setAvailable(isAvailable);
			return ret;
		}

		public MapData next() throws IOException, ParseException{
			MapData ss = null;
			if(backSS == null){
				if(isFirstEntry){
					for(int i=0; i<skipEntryCount; i++){
						br.readLine();
					}
					isFirstEntry = false;
				}
				String line = br.readLine();
				if(line != null){
					line = line.trim();
					ss = create(line);
					if(isRanged(ss.getDate()) == HIT){
						//ignore
					}else if(isRanged(ss.getDate()) == LESS){
						//再起呼び出しなのでスタックが溢れるかもしれない
						//ss = next();
						do{
							line = br.readLine();
							if(line != null){
								line = line.trim();
								ss = create(line);
							}
						}while(isRanged(ss.getDate()) == LESS);
					}else if(isRanged(ss.getDate()) == MORE){
						ss = null;
					}
				}
			}else{
				ss = backSS;
				backSS = null;
			}

			return ss;
		}

		public void back(MapData ss){
			backSS = ss;
		}

		private short isRanged(Date target){
			short ret = HIT;
			if(!isAlwaysRanged){
				if(startDate != null && target.compareTo(startDate) < 0){
					ret = LESS;
				}
				if(endDate != null && target.compareTo(endDate) > 0){
					ret = MORE;
				}
			}
			return ret;
		}
	}


}
