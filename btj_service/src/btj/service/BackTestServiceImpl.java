package btj.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.rmi.PortableRemoteObject;

import btj.core.dataset.DataSet;
import btj.core.dataset.DataSetManager;
import btj.core.dataset.DataSource;
import btj.core.util.Parameters;

import btj.common.DailyLogger;
import btj.common.JobPack;

public class BackTestServiceImpl extends PortableRemoteObject implements
															BackTestService {
	private StrategyFactory factory;
	private DailyLogger logger;
	private Map<String, DataSetManager> dataSetManagerMap;
	private StrategyExecuter executer;

	public BackTestServiceImpl() throws RemoteException{
		factory = StrategyFactory.getInstance();
		logger = new DailyLogger("btjserv", "./LOG/",
				Short.parseShort(System.getProperty("logLevel", "0")),
				Integer.parseInt(System.getProperty("logFileCount", "30"))
				);
		int maxThreads = Integer.parseInt(System.getProperty("maxThreads", "2"));
		dataSetManagerMap = new HashMap<String, DataSetManager>();
		executer = new StrategyExecuter(maxThreads, dataSetManagerMap, factory, logger);
	}

	public Serializable loadDataSet(String dsMngName, Parameters props) throws RemoteException {
		logger.write(DailyLogger.DEBUG, "start loadDataSet");

		try{
			synchronized(dataSetManagerMap){
				DataSource source = new DataSource(props);
				DataSet ds = source.getDataSet();
				DataSetManager dsMng = dataSetManagerMap.get(dsMngName);
				if(dsMng == null){
					dsMng = new DataSetManager();
					dataSetManagerMap.put(dsMngName, dsMng);
				}
				dsMng.addLabelDataset(ds);
			}
		}catch(IOException ioe){
			dumpStackTrace(ioe);
			throw new RemoteException(ioe.toString());
		}

		logger.write(DailyLogger.DEBUG, "end loadDataSet");
		return "OK";
	}

	public Serializable unloadDataSet(String dsMngName, Parameters props) throws RemoteException {
		logger.write(DailyLogger.DEBUG, "start unloadDataSet(Parameters)");

		try{
			synchronized(dataSetManagerMap){
				DataSetManager dsMng = dataSetManagerMap.get(dsMngName);
				if(dsMng == null){
					String message = "マップに登録されていません。dsMngName=" + dsMngName;
					logger.write(DailyLogger.ERR, message);
					throw new RemoteException(message);
				}
				dsMng.removeLabelDataset(props.getString("dsName", ""));
			}
		}catch(IOException ioe){
			dumpStackTrace(ioe);
			throw new RemoteException(ioe.toString());
		}

		logger.write(DailyLogger.DEBUG, "end unloadDataSet(Parameters)");
		return "OK";
	}

	public Serializable unloadDataSet(String dsMngName, String name) throws RemoteException {
		logger.write(DailyLogger.DEBUG, "start unloadDataSet");

		try{
			synchronized(dataSetManagerMap){
				DataSetManager dsMng = dataSetManagerMap.get(dsMngName);
				if(dsMng == null){
					String message = "マップに登録されていません。dsMngName=" + dsMngName;
					logger.write(DailyLogger.ERR, message);
					throw new RemoteException(message);
				}
				dsMng.removeLabelDataset(name);
			}
		}catch(IOException ioe){
			dumpStackTrace(ioe);
			throw new RemoteException(ioe.toString());
		}

		logger.write(DailyLogger.DEBUG, "end unloadDataSet");
		return "OK";
	}

	public Serializable setSpreadMap(String dsMngName, Parameters props) throws RemoteException {
		logger.write(DailyLogger.DEBUG, "start setSpreadMap");

		try{
			synchronized(dataSetManagerMap){
				DataSetManager dsMng = dataSetManagerMap.get(dsMngName);
				if(dsMng == null){
					dsMng = new DataSetManager();
					dataSetManagerMap.put(dsMngName, dsMng);
				}
				dsMng.setSpreadMap(props.getProperties());
			}
		}catch(Exception ioe){
			dumpStackTrace(ioe);
			throw new RemoteException(ioe.toString());
		}

		logger.write(DailyLogger.DEBUG, "end setSpreadMap");
		return "OK";
	}

	public Serializable setLeverageMap(String dsMngName, Parameters props) throws RemoteException {
		logger.write(DailyLogger.DEBUG, "start setLeverageMap");

		try{
			synchronized(dataSetManagerMap){
				DataSetManager dsMng = dataSetManagerMap.get(dsMngName);
				if(dsMng == null){
					dsMng = new DataSetManager();
					dataSetManagerMap.put(dsMngName, dsMng);
				}
				dsMng.setLeverageMap(props.getProperties());
			}
		}catch(Exception ioe){
			dumpStackTrace(ioe);
			throw new RemoteException(ioe.toString());
		}

		logger.write(DailyLogger.DEBUG, "end setLeverageMap");
		return "OK";
	}

	public Serializable setMaxThreads(int max) throws RemoteException{
		logger.write(DailyLogger.DEBUG, "start setMaxThreads");


		StringBuilder sb = new StringBuilder();
		synchronized(executer){
			int current = executer.getMaxThreads();
			int after = max;

			executer.setMaxThreads(max);

			sb.append("setMaxThreads: ").append(current).append("->").append(after);
			logger.write(DailyLogger.INFO, sb.toString());
		}

		logger.write(DailyLogger.DEBUG, "end setMaxThreads");
		return sb.toString();
	}

	public Serializable execute(JobPack pack) throws RemoteException {
		logger.write(DailyLogger.DEBUG, "start execute");

		synchronized(executer){
			//チェック処理
			DataSetManager dsMng = dataSetManagerMap.get(pack.getDsMngName());
			if(dsMng == null){
				RemoteException re = new RemoteException("getDsMngName=" + pack.getDsMngName()
										+ " に該当するDataSetManagerが登録されていません。");
				dumpStackTrace(re);
				throw re;
			}
			executer.setJobPack(pack);
		}

		logger.write(DailyLogger.DEBUG, "end execute");
		return "OK";
	}

	public Serializable getDataSetInfo() throws RemoteException{
		logger.write(DailyLogger.DEBUG, "start getDataSetInfo");

		StringBuilder sb = new StringBuilder();
		synchronized(dataSetManagerMap){
			Iterator<String> ite = dataSetManagerMap.keySet().iterator();
			while(ite.hasNext()){
				String key = ite.next();
				sb.append(key).append("[");
				Iterator<String> labelIte = dataSetManagerMap.get(key).getMap().keySet().iterator();
				while(labelIte.hasNext()){
					String label = labelIte.next();
					sb.append(label);
					if(labelIte.hasNext()){
						sb.append(",");
					}
				}
				sb.append("]");
				if(ite.hasNext()){
					sb.append("\n");
				}
			}
		}
		logger.write(DailyLogger.INFO, sb.toString());

		logger.write(DailyLogger.DEBUG, "end getDataSetInfo");
		return sb.toString();
	}

	public Serializable getHeapInfo() throws RemoteException {
		logger.write(DailyLogger.DEBUG, "start getHeapInfo");

		Runtime runtime = Runtime.getRuntime();
		long totalValue = runtime.totalMemory() / (1000 * 1000);
		long freeValue = runtime.freeMemory() / (1000 * 1000);
		long useValue = (totalValue - freeValue);

		StringBuilder sb = new StringBuilder();
		sb.append("getHeapInfo: ");
		sb.append("use=").append(useValue).append("[MB] ");
		sb.append("free=").append(freeValue).append("[MB] ");
		sb.append("total=").append(totalValue).append("[MB]");
		logger.write(DailyLogger.INFO, sb.toString());

		logger.write(DailyLogger.DEBUG, "end getHeapInfo");
		return sb.toString();
	}

	public Serializable runGC() throws RemoteException {
		logger.write(DailyLogger.DEBUG, "start runGC");

		Runtime runtime = Runtime.getRuntime();
		runtime.gc();
		long totalValue = runtime.totalMemory() / (1000 * 1000);
		long freeValue = runtime.freeMemory() / (1000 * 1000);
		long useValue = (totalValue - freeValue);

		StringBuilder sb = new StringBuilder();
		sb.append("runGC: ");
		sb.append("use=").append(useValue).append("[MB] ");
		sb.append("free=").append(freeValue).append("[MB] ");
		sb.append("total=").append(totalValue).append("[MB]");
		logger.write(DailyLogger.INFO, sb.toString());

		logger.write(DailyLogger.DEBUG, "end runGC");
		return sb.toString();
	}

	public Serializable clearLoader() throws RemoteException {
		logger.write(DailyLogger.DEBUG, "start clearLoader");

		StringBuilder sb = new StringBuilder();
		int current = factory.getUrlMapCount();
		factory.clearLoader();
		int after = factory.getUrlMapCount();
		sb.append("clearLoader: ").append(current).append("->").append(after);
		logger.write(DailyLogger.INFO, sb.toString());

		logger.write(DailyLogger.DEBUG, "end clearLoader");
		return sb.toString();
	}

	public Serializable clearJobList() throws RemoteException {
		logger.write(DailyLogger.DEBUG, "start clearJobList");

		executer.clearJobList();

		logger.write(DailyLogger.DEBUG, "end clearJobList");
		return "OK";
	}

	private void dumpStackTrace(Throwable th){
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		th.printStackTrace(pw);
		logger.write(DailyLogger.ERR, sw.toString());
	}
}
