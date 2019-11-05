import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.Scanner;

public class Patient implements Serializable {

	private static final long serialVersionUID = 3L;
	String name;
	Device[] devices;
	
	public Patient(String name) {
		this.name=name;
	}
	Device[] getDevices(){
		try {
			Scanner sc=new Scanner(new File("DevicesInfo.txt"));
			devices=new Device[sc.nextInt()];
			sc.nextLine();
			int i=0;
			while(sc.hasNext()) {
				String temp[]=sc.nextLine().split(", ");
				if(DGW_Main.iflog)System.out.println(temp[0]+" "+ temp[1]+" "+ temp[2]);
				Device dev=new Device(temp[0], temp[1], Integer.parseInt(temp[2]));
				devices[i++]=dev;
			}
			
			sc.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		return devices;
	}
	
}

