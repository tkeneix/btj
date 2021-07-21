package gridsample;

import javax.naming.InitialContext;

import btj.core.util.Parameters;

import java.util.Hashtable;

public class GridServer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try{
			Parameters params = new Parameters("gserver.properties");
			String serverName = args[0];
			String providerUrl = params.getString("providerUrl", "iiop://localhost:900");
			String serviceClassName = params.getString("serviceClassName", "SampleService");

			Hashtable env = new Hashtable();
			env.put("java.naming.factory.initial", "com.sun.jndi.cosnaming.CNCtxFactory");
			env.put("java.naming.provider.url", providerUrl);

			InitialContext initialNamingContext = new InitialContext(env);
			InnerServiceImpl service = new InnerServiceImpl(serverName, serviceClassName);
			initialNamingContext.rebind(serverName, service);

			System.out.println("GridServer[" + serverName + "] is ready ...");

		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

}
