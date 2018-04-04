# CS112-Projects
Data Structures (CS112) projects completed in first semester at Rutgers.
Written in Java.

```java
Project[] myProjects = {new Project("Expression Evaluator"),
                        new Project("Friends"),
                        new Project("Little Search Engine"),
                        new Project("Trie")};
            
for(Project p : myProjects) {
    System.out.println(p.toString());
}
```


## Projects:

**1. Expression Evaluator:**

  * Purpose: Evaluates an expression that is inputted by the user.
           The user can also specify variables and their values.

  * Input -> Expression of any length
  * Output -> Value of expression
  
  * Just an example input: 75 * (A + 52 * 2) - 2 / (6 + 2 - B * (C * A * B) / 5))
  
  
**2. Friends**
  
  * Purpose: Create a Graph structure representing connections between "friends".
             Shortest path to get from one person to another person in terms of number of connections.
             Can find cliques or a group of friends seperated from all others by school.
             Identifies "connector" people, which are people who can isolate some friends from everyone 
             else if they are removed.
           

  * Input -> Text file containing data formatted to create a graph
  * Output -> Shortest path, cliques, or connectors in graph
  
  
**3. Little Search Engine**

  * Purpose: Allows the user to identify which documents contain the most uses of two specified words.
           Utilizes a hashtable to keep track of word frequencies and the documents in which they are contained.

  * Input -> Two words, and text files of documents
  * Output -> List of documents where the specified words appear, sorted by frequency of occurance
  
  
**4. Trie**
  
  * Purpose: Use a tree like structure to store prefixes of words, and generate an "autocomplete" list when given a prefix
  
  * Input -> A word prefix and a dictionary of words to create trie from
  * Output -> List of all words in the tree that start with that prefix
  
  
  
  
-----------------------------------------------------------------------------------------------------------------------------  
  
Please follow both Rutgers University's [Principles of Academic Integrity](http://academicintegrity.rutgers.edu/academic-integrity-policy/) and the Rutgers Department of Computer Science's [Academic Integrity Policy](https://www.cs.rutgers.edu/academic-integrity/programming-assignments)
