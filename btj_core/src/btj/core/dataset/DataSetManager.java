package btj.core.dataset;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import btj.core.util.MathUtil;



public class DataSetManager {
	private Map<String, DataSet> labelDataSets;
	private Properties spreadMap;
	private Properties leverageMap;
	/**
	 * 足の本数を最小に合わせるための変数だが
	 * 実際には足の本数を合わせないことにはサヤ取りや
	 * 並列トレードの検証は行えない。そのため、実質的には意味のない変数。
	 */
	private int minEndNum;

	public DataSetManager() {
		super();
		this.labelDataSets = new HashMap<String, DataSet>();
		this.minEndNum = Integer.MAX_VALUE;
	}

	public DataSet getLabelDataset(DataSet set) {
		return (DataSet)(labelDataSets.get(set.getName()));
	}

	/**
	 * ラベル名を指定して登録されているDataSetを返します。
	 * 登録されていない場合はnullを返します。
	 * @param label
	 * @return
	 */
	public DataSet getLabelDataset(String label) {
		return (DataSet)(labelDataSets.get(label));
	}

	/**
	 * DataSetクラスをDataSetManagerに登録します。
	 * keyはDataSet#getName()の戻り値を使用します。
	 * (設計注)
	 * ラベル名の指定ミスが生じないように、あえて登録時に
	 * ラベル名を指定させないようにする。getとaddで対象性が
	 * 無いのは目をつむろう。
	 * @param set DataSetクラス
	 */
	public void addLabelDataset(DataSet set) {
		minEndNum = MathUtil.min(set.length(), minEndNum);
		labelDataSets.put(set.getName(), set);
		Judgement judge = set.getJudgement();
		if(judge != null){
			//BackTesterの場合はnullでない
			judge.setDsMng(this);
		}
		//RealExecuterの場合はnull
	}

	public void removeLabelDataset(String label){
		DataSet ds = getLabelDataset(label);
		if(ds != null){
			labelDataSets.remove(label);
			ds.close();
		}
	}

	/**
	 * このDataSetManagerに登録されたDataSetのlengthのうち
	 * 最も小さい値を返します。BackTesterはこの値の数だけ
	 * 検証を繰り返します。
	 * @param num
	 * @return
	 */
	public boolean hasNum(int num){
		return num >= minEndNum ? false : true;
	}

	public Map getMap(){
		return labelDataSets;
	}

	public Properties getSpreadMap() {
		return spreadMap;
	}

	public void setSpreadMap(Properties spreadMap) {
		this.spreadMap = spreadMap;
	}
	public Properties getLeverageMap() {
		return leverageMap;
	}

	public void setLeverageMap(Properties leverageMap) {
		this.leverageMap = leverageMap;
	}

}
