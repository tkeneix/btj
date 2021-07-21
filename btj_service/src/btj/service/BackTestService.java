package btj.service;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Properties;

import btj.core.util.Parameters;

import btj.common.JobPack;

public interface BackTestService extends Remote {
	public Serializable loadDataSet(String dsMngName, Parameters props) throws RemoteException;
	public Serializable unloadDataSet(String dsMngName, Parameters props) throws RemoteException;
	public Serializable unloadDataSet(String dsMngName, String name) throws RemoteException;
	public Serializable setSpreadMap(String dsMngName, Parameters props) throws RemoteException;
	public Serializable setLeverageMap(String dsMngName, Parameters props) throws RemoteException;

	public Serializable setMaxThreads(int max) throws RemoteException;

	public Serializable execute(JobPack pack) throws RemoteException;

	public Serializable getDataSetInfo() throws RemoteException;
	public Serializable getHeapInfo() throws RemoteException;
	public Serializable runGC() throws RemoteException;
	public Serializable clearLoader() throws RemoteException;
	public Serializable clearJobList() throws RemoteException;

}
