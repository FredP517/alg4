import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycle;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.ST;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class WordNet {
    private ST<String, List<Integer>> RB_tree;
    private Digraph G;
    private SAP sap;
    private ArrayList<String> my_noun;
    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms){
        if(synsets == null || hypernyms == null){
            throw new IllegalArgumentException();
        }
        int V = 0;
        RB_tree = new ST<>();
        my_noun = new ArrayList<>();
        In synin = new In(synsets);
        while (synin.hasNextLine()){
            V++;
            String[] data = synin.readLine().split(",");
            int id = Integer.parseInt(data[0]);
            String[] noun = data[1].split(" ");
            my_noun.add(data[1]);
            for(String key : noun){
                if(!RB_tree.contains(key)){
                    List<Integer> tem = new LinkedList<>();
                    tem.add(id);
                    RB_tree.put(key, tem);
                }
                else{
                    List<Integer> tem = RB_tree.get(key);
                    tem.add(id);
                    RB_tree.put(key, tem);
                }
            }
        }
        G = new Digraph(V);
        boolean[] isNotRoot = new boolean[V];
        In hypin = new In(hypernyms);
        while (hypin.hasNextLine()){
            String[] IDs = hypin.readLine().split(",");
            int childId = Integer.parseInt(IDs[0]);
            isNotRoot[childId] = true;
            for(int i = 1; i < IDs.length; i++){
                int parentID = Integer.parseInt(IDs[i]);
                G.addEdge(childId, parentID);
            }
        }
        sap = new SAP(G);
        int num_of_root = 0;
        for(boolean notRoot : isNotRoot){
            if(!notRoot) num_of_root++;
        }
        DirectedCycle cycle = new DirectedCycle(G);
        if(num_of_root > 1 || cycle.hasCycle()){
            throw new IllegalArgumentException("Not a valid wordnet!");
        }
    }

    // returns all WordNet nouns
    public Iterable<String> nouns(){
        return RB_tree.keys();
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word){
        if(word == null) throw new IllegalArgumentException();
        return RB_tree.contains(word);
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB){
        if(nounA == null || nounB == null){
            throw new IllegalArgumentException();
        }
        List<Integer> lst1 = RB_tree.get(nounA);
        List<Integer> lst2 = RB_tree.get(nounB);
        return sap.length(lst1, lst2);
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB){
        if(nounA == null || nounB == null){
            throw new IllegalArgumentException();
        }
        List<Integer> lst1 = RB_tree.get(nounA);
        List<Integer> lst2 = RB_tree.get(nounB);
        int ans = sap.ancestor(lst1, lst2);
        if(ans == -1) return null;
        return my_noun.get(ans);
    }
}
