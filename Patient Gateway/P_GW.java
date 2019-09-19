import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

public class P_GW extends java.rmi.server.UnicastRemoteObject implements PGW_interface{

	
	BloomFilter<String> bf;
	Device[] devices;
	Server clouds[];
	
	public  P_GW(Device[] devices,Server[] clouds) throws java.rmi.RemoteException {  
		super();    

		this.devices=devices;
		this.clouds=clouds;
	}  

	public Device[] getDevices() {
		return devices;
	}
	
	public Server[] getClouds() {
		return clouds;
	}




	public DataNode[][] seperateDataToStoreInClouds(DataNode[] blocks, Device devices[] ){
		//send data to the clouds
		DataNode Adata[]=new DataNode[devices[0].numberOfReceivedPackets];
		int a=0;
		DataNode Bdata[]=new DataNode[devices[1].numberOfReceivedPackets];
		int b=0;
		DataNode Cdata[]=new DataNode[devices[2].numberOfReceivedPackets];
		int c=0;

		for (int i = 0; i < blocks.length; i++) {
			if(blocks[i].device.deviceName.equals("A")){
				Adata[a++]=blocks[i];
			}
			if(blocks[i].device.deviceName.equals("B")){
				Bdata[b++]=blocks[i];
			}
			if(blocks[i].device.deviceName.equals("C")){
				Cdata[c++]=blocks[i];
			}
		}

		DataNode[][] seperatedDN={Adata,Bdata,Cdata};
		return seperatedDN;
	}


	public BloomFilter<String> createBloomFilter(DataNode [] leaves,FileWriter fw, int bfSize, int tmpBfSize){
		try {
			bf=new BloomFilter<>(bfSize, leaves.length);
			bf.setK(5);
			BloomFilter<String> tempBf=new BloomFilter<>(tmpBfSize, 1);
			for(int i=0; i<leaves.length;i++){
				tempBf.add(leaves[i].date+" "+leaves[i].device.deviceName+" "+leaves[i].value, false);//put each instance into a bloomfilter
				bf.add(tempBf.StringOfBits(fw), true);//hash that bloom filter in a main bloom filter
				tempBf.clear();//clear the temp bloom filter for next instance
			}
			if(PGW_Main.iflog)System.out.println("bloom filter size "+bf.getBitSetSize());
			fw.write("bloom filter size "+bf.getBitSetSize()+"\n");

			
			if(PGW_Main.iflog)System.out.println("temp bloom filter size "+tempBf.getBitSetSize());
			fw.write("temp bloom filter size "+tempBf.getBitSetSize()+"\n");
			
			
			if(PGW_Main.iflog)System.out.println("bloom filter falsepositive "+bf.getFalsePositiveProbability(fw,true));
			fw.write("bloom filter falsepositive "+bf.getFalsePositiveProbability(fw,true)+"\n");
			

			if(PGW_Main.iflog)System.out.println("temp bloom filter falsepositive "+tempBf.getFalsePositiveProbability(fw,false));
			fw.write("temp bloom filter falsepositive "+tempBf.getFalsePositiveProbability(fw,false)+"\n");
			//once upon a time it was 1.8555504930700443E-42
			
			
			if(PGW_Main.iflog)System.out.println("bloom filter falseNegative "+bf.getFalseNegativeProbability(fw,true));
			fw.write("bloom filter falseNegative "+bf.getFalseNegativeProbability(fw,true)+"\n");
			
			//if(My_IoT.iflog)System.out.println(":"+bf.getBitSet());
			if(PGW_Main.iflog)System.out.println("k: "+bf.getK());
			fw.write("k: "+bf.getK()+"\n");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bf;
	}

	public Tree buildTheTree(DataNode[] leaves,FileWriter fw){
		Tree t=new Tree();

		Vector<DataNode> yearnodes=new Vector<>();

		leaves=Tree.sort(leaves);

		//add leaves, level 4
		for (int i = 0; i < leaves.length; i++) {
			t.addLeaves(leaves[i]);
		}

		for(int i = 0; i < leaves.length; i++){
			leaves[i].print(fw);
		}


		//add year nodes, level 3
		Vector<DataNode> tempChildren=new Vector<>();


		for (int i = 0; i < leaves.length;) {
			while(	i<leaves.length &&
					(tempChildren.isEmpty() ||
							(tempChildren.elementAt(0).date.year== leaves[i].date.year && 
							tempChildren.elementAt(0).device.equals(leaves[i].device)) )){
				tempChildren.addElement(leaves[i]);
				i++;
			}

			yearnodes.addElement(t.addParent(3, tempChildren,fw));
			tempChildren.removeAllElements();
		}

		//add device nodes, level 2
		Vector<DataNode> devicenodes=new Vector<>();

		for (int i = 0; i < yearnodes.size(); ) {

			while(	i<yearnodes.size() &&
					(tempChildren.isEmpty() ||
							tempChildren.elementAt(0).device.equals(yearnodes.elementAt(i).device) )){
				tempChildren.addElement(yearnodes.elementAt(i));
				i++;
			}

			devicenodes.addElement(t.addParent(2, tempChildren,fw));
			tempChildren.removeAllElements();
		}

		// add root, level 1
		t.root=t.addParent(1, devicenodes,fw);

		//remove leaves
		//..............later

		return t;

	}


	public boolean verifyData(String[] bf2,DataNode[] SHAnodes,Vector<DataNode> relatedNodesInTree, FileWriter fw)throws java.rmi.RemoteException {
		try {
			boolean checkWithTree=false;
			for (int i = 0; i < bf2.length; i++) {
				if(!bf.contains(bf2[i], true)){
					//// not included or need further verification with the tree
					checkWithTree=true;
				}
			}

			if(!checkWithTree){
				if(PGW_Main.iflog)System.out.println("**confirmed with bf**");
				fw.write("**confirmed with bf**"+"\n");
				
				return true;
			}
			if(PGW_Main.iflog)System.out.println("not confirmed with bf");
			fw.write("not confirmed with bf"+"\n");

			Vector<DataNode> rebuildedTreeDataNodes=new Vector<>();
			for (int i = 0; i < SHAnodes.length; i++) {
				rebuildedTreeDataNodes=createNodesforverification(SHAnodes,relatedNodesInTree,fw);
			}

			for (int i = 0; i < rebuildedTreeDataNodes.size(); i++) {
				boolean exists=false;
				for (int j = 0; j < relatedNodesInTree.size(); j++) {
					if(rebuildedTreeDataNodes.elementAt(i).isEqualForRebuildedTree(relatedNodesInTree.elementAt(j))){
						exists=true;
					}
				}
				if(!exists){
					return false;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}


	public Vector<DataNode> createNodesforverification(DataNode[] SHAnodes, Vector<DataNode> relatedNodesInTree,FileWriter fw){

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
			while(	i<SHAnodes.length &&
					(tempChildren.isEmpty() ||
							(tempChildren.elementAt(0).date.year== SHAnodes[i].date.year && 
							tempChildren.elementAt(0).device.equals(SHAnodes[i].device)) )){
				tempChildren.addElement(SHAnodes[i]);
				i++;
			}

			yearnodes.addElement(t.addParent(3, tempChildren,fw));
			NodesForVerification.addElement(t.addParent(3, tempChildren,fw));
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
							tempChildren.elementAt(0).device.equals(yearnodes.elementAt(i).device) )){
				tempChildren.addElement(yearnodes.elementAt(i));
				i++;
			}

			devicenodes.addElement(t.addParent(2, tempChildren,fw));
			NodesForVerification.addElement(t.addParent(2, tempChildren,fw));
			tempChildren.removeAllElements();
		}

		for(int i=0; i< relatedNodesInTree.size();i++){
			if(relatedNodesInTree.elementAt(i).level==2 && !relatedNodesInTree.elementAt(i).device.deviceName.equals(devicenodes.firstElement().device.deviceName)){
				devicenodes.addElement(relatedNodesInTree.elementAt(i));
			}
		}

		// add root, level 1
		t.root=t.addParent(1, devicenodes,fw);
		NodesForVerification.addElement(t.root);
		return NodesForVerification;
	}


}