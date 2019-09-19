import java.util.Vector;

public class Doctor{
	
	Date queriedDate;
	Device queriedDevice;
	Vector<DataNode> requiredData;
	Vector<DataNode> requestForBlock(Device devices[]){
		Vector<DataNode> queries=new Vector<>();
		double year= Math.random()*3+2015;
		double month=Math.random()*11+1;
		double day=Math.random()*30+1;
		queriedDate=new Date((int)year, (int) month,(int)day);
		double dev= Math.random();
		if(dev<0.33){
			queriedDevice=devices[0];
		}
		else if(dev>0.66){
			queriedDevice=devices[1];
		}
		else {
			queriedDevice=devices[2];
		}
		DataNode query=new DataNode(null, queriedDate, queriedDevice, null);
		queries.add(query);
		return queries;
	}

}
