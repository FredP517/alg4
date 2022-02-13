import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.Digraph;


public class SAP {
    private Digraph G;
    private final int MAX = Integer.MAX_VALUE;
    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G){
        if(G == null) throw new IllegalArgumentException();
        this.G = G;
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w){
        boolean flag1 = v < 0 || v > G.V();
        boolean flag2 = w < 0 || w > G.V();
        if(flag1 || flag2) throw new IllegalArgumentException();
        return getLength(v, w);
    }
    private int getLength(int v, int w){
        //start at v, bfs the rest points
        int ans = MAX;
        BreadthFirstDirectedPaths bfs1 = new BreadthFirstDirectedPaths(G, v);
        //start at w, bfs the rest points
        BreadthFirstDirectedPaths bfs2 = new BreadthFirstDirectedPaths(G, w);
        for(int s = 0; s < G.V(); s++){
            if(bfs1.hasPathTo(s) && bfs2.hasPathTo(s)){
                int l = bfs1.distTo(s) + bfs2.distTo(s);
                if(l < ans){
                    ans = l;
                }
            }
        }
        return (ans == MAX) ? -1 : ans;
    }
    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w){
        boolean flag1 = v < 0 || v > G.V();
        boolean flag2 = w < 0 || w > G.V();
        if(flag1 || flag2) throw new IllegalArgumentException();
        return getAncestor(v, w);
    }
    private int getAncestor(int v, int w){
        int ans = MAX;
        int ancestor = -1;
        BreadthFirstDirectedPaths bfs1 = new BreadthFirstDirectedPaths(G, v);
        //start at w, bfs the rest points
        BreadthFirstDirectedPaths bfs2 = new BreadthFirstDirectedPaths(G, w);
        for(int s = 0; s < G.V(); s++){
            if(bfs1.hasPathTo(s) && bfs2.hasPathTo(s)){
                int l = bfs1.distTo(s) + bfs2.distTo(s);
                if(l < ans){
                    ans = l;
                    ancestor = s;
                }
            }
        }
        return ancestor;
    }
    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w){
        if(v == null || w == null) throw new IllegalArgumentException();
        for(int v1 : v){
            if(v1 < 0 || v1 > G.V()) throw new IllegalArgumentException();
        }
        for(int w1 : w){
            if(w1 < 0 || w1 > G.V()) throw new IllegalArgumentException();
        }
        return getLength(v, w);
    }
    private int getLength(Iterable<Integer> v, Iterable<Integer> w){
        //start at v, bfs the rest points
        int ans = MAX;
        BreadthFirstDirectedPaths bfs1 = new BreadthFirstDirectedPaths(G, v);
        //start at w, bfs the rest points
        BreadthFirstDirectedPaths bfs2 = new BreadthFirstDirectedPaths(G, w);
        for(int s = 0; s < G.V(); s++){
            if(bfs1.hasPathTo(s) && bfs2.hasPathTo(s)){
                int l = bfs1.distTo(s) + bfs2.distTo(s);
                if(l < ans){
                    ans = l;
                }
            }
        }
        return (ans == MAX) ? -1 : ans;
    }
    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w){
        if(v == null || w == null) throw new IllegalArgumentException();
        for(int v1 : v){
            if(v1 < 0 || v1 > G.V()) throw new IllegalArgumentException();
        }
        for(int w1 : w){
            if(w1 < 0 || w1 > G.V()) throw new IllegalArgumentException();
        }
        return getAncestor(v, w);
    }
    private int getAncestor(Iterable<Integer> v, Iterable<Integer> w){
        int ans = MAX;
        int ancestor = -1;
        BreadthFirstDirectedPaths bfs1 = new BreadthFirstDirectedPaths(G, v);
        //start at w, bfs the rest points
        BreadthFirstDirectedPaths bfs2 = new BreadthFirstDirectedPaths(G, w);
        for(int s = 0; s < G.V(); s++){
            if(bfs1.hasPathTo(s) && bfs2.hasPathTo(s)){
                int l = bfs1.distTo(s) + bfs2.distTo(s);
                if(l < ans){
                    ans = l;
                    ancestor = s;
                }
            }
        }
        return ancestor;
    }
}
