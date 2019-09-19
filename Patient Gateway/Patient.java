public class Patient{
	String name;
	
	public Patient(String name) {
		this.name=name;
	}
	Device[] creatDevices(double maxNumofPackets){
		
		Device devA=new Device("A");
		double temp=Math.random()*maxNumofPackets;
		devA.numberOfReceivedPackets=(int) temp;
		
		Device devB=new Device("B");
		temp=Math.random()*(maxNumofPackets-(double)devA.numberOfReceivedPackets);
		devB.numberOfReceivedPackets=(int) temp;
		
		Device devC=new Device("C");
		temp=maxNumofPackets-devA.numberOfReceivedPackets-devB.numberOfReceivedPackets;
		devC.numberOfReceivedPackets=(int) temp;

		Device[] devices={devA, devB, devC};
		return devices;
	}
	
}

