import java.io.FileWriter;
import java.io.IOException;

public class Device{
	String deviceName;
	int numberOfReceivedPackets;
	//http://radacad.com/be-fitbit-bi-developer-in-few-steps-first-step-get-data-from-csv
	//https://www.linkedin.com/pulse/fitbit-bi-developer-few-steps-first-step-get-data-from-reza-rad/
	//fitbit data:
	//Date, Calories, Burned, Steps, Distance, Floors, Minutes Sedentary, Minutes Lightly Active,Minutes fairly Active,Minutes very Active, activity calories
	//01-07-2015, 3953,13361,10.64,16,1058,196,71,87,2393

	public Device(String name) {
		deviceName=name;
	}

	public DataNode createRandomDataPacket(int id, FileWriter fw) {
		
		String s="";
		for(int i=0;i<2048;i++){
			double c=Math.random()*26;
			s+=String.valueOf((char)((int)c + 64));
		}
		double year= Math.random()*3+2015;
		double month=Math.random()*11+1;
		double day=Math.random()*30+1;
		DataNode n=new DataNode(id+"", new Date((int)year, (int) month,(int)day), this, s);
		try {
			if(PGW_Main.iflog)System.out.println("createRandomDataPacket "+n.value);
			fw.write("createRandomDataPacket "+n.value+"\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return n;
	}
}