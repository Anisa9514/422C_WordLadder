Project: Word Ladder

Given two words, this project outputs a word ladder from one word to the other 
such that each rung of the ladder is exactly one letter different from the preceding rung.
Main.java contains all methods necessary to implement this word ladder program. Listed below
are implementation details about the methods within Main.java along with assumptions made 
during implementation.

Project Structure
    Main.java  
        private Node class
        public Main class  
            public functions
                main()
                initialize()
                parse()
                getWordLadderDFS()
                getWordLadderBFS()
                printLadder()
                makeDictionary()
            private helper functions
                dfsRecursive()
                howManySameLetters()
                createGraph()
                changeCharAtIndex()
                

Implementation Notes 
    Breadth First Search
    Depth First Search
        - to avoid stack overflow, a depth limit is set. If the depth limit is reached 
            then the function returns and is forced to look for an alternate path
            - the depth limit is assumed to be 1000 (assumes the stack can handle 1000 nested calls)
        - to reduce path length (and avoid stack overflow), the start word's neighbors are sorted so that the neighbor
            that shares the closest amount of letters to the ending word is traversed first


Assumptions
- Both getWordLadder() functions assume that the inputs are two words of the same length and are in the provided dictionary
