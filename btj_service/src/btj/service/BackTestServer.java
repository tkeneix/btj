package btj.service;

import javax.naming.InitialContext;
import btj.core.util.Parameters;
import java.util.Hashtable;

public class BackTestServer {
	public static void main(String[] args) {
		try{
			Parameters params = new Parameters("btjserver.properties");
			String serverName = args[0];
			String providerUrl = params.getString("providerUrl", "iiop://localhost:900");

			Hashtable env = new Hashtable();
			env.put("java.naming.factory.initial", "com.sun.jndi.cosnaming.CNCtxFactory");
			env.put("java.naming.provider.url", providerUrl);

			InitialContext initialNamingContext = new InitialContext(env);
			BackTestServiceImpl service = new BackTestServiceImpl();
			initialNamingContext.rebind(serverName, service);

			System.out.println("BackTestServer[" + serverName + "] is ready ...");

		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
}
