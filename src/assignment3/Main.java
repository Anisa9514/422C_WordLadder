/* WORD LADDER Main.java
 * EE422C Project 3 submission by
 * Replace <...> with your actual data.
 * Name: Angelique Bautista
 * UTeid: ab54429
 * Unique: 15465
 * Slip days used: <0>
 * Git URL: https://github.com/Anisa9514/422C_WordLadder
 * Spring 2018
 */

package assignment3;
import java.util.*;
import java.io.*;

/** HELPER CLASS: Node
 * Data structure that holds both the word and the parent it was derived from
 * Used when tracing back from bfs to list the word ladder
 **/
class Node{
	public String word;
	public String parent;
	
	public Node(String word, String parent){
		this.word = word;
		this.parent = parent;
	}
}

/**
 * Main Class that implements
 * @author Angelique Bautista
 */
public class Main {
	
	public static void main(String[] args) throws Exception {
		
		Scanner kb;	// input Scanner for commands
		PrintStream ps;	// output file, for student testing and grading only
		
		// If arguments are specified, read/write from/to files instead of Std IO.
		if (args.length != 0) {
			kb = new Scanner(new File(args[0]));
			ps = new PrintStream(new File(args[1]));
			System.setOut(ps);			// redirect output to ps
		} else {
			kb = new Scanner(System.in);// default input from Stdin
			ps = System.out;			// default output to Stdout
		}
		initialize();

		ArrayList<String> kbInput = parse(kb);
		ArrayList<String> ladder;
		
		// Create word ladder from kbInput
		while(!kbInput.isEmpty()) {
			
			// BFS
			ladder = getWordLadderBFS(kbInput.get(0), kbInput.get(1));
			System.out.println("BFS");
			printLadder(ladder);
			
			// DFS
			ladder = getWordLadderDFS(kbInput.get(0), kbInput.get(1));
			System.out.println("DFS");
			printLadder(ladder);
			
			// Get next input
			kbInput = parse(kb);
		}	
		
		return;
		
	}
	
	public static void initialize() {
		// initialize your static variables or constants here.
		// We will call this method before running our JUNIT tests.  So call it 
		// only once at the start of main.
	}
	
	/**
	 * This function parses through keyboard input to isolate start and end word of word ladder
	 * @param keyboard Scanner connected to System.in
	 * @return ArrayList of Strings containing start word and end word. 
	 * If command is /quit, return empty ArrayList. 
	 */
	public static ArrayList<String> parse(Scanner keyboard) {
		ArrayList<String> startAndEndWords = new ArrayList<String>();
		String input = keyboard.nextLine().trim().toUpperCase();
		
		if(input.equals("/QUIT")){
			return startAndEndWords;
		}

		String[] arrInput = input.split(" ");
		startAndEndWords.add(arrInput[0]);
		startAndEndWords.add(arrInput[1]);
		
		return startAndEndWords;
	}
	
	/**
	 * This function generates the word ladder from start to end word using depth first search
	 * Each rung of word ladder will differ from previous rung by one letter
	 * @param start start of the word ladder
	 * @param end end of the word ladder
	 * @return array list containing the word ladder including start and end
	 */
	public static ArrayList<String> getWordLadderDFS(String start, String end) {
		
		start = start.toUpperCase();
		end = end.toUpperCase();
		Set<String> dict = makeDictionary();
		HashMap<String, ArrayList<String>> graph = createGraph(dict);		// Adjacency list. Each key is mapped to words differing by one letter
		HashSet<String> visited = new HashSet<String>();					// Contains set of all nodes visited
		HashMap<String, String> traversed = new HashMap<String, String>();	// Contains how each node was reached; key is mapped to the parent used to reach it
		
		dfsRecursive(0, start, end, visited, traversed, graph);			// Recursive depth first search

		// CREATE LADDER
		ArrayList<String> ladder = new ArrayList<String>();
		ladder.add(end);							// add end word to ladder
		String curWordParent = traversed.get(end);	// grab parent used to get to end
		
		// Keep pushing each parent to the front of the ladder until start word is reached
		while(curWordParent != null) {
			ladder.add(0,curWordParent);
			curWordParent = traversed.get(curWordParent);
		}
		
		// If ladder is empty, return list with just start and end.
		if(ladder.size() == 1) {
			ladder.add(0,start);
		}
		
		return ladder; 
	}
	
	
	/**
	 * This function is a wrapper function for the recursive calls used in DFS
	 * @param depth -the depth at which the node is visited
	 * @param start -start word of ladder
	 * @param end -end word of ladder
	 * @param visited -the words already visited
	 * @param traversed -how the node is visited
	 * @param graph -adjacency list used to grab neighboring words 
	 */
	private static void dfsRecursive(int depth, String start, String end, HashSet<String> visited, HashMap<String, String> traversed, HashMap<String, ArrayList<String>> graph){
		
		visited.add(start);	 // add current word to list of words already visited
		depth++;			 // increment depth level of search
		
		// If maximum depth has been reached, return to explore other options
		// Reduces size of word ladder
		if(depth > 1000) {
			// if at depth limit, then erase node from visited so it can be traversed again later on if encountered at a higher depth
			// traversed should not need be altered because it will rewrite value later on if traversed again
			visited.remove(start);	
			return;
		}
		
		// If end word has not been reached, search through unvisited neighbors
		if(!start.equals(end)) {
			ArrayList<String> neighbors = graph.get(start);
			if(neighbors == null) {
				return;
			}
			ArrayList<Integer> sameLettersCountArr = new ArrayList<Integer>();	
			
			// SORT NEIGHBORS: Neighbors closest to end word will be searched first
			// Determine how close each neighbor is to end word.
			for(int i = 0, size = neighbors.size(); i < size; i++) {
				int count = howManySameLetters(end, neighbors.get(i));
				sameLettersCountArr.add(count);
			}
			
			// Sort neighbors according to which one is closest to end word
			for(int i = 0, size = neighbors.size(); i < size; i++) {
				for(int k = i + 1, compSize = neighbors.size(); k < compSize; k++) {
					if(sameLettersCountArr.get(i) < sameLettersCountArr.get(k)) {
						int tempCount = sameLettersCountArr.get(i);
						String tempWord = neighbors.get(i);
						
						sameLettersCountArr.set(i, sameLettersCountArr.get(k));
						sameLettersCountArr.set(k, tempCount);
						
						neighbors.set(i, neighbors.get(k));
						neighbors.set(k, tempWord);
					}
				}
			}

			
			// Recurse through unvisited neighbors
			for(int i = 0, size = neighbors.size(); i < size; i++) {
				String neighbor = neighbors.get(i);
				String parent = start;
				if(!visited.contains(neighbor)) {
					traversed.put(neighbor, parent);
					dfsRecursive(depth, neighbor, end, visited, traversed, graph);
				}
			}
			
		}
		
		return;
	}
	
	/**
	 * Helper function used in depth first search to sort which neighbors are visited first
	 * @param ref reference word
	 * @param word word to compare the reference word to
	 * @return number of letters that share the same letter and position in the two words
	 */
	private static int howManySameLetters(String ref, String word) {
		int count = 0;
		
		for(int i = 0, size = ref.length(); i < size; i++) {
			if(ref.charAt(i) == word.charAt(i)) {
				count = count + 1;
			}
		}
		
		return count;
	}
	
	/**
	 * This function generates the word ladder from start to end word using breadth first search
	 * Each rung of word ladder will differ from previous rung by one letter
	 * @param start start of the word ladder
	 * @param end end of the word ladder
	 * @return array list containing the word ladder including start and end
	 */
    public static ArrayList<String> getWordLadderBFS(String start, String end) {
		
    	start = start.toUpperCase();
    	end = end.toUpperCase();
		Set<String> dict = makeDictionary();								
		HashMap<String, ArrayList<String>> graph = createGraph(dict);		// Adjacency list. Each key is mapped to words differing by one letter
		LinkedList<Node> queue = new LinkedList<Node>();					// Queue used to keep track of nodes to be visited
		HashMap<String, String> traversed = new HashMap<String, String>();	// Contains how each node was reached; key is mapped to the parent used to reach it
		Set<String> visited = new HashSet<String>();						// Contains set of all nodes visited.
		
		// Add root node to queue and set it as visited
		queue.add(new Node(start, null));	
		visited.add(start);
		
		
		while(!queue.isEmpty()) {
			// Dequeue element and mark it as traversed 
			Node curElem = queue.remove();
			String curWord = curElem.word;
			traversed.put(curWord, curElem.parent);
			
			// Check to see if current word matches end word
			if(curWord == end) {
				break;
			}
			
			// If not, grab all neighbors: words that share only one letter difference from current word
			ArrayList<String> neighbors = graph.get(curWord);
			if(neighbors == null) {
				continue;
			}
			
			// Iterate through all unvisited neighbors and add them to queue
			for(int i = 0, size = neighbors.size(); i < size; i++) {
				String neighbor = neighbors.get(i);
				if(!visited.contains(neighbor)) {
					queue.add(new Node(neighbor, curWord));
					visited.add(neighbor);
				}
			}
		}

		// CREATE LADDER
		ArrayList<String> ladder = new ArrayList<String>();	
		ladder.add(end);							// add end word to ladder
		String curWordParent = traversed.get(end);	// grab parent word used to get to end word

		// Keep pushing each parent to the front of the ladder until start word is reached
		while(curWordParent != null) {
			ladder.add(0,curWordParent);
			curWordParent = traversed.get(curWordParent);
		}
		
		// If ladder is empty, return list with just start and end.
		if(ladder.size() == 1) {
			ladder.add(0,start);
		}
		return ladder; 
	}
    
    
	/**
	 * Prints the ladder from starting word to ending word
	 * @param ladder array list containing all the words in the ladder
	 */
	public static void printLadder(ArrayList<String> ladder) {
		String start = ladder.get(0);
		String end = ladder.get(ladder.size() - 1);
		
		// If ladder only contains two words, it only has start and end words and no rungs
		if(ladder.size() == 2) {	
			System.out.println("no word ladder can be found between " + start.toLowerCase() + " and " + end.toLowerCase());
		}
		else {
			int rungSize = ladder.size() - 2;
			System.out.println("A " + rungSize + "-rung word ladder exists between " + start.toLowerCase() + " and " + end.toLowerCase());
			for(int i = 0, size = ladder.size(); i < size ; i++) {
				System.out.println(ladder.get(i).toLowerCase());
			}	
		}
	}
	
	/**
	 * HELPER FUNCTION: Creates graph using a hash map to create adjacency lists
	 * Will create a Hash Map where the keys are the vertex and the values are the vertices adjacent to the key
	 * @param dict dictionary containing all words needed for traversal
	 * @return adjacency list in the form of a hash map where keys are the words values are the words they share only a one letter difference with
	 */
	private static HashMap<String, ArrayList<String>> createGraph (Set<String> dict) {
		
		HashMap<String, ArrayList<String>> graph = new HashMap<String, ArrayList<String>>();
		Iterator<String> vertexIt = dict.iterator();
		String curVertex, posEdge;
		
		while(vertexIt.hasNext()) {
			curVertex = vertexIt.next();
			graph.put(curVertex, new ArrayList<String>());
			
			// Iterate through every letter in vertex
			for(int i = 0, wordSize = curVertex.length(); i < wordSize; i++) {
				// Create possible word by replacing one letter of vertex with another possible letter
				for(char letter = 'A'; letter <= 'Z'; letter++) {
					posEdge = changeCharAtIndex(i, letter, curVertex);
					// Do not include vertex itself in adjacency list
					if(posEdge.equals(curVertex)) {	
						continue;
					}
					// Check dictionary if new word possibility exists
					else {
						// If word exists, add to adjacency list; if not, continue;
						if(dict.contains(posEdge)) {
							graph.get(curVertex).add(posEdge);
						}
					}
				}
			}
		}
		
		return graph;
	}
	
	/* HELPER FUNCTION: Changes one character in string 
	 * Change character at specified index 
	 */
	private static String changeCharAtIndex(int index, char ch, String str) {
		
		char[] chArray = str.toCharArray();
		chArray[index] = ch;
		return new String(chArray);
	
	}
	
	/* REQUIRED FUNCTION: Makes Dictionary
	 * Do not modify makeDictionary 
	 * */
	public static Set<String>  makeDictionary () {
		Set<String> words = new HashSet<String>();
		Scanner infile = null;
		try {
			infile = new Scanner (new File("five_letter_words.txt"));
		} catch (FileNotFoundException e) {
			System.out.println("Dictionary File not Found!");
			e.printStackTrace();
			System.exit(1);
		}
		while (infile.hasNext()) {
			words.add(infile.next().toUpperCase());
		}
		return words;
	}
}
