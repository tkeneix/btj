/**
 * FileManagerを保持する単位はJobPack。
 * JobPackのJobがすべて完了したあとにFileManagerのShutdownを実行する。
 * Tallyはcloneし、Jobと1対1で対応付ける。
 */

package btj.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import btj.core.tester.FileManager;
import btj.core.tally.Tally;

public class JobPack implements Serializable {
	public static final short PRIORITY_HIGH = (short)1;
	public static final short PRIORITY_LOW = (short)2;

	FileManager fmng;
	List<Tally> tallyList;
	List<Job> jobList;
	short priority;
	String dsMngName;

	int finishedJobCount;
	int currentJobCount;

	private long startTime;
	private long endTime;

	long totalTime;

	public JobPack(){
		this.finishedJobCount = 0;
		this.currentJobCount = 0;
		this.priority = PRIORITY_LOW;
		this.tallyList = new ArrayList<Tally>();
		this.jobList = new LinkedList<Job>();
	}



	public synchronized String getDsMngName() {
		return dsMngName;
	}



	public synchronized void setDsMngName(String dsMngName) {
		this.dsMngName = dsMngName;
	}

	public synchronized List getJobList(){
		return jobList;
	}

	public synchronized short getPriority() {
		return priority;
	}


	public synchronized void setPriority(short priority) {
		this.priority = priority;
	}


	public synchronized void setFileManager(FileManager fmng){
		this.fmng = fmng;
	}

	public synchronized FileManager getFileManager(){
		return fmng;
	}

	public synchronized void setTally(Tally tally){
		tallyList.add(tally);
	}

	public synchronized void setJob(Job job){
		jobList.add(job);
		job.setParent(this);
	}

	public synchronized Job getNextJob(){
		if(currentJobCount>=jobList.size()){
			return null;
		}
		return jobList.get(currentJobCount++);
	}

	public synchronized int getRunnableJobCount(){
		return currentJobCount - finishedJobCount;
	}

	//使用する想定はない
	public synchronized void resetCount(){
		currentJobCount = 0;
		finishedJobCount = 0;
	}

	protected synchronized void setJobStarted(){
		if(startTime == 0){
			startTime = System.currentTimeMillis();
		}
	}

	protected synchronized boolean setJobFinished(long calcTime){
		boolean ret = false;
		if(++finishedJobCount>=jobList.size()){
			//すべてのJobが終了した場合
			//fmngがnullでないことは最初のJobを使用する際にチェックする必要がある
			fmng.shutdown();
			ret = true;
		}
		totalTime += calcTime;
		endTime = System.currentTimeMillis();
		return ret;
	}

	/**
	 * jobListをクリアしてから発行する
	 * ただし、当該JobPackのJobがまだ検証中かもしれないので待ち合わせる
	 * 処理も必要
	 */
	protected synchronized void forceJobFinished(){
		fmng.shutdown();
	}

	public synchronized long getCalcTime(){
		return endTime - startTime;
	}

	public synchronized long getTotalTime(){
		return totalTime;
	}

	public synchronized int getFinishedCount(){
		return finishedJobCount;
	}

	public synchronized int getTotalCount(){
		return jobList.size();
	}
}