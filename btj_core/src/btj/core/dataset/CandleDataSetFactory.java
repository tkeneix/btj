/**
 * 日経225day_MasterData.csv 形式の日足データを想定している
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
import java.util.Iterator;

import btj.core.util.DateFormat;

public class CandleDataSetFactory implements DataSetFactory {
	private SimpleDateFormat dateFormatYMD;

	public DataSet create(DataSetFactoryArgs args) throws FileNotFoundException, IOException,
			ParseException {

		CandleDataSetFactoryArgs cdArgs = (CandleDataSetFactoryArgs)args;
		String dsName = cdArgs.getDsName();
		String fileName = cdArgs.getDayTermFileName();
		String dateFormatStr = cdArgs.getDayTermDateFormat();
		if(dateFormatStr != null && dateFormatStr != ""){
			dateFormatYMD = new SimpleDateFormat(dateFormatStr);
		}else{
			dateFormatYMD = DateFormat.DF_YMD_SL;
		}

		Date startDate = cdArgs.getStartDate();
		Date endDate = cdArgs.getEndDate();
		Judgement judgement = cdArgs.getJudgement();

		DataStream ds = new DataStream(fileName, startDate, endDate);
		ArrayList list = new ArrayList();
		CandleData before = null;
		while(true){
			CandleData d = ds.next();
			if(d != null){
				if(before != null){
					//双方向リストを構築する
					before.setNext(d);
					d.setBack(before);
				}
				list.add(d);
			}else{
				break;
			}

			before = d;
		}
		return new CandleDataSet(dsName, fileName, startDate, endDate,
				judgement, list);
	}

	class DataStream{
		BufferedReader br;
		boolean isAlwaysRanged;
		Date startDate;
		Date endDate;
		long count;
		CandleData backSS;

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
		}

		/**
		 * ,区切りのリストからCandleDataインスタンスを作成する
		 * @param list
		 * @return
		 */
		public CandleData create(String line) throws ParseException{
			CandleData ret = new CandleData();
			String[] splits = line.split(",");
			if(splits.length == 6){
				ret.date = dateFormatYMD.parse(splits[0]);
				ret.open = Double.parseDouble(splits[1]);
				ret.high = Double.parseDouble(splits[2]);
				ret.low = Double.parseDouble(splits[3]);
				ret.close = Double.parseDouble(splits[4]);
				ret.volume = Double.parseDouble(splits[5]);
			}else if(splits.length == 5){
				//FXのデータはvolumeがない
				ret.date = dateFormatYMD.parse(splits[0]);
				ret.open = Double.parseDouble(splits[1]);
				ret.high = Double.parseDouble(splits[2]);
				ret.low = Double.parseDouble(splits[3]);
				ret.close = Double.parseDouble(splits[4]);
			}else{
				throw new RuntimeException("CandleDataのパースに失敗しました。 line="
						+ line);
			}
			return ret;
		}

		public CandleData next() throws IOException, ParseException{
			CandleData ss = null;
			if(backSS == null){
				String line = br.readLine();
				if(line != null){
					line = line.trim();
					ss = create(line);
					if(isRanged(ss.date)){
						//ignore
					}else{
						//再起呼び出しなのでスタックが溢れるかもしれない
						ss = next();
					}
				}
			}else{
				ss = backSS;
				backSS = null;
			}

			return ss;
		}

		public void back(CandleData ss){
			backSS = ss;
		}

		private boolean isRanged(Date target){
			boolean ret = true;
			if(!isAlwaysRanged){
				if(startDate != null && target.compareTo(startDate) < 0){
					ret = false;
				}
				if(endDate != null && target.compareTo(endDate) > 0){
					ret = false;
				}
			}
			return ret;
		}
	}


}
