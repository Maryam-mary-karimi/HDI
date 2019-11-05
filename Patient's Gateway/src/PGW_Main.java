import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Vector;

public class PGW_Main {

	Patient peter;
	P_GW patientGW;
	static FileWriter fw;
	
	Vector <Socket> cloudsAddress;

	//static boolean iflog=false;


	public static void main(String[] args) {
				
		PGW_Main iot=new PGW_Main();

		try {
			
			
			fw=new FileWriter(new File("res365.txt"),true);

			long time1s= System.currentTimeMillis(); 

			int bfSize=10700;//10700 for a year, 365 blocks
			int tmpBfSize=20;
			int k=5;
			iot.firstStoreDataInCloud( bfSize, k, tmpBfSize);//bfSize this will create a P_GW

			long time2s= System.currentTimeMillis(); 

			//if(iflog)System.out.println("required time to store "+(time2s-time1s)+" ms ");
			fw.write("required time to store "+(time2s-time1s)+" ms \n");

			new ListenToTheDoctor(iot.patientGW).start();//listen to the doctor
			
			new Update(iot.patientGW, tmpBfSize).start();
			
			 
			fw.flush();
			//fw.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	} 


	public void firstStoreDataInCloud( int bfSize, int hashNum,int tmpBfSize){
		try {
			
			patientGW=new P_GW(bfSize, hashNum);//creat generalized bloom filter

			peter= new Patient("peter");
			patientGW.devices= peter.getDevices();
			
			
			DataNode[] alldataset=new DataNode[0];

			for (int i = 0; i < patientGW.devices.length; i++) {
				DataNode [] newdata = patientGW.devices[i].readDataPacket(patientGW.devices[i].deviceName);
				DataNode [] temp = new DataNode[alldataset.length+newdata.length];
				int j = 0;
				for (; j < alldataset.length; j++) {
					temp[j] = alldataset[j];
				}
				for (; j < temp.length; j++) {
					System.out.println(newdata[j-alldataset.length].id);
					temp[j]= newdata[j-alldataset.length];
				}
				alldataset=temp;
			}
			

			//////////////////// P GateWay //////////////////////////////
			patientGW.addDataToBloomFilter(alldataset,fw, tmpBfSize);
			
			
			//if(iflog)System.out.println("build tree");
			DataNode[] leaves=new DataNode[alldataset.length];
			for (int i = 0; i < alldataset.length; i++) {
				leaves[i]=new DataNode(alldataset[i].id, alldataset[i].date, alldataset[i].device, alldataset[i].value);
			}
			Tree tree=patientGW.buildTheTree(leaves);
			tree.PrintTree(fw);

			///////////////// cloud //////////////////
		

			//send blocks and tree to server//////////////////////////////
			for (int i = 0; i < patientGW.devices.length; i++) {
				Message dataMessage= new Message("data", patientGW.devices[i].dataToSend,tree);
				for (int j = 0; j < patientGW.devices[i].dataToSend.length; j++) {
					System.out.println(patientGW.devices[i].dataToSend[j].id+" "+patientGW.devices[i].dataToSend[j].date.year+" "+patientGW.devices[i].dataToSend[j].device.deviceName+" "+patientGW.devices[i].dataToSend[j].value+" "+patientGW.devices[i].dataToSend[j].level);
				}
				SendToServer(dataMessage,patientGW.devices[i].cloudIP,patientGW.devices[i].cloudport);
			}
			
			
			System.out.println("Data sent");

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static Message SendToServer(Message message, String IP, int port) throws UnknownHostException,
	IOException, ClassNotFoundException {
		System.out.println("welcome client to the "+ IP+" "+ port);
		Socket psSocket = new Socket(IP, port);
		System.out.println("Client connected");
		ObjectOutputStream os = new ObjectOutputStream(psSocket.getOutputStream());
		//message.print();
		
		System.out.print("message: "+ message.getType()+", size: "+message.size);
		if(message.getType().equals("data")) {
			for (int i = 0; i < message.getDataNodes().length; i++) {
				System.out.println(message.getDataNodes()[i].id+" "+message.getDataNodes()[i].date.year+" "+message.getDataNodes()[i].device.deviceName+" "+message.getDataNodes()[i].value+" "+message.getDataNodes()[i].level);
			}
		}
		
		
		os.writeObject(message);
		System.out.println("sent informations to the server with port "+ port);
		psSocket.close();

		ServerSocket sp = new ServerSocket(port-1000);
		Socket spSocket = sp.accept();
		ObjectInputStream is = new ObjectInputStream(spSocket.getInputStream());
		Message returnMessage = (Message) is.readObject();
		returnMessage.print();
		spSocket.close();
		sp.close();
		return returnMessage;
	}

	
	

}

