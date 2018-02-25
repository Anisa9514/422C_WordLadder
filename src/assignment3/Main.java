/* WORD LADDER Main.java
 * EE422C Project 3 submission by
 * Replace <...> with your actual data.
 * Name: Angelique Bautista
 * UTeid: ab54429
 * Unique: 15465
 * Slip days used: <0>
 * Git URL
 * Fall 2017
 */


package assignment3;
import java.util.*;
import java.io.*;

/* HELPER CLASS: Node
 * Data structure that hold both the element and the parent it was derived from
 * Used when tracing back from bfs to list the word ladder
 * */
class Node{
	public String word;
	public String parent;
	
	public Node(String word, String parent){
		this.word = word;
		this.parent = parent;
	}
}


public class Main {
	
	// static variables and constants only here.
	
	public static void main(String[] args) throws Exception {
		
		Scanner kb;	// input Scanner for commands
		PrintStream ps;	// output file, for student testing and grading only
		HashMap<String, ArrayList<String>> graph;
		
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

		// TODO methods to read in words, output ladder
		ArrayList<String> kbInput = parse(kb);
		ArrayList<String> ladder;
		
		while(!kbInput.isEmpty()) {
			// Create word ladder from kbInput
//			ladder = getWordLadderBFS(kbInput.get(0), kbInput.get(1));
//			System.out.println("BFS");
//			printLadder(ladder);
			ladder = getWordLadderDFS(kbInput.get(0), kbInput.get(1));
			System.out.println("DFS");
			printLadder(ladder);
//			System.out.println(howManySameLetters(kbInput.get(0), kbInput.get(1)));
			kbInput = parse(kb);
		}
		
		// Test creation of graph
//		Set<String> dict = makeDictionary();
//		graph = createGraph(dict);
//		printGraph(graph, "adj_list_long_dict.txt");
		
		
		return;
		
	}
	
	public static void initialize() {
		// initialize your static variables or constants here.
		// We will call this method before running our JUNIT tests.  So call it 
		// only once at the start of main.
	}
	
	/**
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
	
	public static ArrayList<String> getWordLadderDFS(String start, String end) {
		
		// Returned list should be ordered start to end.  Include start and end.
		// If ladder is empty, return list with just start and end.
		// TODO some code
		Set<String> dict = makeDictionary();
		HashMap<String, ArrayList<String>> graph = createGraph(dict);
		HashSet<String> visited = new HashSet<String>();
		HashMap<String, String> traversed = new HashMap<String, String>();
		
		DFS_Recursive(0, start, end, visited, traversed, graph);

		/* CREATE LADDER*/
		ArrayList<String> ladder = new ArrayList<String>();
		String curWordParent = traversed.get(end);

		ladder.add(end);
		while(curWordParent != null) {
			ladder.add(0,curWordParent);
			curWordParent = traversed.get(curWordParent);
		}
		
		// If only end word is in ladder (no path could be found)
		if(ladder.size() == 1) {
			ladder.add(0,start);
		}
		return ladder; 
	}
	
	private static void DFS_Recursive(int depth, String start, String end, HashSet<String> visited, HashMap<String, String> traversed, HashMap<String, ArrayList<String>> graph){
		visited.add(start);
		System.out.println(start);
//		System.out.println("Visited Nodes Size: " + visited.size());
		System.out.println("Depth: " + depth);
		depth++;
		if(depth > 200) {
			return;
		}
		
		if(!start.equals(end)) {
			ArrayList<String> neighbors = graph.get(start);
			ArrayList<Integer> sameLettersCountArr = new ArrayList<Integer>();
			
			// Sort neighbors according to which one is closest to end word
			for(int i = 0, size = neighbors.size(); i < size; i++) {
				int count = howManySameLetters(end, neighbors.get(i));
				sameLettersCountArr.add(count);
			}
			
			for(int i = 0, size = neighbors.size(); i < size; i++) {
				for(int k = i + 1, compSize = neighbors.size(); k < compSize; k++) {
//					System.out.println("Compare " + sameLettersCountArr.get(i) + " with " + sameLettersCountArr.get(k) );
					if(sameLettersCountArr.get(i) < sameLettersCountArr.get(k)) {
//						System.out.println("swap");
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
//					System.out.println("Traversed Nodes Size: " + traversed.size());
					DFS_Recursive(depth, neighbor, end, visited, traversed, graph);
				}
			}
			
		}
		
		return;
	}
	
	/**Helper Function:
	 * Returns how many letters are equal in both letter and position between the words
	 * */
	private static int howManySameLetters(String ref, String word) {
		int count = 0;
		
		for(int i = 0, size = ref.length(); i < size; i++) {
			if(ref.charAt(i) == word.charAt(i)) {
				count = count + 1;
			}
		}
		
		return count;
	}
	
    public static ArrayList<String> getWordLadderBFS(String start, String end) {
		
		// TODO some code
		Set<String> dict = makeDictionary();
		HashMap<String, ArrayList<String>> graph = createGraph(dict);
		
		LinkedList<Node> queue = new LinkedList<Node>();
		HashMap<String, String> traversed = new HashMap<String, String>();
		Set<String> visited = new HashSet<String>();
		
		queue.add(new Node(start, null));
		visited.add(start);
		
		while(!queue.isEmpty()) {
			Node curElem = queue.remove();
			String curWord = curElem.word;
			
			traversed.put(curWord, curElem.parent);
			if(curWord == end) {
				break;
			}
			
			ArrayList<String> adjVertices = graph.get(curWord);
			if(adjVertices == null) {
				continue;
			}
				
			for(int i = 0, size = adjVertices.size(); i < size; i++) {
				String adjVertex = adjVertices.get(i);
				if(!visited.contains(adjVertex)) {
					queue.add(new Node(adjVertex, curWord));
					visited.add(adjVertex);
				}
			}
		}

		/* CREATE LADDER*/
		ArrayList<String> ladder = new ArrayList<String>();
		String curWordParent = traversed.get(end);

		ladder.add(end);
		while(curWordParent != null) {
			ladder.add(0,curWordParent);
			curWordParent = traversed.get(curWordParent);
		}
		
		// If only end word is in ladder (no path could be found)
		if(ladder.size() == 1) {
			ladder.add(0,start);
		}
		return ladder; 
	}
    
	
	public static void printLadder(ArrayList<String> ladder) {
		String start = ladder.get(0);
		String end = ladder.get(ladder.size() - 1);
		
		// ladder size = two
		if(ladder.size() == 2) {	
			System.out.println("No word ladder exists between " + start + " and " + end);
		}
		else {
			int rungSize = ladder.size() - 2;
			System.out.println("A " + rungSize + "-rung word ladder exists between " + start + " and " + end);
			for(int i = 0, size = ladder.size(); i < size ; i++) {
				System.out.println(ladder.get(i).toLowerCase());
			}	
		}
	}
	
	// TODO
	// Other private static methods here
	
	/* HELPER FUNCTION: Creates graph using a hash map to create adjacency lists
	 * Will create a Hash Map where the keys are the vertex and the values are the vertices adjacent to the key
	 * */ 
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
	
	/* DEBUGGING PURPOSES: Prints Graph
	 * Outputs adjacency list to output file
	 */
	private static void printGraph(HashMap<String, ArrayList<String>> graph, String filename) {
		PrintWriter output = null;
		File oFile = new File(filename);
		
		try{
			output = new PrintWriter(oFile);
		} catch (FileNotFoundException e){
			System.out.println("File not found: " + e);
		}
		
		for (Map.Entry<String, ArrayList<String>> entry : graph.entrySet())  {
			String curVertex = entry.getKey();
			ArrayList<String> arrList = entry.getValue();
			
			// Output Adjacency Lists
			output.println("Current Vertex: " + curVertex);
			output.print(arrList.size() + " Adjacent Vertices: ");
			for(int i = 0, size = arrList.size(); i < size; i++) {
				output.print(" " + arrList.get(i) + ",");
			}
			output.print("\n");
			output.println("======================");
		}
		
		output.println("Hashmap(graph) size: " + graph.size());
		output.close();
	}
	
	/* REQUIRED FUNCTION: Makes Dictionary
	 * Do not modify makeDictionary 
	 * */
	public static Set<String>  makeDictionary () {
		Set<String> words = new HashSet<String>();
		Scanner infile = null;
		try {
			infile = new Scanner (new File("five_letter_words.txt"));
//			infile = new Scanner (new File("meddict.txt"));
//			infile = new Scanner (new File("short_dict.txt"));
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
