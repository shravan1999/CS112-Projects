package lse;

import java.io.*;
import java.util.*;

/**
 * This class builds an index of keywords. Each keyword maps to a set of pages in
 * which it occurs, with frequency of occurrence in each page.
 *
 */
public class LittleSearchEngine {
	
	/**
	 * This is a hash table of all keywords. The key is the actual keyword, and the associated value is
	 * an array list of all occurrences of the keyword in documents. The array list is maintained in 
	 * DESCENDING order of frequencies.
	 */
	HashMap<String,ArrayList<Occurrence>> keywordsIndex;
	
	/**
	 * The hash set of all noise words.
	 */
	HashSet<String> noiseWords;
	
	/**
	 * Creates the keyWordsIndex and noiseWords hash tables.
	 */
	public LittleSearchEngine() {
		keywordsIndex = new HashMap<String,ArrayList<Occurrence>>(1000,2.0f);
		noiseWords = new HashSet<String>(100,2.0f);
	}
	
	/**
	 * Scans a document, and loads all keywords found into a hash table of keyword occurrences
	 * in the document. Uses the getKeyWord method to separate keywords from other words.
	 * 
	 * @param docFile Name of the document file to be scanned and loaded
	 * @return Hash table of keywords in the given document, each associated with an Occurrence object
	 * @throws FileNotFoundException If the document file is not found on disk
	 */
	public HashMap<String,Occurrence> loadKeywordsFromDocument(String docFile) 
	throws FileNotFoundException {

		HashMap<String, Occurrence> keywords = new HashMap<>();
		
		Scanner sc = new Scanner(new File(docFile));
		while (sc.hasNext()) {
			String line = sc.next();
			
			StringTokenizer strTok = new StringTokenizer(line, " ");
			while(strTok.hasMoreTokens()) {
				
				String token = strTok.nextToken();
				String keywd = this.getKeyword(token);
				
				if(keywd != null) {
					
					//if this keywd is not already in the hashmap, put a 1 occuranece
					if(keywords.containsKey(keywd) == false) {
						keywords.put(keywd, new Occurrence(docFile, 1));
					} 
					//its already in there
					else {
						keywords.get(keywd).frequency += 1;
					}
					
				}
				
			}
			
		}
		
		sc.close();
		
		return keywords;
		
	}
	
	/**
	 * Merges the keywords for a single document into the master keywordsIndex
	 * hash table. For each keyword, its Occurrence in the current document
	 * must be inserted in the correct place (according to descending order of
	 * frequency) in the same keyword's Occurrence list in the master hash table. 
	 * This is done by calling the insertLastOccurrence method.
	 * 
	 * @param kws Keywords hash table for a document
	 */
	public void mergeKeywords(HashMap<String,Occurrence> kws) {

		//loops thru every entry
		for(Map.Entry<String, Occurrence> entry : kws.entrySet()) {
			String word = entry.getKey();
			Occurrence occ = entry.getValue();
			
			if(keywordsIndex.containsKey(word) == false) {
				ArrayList<Occurrence> temp = new ArrayList<>();
				temp.add(occ);
				keywordsIndex.put(word, temp);
			}
			
			//that keyword is already in the master hashset
			else {
				ArrayList<Occurrence> masterOccs = keywordsIndex.get(word);
				masterOccs.add(occ); 
				this.insertLastOccurrence(masterOccs);
			}
		}
		
	}
	
	/**
	 * Given a word, returns it as a keyword if it passes the keyword test,
	 * otherwise returns null. A keyword is any word that, after being stripped of any
	 * trailing punctuation, consists only of alphabetic letters, and is not
	 * a noise word. All words are treated in a case-INsensitive manner.
	 * 
	 * Punctuation characters are the following: '.', ',', '?', ':', ';' and '!'
	 * 
	 * @param word Candidate word
	 * @return Keyword (word without trailing punctuation, LOWER CASE)
	 */
	public String getKeyword(String word) {
		
		//makes lowercase, since case doesn't matter
		word = word.trim().toLowerCase();
		
		//if there are trailing punctuation marks, delete them
		//k >= 1 only because otherwise string out of bounds
		
		String newWord = word;
		
		for(int k = word.length() - 1; k >= 1; k--) {
			char currentChar = word.charAt(k);
			
			//if current char is a trailing punc, delete it
			if(currentChar == '.' || currentChar==',' || currentChar =='?' || currentChar ==':' ||
					currentChar ==';' || currentChar =='!') {
				
				newWord = word.substring(0, k);
			}
			
			//if no more trailing punctuations in a row, break
			else {
				break;
			}
		}
		
		//checks to see if noiseword, if it is, return null
		if(noiseWords.contains(newWord)) {
			return null;
			
		}
		
		//check to see if every letter is a alphabetic 
		for(int i = 0; i < newWord.length(); i++) {
			int ascii = newWord.charAt(i);
			
			//if a valid char, continue to next loop
			if(ascii >= 97 && ascii <= 122)
				continue;
			
			//comes here if fails the prev statement
			return null;
			
		}
		
		return newWord;
		
	}
	
	/**
	 * Inserts the last occurrence in the parameter list in the correct position in the
	 * list, based on ordering occurrences on descending frequencies. The elements
	 * 0..n-2 in the list are already in the correct order. Insertion is done by
	 * first finding the correct spot using binary search, then inserting at that spot.
	 * 
	 * @param occs List of Occurrences
	 * @return Sequence of mid point indexes in the input list checked by the binary search process,
	 *         null if the size of the input list is 1. This returned array list is only used to test
	 *         your code - it is not used elsewhere in the program.
	 */
	public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs) {
		
//		System.out.println("occs before inserting the last occurance " + occs + "\n");
		
		ArrayList<Integer> midpts = new ArrayList<Integer>();
		Occurrence toInsert = occs.remove(occs.size() - 1);
		
//		System.out.println("occs size in insert last occs" + occs.size());
		
		int low = 0, high = occs.size() - 1;
		while(low <= high) {
			
			int mid = (low + high)/2;
			midpts.add(mid);
			
//			System.out.println("mid " + mid);
			
			int midFreq = occs.get(mid).frequency;
			
			
			if(toInsert.frequency == midFreq) {
				break;
			}
			
			else if(toInsert.frequency < midFreq) {
				low = mid + 1;
			}
			else {
				high = mid - 1;
			}
			
		}
		
		//index of the final place the binary search reached
		//if the thing to insert is less or equal to than the current element, we want to insert it after
		int i = 0;
		
		if(occs.size() > 0) {
			
				i = midpts.get(midpts.size() - 1);
				
				if(toInsert.frequency <= occs.get(i).frequency)
					i++;
		}
		
		
		
		//if its greater, than insert before
		
		occs.add(i, toInsert);
//		System.out.println(toInsert + " is inserted");
		return midpts;
		
	}
	
	/**
	 * This method indexes all keywords found in all the input documents. When this
	 * method is done, the keywordsIndex hash table will be filled with all keywords,
	 * each of which is associated with an array list of Occurrence objects, arranged
	 * in decreasing frequencies of occurrence.
	 * 
	 * @param docsFile Name of file that has a list of all the document file names, one name per line
	 * @param noiseWordsFile Name of file that has a list of noise words, one noise word per line
	 * @throws FileNotFoundException If there is a problem locating any of the input files on disk
	 */
	public void makeIndex(String docsFile, String noiseWordsFile) 
	throws FileNotFoundException {
		// load noise words to hash table
		Scanner sc = new Scanner(new File(noiseWordsFile));
		while (sc.hasNext()) {
			String word = sc.next();
			noiseWords.add(word);
		}
		
		// index all keywords
		sc = new Scanner(new File(docsFile));
		while (sc.hasNext()) {
			String docFile = sc.next();
			HashMap<String,Occurrence> kws = loadKeywordsFromDocument(docFile);
			mergeKeywords(kws);
		}
		sc.close();
	}
	
	/**
	 * Search result for "kw1 or kw2". A document is in the result set if kw1 or kw2 occurs in that
	 * document. Result set is arranged in descending order of document frequencies. (Note that a
	 * matching document will only appear once in the result.) Ties in frequency values are broken
	 * in favor of the first keyword. (That is, if kw1 is in doc1 with frequency f1, and kw2 is in doc2
	 * also with the same frequency f1, then doc1 will take precedence over doc2 in the result. 
	 * The result set is limited to 5 entries. If there are no matches at all, result is null.
	 * 
	 * @param kw1 First keyword
	 * @param kw1 Second keyword
	 * @return List of documents in which either kw1 or kw2 occurs, arranged in descending order of
	 *         frequencies. The result size is limited to 5 documents. If there are no matches, returns null.
	 */
	public ArrayList<String> top5search(String kw1, String kw2) {
		
		//ALGORITHM
		//take the first occurance in the k1 array and get its frequency and document
		//see if the other word occurs in the same document and has a higher frequency, 
		//save that higher frequency, and add it to the result arraylist
		
		//do the same for the second occurrance in the k2 array
		//if this frequency is higher than the frequency of the thing already in the result arraylist
		//which is for the first occurance, then put the two things in the correct order
		
		//repeat for everything in the k1 arraylist
		
		//go thru each element in the k2 arraylist, if a new document is encountered, add it to the correct
		//spot in the result arraylist
		
		ArrayList<Occurrence> resultOccs = new ArrayList<>();
		
		ArrayList<Occurrence> occs1 = new ArrayList<>();
		ArrayList<Occurrence> occs2 = new ArrayList<>();
		
		
		if(keywordsIndex.get(kw1) != null)
			occs1 = keywordsIndex.get(kw1);
		
		if(keywordsIndex.get(kw2) != null)
			occs2 = keywordsIndex.get(kw2);
		
		HashSet<String> seenDocs = new HashSet<String>();
		
		
		
		//loop thru the first keyword occurrences
		for(int i = 0; i < occs1.size(); i++) {
			
			Occurrence occi = occs1.get(i);
			String occiDoc = occi.document;
			int freq1 = occi.frequency;
			
			
			boolean matchFound = false;
			
			//add to seen docs, so that we can access this later and know whats already in the result
			//list
			seenDocs.add(occiDoc);
			
			//loop thru and try to find the same document
			for(int j = 0; j < occs2.size(); j++) {
				
				Occurrence occj = occs2.get(j);
				
				int freq2 = occj.frequency;
				
				//if found the same document, compare frequencies
				if(occiDoc.equals(occj.document)) {
//					System.out.println("found the same doc");
					
					//adds the higher frequency value to the return arraylist
					if(freq1 >= freq2) {
//						System.out.println("adding to resultOccs fre1 1 >= freq2" + occi.toString());
						resultOccs.add(occi);
					}
					else {
//						System.out.println("adding to result Occs fre1 < fre2" + occi.toString());
						resultOccs.add(occj);
					}
					
					//uses the method before to put this result in the correct spot.
					this.insertLastOccurrence(resultOccs);
					
					matchFound = true;
					
					break;
					
				}
			}
			
			//if the current document has the first word but not the second word, we will
			//insert it anyways
			if(matchFound == false) {
				resultOccs.add(occi);
				this.insertLastOccurrence(resultOccs);
			}
	
		}
		
		//loops thru the word 2 arraylist, makes sure that if there is some docs in the word2 arraylist
		//that aren't in the word1 arraylist, that they get added too.
		for(int j = 0; j < occs2.size(); j++) {
			
			//if occs1 does not have the current element, add to result arraylist
			//if it already had the curent element, we do nothing since we already take care of it in the freq loop
			if(seenDocs.contains(occs2.get(j).document) == false) {
				
				//TODO MUST COMPARE THE DOCUMENT STUFF NOT THE OBJECT ITSELF
				
//				System.out.println("adding in the second loop" + occs2 + "\n");
				resultOccs.add(occs2.get(j));
				
				this.insertLastOccurrence(resultOccs);
			}
		}
		
		
		ArrayList<String> docResult = new ArrayList<>();
		
		
		//now we need to extract the document names and only the top 5
		for(int a = 0; a < resultOccs.size(); a++) {
			docResult.add(resultOccs.get(a).document);
			
			if(docResult.size() >= 5) 
				break;
		}
		
		return docResult;
	
	}
}
