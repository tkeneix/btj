package gridsample;

import java.io.Serializable;
import java.rmi.RemoteException;

import javax.rmi.PortableRemoteObject;

public class InnerServiceImpl extends PortableRemoteObject implements InnerService {
	private String name;
	private String className;
	private Service service;

	public InnerServiceImpl(String name, String className) throws RemoteException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		this.name = name;
		this.className = className;
		this.service = (Service)Class.forName(className).newInstance();
	}

	public Serializable deinit(Serializable value) throws RemoteException {
		return service.deinit(value);
	}

	public Serializable execute(Serializable value) throws RemoteException {
		return service.execute(value);
	}

	public Serializable init(Serializable value) throws RemoteException {
		return service.init(value);
	}

}
