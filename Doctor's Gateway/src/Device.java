import java.io.Serializable;


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

	
}