import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

import java.rmi.Naming;   

public class PGW_Main {

	Patient peter;
	Device[] devices;
	P_GW pgw;
	Server clouds[];
	DataNode[] dataNodes;
	DataNode[] blocks;

	static boolean iflog=false;


	public static void main(String[] args) {
		PGW_Main iot=new PGW_Main();
		try {
			FileWriter fw=new FileWriter(new File("res365.txt"),true);
			int numberOfRuns=0;
			for(int r=0; r<100; r++)
				try {
					int bfSize=10700;
					int tmpBfSize=20;
					int i=365; 

					numberOfRuns++;
					System.out.println(numberOfRuns);
					fw.write("<<<<<<<<<<<<<>>>>>>>>>>>>::::::"+numberOfRuns+" \n");

					long time1s= System.currentTimeMillis(); 
					iot.StoreDataInCloud(fw, i, bfSize, tmpBfSize);//bfSize

					long time2s= System.currentTimeMillis(); 

					if(iflog)System.out.println("required time to store "+(time2s-time1s)+" ms ");
					fw.write("required time to store "+(time2s-time1s)+" ms \n");


				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			//}
			fw.flush();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}



	public void StoreDataInCloud(FileWriter fw,double numberOfPackets, int bfSize, int tmpBfSize){
		try {
			peter= new Patient("peter");
			devices= peter.creatDevices(numberOfPackets);

			int numberOfLeaves=devices[0].numberOfReceivedPackets+devices[1].numberOfReceivedPackets+devices[2].numberOfReceivedPackets; // should be equal to numberOfPackets

			if(iflog)System.out.println("numberOfLeaves"+numberOfLeaves);
			fw.write("numberOfLeaves"+numberOfLeaves+"\n");

			dataNodes=new DataNode[numberOfLeaves];

			blocks=new DataNode[numberOfLeaves];

			int k=0;//index for creating leaves/data nodes
			for (int i = 0; i < devices.length && k<numberOfLeaves; i++) {
				for(int j=0; j<devices[i].numberOfReceivedPackets && k<numberOfLeaves; j++){
					dataNodes[k]=devices[i].createRandomDataPacket(k,fw);
					blocks[k]=new DataNode(k+"", dataNodes[k].date, dataNodes[k].device, dataNodes[k].value);
					k++;
				}
			}	

			//////////////////// P GateWay //////////////////////////////
			if(iflog)System.out.println("GW");
			fw.write("GW"+"\n");
			pgw=new P_GW(devices,clouds);
			try {   
				PGW_interface pgw = new P_GW(devices,clouds);   
				System.setProperty("java.rmi.server.hostname","10.215.45.56");
				Naming.rebind("rmi://localhost:2001/PatientGateway", pgw);   
			} catch (Exception e) {   
				e.printStackTrace();  
			}   


			pgw.createBloomFilter(dataNodes,fw, bfSize , tmpBfSize);

			if(iflog)System.out.println("build tree");
			fw.write("build tree"+"\n");
			Tree tree=pgw.buildTheTree(dataNodes,fw);
			tree.PrintTree(fw);

			//seperate data for clouds
			DataNode dataForClouds[][]=pgw.seperateDataToStoreInClouds(blocks, devices);	

			///////////////// cloud //////////////////
			if(iflog)System.out.println("cloud");
			fw.write("cloud"+"\n");
			clouds=new Server[3];
			clouds[0]=new Server(dataForClouds[0],tree);
			clouds[1]=new Server(dataForClouds[1],tree);
			clouds[2]=new Server(dataForClouds[2],tree);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}



}

