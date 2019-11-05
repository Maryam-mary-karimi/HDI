import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.Scanner;

public class Device implements Serializable {

	private static final long serialVersionUID = 2L;
	String deviceName;
	int numberOfReceivedPackets=0;// sequence number or id
	int cloudport;
	String cloudIP;
	DataNode[] dataToSend; 
	
	//http://radacad.com/be-fitbit-bi-developer-in-few-steps-first-step-get-data-from-csv
	//https://www.linkedin.com/pulse/fitbit-bi-developer-few-steps-first-step-get-data-from-reza-rad/
	//fitbit data:
	//Date, Calories, Burned, Steps, Distance, Floors, Minutes Sedentary, Minutes Lightly Active,Minutes fairly Active,Minutes very Active, activity calories
	//01-07-2015, 3953,13361,10.64,16,1058,196,71,87,2393

	public Device(String name, String cloudIP, int cloudport) {
		deviceName=name;
		this.cloudIP=cloudIP;
		this.cloudport=cloudport;
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
			//if(PGW_Main.iflog)System.out.println("createRandomDataPacket "+n.value);
			fw.write("createRandomDataPacket "+n.value+"\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return n;
	}


	public DataNode[] readDataPacket(String fileName) {

		//Date : 4/5/2016 , Device: HRM ,  Data : { Heart Rate : 83.47432808155699 }
		
		try {
			Scanner sc=new Scanner(new File(fileName+".txt"));
			int numberOfPackets=sc.nextInt();
			sc.nextLine();
			dataToSend=new DataNode[numberOfPackets];
			int i=0;
			while(sc.hasNext()) {
				String temp=sc.nextLine();
				
				System.out.println("Date"+temp.substring(temp.indexOf("Date")+7, temp.indexOf(" ,") ) );
				
				String date[] = temp.substring(temp.indexOf("Date")+7, temp.indexOf(" ,") ).split("/");

				int year= Integer.parseInt(date[2]);
				int month=Integer.parseInt(date[1]);
				int day=Integer.parseInt(date[0]);

				String data=temp.substring(temp.indexOf("Data : ")+7);
				System.out.println(data);
				DataNode n=new DataNode(numberOfReceivedPackets+"", new Date(year, month,day), this, data);
				numberOfReceivedPackets++;
				dataToSend[i++]=n;
				//if(PGW_Main.iflog)System.out.println("readDataPacket "+n.value);
				//fw.write("readDataPacket "+n.value+"\n");
			}
			sc.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return dataToSend;

	}
}