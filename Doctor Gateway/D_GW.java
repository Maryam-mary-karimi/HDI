import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

public class D_GW{

	public Server sendRequestToCloud(Server clouds[], DataNode query){
		if(query.device.deviceName.equals("A")){
			return clouds[0];
		}
		else if(query.device.deviceName.equals("B")){
			return clouds[1];
		}
		else{
			return clouds[2];
		}
	}

	public String[] send_To_PGW_For_BFVerification(Vector<DataNode> blocks, int tmpBfSize, FileWriter fw){
		String output[]=new String[blocks.size()];

		try {
			if(DGW_Main.iflog)System.out.println("send_To_PGW_For_BFVerification");

			fw.write("send_To_PGW_For_BFVerification"+"\n");
		
		for (int i = 0; i < blocks.size(); i++) {

			if(DGW_Main.iflog)System.out.println("block size "+blocks.size());
			fw.write("block size "+blocks.size()+"\n");
			BloomFilter<String> tempBf=new BloomFilter<>(tmpBfSize, 1);
			blocks.elementAt(i).print(fw);
			tempBf.add(blocks.elementAt(i).date+" "+blocks.elementAt(i).device.deviceName+" "+blocks.elementAt(i).value, false);//put each instance into a bloomfilter
			output[i]=tempBf.StringOfBits(fw);
			tempBf.clear();//clear the temp bloom filter for next instance

			if(DGW_Main.iflog)System.out.println(output[i]);
			fw.write(output[i]+"\n");
		}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return output;

	}



	public DataNode[] send_To_PGW_For_TreeVerification(Vector<DataNode> blocks,FileWriter fw){
		DataNode[] blocksArray=new DataNode[blocks.size()];
		try {
		if(DGW_Main.iflog)System.out.println("send_To_PGW_For_TreeVerification");
		
			fw.write("send_To_PGW_For_TreeVerification"+"\n");
		
		
		
		for (int i = 0; i < blocks.size(); i++) {
			String s = HexUtils.getHex(blocks.elementAt(i).value.getBytes());
			Keccak keccak = new Keccak();
			blocks.elementAt(i).value =keccak.getHash(s, Parameters.SHA3_512);
			blocksArray[i]=(blocks.elementAt(i));
			blocksArray[i].print(fw);
		}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return blocksArray;
		
	}

}
