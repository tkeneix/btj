package btj.core.dataset;

import java.util.ArrayList;
import java.util.Date;


public class CandleDataSet extends DataSet{
	private ArrayList<Data> array;
	private int current;

	public CandleDataSet(String name, String filePath, Date startDate,
			Date endDate, Judgement judgement, ArrayList<Data> array) {
		super(name, filePath, startDate, endDate, judgement);
		this.array = array;
		current = 0;
	}

	public boolean hasNext(){
		return current >= length() ? false : true;
	}

	public Data next(){
		return array.get(current++);
	}

	public Data get(int num){
		Data ret = null;
		if(num < 0){
			ret = new CandleData();
			ret.setAvailable(false);
		}else{
			ret = array.get(num);
		}
		return ret;
	}

	public void add(Data value){
		array.add(value);
	}

	public void padding(int num, Data d){
		for(int j=0; j<num; j++){
			add(d);
		}
	}

	public int length(){
		return array.size();
	}

	public int offset(){
		return current;
	}

	public void clearOffset(){
		current = 0;
	}

	public void close(){
		if(array != null){
			array.clear();
		}
	}
}
