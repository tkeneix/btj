/**
 * CandleDataとMapDataからOptionalDataを生成するクラス
 */

package btj.core.dataset;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import btj.core.util.DateFormat;


public class OptionalDataSetFactory{

	public DataSet create(DataSetFactoryArgs args) throws FileNotFoundException,
			IOException, ParseException {

		OptionalDataSetFactoryArgs opArgs = (OptionalDataSetFactoryArgs)args;
		String dsName = opArgs.getDsName();
		Date startDate = opArgs.getStartDate();
		Date endDate = opArgs.getEndDate();
		Judgement judgement = opArgs.getJudgement();
		String mapDataFileName = opArgs.getMapDataFileName();
		String dayTermFileName = opArgs.getDayTermFileName();
		int skipEntryCount = opArgs.getSkipEntryCount();
		MapDataTarget[] target = opArgs.getTarget();

		DataSetFactory sFactory = new MapDataSetFactory(skipEntryCount, target);
		MapDataSet mDataSet =  (MapDataSet) sFactory.create(args);
		DataSetFactory dFactory = new CandleDataSetFactory();
		CandleDataSet dDataSet =  (CandleDataSet) dFactory.create(args);

		int sCount = 0;
		OptionalData before = null;
		OptionalData optData = null;
		boolean isCorrect = false;
		ArrayList list = new ArrayList();
		//日足で大きく回す
		while(dDataSet.hasNext()){
			CandleData ddata = (CandleData)dDataSet.next();
			for(int j=sCount; j<mDataSet.length(); j++){
				MapData mdata = (MapData)mDataSet.get(j);
				//System.out.println(DateFormat.DF_YMDHMS_SL.format(opdata.getCandle().date) + "," + DateFormat.DF_YMDHMS_SL.format(sdata.daytime));

				//日足がmdata以前のデータの場合
				if(ddata.date.compareTo(mdata.getDate()) < 0){
					//System.out.println("スキップ=" + opdata.getCandle().date);
					break;
				}

				if(ddata.date.equals(mdata.getDate())){
					//日にちが合致した場合
					sCount = j+1;
					optData = new OptionalData(ddata, mdata.getTable());
					list.add(optData);
				}
			}
			if(before != null && optData != null){
				//OptionalDataの双方向リストを構築する
				before.setNext(optData);
				optData.setBack(before);
			}
			before = optData;
		}
		//offsetをクリアしてDataSetを使えるようにする
		mDataSet.close();
		dDataSet.close();
		return new OptionalDataSet(dsName, dayTermFileName, startDate, endDate, judgement, list);
	}

}
