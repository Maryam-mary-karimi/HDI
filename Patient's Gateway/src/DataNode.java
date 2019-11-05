import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;

class DataNode implements Serializable {

	private static final long serialVersionUID = 1L;
	
	
	DataNode parent;
	DataNode[] children=new DataNode[200];
	String[] childrenId=new String[200];
	int numOfchildren;
	Date date;
	Device device;
	String id;
	String value; 
	int level;


	public DataNode(String id, Date date, Device device, String value) {
		this.id=id;
		this.date=date;
		this.device=device;
		this.value=value;
	} 

	void print(FileWriter fw){
		try {
			System.out.println(this.id+" "+this.date.year+" "+this.device.deviceName+" "+this.value+" "+this.level);
			fw.write(this.id+" "+this.date.year+" "+this.device.deviceName+" "+this.value+" "+this.level+"\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	} 

	boolean isEqualto(DataNode datanode){
		System.out.println("node1: "+this.id+" "+this.date.year+" "+this.device.deviceName+" "+this.value);
		System.out.println("node2: "+datanode.id+" "+datanode.date.year+" "+datanode.device.deviceName+" "+datanode.value);

		if(this.device.deviceName.equals(datanode.device.deviceName)
				&& this.date.year==datanode.date.year && this.date.month==datanode.date.month && this.date.day==datanode.date.day
				&& this.value.equals(datanode.value)){
			
			return true;
		}
		return false;
	}
	
	boolean isEqualForRebuildedTree(DataNode datanode){
		System.out.println("node1: "+this.id+" "+this.date.year+" "+this.device.deviceName+" "+this.value);
		System.out.println("node2: "+datanode.id+" "+datanode.date.year+" "+datanode.device.deviceName+" "+datanode.value);
		
		if(this.device.deviceName.equals(datanode.device.deviceName)
				&& this.value.equals(datanode.value)){
			return true;
		}
		return false;
	}
	
}

class Date implements Serializable{

	private static final long serialVersionUID = 6L;
	int day;
	int month;
	int year;
	public Date(int year, int month, int day) {
		this.year=year;
		this.month=month;
		this.day=day;
	}
	
	

}