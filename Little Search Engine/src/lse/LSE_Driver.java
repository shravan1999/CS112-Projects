package lse;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public class LSE_Driver {

	public static void main(String[] args) {

		LittleSearchEngine myEng = new LittleSearchEngine();
		try {
			myEng.makeIndex("docs.txt", "noisewords.txt");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		
		
		for(String s : myEng.keywordsIndex.keySet()) {
			
			
			System.out.print(s + "	{");
			
			ArrayList<Occurrence> value = myEng.keywordsIndex.get(s);
			for(int i = 0; i < value.size(); i++) {
				System.out.print("(" + value.get(i).document + ", " + value.get(i).frequency + "); ");
			}
			
			System.out.println(" } ");
		}
		
		
		String kw1 = "test", kw2 = "salient";
		
		System.out.println("calling top 5 search on " + kw1 + "  and  " + kw2 +"\n\n");
		ArrayList<String> arr = new ArrayList<>();
		arr = myEng.top5search(kw1, kw2);


		for(int i = 0; i < arr.size(); i++) {
			System.out.print(arr.get(i) + ", ");
		}
		
		
		System.out.println("\n\nyoloswag  " + myEng.keywordsIndex);
		
	}

}
