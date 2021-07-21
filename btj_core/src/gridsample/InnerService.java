package gridsample;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface InnerService extends Remote {
	Serializable init(Serializable value) throws RemoteException;
	Serializable execute(Serializable value) throws RemoteException;
	Serializable deinit(Serializable value) throws RemoteException;
}
