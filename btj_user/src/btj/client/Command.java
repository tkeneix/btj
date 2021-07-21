package btj.client;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import btj.service.BackTestService;

import btj.core.util.Parameters;

public class Command {

	public static void main(String[] args){
		try{
			String serverName = args[0];
			String methodName = args[1];

			Parameters params = new Parameters("btjclient.properties");
			String providerUrl = params.getString("providerUrl", "iiop://localhost:900");

			Hashtable<String, String> env = new Hashtable<String, String>();
			env.put("java.naming.factory.initial", "com.sun.jndi.cosnaming.CNCtxFactory");
			env.put("java.naming.provider.url", providerUrl);

			InitialContext initialNamingContext = new InitialContext(env);
			BackTestService service = (BackTestService)initialNamingContext.lookup(serverName);

			if("loadDataSet".equals(methodName)){
				String dsMngName = args[2];
				for(int i=3; i<args.length; i++){
					String propPath = args[i];

					Serializable ret = service.loadDataSet(dsMngName, new Parameters(propPath));
					if(ret != null){
						System.out.println(ret + " " + propPath);
					}
				}
			}else if("unloadDataSet".equals(methodName)){
				String dsMngName = args[2];
				String dsName = args[3];

				Serializable ret = service.unloadDataSet(dsMngName, dsName);
				if(ret != null){
					System.out.println(ret);
				}
			}else if("setMaxThreads".equals(methodName)){
				String max = args[2];

				Serializable ret = service.setMaxThreads(Integer.parseInt(max));
				if(ret != null){
					System.out.println(ret);
				}
			}else if("getDataSetInfo".equals(methodName)){
				Serializable ret = service.getDataSetInfo();
				if(ret != null){
					System.out.println(ret);
				}
			}else if("getHeapInfo".equals(methodName)){
				Serializable ret = service.getHeapInfo();
				if(ret != null){
					System.out.println(ret);
				}
			}else if("runGC".equals(methodName)){
				Serializable ret = service.runGC();
				if(ret != null){
					System.out.println(ret);
				}
			}else if("clearLoader".equals(methodName)){
				Serializable ret = service.clearLoader();
				if(ret != null){
					System.out.println(ret);
				}
			}else if("clearJobList".equals(methodName)){
				Serializable ret = service.clearJobList();
				if(ret != null){
					System.out.println(ret);
				}
			}else if("setSpreadMap".equals(methodName)){
				String dsMngName = args[2];
				String propPath = args[3];
				Serializable ret = service.setSpreadMap(dsMngName, new Parameters(propPath));
				if(ret != null){
					System.out.println(ret + " " + propPath);
				}
			}else if("setLeverageMap".equals(methodName)){
				String dsMngName = args[2];
				String propPath = args[3];
				Serializable ret = service.setLeverageMap(dsMngName, new Parameters(propPath));
				if(ret != null){
					System.out.println(ret + " " + propPath);
				}
			}else{
				System.out.println("予期しないオプションです。 methodName=" + methodName);
				StringBuilder sb = new StringBuilder();
				sb.append("loadDataSet,");
				sb.append("unloadDataSet,");
				sb.append("setMaxThreads,");
				sb.append("getDataSetInfo,");
				sb.append("getHeapInfo,");
				sb.append("runGC,");
				sb.append("clearLoader,");
				sb.append("clearJobList,");
				sb.append("setSpreadMap,");
				sb.append("setLeverageMap");
				System.out.println(sb.toString());
			}

		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
}
