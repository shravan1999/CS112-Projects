package friends;

import structures.Queue;
import structures.Stack;

import java.util.*;

public class Friends {

	/**
	 * Finds the shortest chain of people from p1 to p2.
	 * Chain is returned as a sequence of names starting with p1,
	 * and ending with p2. Each pair (n1,n2) of consecutive names in
	 * the returned chain is an edge in the graph.
	 * 
	 * @param g Graph for which shortest chain is to be found.
	 * @param p1 Person with whom the chain originates
	 * @param p2 Person at whom the chain terminates
	 * @return The shortest chain from p1 to p2. Null if there is no
	 *         path from p1 to p2
	 */
	public static ArrayList<String> shortestChain(Graph g, String p1, String p2) {
		
		Queue<Person> q = new Queue<>();
		boolean[] visited = new boolean[g.members.length];
		int[] prevVisited = new int[g.members.length];
		
		Person start = g.members[g.map.get(p1)];
		Person target = g.members[g.map.get(p2)];
		boolean found = false;
		
		
		for(int i = 0; i < prevVisited.length; i++) {
			prevVisited[i] = -1;
		}
		
		
		q.enqueue(start);
		
		while(q.isEmpty() == false) {
			
			Person curr = q.dequeue();
			//found target
			if(curr == target) {
				found = true;
				break;
			}
			
			int num = g.map.get(curr.name);
			visited[num] = true;
			
			Friend friendPtr = g.members[num].first;
			while(friendPtr != null) {

				if(visited[friendPtr.fnum] == false) {
					
					q.enqueue(g.members[friendPtr.fnum]);
					prevVisited[friendPtr.fnum] = num;
					
					visited[friendPtr.fnum] = true;
				}
				
				friendPtr = friendPtr.next;
			}
			
		}
		
		
		if(!found)
			return null;
		
		
		Stack<String> s = new Stack<>();
		
		int targetNum = g.map.get(target.name);
		int prevNum = prevVisited[targetNum];
		s.push(target.name);
		
		while(prevNum != -1) {
			s.push(g.members[prevNum].name);
			prevNum = prevVisited[prevNum];
		}
		
		
		ArrayList<String> arr = new ArrayList<>();
		while(!s.isEmpty()) {
			arr.add(s.pop());
		}
		
		return arr;
		
	}
	
	/**
	 * Finds all cliques of students in a given school.
	 * 
	 * Returns an array list of array lists - each constituent array list contains
	 * the names of all students in a clique.
	 * 
	 * @param g Graph for which cliques are to be found.
	 * @param school Name of school
	 * @return Array list of clique array lists. Null if there is no student in the
	 *         given school
	 */
	public static ArrayList<ArrayList<String>> cliques(Graph g, String school) {
		
		ArrayList<ArrayList<String>> a = new ArrayList<ArrayList<String>>();
		
		boolean[] visited = new boolean[g.members.length];
		
		
		for(int i = 0; i < g.members.length; i++) {
			
			
			ArrayList<String> temp = new ArrayList<String>();
			Person currP = g.members[i];
			int currNum = g.map.get(currP.name);
			
			if(currP.school == null)
				continue;
		
			if(currP.school.equals(school) && visited[currNum] == false) {
				cliqueBFS(g, school, currP, visited, temp);
				a.add(temp);
			}
			
		}
		
		return a;
		
	}
	
	private static void cliqueBFS(Graph g, String school, Person p, 
			boolean[] visited, ArrayList<String> a) {
		
		Queue<Person> q = new Queue<>();
		
		q.enqueue(p);
		
		while(q.isEmpty() == false) {
			
			Person curr = q.dequeue();
			a.add(curr.name);
			
			int currNum = g.map.get(curr.name);
			visited[currNum] = true;
			
			Friend friendPtr = g.members[currNum].first;
			
			while(friendPtr != null) {
				
				String friendSchool = g.members[friendPtr.fnum].school;
				if(friendSchool == null) {
					friendPtr = friendPtr.next;
					continue;
				}
				
				if(visited[friendPtr.fnum] == false && friendSchool.equals(school)) {
					
					q.enqueue(g.members[friendPtr.fnum]);
					
					//mark as visited again
					visited[friendPtr.fnum] = true;
				}
				
				friendPtr = friendPtr.next;
				
			}
			
		}
		
		
	}
	
	/**
	 * Finds and returns all connectors in the graph.
	 * 
	 * @param g Graph for which connectors needs to be found.
	 * @return Names of all connectors. Null if there are no connectors.
	 */
	public static ArrayList<String> connectors(Graph g) {
		
		ArrayList<String> conns = new ArrayList<>();
		
		int n = g.members.length;
		boolean[] visited = new boolean[n];
		int[] dfsnums = new int[n], backs = new int[n];
		boolean[] hasBeenBacked = new boolean[n];
		

		for(int i = 0; i < n; i++) {
			if(!visited[i])
				DFSConnectors(g, i, visited, dfsnums, backs, i, conns, hasBeenBacked, i);
		}
		
		return conns;
		
	}
	
	
	
	private static void DFSConnectors(Graph g, int curr, boolean[] visited, int[] dfsnums, int backs[], 
			int prevIndex, ArrayList<String> conns, boolean[] hasBeenBacked, int startIndex) {
		
		if(visited[curr]) {
			return;
		}
		
		
		visited[curr] = true;
		dfsnums[curr] = dfsnums[prevIndex] + 1;
		backs[curr] = dfsnums[curr];
		
		//first friend of the current dude
		Friend friendPtr = g.members[curr].first;
		while(friendPtr != null) {
			
			if(visited[friendPtr.fnum]) {
				backs[curr] = Math.min(backs[curr], dfsnums[friendPtr.fnum]);
			}
			//not visited next one
			else {

				DFSConnectors(g, friendPtr.fnum, visited, dfsnums, backs, curr, conns, hasBeenBacked, startIndex);
				
				if(dfsnums[curr] <= backs[friendPtr.fnum] && !conns.contains(g.members[curr].name)) {
					
					if(curr != startIndex || hasBeenBacked[curr] == true) {
						conns.add(g.members[curr].name);
					}
				}
				
				//after backup , check this thing to decrement
				if(dfsnums[curr] > backs[friendPtr.fnum]){
					backs[curr] = Math.min(backs[curr], backs[friendPtr.fnum]);
				}
				
				//after the DFS thing, so it has been backed
				hasBeenBacked[curr] = true;
			}
			
			friendPtr = friendPtr.next;
		}
		
		
		
	}
}

