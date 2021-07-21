/**
 * DoubleArrayクラスをラップするDataSet
 * get/putのデータ型に対象性はない。
 */

package btj.core.dataset;

import java.util.Date;

import btj.core.util.DoubleArray;


public class DoubleDataSet extends DataSet{
	private DoubleArray array;
	private int current;

	public DoubleDataSet(String name, String filePath, Date startDate,
			Date endDate, Judgement judgement, DoubleArray array) {
		super(name, filePath, startDate, endDate, judgement);
		// TODO 自動生成されたコンストラクター・スタブ
		this.array = array;
		current = 0;
	}

	public boolean hasNext(){
		return current >= length() ? false : true;
	}

	public Data next(){
		return new DoubleData(array.get(current++));
	}

	public Data get(int num){
		return new DoubleData(array.get(num));
	}

	public void add(double value){
		array.add(value);
	}

	public void padding(int num, double d){
		array.padding(num, d);
	}

	public int capacity(){
		return array.capacity();
	}

	public int length(){
		return array.length();
	}

	public int offset(){
		return current;
	}

	public void add(Data value) {
		//使用しない予定
	}

	public void padding(int num, Data d) {
		//使用しない予定
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
