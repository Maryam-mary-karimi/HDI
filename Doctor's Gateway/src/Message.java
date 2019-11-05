
import java.io.Serializable;
import java.util.Vector;

public class Message implements Serializable {
	private static final long serialVersionUID = -5399605122490343339L;

	private String type;
	private Tree tree;
	private DataNode [] dataNodes; 
	private boolean ack;
	int size;

	private Device[] devices;

	String[] bf2;
	DataNode[] SHAnodes;

	private Vector<DataNode> queries;
	private Vector<DataNode> datablocks;
	private Vector<DataNode> extradatablocks;
	private Vector<DataNode> relatedDataNodesinTree;//for queries and data and verify


	public Message(String type, DataNode[] dataNodes, Tree tree){
		this.type=type;
		this.size=dataNodes.length;
		if(type.equals("data")) {
			this.dataNodes=dataNodes;
			this.tree=tree; 
		} 
	}


	public Message(String type, Vector<DataNode> queries){
		this.type=type;
		this.size=queries.size();
		if(type.equals("req")) {
			this.queries=queries;
		}
	} 

	public Message(String type, Device [] devices){
		this.type=type;
		this.size=devices.length;
		if(type.equals("askpgw")) {
			this.devices=devices;
		}
	} 


	public Message(String type, String[] bf2,DataNode[] SHAnodes,Vector<DataNode> relatedNodesInTree){
		this.type=type;
		this.size=relatedNodesInTree.size();
		if(type.equals("verify")) {
			this.bf2=bf2;
			this.SHAnodes=SHAnodes;
			this.relatedDataNodesinTree=relatedNodesInTree;
		}
	} 


	public Message(String type, boolean ack ){
		this.type=type;
		this.size=1;
		if(type.equals("ack")) {
			this.ack=ack;
		}
	}

	public String getType() {
		return type;
	}

	public int getSize() {
		return size;
	}

	public Tree getTree() {
		return tree;
	}

	public DataNode[] getDataNodes() {
		return dataNodes;
	}

	public Vector<DataNode> getQueries()  {
		return queries;
	}

	public Vector<DataNode> getDataBlocks()  {
		return datablocks;
	}

	public Vector<DataNode> getExtraDataBlocks()  {
		return extradatablocks;
	}

	public Vector<DataNode> getRelatedDataNodesinTree(){
		return relatedDataNodesinTree;
	}


	public Device[] getDevices() {
		return devices;
	}

	public boolean getAck() {
		return ack;
	}


	public void setQueries(Vector<DataNode> queries)  {
		this.queries=queries;
	}

	public void setDataBlocks(Vector<DataNode> datablocks)  {
		this.datablocks=datablocks;
	}

	public void setExtraDataBlocks(Vector<DataNode> extradatablocks)  {
		this.extradatablocks=extradatablocks;
	}

	public void setRelatedDataNodesinTree(Vector<DataNode> relatedDataNodesinTree){
		this.relatedDataNodesinTree=relatedDataNodesinTree;
	}

	public void print() {
		System.out.print("message: "+ type+", size: "+size);
		if(type.equals("data")) {
			//for (int i = 0; i < dataNodes.length; i++) {
			//	System.out.println(dataNodes[i].id+" "+dataNodes[i].date.year+" "+dataNodes[i].device.deviceName+" "+dataNodes[i].value+" "+dataNodes[i].level);
			//}
			System.out.println("\n___tree____");
			Vector<DataNode> parents=new Vector<>();
			parents.add(tree.root);
			while(!parents.isEmpty()){
				parents=printArray(parents);
			}	
		}
		else if(type.equals("req")) {
			for(int i=0;i<queries.size();i++) {
				System.out.print(queries.elementAt(i).id+" "+ queries.elementAt(i).value+ " "+queries.elementAt(i).date.year+ "/"+queries.elementAt(i).date.month+ "/"+queries.elementAt(i).date.day+ " "+queries.elementAt(i).device.deviceName);
			}
		}
		else if(type.equals("ack")) {
			System.out.print(ack);
		}
		else if(type.equals("verify")) {
			System.out.print(bf2.toString()+" ");
			for(int i=0;i<relatedDataNodesinTree.size();i++) {
				System.out.println(relatedDataNodesinTree.elementAt(i).id+""
						+ " "+relatedDataNodesinTree.elementAt(i).value+""
								+ " "+ relatedDataNodesinTree.elementAt(i).date+ " "
										+ ""+relatedDataNodesinTree.elementAt(i).device);
			}
		}

		System.out.println();
	}

	public Vector<DataNode> printArray(Vector<DataNode> nodes){
		Vector<DataNode> children= new Vector<>();
		for (int i = 0; i < nodes.size(); i++) {
			if(nodes.elementAt(i).id.equals("|")){
				System.out.print("|");
			}
			else{
				System.out.print(nodes.elementAt(i).id+" "+nodes.elementAt(i).date.year+" "+nodes.elementAt(i).device.deviceName+" * ");	
			}
			for (int j = 0; j < nodes.elementAt(i).numOfchildren; j++) {
				if(!nodes.elementAt(i).children[j].id.equals("|"))children.add(nodes.elementAt(i).children[j]);

			}
			if (!children.isEmpty()) {
				DataNode n=new DataNode("|", null, null, "|");
				children.add(n);
			}
		}
		System.out.println();
		return children;
	}
}