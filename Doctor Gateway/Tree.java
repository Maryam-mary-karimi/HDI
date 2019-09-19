import java.io.FileWriter;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Vector;



public class Tree{

	DataNode root;
	int numOfNodes;


	public static DataNode[] sort(DataNode arr[])
	{
		int n = arr.length;
		for (int i=1; i<n; ++i)
		{
			DataNode key = arr[i];
			int j = i-1;

			/* Move elements of arr[0..i-1], that are greater than key, to one position ahead of their current position */
			while (j>=0 && ( arr[j].device.deviceName.compareTo(key.device.deviceName)>0 
					|| (arr[j].device.deviceName.compareTo(key.device.deviceName)==0 && arr[j].date.year>key.date.year )))
			{
				arr[j+1] = arr[j];
				j = j-1;
			}
			arr[j+1] = key;
		}

		return arr;
	}



	public void addLeaves(DataNode node){
		String s = HexUtils.getHex(node.value.getBytes());
		Keccak keccak = new Keccak();
		node.value =keccak.getHash(s, Parameters.SHA3_512);
		node.level=4;
		numOfNodes++;
	}

	public void addChild(DataNode node, DataNode parent) {
		//parent.print();
		if (numOfNodes==0) {
			root=node;
		}
		else if (parent.numOfchildren<4) {
			node.parent=parent;
			parent.children[parent.numOfchildren]=node;
			parent.childrenId[parent.numOfchildren]=node.id;
			parent.numOfchildren++;
			//numOfNodes++;	
		}
		else {//change later ????????????? for having 4 per branch
			node.parent=parent;
			parent.children[parent.numOfchildren]=node;
			parent.childrenId[parent.numOfchildren]=node.id;
			parent.numOfchildren++;
		}
	}


	//levels in general: 1=root, 2=device, 3=year, 4=leaves that be omitted
	public DataNode addParent(int level, Vector<DataNode> children,FileWriter fw) {
		String valueForParent=children.elementAt(0).value;
		for (int i = 1; i < children.size(); i++) {
			valueForParent=xor(valueForParent,children.elementAt(i).value);
		}
		try {
			String hmac = HmacSha1Signature.calculateRFC2104HMAC(valueForParent, "gatewaykey");
			valueForParent=hmac;
		} catch (InvalidKeyException | SignatureException | NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		DataNode n;
		switch (level){
		case 3:
			n=new DataNode((children.elementAt(0).date.year)+"", new Date(children.elementAt(0).date.year,1,1), children.elementAt(0).device,valueForParent );
			numOfNodes++;
			n.print(fw);
			break;
		case 2:
			n=new DataNode(children.elementAt(0).device.deviceName, new Date(children.elementAt(0).date.year,1,1), children.elementAt(0).device,valueForParent );
			numOfNodes++;
			n.print(fw);
			break;
		case 1:
			n=new DataNode("root", new Date(0,0,0) , new Device("allDevices") , valueForParent);
			numOfNodes++;
			n.print(fw);
			break;
		default:
			n=new DataNode(null, null, null , valueForParent);
			numOfNodes++;
			n.print(fw);
			break;
		}

		for (int i = 0; i < children.size(); i++) {
			addChild(children.elementAt(i), n);
		}
		n.level=level;
		return n;
	}


	public String xor(String s1, String s2){
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < s1.length(); i++)
			sb.append((char)(s1.charAt(i) ^ s2.charAt(i % s2.length())));
		return sb.toString();
	}


	public void PrintTree(FileWriter fw){
		try {
			if(DGW_Main.iflog)System.out.println("___print____");
			fw.write("___print____"+"\n");

			Vector<DataNode> parents=new Vector<>();
			parents.add(root);
			while(!parents.isEmpty()){
				parents=printArray(parents,fw);
				fw.write("\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public void printTreeNodes(DataNode datanode, FileWriter fw){
		try {
			if(datanode.id.equals("|")){
				if(DGW_Main.iflog)System.out.print("|");
				fw.write("|");
			}
			else{
				if(DGW_Main.iflog)System.out.print(datanode.id+" "+datanode.date.year+" "+datanode.device.deviceName+" * ");
				fw.write(datanode.id+" "+datanode.date.year+" "+datanode.device.deviceName+" * ");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Vector<DataNode> printArray(Vector<DataNode> nodes, FileWriter fw){
		Vector<DataNode> children= new Vector<>();
		for (int i = 0; i < nodes.size(); i++) {
			printTreeNodes(nodes.elementAt(i), fw);
			for (int j = 0; j < nodes.elementAt(i).numOfchildren; j++) {
				if(!nodes.elementAt(i).children[j].id.equals("|"))children.add(nodes.elementAt(i).children[j]);

			}
			if (!children.isEmpty()) {
				DataNode n=new DataNode("|", null, null, "|");
				children.add(n);
			}
		}
		if(DGW_Main.iflog)System.out.println();//fw ro bade seda zadan method gozashtam
		return children;
	}



	/*public void printChildren(DataNode parent){
		for (int i = 0; i < parent.numOfchildren; i++) {
			if(!parent.equals(root))if(false)if(My_IoT.iflog)System.out.println("*"+parent.id+" "+parent.date+" "+parent.device.deviceName+" "+parent.value+"*");
			if(false)if(My_IoT.iflog)System.out.print(parent.children[i].id+" "+parent.children[i].date+" "+parent.children[i].device.deviceName+" "+parent.children[i].value+"  |  ");
		}
		if(false)if(My_IoT.iflog)System.out.println();
		for (int i = 0; i < parent.numOfchildren; i++) {
			printChildren(parent.children[i]);
		}
	}*/

}

