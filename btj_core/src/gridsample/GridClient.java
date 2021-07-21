package gridsample;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import btj.core.util.Parameters;


public class GridClient {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try{
			Parameters params = new Parameters("gclient.properties");
			String[] serverNames = params.getString("serverNames", "001").split(",");
			String providerUrl = params.getString("providerUrl", "iiop://localhost:900");

			Hashtable env = new Hashtable();
			env.put("java.naming.factory.initial", "com.sun.jndi.cosnaming.CNCtxFactory");
			env.put("java.naming.provider.url", providerUrl);

			InitialContext initialNamingContext = new InitialContext(env);
			//create job
			ArrayList jobList = new ArrayList();
			for(int i=0; i<30000; i++){
				jobList.add(String.valueOf(i));
			}
			JobManager manager = new JobManager(jobList);
			Thread[] invokeThreads = new InvokeThread[serverNames.length];
			for(int i=0; i<serverNames.length; i++){
				invokeThreads[i] = new InvokeThread(serverNames[i], initialNamingContext,
										20,
										manager, new ArrayList());
			}

			long startTime = System.currentTimeMillis();
			for(int i=0; i<serverNames.length; i++){
				invokeThreads[i].start();
			}
			for(int i=0; i<serverNames.length; i++){
				invokeThreads[i].join();
			}
			long endTime = System.currentTimeMillis();

			System.out.println((endTime - startTime) + "[ms]");
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	static class InvokeThread extends Thread{
		private ArrayList replyList;
		private String name;
		private int queueSize;
		private InnerService service;
		private JobManager manager;
		private InitialContext initialNamingContext;

		public InvokeThread(String name, InitialContext initialNamingContext, int queueSize,
				JobManager manager , ArrayList replyList) throws NamingException, RemoteException {
			super();
			this.replyList = replyList;
			this.name = name;
			this.queueSize = queueSize;
			this.service = service;
			this.manager = manager;
			this.initialNamingContext = initialNamingContext;

			this.service = (InnerService)initialNamingContext.lookup(name);
			Serializable replyInit = service.init("start");
		}

		public void run(){
			int totalJobCount = 0;
			try{
				while(true){
					ArrayList jobList = manager.getJobList(queueSize);
					if(jobList.size() == 0){
						break;
					}
					totalJobCount += jobList.size();
					Iterator jobIte = jobList.iterator();
					while(jobIte.hasNext()){
						Serializable replyExecute = service.execute((Serializable)jobIte.next());
					}
				}
			}catch(Exception ex){
				ex.printStackTrace();
			}finally{
				System.out.println(name + "," + totalJobCount);
			}
		}
	}

	static class JobManager{
		private ArrayList jobList;
		private int currentCount;

		public JobManager(ArrayList jobList){
			this.jobList = jobList;
			this.currentCount = 0;
		}

		public synchronized ArrayList getJobList(int size){
			ArrayList ret = new ArrayList();
			int maxLen = jobList.size() < (currentCount + size) ? jobList.size() : (currentCount + size);
			for(int i=currentCount; i<maxLen; i++){
				ret.add(jobList.get(i));
				currentCount++;
			}

			return ret;
		}
	}

}
