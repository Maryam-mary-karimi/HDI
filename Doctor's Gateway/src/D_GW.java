import java.util.Vector;

public class D_GW{

	

	public String[] BF_required_For_BFVerification(Vector<DataNode> blocks, int tmpBfSize){
		String output[]=new String[blocks.size()];

		//try {
			//if(DGW_Main.iflog)System.out.println("send_To_PGW_For_BFVerification");

			//fw.write("send_To_PGW_For_BFVerification"+"\n");
		
		for (int i = 0; i < blocks.size(); i++) {

			//if(DGW_Main.iflog)System.out.println("block size "+blocks.size());
			//fw.write("block size "+blocks.size()+"\n");
			BloomFilter<String> tempBf=new BloomFilter<>(tmpBfSize, 1);
			//blocks.elementAt(i).print(fw);
			tempBf.add(blocks.elementAt(i).date.year+"/"+blocks.elementAt(i).date.month+"/"+blocks.elementAt(i).date.day+" "+blocks.elementAt(i).device.deviceName+" "+blocks.elementAt(i).value, false);//put each instance into a bloomfilter
			output[i]=tempBf.StringOfBits();
			tempBf.clear();//clear the temp bloom filter for next instance

			//if(DGW_Main.iflog)System.out.println(output[i]);
			//fw.write(output[i]+"\n");
		}
		//} catch (IOException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
		//}
		return output;

	}



	public DataNode[] tree_required_For_TreeVerification(Vector<DataNode> blocks){
		DataNode[] blocksArray=new DataNode[blocks.size()];
		//try {
		//if(DGW_Main.iflog)System.out.println("send_To_PGW_For_TreeVerification");
		
			//fw.write("send_To_PGW_For_TreeVerification"+"\n");
		
		
		
		for (int i = 0; i < blocks.size(); i++) {
			String s = HexUtils.getHex(blocks.elementAt(i).value.getBytes());
			Keccak keccak = new Keccak();
			blocks.elementAt(i).value =keccak.getHash(s, Parameters.SHA3_512);
			blocksArray[i]=(blocks.elementAt(i));
			//System.out.print("sha : ");blocksArray[i].print(fw);
		}
	//	} catch (IOException e) {
			// TODO Auto-generated catch block
	//		e.printStackTrace();
	//	}
		return blocksArray;
		
	}

}
