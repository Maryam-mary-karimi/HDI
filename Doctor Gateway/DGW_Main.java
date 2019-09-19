import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.util.Vector;

public class DGW_Main {

    
   
	
	
	D_GW dgw;
	Doctor doris;

	static boolean iflog=false;
	

	public static void main(String[] args) {
		DGW_Main iot=new DGW_Main();
		try {
			FileWriter fw=new FileWriter(new File("res365.txt"),true);
			int numberOfRuns=0;
			for(int r=0; r<100; r++) {// r is the number of repeats
				//for(int bfSize=10000; bfSize<=40000; bfSize+=1000){

				int tmpBfSize=20;

				numberOfRuns++;
				System.out.println(numberOfRuns);
				fw.write("<<<<<<<<<<<<<>>>>>>>>>>>>::::::"+numberOfRuns+" \n");


				long time1= System.currentTimeMillis(); 

				iot.RetriveDataFromCloud(fw, tmpBfSize);

				long time2= System.currentTimeMillis();

				if(iflog)System.out.println("required time to retrieve "+(time2-time1)+" ms ");
				fw.write("required time to retrieve "+(time2-time1)+" ms \n");
			}

			//}
			fw.flush();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	public void RetriveDataFromCloud(FileWriter fw, int tmpBfSize){
		
		try{
			
			
			PGW_interface pgw = (PGW_interface)Naming.lookup("rmi://localhost/PatientGateway");
			
			Device[] devices=pgw.getDevices();
			Server[] clouds=pgw.getClouds();
			
			//doctor
			doris=new Doctor();
			Vector<DataNode> queries=doris.requestForBlock(devices);

			//dgw ask for data from the cloud and ask for verification from PGW
			dgw=new D_GW();
			Server queriedServer=dgw.sendRequestToCloud(clouds, queries.firstElement());//each query should be from one device only////////	

			//cloud 
			Vector<Vector<DataNode>> requiredDataBlocksforBFandTree=queriedServer.replyToQueries(queries,fw);
			Vector<DataNode> dataBlocksTobeVerified=requiredDataBlocksforBFandTree.elementAt(0);
			Vector<DataNode> dataBlocksUsedForVerification=requiredDataBlocksforBFandTree.elementAt(1);
			if(dataBlocksTobeVerified.isEmpty()){
				if(iflog)System.out.println("data do not exists in the cloud");
				fw.write("data do not exists in the cloud"+"\n");
			}
			else{
				Vector<DataNode> relatedDataNodesinTree=queriedServer.SendTheRelatedPartOfTheTree(queries,fw);

				if(iflog)System.out.println(dataBlocksTobeVerified.size());
				fw.write(dataBlocksTobeVerified.size()+"\n");
				//dgw
				String bf2[]=dgw.send_To_PGW_For_BFVerification(dataBlocksTobeVerified,tmpBfSize,fw);
				DataNode[] SHAnodes=dgw.send_To_PGW_For_TreeVerification(dataBlocksUsedForVerification,fw);

				//pgw
				long time1= System.currentTimeMillis(); 

				boolean verified=pgw.verifyData(bf2,SHAnodes, relatedDataNodesinTree,fw);

				long time2= System.currentTimeMillis();

				if(iflog)System.out.println("required verify time "+(time2-time1)+" ms ");
				fw.write("required verify time "+(time2-time1)+" ms \n");


				if(verified){
					if(iflog)System.out.println("success"); 
					fw.write("success"+"\n");
					doris.requiredData=dataBlocksTobeVerified;
				}
				else{
					if(iflog)System.out.println("data not verified");
					fw.write("data not verified"+"\n");
					System.exit(0);
				}
			}

		} 
		catch (IOException e) {
			e.printStackTrace();
		}  
		catch (NotBoundException e) {    
			e.printStackTrace();   
		}    
		catch (java.lang.ArithmeticException e) {	
			e.printStackTrace();
		} 

	}
}

