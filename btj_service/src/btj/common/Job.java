package btj.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import btj.core.tester.FileManager;
import btj.core.strategy.Strategy;
import btj.core.tally.Tally;

public class Job implements Serializable{
	/**
	 * クライアント側で設定する項目
	 */
	/**
	 * Jobが登録されているJobPackへの参照
	 */
	private JobPack parent;
	/**
	 * 当該Jobの完了ステータス
	 */
	private boolean isFinished;
	/**
	 * Strategyクラスが含まれるJarファイル名
	 */
	private String jarUrl;
	/**
	 * Strategyクラスの名前（フルパス）
	 */
	private String className;
	/**
	 * Strategyクラスのフィールド情報（名前＝値）
	 */
	private Map fieldMap;

	/**
	 * サービス側で設定する項目
	 */

	/**
	 * ストラテジのインスタンス
	 */
	private Strategy strategy;
	/**
	 * ファイルマネージャインスタンス（JobPackの参照を使いまわす）
	 */
	private FileManager fmng;
	/**
	 * Tallyインスタンス（cloneしたインスタンス）を登録する
	 */
	private List<Tally> tallyList;
	private long startTime;
	private long endTime;

	public Job(String jarUrl, String className, Map fieldMap){
		this.jarUrl = jarUrl;
		this.className = className;
		this.fieldMap = fieldMap;
		this.tallyList = new ArrayList<Tally>();
	}

	public void pack(){
		fmng = parent.getFileManager();
		Iterator ite = parent.tallyList.iterator();
		while(ite.hasNext()){
			Tally tally = (Tally)ite.next();
			//System.out.println("pack = " + tally);
			setTally((Tally)tally.clone());
		}
	}

	public Strategy getStrategy() {
		return strategy;
	}



	public void setStrategy(Strategy strategy) {
		this.strategy = strategy;
	}



	public FileManager getFileManager() {
		return fmng;
	}



	public void setFileManager(FileManager fmng) {
		this.fmng = fmng;
	}



	public List<Tally> getTallyList() {
		return tallyList;
	}



	public void setTally(Tally tally) {
		tallyList.add(tally);
	}



	public void setParent(JobPack parent) {
		this.parent = parent;
	}



	public JobPack getParent() {
		return parent;
	}



	public String getJarUrl() {
		return jarUrl;
	}



	public String getClassName() {
		return className;
	}



	public Map getFieldMap() {
		return fieldMap;
	}

	public void start() {
		startTime = System.currentTimeMillis();
		parent.setJobStarted();
	}

	//Jobはスレッド1つに付き1つのJobを割り当てるため排他は不要
	public boolean finish(){
		isFinished = true;
		endTime = System.currentTimeMillis();
		return parent.setJobFinished(getCalcTime());
	}

	public void clear(){
		//null clear
		jarUrl = null;
		className = null;
		fieldMap = null;
		strategy = null;
		fmng = null;
		tallyList.clear();
		tallyList = null;
	}

	public long getCalcTime(){
		return endTime - startTime;
	}

}
