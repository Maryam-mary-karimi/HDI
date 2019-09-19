import java.io.FileWriter;
import java.util.Vector;

public interface PGW_interface   
extends java.rmi.Remote{
	
	boolean verifyData(String[] bf2,DataNode[] SHAnodes,Vector<DataNode> relatedNodesInTree, FileWriter fw) throws java.rmi.RemoteException; 
	
	public Device[] getDevices()  throws java.rmi.RemoteException; 
	
	public Server[] getClouds()  throws java.rmi.RemoteException; 

}