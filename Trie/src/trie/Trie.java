package trie;

import java.util.ArrayList;

/**
 * This class implements a Trie. 
 * 
 * @author Sesh Venugopal
 *
 */
public class Trie {
	
	// prevent instantiation
	private Trie() { }
	
	/**
	 * Builds a trie by inserting all words in the input array, one at a time,
	 * in sequence FROM FIRST TO LAST. (The sequence is IMPORTANT!)
	 * The words in the input array are all lower case.
	 * 
	 * @param allWords Input array of words (lowercase) to be inserted.
	 * @return Root of trie with all words inserted from the input array
	 */
	public static TrieNode buildTrie(String[] allWords) {
		
		//create the null root node
		TrieNode root = new TrieNode(null, null, null);
		
		
		Indexes temp = new Indexes(0, (short)0, (short) (allWords[0].length() - 1));
		root.firstChild = new TrieNode(temp, null , null);
			
		
		
		for(int indexWordToAdd = 1; indexWordToAdd < allWords.length; indexWordToAdd++) {
			insertNodeRec(root.firstChild, allWords, indexWordToAdd, 0, root);
		}
		
		return root;
		
	}
	
	
	private static void insertNodeRec(TrieNode ptr, String[] allWords, int indexWordToAdd,
			int charIndex, TrieNode parent) {
		
		//go thru each character, see how many match
		//if no match, do recursive on each sibling until there is a match
		//if reach the end and no match, create a new sibling
		
		//if some number of characters match
		//if there are no children....
		//replace the ptr node with just the number of characters that match
		//and create 2 children, one for the rest of the characters of the word already in the tree
		//and one for the rest of the characters for the new word
		
		//if there are children... 
		//go to the child and repeat the above step
		
		
		String wordToAdd = allWords[indexWordToAdd];
		String ptrWord = allWords[ptr.substr.wordIndex];
		
		boolean newCommonChars = false;
		
		//char index must be less than the length of both words
		//checks to see how many characters are the same
		while(charIndex <= ptr.substr.endIndex  &&  charIndex < wordToAdd.length() &&
				ptrWord.charAt(charIndex) == wordToAdd.charAt(charIndex)) {
			
			newCommonChars = true;
			charIndex++;
		}
		
		
		//if there are no common characters
		if(newCommonChars == false) {
			
			//if there is a sibling, recurse on that sibling
			if(ptr.sibling != null)
				insertNodeRec(ptr.sibling, allWords, indexWordToAdd, charIndex, parent);
			//changed the last argument from ptr to parent TODO
			
			//otherwise create a new sibling
			else {
				Indexes tempIndex = new Indexes(indexWordToAdd, (short)charIndex, 
						(short) (wordToAdd.length() - 1));
				ptr.sibling = new TrieNode(tempIndex, null, null);
				return;
			}
				
			
		}
		
		//if there are some common characters
		else {
			//ptr.substr.endIndex - ptr.substr.startIndex
			
			//this means only some of the letters matched.....
			//adds an intermediate node before the ptr
			if((charIndex - 1 < ptr.substr.endIndex) && ptr.firstChild != null) {
				
				Indexes intermediateIndex = new Indexes(ptr.substr.wordIndex, ptr.substr.startIndex,
						(short) (charIndex - 1));
				
				TrieNode intermediateNode = new TrieNode(intermediateIndex, ptr, ptr.sibling);
				
				//changes the start index of ptr TODO
				ptr.substr.startIndex = (short)(intermediateNode.substr.endIndex + 1);
				
				//TODO delete
//				parent.firstChild = intermediateNode;
				//goes to the correct part to add the new temp node
				//without this, it might delete some siblings
				if(parent.firstChild == ptr) {
					parent.firstChild = intermediateNode;
				}
				
				else {
					TrieNode tempPtr = parent.firstChild;
					
					while(tempPtr.sibling != ptr && tempPtr.sibling != null) {
						tempPtr = tempPtr.sibling;
					}
					//now tempPtr is at the node before ptr, we need to set the
					//prev node's sibling to the intermediate node
					
					tempPtr.sibling = intermediateNode;
				}
				
				
				
				Indexes wordToAddIndex = new Indexes(indexWordToAdd, (short)charIndex, (short) (wordToAdd.length() - 1));
				ptr.sibling = new TrieNode(wordToAddIndex, null, null);
				
				//adds the sibling temp that we created earlier.
				
				return;
			}
			
			//if there are no children, create the 2 new children
			if(ptr.firstChild == null) {
				//if there are some letters in common, set the current nodes 
				//ending index to index-1
				short tempEndIndex = ptr.substr.endIndex;
				ptr.substr.endIndex = (short) (charIndex-1);
				
				//now create two children, first child is the word originally in the tree
				//second child is the word to add
				
				Indexes indexOldChild = new Indexes(ptr.substr.wordIndex, 
						(short)charIndex, tempEndIndex);
				
				short endIndexNewWord = (short)(wordToAdd.length() - 1);
				Indexes indexNewChild = new Indexes(indexWordToAdd, (short)charIndex, endIndexNewWord);
				
				//create the new nodes
				TrieNode child1 = new TrieNode(indexOldChild, null, null);
				TrieNode child2 = new TrieNode(indexNewChild, null, null);
				
				//sets the sibling
				child1.sibling = child2;
				
				//adds the child to the ptr;
				ptr.firstChild = child1;
				
				return;
			}
			
			//if there are children
			else {
				insertNodeRec(ptr.firstChild, allWords, indexWordToAdd, charIndex, ptr);
			}
		}
		
		
	}
	
	
	/**
	 * Given a trie, returns the "completion list" for a prefix, i.e. all the leaf nodes in the 
	 * trie whose words start with this prefix. 
	 * For instance, if the trie had the words "bear", "bull", "stock", and "bell",
	 * the completion list for prefix "b" would be the leaf nodes that hold "bear", "bull", and "bell"; 
	 * for prefix "be", the completion would be the leaf nodes that hold "bear" and "bell", 
	 * and for prefix "bell", completion would be the leaf node that holds "bell". 
	 * (The last example shows that an input prefix can be an entire word.) 
	 * The order of returned leaf nodes DOES NOT MATTER. So, for prefix "be",
	 * the returned list of leaf nodes can be either hold [bear,bell] or [bell,bear].
	 *
	 * @param root Root of Trie that stores all words to search on for completion lists
	 * @param allWords Array of words that have been inserted into the trie
	 * @param prefix Prefix to be completed with words in trie
	 * @return List of all leaf nodes in trie that hold words that start with the prefix, 
	 * 			order of leaf nodes does not matter.
	 *         If there is no word in the tree that has this prefix, null is returned.
	 */
	public static ArrayList<TrieNode> completionList(TrieNode root,
										String[] allWords, String prefix) {
		
		ArrayList<TrieNode> list = new ArrayList<TrieNode>();
		TrieNode ptr = root.firstChild;
		
 		Trie.completionListRec(ptr, allWords, prefix, list);
 		
 		if(list.size() > 0)
 			return list;
 		
 		return null;
	}
	
	
	
	private static void completionListRec(TrieNode ptr, String[] allWords, 
										String prefix, ArrayList<TrieNode> list) {
		
		if(prefix.length() == 0) {
			Trie.addAllInSubtree(ptr, allWords, list, prefix);
			return;
		}
		
		String currentWord = allWords[ptr.substr.wordIndex].substring(
				ptr.substr.startIndex, ptr.substr.endIndex + 1);
		
		
		if(currentWord.startsWith(prefix) || prefix.startsWith(currentWord)) {
			Trie.addAllInSubtree(ptr, allWords, list, prefix);
		}
		else if(ptr.sibling != null){
			Trie.completionListRec(ptr.sibling, allWords, prefix, list);
		}
		
		
	}
	
	private static void addAllInSubtree(TrieNode ptr, String[] allWords, ArrayList<TrieNode> list, 
			String prefix) {
		
		if(ptr.firstChild == null && 
				allWords[ptr.substr.wordIndex].startsWith(prefix)) {
			list.add(ptr);
		}
		
		if(ptr.firstChild != null) {
			Trie.addAllInSubtree(ptr.firstChild, allWords, list, prefix);
		}
		
		if(ptr.sibling != null) {
			Trie.addAllInSubtree(ptr.sibling, allWords, list, prefix);
		}
		
		if(ptr.firstChild == null && ptr.sibling == null)
			return;
			
	}
	
	public static void print(TrieNode root, String[] allWords) {
		System.out.println("\nTRIE\n");
		print(root, 1, allWords);
	}
	
	private static void print(TrieNode root, int indent, String[] words) {
		if (root == null) {
			return;
		}
		for (int i=0; i < indent-1; i++) {
			System.out.print("    ");
		}
		
		if (root.substr != null) {
			String pre = words[root.substr.wordIndex]
							.substring(0, root.substr.endIndex+1);
			System.out.println("      " + pre);
		}
		
		for (int i=0; i < indent-1; i++) {
			System.out.print("    ");
		}
		System.out.print(" ---");
		if (root.substr == null) {
			System.out.println("root");
		} else {
			System.out.println(root.substr);
		}
		
		for (TrieNode ptr=root.firstChild; ptr != null; ptr=ptr.sibling) {
			for (int i=0; i < indent-1; i++) {
				System.out.print("    ");
			}
			System.out.println("     |");
			print(ptr, indent+1, words);
		}
	}
	
 }
