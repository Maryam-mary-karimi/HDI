import java.io.FileWriter;
import java.io.IOException;

class DataNode{

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
			if(PGW_Main.iflog)System.out.println(this.id+" "+this.date.year+" "+this.device.deviceName+" "+this.value+" "+this.level);
			fw.write(this.id+" "+this.date.year+" "+this.device.deviceName+" "+this.value+" "+this.level+"\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	boolean isEqualto(DataNode datanode){
		if(this.device.deviceName.equals(datanode.device.deviceName)
				&& this.date.year==datanode.date.year && this.date.month==datanode.date.month && this.date.day==datanode.date.day
				&& this.value.equals(datanode.value)){
			return true;
		}
		return false;
	}
	
	boolean isEqualForRebuildedTree(DataNode datanode){
		if(this.device.deviceName.equals(datanode.device.deviceName)
				&& this.value.equals(datanode.value)){
			return true;
		}
		return false;
	}
	
}

class Date{
	int day;
	int month;
	int year;
	public Date(int year, int month, int day) {
		this.year=year;
		this.month=month;
		this.day=day;
	}

}