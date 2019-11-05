import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.Vector;

public class DGW_Main extends Thread{

	D_GW dgw;
	Doctor doris;

	static boolean iflog=false;

	public static void main(String[] args) {
		DGW_Main iot=new DGW_Main();
		try {
			FileWriter fw=new FileWriter(new File("res365.txt"),true);

			int tmpBfSize=20;



			long time1= System.currentTimeMillis(); 

			iot.RetriveDataFromCloud(fw, tmpBfSize);

			long time2= System.currentTimeMillis();

			System.out.println("required time to retrieve "+(time2-time1)+" ms ");
			fw.write("required time to retrieve "+(time2-time1)+" ms \n");
			//}

			//}
			fw.flush();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public void RetriveDataFromCloud(FileWriter fw, int tmpBfSize){

		try{

			Device [] devices=new Device[0];
			devices=SendToPGW(new Message("askpgw", devices)).getDevices();

			//doctor
			doris=new Doctor();
			Vector<DataNode> queries=doris.requestForBlock(devices);

			//dgw ask for data from the cloud and ask for verification from PGW
			dgw=new D_GW();


			//cloud
			Message m=new Message("req", queries);
			Message replyMessage=SendToServer(m,queries.firstElement().device.cloudIP ,queries.firstElement().device.cloudport);


			Vector<DataNode> dataBlocksTobeVerified=replyMessage.getDataBlocks();
			Vector<DataNode> dataBlocksUsedForVerification=replyMessage.getExtraDataBlocks();
			if(dataBlocksTobeVerified.size()==0){
				if(iflog)System.out.println("data do not exists in the cloud");
				fw.write("data do not exists in the cloud"+"\n");
			}
			else{
				Vector<DataNode> relatedDataNodesinTree=replyMessage.getRelatedDataNodesinTree();

				if(iflog)System.out.println("print related nodes in the tree size:"+relatedDataNodesinTree.size());

				if(iflog)System.out.println(dataBlocksTobeVerified.size());
				fw.write(dataBlocksTobeVerified.size()+"\n");
				//dgw
				String bf2[]=dgw.BF_required_For_BFVerification(dataBlocksTobeVerified,tmpBfSize,fw);
				DataNode[] SHAnodes=dgw.tree_required_For_TreeVerification(dataBlocksUsedForVerification,fw);

				//pgw
				long time1= System.currentTimeMillis(); 

				Message verify_message=new Message("verify", bf2,SHAnodes, relatedDataNodesinTree);
				if(iflog)System.out.println(verify_message.getRelatedDataNodesinTree().size()); 
				boolean verified=SendToPGW(verify_message).getAck();

				long time2= System.currentTimeMillis();

				System.out.println("required verify time "+(time2-time1)+" ms ");
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
		catch (java.lang.ArithmeticException e) {	
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

	}

	/*
pgw localhost 3000
dgw localhost 2000

3
servers with IP and port for dgw:
HRM, localhost, 5001
activity_tracker, localhost, 5002
calorimeter, localhost, 5003

doctors gateway for servers:localhost cloud server-3000(2001,2002,2003)*/

	public Message SendToPGW(Message message) throws UnknownHostException,
	IOException, ClassNotFoundException {
		
		//read IPs
		Scanner sc=new Scanner(new File("DevicesInfo.txt"));
		while(!sc.nextLine().equals("IP and ports:")) {
		}
		String pgwAddress=sc.nextLine();
		String pgwIP=pgwAddress.split(" ")[1];
		int pgwPort=Integer.parseInt(pgwAddress.split(" ")[2]);
		String doctorAddress=sc.nextLine();
		//String doctorsIP=doctorAddress.split(" ")[1];
		int doctorPort=Integer.parseInt(doctorAddress.split(" ")[2]);
		sc.close();

		if(iflog)System.out.println("welcome client to the "+ pgwIP+" "+ pgwPort);
		Socket psSocket = new Socket(pgwIP, pgwPort);
		if(iflog)System.out.println("Client connected");
		ObjectOutputStream os = new ObjectOutputStream(psSocket.getOutputStream());
		
		//message.print();
		os.writeObject(message);
		System.out.println("sent informations to the PGW with port "+ pgwPort);
		psSocket.close();

		ServerSocket sp = new ServerSocket(doctorPort);
		System.out.println("port "+doctorPort+" is ready in dgw to accept connection");
	
		Socket spSocket = sp.accept();

		System.out.println("port "+doctorPort+" accepted the connection");
		ObjectInputStream is = new ObjectInputStream(spSocket.getInputStream());
		Message returnMessage = (Message) is.readObject();
		//returnMessage.print();
		spSocket.close();
		sp.close();
		return returnMessage;
	}


	public Message SendToServer(Message message, String IP, int port) throws UnknownHostException,
	IOException, ClassNotFoundException {
		if(iflog)System.out.println("welcome client to the "+ IP+" "+ port);
		Socket psSocket = new Socket(IP, port);
		if(iflog)System.out.println("Client connected");
		ObjectOutputStream os = new ObjectOutputStream(psSocket.getOutputStream());
		//message.print();
		os.writeObject(message);
		System.out.println("sent informations to the server with port "+ port);
		psSocket.close();

		int port2=port+1000;
		ServerSocket sp = new ServerSocket(port2);
		if(iflog)System.out.println("wait for information from the server on port "+port2);
		Socket spSocket = sp.accept();
		System.out.println("received information from server");
		ObjectInputStream is = new ObjectInputStream(spSocket.getInputStream());
		Message returnMessage = (Message) is.readObject();
		//returnMessage.print();
		spSocket.close();
		sp.close();
		return returnMessage;
	}
}

