package friends;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class FriendsTester {

	public static void main(String[] args) {
		
		Graph g = null;
		
		try {
			g = new Graph(new Scanner(new File("myTest.txt")));
		} catch (FileNotFoundException e) {
			System.out.println("file not found");
			return;
		}
		
//		System.out.println(Friends.cliques(g, "foreign"));
//		System.out.println(Friends.shortestChain(g, "bmw", "audi"));
		System.out.println(Friends.connectors(g));
		
	}

}
