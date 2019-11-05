import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.Vector;

public class P_GW{

	BloomFilter<String> bf;
	Device[] devices;

	public  P_GW(int bfSize, int hashNum) {  
		super();
		bf=new BloomFilter<>(bfSize, 365);
		bf.setK(hashNum);
	}  


	public BloomFilter<String> addDataToBloomFilter(DataNode [] leaves,FileWriter fw, int tempbfsize){
		try {

			BloomFilter<String> tempBf=new BloomFilter<>(tempbfsize, 1);
			for(int i=0; i<leaves.length;i++){
				tempBf.add(leaves[i].date.year+"/"+leaves[i].date.month+"/"+leaves[i].date.day+" "+leaves[i].device.deviceName+" "+leaves[i].value, false);//put each instance into a bloomfilter
				System.out.println("while buildind bf: "+tempBf.StringOfBits(fw)+" "+leaves[i].date+" "+leaves[i].device.deviceName+" "+leaves[i].value);
				bf.add(tempBf.StringOfBits(fw), true);//hash that bloom filter in a main bloom filter
				tempBf.clear();//clear the temp bloom filter for next instance
			} 
			//if(PGW_Main.iflog)System.out.println("bloom filter size "+bf.getBitSetSize());
			fw.write("bloom filter size "+bf.getBitSetSize()+"\n");


			//if(PGW_Main.iflog)System.out.println("temp bloom filter size "+tempBf.getBitSetSize());
			fw.write("temp bloom filter size "+tempBf.getBitSetSize()+"\n");


			//if(PGW_Main.iflog)System.out.println("bloom filter falsepositive "+bf.getFalsePositiveProbability(fw,true));
			fw.write("bloom filter falsepositive "+bf.getFalsePositiveProbability(fw,true)+"\n");


			//if(PGW_Main.iflog)System.out.println("temp bloom filter falsepositive "+tempBf.getFalsePositiveProbability(fw,false));
			fw.write("temp bloom filter falsepositive "+tempBf.getFalsePositiveProbability(fw,false)+"\n");
			//once upon a time it was 1.8555504930700443E-42


			//if(PGW_Main.iflog)System.out.println("bloom filter falseNegative "+bf.getFalseNegativeProbability(fw,true));
			fw.write("bloom filter falseNegative "+bf.getFalseNegativeProbability(fw,true)+"\n");

			//if(My_IoT.iflog)System.out.println(":"+bf.getBitSet());
			//if(PGW_Main.iflog)System.out.println("k: "+bf.getK());
			fw.write("k: "+bf.getK()+"\n");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//System.out.println("GBF"+bf.StringOfBits(fw));
		return bf;
	}

	
	
	public Tree buildTheTree(DataNode[] leaves){
		Tree t=new Tree();

		Vector<DataNode> yearnodes=new Vector<>();

		leaves=Tree.sort(leaves);

		//add leaves, level 4
		for (int i = 0; i < leaves.length; i++) {
			t.addLeaves(leaves[i]);
		}

	
		//add year nodes, level 3 
		Vector<DataNode> tempChildren=new Vector<>();


		for (int i = 0; i < leaves.length;) {
			while(	i<leaves.length &&
					(tempChildren.isEmpty() ||
							(tempChildren.elementAt(0).date.year== leaves[i].date.year && 
							tempChildren.elementAt(0).device.deviceName.equals(leaves[i].device.deviceName)) )){
				tempChildren.addElement(leaves[i]);
				i++;
			}

			yearnodes.addElement(t.addParent(3, tempChildren));
			tempChildren.removeAllElements();
		}

		//add device nodes, level 2
		Vector<DataNode> devicenodes=new Vector<>();

		for (int i = 0; i < yearnodes.size(); ) {

			while(	i<yearnodes.size() &&
					(tempChildren.isEmpty() ||
							tempChildren.elementAt(0).device.deviceName.equals(yearnodes.elementAt(i).device.deviceName) )){
				tempChildren.addElement(yearnodes.elementAt(i));
				i++;
			}

			devicenodes.addElement(t.addParent(2, tempChildren));
			tempChildren.removeAllElements();
		}

		// add root, level 1
		t.root=t.addParent(1, devicenodes);

		//in addParent method the year nodes with level 3 will not have any children which removes the leaves form the tree

		return t;

	}


	public Tree updateTree(DataNode[] newleaves, Tree oldtree, FileWriter fw){


		Tree t=new Tree();

		Vector<DataNode> yearnodes=new Vector<>();

		newleaves=Tree.sort(newleaves);

		//add leaves, level 4
		for (int i = 0; i < newleaves.length; i++) {
			t.addLeaves(newleaves[i]);
		}

	
		
		//add year nodes from oldTree, this is the only part that is different form build the tree method
		for (int i = 0; i < oldtree.root.numOfchildren; i++) {
			for (int j = 0; j < oldtree.root.children[i].numOfchildren; j++) {
				yearnodes.add(oldtree.root.children[i].children[j]);
			}
		}
		
		//add year nodes, level 3 
		Vector<DataNode> tempChildren=new Vector<>();


		for (int i = 0; i < newleaves.length;) {
			while(	i<newleaves.length &&
					(tempChildren.isEmpty() ||
							(tempChildren.elementAt(0).date.year== newleaves[i].date.year && 
							tempChildren.elementAt(0).device.deviceName.equals(newleaves[i].device.deviceName)) )){
				tempChildren.addElement(newleaves[i]);
				i++;
			}

			yearnodes.addElement(t.addParent(3, tempChildren));
			tempChildren.removeAllElements();
		}

		yearnodes=Tree.sort(yearnodes);
		
		//add device nodes, level 2
		Vector<DataNode> devicenodes=new Vector<>();

		for (int i = 0; i < yearnodes.size(); ) {

			while(	i<yearnodes.size() &&
					(tempChildren.isEmpty() ||
							tempChildren.elementAt(0).device.deviceName.equals(yearnodes.elementAt(i).device.deviceName) )){
				tempChildren.addElement(yearnodes.elementAt(i));
				i++;
			}

			devicenodes.addElement(t.addParent(2, tempChildren));
			tempChildren.removeAllElements();
		}

		// add root, level 1
		t.root=t.addParent(1, devicenodes);

		//in addParent method the year nodes with level 3 will not have any children which removes the leaves form the tree

		return t;
	
	}





	public boolean verifyData(String[] bf2,DataNode[] SHAnodes,Vector<DataNode> relatedNodesInTree, FileWriter fw) throws java.rmi.RemoteException {
		try {
			boolean checkWithTree=false;
			for (int i = 0; i < bf2.length; i++) {
				System.out.println(bf2[i]);
				if(!bf.contains(bf2[i], true)){
					//// not included or need further verification with the tree
					checkWithTree=true;
				}
			}

			if(!checkWithTree){
				System.out.println("**confirmed with bf**");
				//if(PGW_Main.iflog)System.out.println("**confirmed with bf**");
				fw.write("**confirmed with bf**"+"\n");

				return true;
			}
			//if(PGW_Main.iflog)System.out.println("not confirmed with bf");
			fw.write("not confirmed with bf"+"\n");

			Vector<DataNode> rebuildedTreeDataNodes=new Vector<>();
			for (int i = 0; i < SHAnodes.length; i++) {
				rebuildedTreeDataNodes=createNodesforVerification(SHAnodes,relatedNodesInTree);
			}

			for (int i = 0; i < rebuildedTreeDataNodes.size(); i++) {
				boolean exists=false;
				int j = 0;
				for (; j < relatedNodesInTree.size(); j++) {
					if(rebuildedTreeDataNodes.elementAt(i).isEqualForRebuildedTree(relatedNodesInTree.elementAt(j))){
						System.out.print(relatedNodesInTree.size()+"related:");
						exists=true;
					}
				}
				if(!exists){
					//if(PGW_Main.iflog)System.out.println("not confirmed with tree");
					fw.write("not confirmed with tree"+"\n");
					return false;
				}
			}
			
			fw.write("**confirmed with tree**"+"\n");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return true;
	}


	public Vector<DataNode> createNodesforVerification(DataNode[] SHAnodes, Vector<DataNode> relatedNodesInTree){

		Vector<DataNode> NodesForVerification=new Vector<>();

		Tree t=new Tree();
		Vector<DataNode> yearnodes=new Vector<>();

		SHAnodes=Tree.sort(SHAnodes);

		//add year nodes, level 3
		Vector<DataNode> tempChildren=new Vector<>();

		/*	for(int i = 0; i < SHAnodes.length; i++){
			SHAnodes[i].print();
		}*/ 
		for(int i = 0; i < SHAnodes.length;) {
			while( i < SHAnodes.length &&
					(tempChildren.isEmpty() ||
							(tempChildren.elementAt(0).date.year== SHAnodes[i].date.year && 
							 tempChildren.elementAt(0).device.deviceName.equals(SHAnodes[i].device.deviceName)) )){
				tempChildren.addElement(SHAnodes[i]);
				i++;
			}

			yearnodes.addElement(t.addParent(3, tempChildren));
			NodesForVerification.addElement(yearnodes.lastElement());
			tempChildren.removeAllElements();
		}

		for(int i=0; i< relatedNodesInTree.size();i++){
			if(relatedNodesInTree.elementAt(i).level==3 && relatedNodesInTree.elementAt(i).date.year!=yearnodes.firstElement().date.year){
				yearnodes.addElement(relatedNodesInTree.elementAt(i));
			}
		}

		//add device nodes, level 2
		Vector<DataNode> devicenodes=new Vector<>();

		for (int i = 0; i < yearnodes.size(); ) {

			while(	i<yearnodes.size() &&
					(tempChildren.isEmpty() ||
							tempChildren.elementAt(0).device.deviceName.equals(yearnodes.elementAt(i).device.deviceName) )){
				tempChildren.addElement(yearnodes.elementAt(i));
				i++;
			}

			devicenodes.addElement(t.addParent(2, tempChildren));
			NodesForVerification.addElement(devicenodes.lastElement());
			tempChildren.removeAllElements();
		}

		for(int i=0; i< relatedNodesInTree.size();i++){
			if(relatedNodesInTree.elementAt(i).level==2 && !relatedNodesInTree.elementAt(i).device.deviceName.equals(devicenodes.firstElement().device.deviceName)){
				devicenodes.addElement(relatedNodesInTree.elementAt(i));
			}
		}

		// add root, level 1
		t.root=t.addParent(1, devicenodes);
		NodesForVerification.addElement(t.root);
		return NodesForVerification;
	}


}



class ListenToTheDoctor extends Thread{
	
	P_GW pgw;
	
	public ListenToTheDoctor(P_GW pgw) {
		this.pgw=pgw;
	}
	
	public void  run() {//listen to doctor
		
		try {
			
		
			//read IPs
			Scanner sc=new Scanner(new File("DevicesInfo.txt"));
			while(!sc.nextLine().equals("IP and ports:")) {
			}
			String pgwAddress=sc.nextLine();
			//String pgwIP=pgwAddress.split(" ")[1];
			int pgwPort=Integer.parseInt(pgwAddress.split(" ")[2]);
			String doctorAddress=sc.nextLine();
			String doctorsIP=doctorAddress.split(" ")[1];
			int doctorPort=Integer.parseInt(doctorAddress.split(" ")[2]);
			sc.close();
			
			System.out.println("pgw is ready to accept the connection from "+ pgwPort);
			
			while(true) {
				ServerSocket ss = new ServerSocket(pgwPort);
				Socket socket = ss.accept();
				ObjectInputStream is = new ObjectInputStream(socket.getInputStream());
				Message m = (Message) is.readObject();
				m.print();
				
				Message returnMessage= new Message("ack",false);
				if(m.getType().equals("askpgw")){
					returnMessage= new Message("askpgw",pgw.devices);
					System.out.println("received device request successfully through port:"+ pgwPort);
					sleep(10);

				}
				if(m.getType().equals("verify")){
					long time1s= System.nanoTime();  
					boolean verification=pgw.verifyData(m.bf2,m.SHAnodes,m.getRelatedDataNodesinTree(), PGW_Main.fw);
					long time2s= System.nanoTime();
					PGW_Main.fw.write("verification time in PGW: "+(time2s-time1s)/1000+" Micro second \n");
					System.out.print("verification time in PGW: "+(time2s-time1s)/1000+" Micro second  \n");
					PGW_Main.fw.flush();
					returnMessage= new Message("ack",verification);
					System.out.println("received verification request from doctor successfully through port:"+ pgwPort);
					sleep(10);

				}
				System.out.println("create socket:"+doctorsIP+" "+doctorPort);
				Socket sendToDsocket = new Socket(doctorsIP, doctorPort);
				System.out.println("start sending the message to the doctor the port: "+ doctorPort);
				ObjectOutputStream os = new ObjectOutputStream(sendToDsocket.getOutputStream());
				os.writeObject(returnMessage);
				returnMessage.print();
				
				sendToDsocket.close();
				ss.close();
			}
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}




class Update extends Thread{
	
	P_GW pgw;
	Tree oldTree;
	int tmpBfSize;
	
	public Update(P_GW pgw, int tmpBfSize) {
		this.pgw=pgw;
		this.tmpBfSize=tmpBfSize;
		
	}
	
	public void  run() {
		try {		
			
			DataNode[] allnewdataset=new DataNode[0];

			for (int i = 0; i < pgw.devices.length; i++) {
				DataNode [] newdata = pgw.devices[i].readDataPacket(pgw.devices[i].deviceName+"update");
				DataNode [] temp = new DataNode[allnewdataset.length+newdata.length];
				int j = 0;
				for (; j < allnewdataset.length; j++) {
					temp[j] = allnewdataset[j];
					System.out.println(temp[j].id);
				}
				for (; j < temp.length; j++) {
					//System.out.println(newdata[j-allnewdataset.length].id);
					temp[j]= newdata[j-allnewdataset.length];
					//System.out.println(temp[j].id);

				}
				allnewdataset=temp;
			}
			

			//////////////////// P GateWay //////////////////////////////


			pgw.addDataToBloomFilter(allnewdataset,PGW_Main.fw, tmpBfSize);
			
			long timeupdate1=System.currentTimeMillis();
			//if(PGW_Main.iflog)System.out.println("recive old tree and rebuild tree");
			PGW_Main.fw.write("build tree"+"\n");
			DataNode[] newleaves=new DataNode[allnewdataset.length];
			for (int i = 0; i < allnewdataset.length; i++) {
				newleaves[i]=new DataNode(allnewdataset[i].id, allnewdataset[i].date, allnewdataset[i].device, allnewdataset[i].value);
			}
			
			
			
			//send blocks server and receive tree//////////////////////////////
			for (int i = 0; i < pgw.devices.length; i++) {
				Message dataMessage= new Message("data", pgw.devices[i].dataToSend,null);
				for (int j = 0; j < pgw.devices[i].dataToSend.length; j++) {
					System.out.println(pgw.devices[i].dataToSend[j].id+" "+pgw.devices[i].dataToSend[j].date.year+" "+pgw.devices[i].dataToSend[j].device.deviceName+" "+pgw.devices[i].dataToSend[j].value+" "+pgw.devices[i].dataToSend[j].level);
				}
				oldTree=PGW_Main.SendToServer(dataMessage,pgw.devices[i].cloudIP,pgw.devices[i].cloudport).getTree();
			}
			
			
			long time1s= System.currentTimeMillis(); 
			Tree newtree=pgw.updateTree(newleaves,oldTree,PGW_Main.fw);

			long time2s= System.currentTimeMillis(); 

			System.out.println("required time to update tree "+(time2s-time1s)+" ms ");
			PGW_Main.fw.write("required time to update tree "+(time2s-time1s)+" ms \n");
			

			long timeupdate2=System.currentTimeMillis();

			//send tree/
			for (int i = 0; i < pgw.devices.length; i++) {
				Message treeMessage= new Message("data", new DataNode[0] ,newtree);
				boolean successUpdate=PGW_Main.SendToServer(treeMessage,pgw.devices[i].cloudIP,pgw.devices[i].cloudport).getAck();
				System.out.println("Was update successfull? "+successUpdate);
			}
			long timeupdate3=System.currentTimeMillis();

			System.out.println("Data sent");
			
			System.out.println("required time to update "+(timeupdate2-timeupdate1)+" ms ");
			PGW_Main.fw.write("required time to update "+(timeupdate2-timeupdate1)+" ms \n");
			System.out.println("required time to update and send "+(timeupdate3-timeupdate1)+" ms ");
			PGW_Main.fw.write("required time to update and send "+(timeupdate3-timeupdate1)+" ms \n");

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}


