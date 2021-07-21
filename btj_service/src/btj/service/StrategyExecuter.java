package btj.service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import btj.core.log.CSVSimpleLogger;
import btj.core.log.IMemoryLogger;
import btj.core.log.ISimpleLogger;
import btj.core.log.MapSimpleLogger;

import btj.core.tester.BackTester;
import btj.core.dataset.DataSetManager;
import btj.core.strategy.IUseMemoryLogger;
import btj.core.strategy.IUseSimpleLogger;
import btj.core.strategy.Strategy;
import btj.core.tally.Tally;

import btj.common.DailyLogger;
import btj.common.Job;
import btj.common.JobPack;

public class StrategyExecuter {
	private int maxThreads;
	private int currentThreads;
	private int threadNum;
	private Map<String,DataSetManager> dataSetManagerMap;
	private LinkedList<Job> jobList;
	private Object share;
	private DailyLogger logger;
	private StrategyFactory factory;

	public StrategyExecuter(int maxThreads, Map dataSetManagerMap,
			StrategyFactory factory, DailyLogger logger){
		this.maxThreads = maxThreads;
		this.currentThreads = 0;
		this.dataSetManagerMap = dataSetManagerMap;
		this.factory = factory;
		this.jobList = new LinkedList<Job>();
		this.share = new Object();
		this.logger = logger;
		this.threadNum = 1;
	}

	public void setMaxThreads(int max){
		synchronized(share){
			maxThreads = max;
			runThread();
		}
	}

	public int getMaxThreads(){
		synchronized(share){
			return maxThreads;
		}
	}

	public void clearJobList(){
		synchronized(jobList){
			jobList.clear();
		}
	}

	public void setJobPack(JobPack pack){
		//JobPackのプライオリティを判定し纏めて登録する。
		synchronized(jobList){
			if(pack.getPriority() == JobPack.PRIORITY_HIGH){
				jobList.addAll(0, pack.getJobList());
			}else{
				jobList.addAll(pack.getJobList());
			}
		}

		runThread();
	}

	private void runThread(){
		synchronized(share){
			int runThreads = maxThreads - currentThreads;
			for(int i=0; i<runThreads; i++){
				(new WorkerThread(threadNum++)).start();
				currentThreads++;
			}
		}
	}

	class WorkerThread extends Thread{

		WorkerThread(int num){
			setName("Worker-" + String.valueOf(num));
		}

		public void run(){
			try{
				logger.write(DailyLogger.INFO, getName() + " start");
				while(true){
					//maxThreadsのチェック
					synchronized(share){
						if(currentThreads > maxThreads){
							logger.write(DailyLogger.INFO, getName()
									+ " maxThreadsを超えたためワーカーは終了します。");
							break;
						}
					}

					Job job;
					//Jobを取得する
					synchronized(jobList){
						job = jobList.poll();
						if(job == null){
							//Jobが空の場合はスレッドは終了する
							logger.write(DailyLogger.INFO, getName()
									+ " Jobが空になったためワーカーは終了します。 size=" + jobList.size());
							break;
						}
					}

					ISimpleLogger slogger = null;
					IMemoryLogger mlogger = null;
					try{
						//JobPackから情報を引き継ぐ
						job.pack();

						//Strategyを生成する
						job.setStrategy(factory.create(job));

						//DataSetManagerを取得する
						DataSetManager dsMng = dataSetManagerMap.get(job.getParent().getDsMngName());
						//System.out.println("dsMng=" + dsMng + " job.getParent().getDsMngName()=" + job.getParent().getDsMngName());

						//バックテスタの生成
						BackTester tester = new BackTester(dsMng, false);

						//ストラテジの取得、ロガー判定、登録
						Strategy stg = job.getStrategy();
						if(stg instanceof IUseSimpleLogger){
							slogger = new CSVSimpleLogger(stg.getName() + "_stglog", job.getFileManager());
							((IUseSimpleLogger) stg).setSimpleLogger(slogger);
						}
						if(stg instanceof IUseMemoryLogger){
							mlogger = new MapSimpleLogger(stg.getName() + "_memlog", job.getFileManager());
							((IUseMemoryLogger) stg).setMemoryLogger(mlogger);
						}
						tester.addStrategy(stg);

						//集計クラスの登録
						Iterator<Tally> tallyIte = job.getTallyList().iterator();
						while(tallyIte.hasNext()){
							Tally tally = tallyIte.next();
							tally.setStrategyName(job.getStrategy().getName());
							tester.addTally(tally);
						}

						//バックテスタ開始
						job.start();
						tester.start();

						//集計結果のダンプ
						tallyIte = job.getTallyList().iterator();
						while(tallyIte.hasNext()){
							tallyIte.next().dump(job.getFileManager());
						}

					}catch(Throwable th){
						dumpThreadStackTrace(th);
					}finally{
						//終了処理
						boolean last = job.finish();
						logger.write(DailyLogger.INFO, getName()
								+ " " + job.getStrategy().getName() + " " + (job.getCalcTime() / 1000) + "[秒] JobPack("
								+ job.getParent().getFinishedCount() + "/"
								+ job.getParent().getTotalCount() + ") 経過時間="
								+ (job.getParent().getCalcTime()/(1000)) + "[秒] 総計算時間="
								+ (job.getParent().getTotalTime()/(1000)) + "[秒] packFinished=" + last);
						job.clear();

						//ロガーclose
						if(slogger != null){
							slogger.close();
						}
						if(mlogger != null){
							mlogger.dump();
						}
					}
				}
			}catch(Throwable ex){
				dumpThreadStackTrace(ex);
			}finally{
				synchronized(share){
					currentThreads--;
					logger.write(DailyLogger.INFO, getName() + " end currentThreads=" + currentThreads + " maxThreads=" + maxThreads);
				}
			}
		}

		private void dumpThreadStackTrace(Throwable th){
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			pw.print(getName() + " ");
			th.printStackTrace(pw);
			logger.write(DailyLogger.ERR, sw.toString());
		}
	}

}
