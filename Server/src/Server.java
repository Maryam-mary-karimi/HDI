import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.Vector;

public class Server extends Thread{
	Tree tree;
	DataNode[] datablocks;
	boolean isNull=true;
	int port;
	String pgwIP;
	int pgwPort;
	String doctorsIP;
	int doctorPort;
	ServerSocket ss;


	public Server(int port) {
		this.port=port;
		try {
			Scanner sc = new Scanner(new File("DevicesInfo.txt"));
			while(!sc.nextLine().equals("IP and ports:")) {
			}
			String pgwAddress=sc.nextLine();
			pgwIP=pgwAddress.split(" ")[1];
			pgwPort=port-1000;
			String doctorAddress=sc.nextLine();
			doctorsIP=doctorAddress.split(" ")[1];
			doctorPort=port+1000;
			sc.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	public void  run() {
		try {

			ss = new ServerSocket(port);

			System.out.println("server is ready to accept the connection from "+ port);

			//read IPs

			Socket sendToDsocket=new Socket();

			while(true) {
				Socket socket = ss.accept();
				ObjectInputStream is = new ObjectInputStream(socket.getInputStream());
				Message m = (Message) is.readObject();
				socket.close();

				//System.out.print("message: "+ m.getType()+", size: "+m.size);
				//if(m.getType().equals("data")) {
				//	for (int i = 0; i < m.getDataNodes().length; i++) {
				//		System.out.println(m.getDataNodes()[i].id+" "+m.getDataNodes()[i].date.year+" "+m.getDataNodes()[i].device.deviceName+" "+m.getDataNodes()[i].value+" "+m.getDataNodes()[i].level);
				//	}
				//}

				Message returnMessage= new Message("ack", false);
				if(m.getType().equals("data")) {
					storeData(m);
					returnMessage= new Message("ack", true);
					System.out.println("stored data successfully through port:"+ port);
					sendToDsocket=new Socket(pgwIP,pgwPort);
				}
				else if(m.getType().equals("req")) {					
					returnMessage= replytoQuery(m, Server_Main.serverfw);
					System.out.println("request data successfully through port:"+ port);
					sendToDsocket=new Socket(doctorsIP,doctorPort);
				}
				ObjectOutputStream os = new ObjectOutputStream(sendToDsocket.getOutputStream());
				os.writeObject(returnMessage);
				System.out.print("sent message to the port "+sendToDsocket.getPort()+" :");
				returnMessage.print();
				sendToDsocket.close();
			}
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	private Message replytoQuery(Message m, FileWriter fw) {

		Vector<Vector<DataNode>> requiredDataBlocksforBFandTree=replyToQueries(m.getQueries(),fw);
		m.setDataBlocks(requiredDataBlocksforBFandTree.elementAt(0));
		m.setExtraDataBlocks(requiredDataBlocksforBFandTree.elementAt(1));
		System.out.println(requiredDataBlocksforBFandTree.elementAt(0).size());
		if(m.getDataBlocks().size()==0){
			System.out.println("data do not exists in the cloud");
			try {
				fw.write("data do not exists in the cloud"+"\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else{
			m.setQueries(requiredDataBlocksforBFandTree.elementAt(0));

			System.out.println("data exists in the cloud");
			m.setRelatedDataNodesinTree(relatedPartOfTheTree(m.getQueries(),fw));
		}
		return m;

	}

	public boolean storeData(Message m){
		if(this.isNull) {
			this.datablocks = m.getDataNodes();
			this.tree = m.getTree();;
			isNull = false;
		}
		else {
			DataNode [] temp = new DataNode[this.datablocks.length+datablocks.length];
			int j = 0;
			for (; j < this.datablocks.length; j++) {
				temp[j] = this.datablocks[j];
				System.out.println(temp[j].id);
			}
			for (; j < temp.length; j++) {
				System.out.println(datablocks[j-this.datablocks.length].id);
				temp[j]= datablocks[j-this.datablocks.length];
				System.out.println(temp[j].id);

			}
			this.datablocks=temp;

			try {
				this.tree=sendToPGW(new Message("data", new DataNode[0], this.tree), pgwIP, pgwPort).getTree();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		return true;
	}




	public Message sendToPGW(Message message, String IP, int port) throws UnknownHostException,
	IOException, ClassNotFoundException {
		System.out.println("welcome client to the "+ IP+" "+ port);
		Socket spSocket = new Socket(IP, port);
		System.out.println("Client connected");
		ObjectOutputStream os = new ObjectOutputStream(spSocket.getOutputStream());
		message.print();
		os.writeObject(message);
		System.out.println("sent informations to the server with port "+ port);
		spSocket.close();

		Socket psSocket = ss.accept();
		ObjectInputStream is = new ObjectInputStream(psSocket.getInputStream());
		Message returnMessage = (Message) is.readObject();
		returnMessage.print();
		psSocket.close();
		return returnMessage;
	}




	public Vector<Vector<DataNode>> replyToQueries(Vector<DataNode> queries, FileWriter fw){
		Vector<DataNode> replyall=new Vector<>();
		Vector<DataNode> replySpecific=new Vector<>();
		boolean queriedDataExists=false;

		for (int i = 0; i < datablocks.length; i++) {
			for (int j = 0; j < queries.size(); j++) {
				if(datablocks[i].device.deviceName.equals(queries.elementAt(j).device.deviceName) &&
						datablocks[i].date.year==queries.elementAt(j).date.year){
					queriedDataExists=true;
					if(!replyall.contains(datablocks[i]))
						replyall.add(datablocks[i]);
					if(datablocks[i].date.month==queries.elementAt(j).date.month) {
						if(!replySpecific.contains(datablocks[i]))
							replySpecific.add(datablocks[i]);
					}
				}
			}
		}
		if (replySpecific.isEmpty()) {
			replySpecific=replyall;

		}
		try {
			fw.write("exists "+queriedDataExists+"\n");
		} catch (IOException e) {
			e.printStackTrace();
		}


		Vector<Vector<DataNode>> output=new Vector<>();
		output.add(replySpecific);
		output.add(replyall);
		return output;
	}

	public Vector<DataNode> relatedPartOfTheTree(Vector<DataNode> queries,FileWriter fw){
		//find related nodes
		Vector<DataNode> relatedNodes=new Vector<>();
		Vector<DataNode> notvisitedParts=new Vector<>();

		try {
			fw.write("SendTheRelatedPartOfTheTree"+"\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
		notvisitedParts.add(tree.root);
		System.out.println("RelatedPartOfTheTree");

		while(!notvisitedParts.isEmpty()){
			DataNode visitingNode=notvisitedParts.remove(0);

			for (int i = 0; i < queries.size(); i++) {
				if(visitingNode.level<3 ||
						(visitingNode.level==3 && visitingNode.device.deviceName.equals(queries.elementAt(i).device.deviceName))
						//||
						//(visitingNode.level==4 && visitingNode.device.deviceName.equals(queries.elementAt(i).device.deviceName)
						//&& visitingNode.date.year==queries.elementAt(i).date.year)
						){
					if(!relatedNodes.contains(visitingNode)) {
						System.out.print("visiting: ");visitingNode.print(fw);
						relatedNodes.add(visitingNode);
						for(int j=0; j<visitingNode.numOfchildren;j++){
							notvisitedParts.addElement(visitingNode.children[j]);
						}
					}
				}
			}

		}


		return relatedNodes;
	}
}
