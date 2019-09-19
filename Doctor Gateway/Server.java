import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

public class Server{
	Tree tree;
	DataNode[] datablocks;

	public Server(DataNode[] datablocks, Tree tree) {
		this.datablocks=datablocks;
		this.tree=tree;
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
					replyall.add(datablocks[i]);
					if(datablocks[i].date.month==queries.elementAt(j).date.month) {
						replySpecific.add(datablocks[i]);
					}
				}
			}
		}
		if (replySpecific.isEmpty()) {
			for (int i = 0; i < replyall.size(); i++) {
				replySpecific.addElement(replyall.elementAt(i));
			}
		}
		try {
			if(DGW_Main.iflog)System.out.println("exists "+queriedDataExists);
			fw.write("exists "+queriedDataExists+"\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(int i = 0; i < replySpecific.size(); i++){
			replySpecific.elementAt(i).print(fw);
		}
		
		Vector output=new Vector<>();
		output.add(replySpecific);
		output.add(replyall);
		return output;
	}

	public Vector<DataNode> SendTheRelatedPartOfTheTree(Vector<DataNode> queries,FileWriter fw){
		//find related nodes
		Vector<DataNode> relatedNodes=new Vector<>();
		Vector<DataNode> notvisitedParts=new Vector<>();

		try {
			if(DGW_Main.iflog)System.out.println("SendTheRelatedPartOfTheTree");
			fw.write("SendTheRelatedPartOfTheTree"+"\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		notvisitedParts.add(tree.root);

		while(!notvisitedParts.isEmpty()){
			DataNode visitingNode=notvisitedParts.remove(0);
			for (int i = 0; i < queries.size(); i++) {
				if(visitingNode.level<3 ||
						(visitingNode.level==3 && visitingNode.device.equals(queries.elementAt(i).device))
						||
						(visitingNode.level==4 && visitingNode.device.equals(queries.elementAt(i).device)
						&& visitingNode.date.year==queries.elementAt(i).date.year)
						){
					relatedNodes.add(visitingNode);
					visitingNode.print(fw);
					for(int j=0; j<visitingNode.numOfchildren;j++){
						notvisitedParts.addElement(visitingNode.children[j]);
					}
				}
			}

		}


		return relatedNodes;
	}
}
