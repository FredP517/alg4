import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.LinkedList;
//import java.util.*;

//I use a lot of Map srtucture to map the relationship, you can also define a inner class Team which has multiple instances to realize this
public class BaseballElimination {
    private int numOfTeam;
    private Map<String, List<String>> map;
    private Map<String, Integer> index; //The index of each team in the input order, such as 0 represents, team xxx is the first line in the input, 1 is the second line...
    private Map<String, Integer> netMap; //index of the team in the flow network   team_name -> index of the flow network
    private Map<Integer, String> reverseNetMap; //map index of the team in flow network to the name of team   index -> team_name
    private Queue<String> queue; // store the team name that eliminate team x
    public BaseballElimination(String filename){
        In in = new In(filename);
        numOfTeam = Integer.parseInt(in.readLine()); // The first line is number of the teams
        map = new HashMap<>();
        index = new HashMap<>();
        int idx = 0;
        while (in.hasNextLine()){
            String line = in.readLine().trim();
            String[] condition = line.split("\\s+"); //The basic conditions of this team
            List<String> tem = new LinkedList<>();
            for(int i = 1; i < condition.length; i++){
                tem.add(condition[i]);
            }
            map.put(condition[0], tem);
            index.put(condition[0], idx);
            idx++;
        }
    }
    // create a baseball division from given filename in format specified below
    public  int numberOfTeams(){
        return numOfTeam;
    }
    // number of teams
    public Iterable<String> teams(){
        return map.keySet();
    }
    // all teams
    public int wins(String team){
        if(team == null || !map.containsKey(team)){
            throw new IllegalArgumentException();
        }
        List<String> tem = map.get(team);
        int win = Integer.parseInt(tem.get(0));
        return win;
    }
    // number of wins for given team
    public int losses(String team){
        if(team == null || !map.containsKey(team)){
            throw new IllegalArgumentException();
        }
        List<String> tem = map.get(team);
        int lose = Integer.parseInt(tem.get(1));
        return lose;
    }
    // number of losses for given team
    public int remaining(String team){
        if(team == null || !map.containsKey(team)){
            throw new IllegalArgumentException();
        }
        List<String> tem = map.get(team);
        int remain = Integer.parseInt(tem.get(2));
        return remain;
    }
    // number of remaining games for given team
    public int against(String team1, String team2){
        if(team1 == null || team2 == null || !map.containsKey(team1) || !map.containsKey(team2)){
            throw new IllegalArgumentException();
        }
        int idx_team2 = index.get(team2);
        List<String> tem = map.get(team1);
        return Integer.parseInt(tem.get(3 + idx_team2)); //The first three elements in tem are win, lose,
        //remain
    }
    // number of remaining games between team1 and team2
    public boolean isEliminated(String team){
        if(team == null || !map.containsKey(team)){
            throw new IllegalArgumentException();
        }
        if(trivialEliminated(team)) return true;
        Map<String, Integer> map2 = new HashMap<>();
        //int win_x = wins(team);
        //int remain_x = remaining(team);
        int node_pairs = totalRemain(team, map2);
        FlowNetwork net = new FlowNetwork(2 + node_pairs + numOfTeam - 1);
        buildNet(net, team, map2, node_pairs);
        FordFulkerson F = new FordFulkerson(net, 0, net.V() - 1);
        double max_flow = F.value();
        if(max_flow == PossibleMaxFlow(team)) return false; //means team is not eliminated
        queue = new LinkedList<>();
        for(int w = 1 + node_pairs; w < net.V() - 1; w++){
            if(F.inCut(w)){
                queue.add(reverseNetMap.get(w));
            }
        }
        return true;
    }
    private boolean trivialEliminated(String team){
        for(String x : teams()){
            if(x.equals(team)) continue;
            int cmp = wins(team) + remaining(team) - wins(x);
            if(cmp < 0) return true;
        }
        return false;
    }
    private int totalRemain(String team, Map<String, Integer> map2){
        //The total number of game that doesn't against with "team" in the same division
        int cnt = 0;
        int index_net = 1; //because node s = 0, so start from 1
        for(String currentTeam : teams()){
            if(currentTeam.equals(team)) continue;
            for(String againstTeam : teams()){
                if(!againstTeam.equals(team)){
                    if(index.get(currentTeam) < index.get(againstTeam)){
                        cnt++; //find a node currentTeam : againstTeam
                        String node = currentTeam + " " + againstTeam;
                        map2.put(node, index_net);
                        index_net++;
                    }
                }
            }
        }
        return cnt;
    }
    private void buildNet(FlowNetwork net, String team, Map<String, Integer> map2, int pairs){
        int team_idx = pairs + 1; //team vertices start from this index
        netMap = new HashMap<>();
        reverseNetMap = new HashMap<>();
        //map the team to its index in the flow net
        for(String CurrentTeam : teams()){
            if(!CurrentTeam.equals(team)){
                netMap.put(CurrentTeam, team_idx);
                reverseNetMap.put(team_idx, CurrentTeam);
                team_idx++;
            }
        }
        //build edges from s to game node and build edges from game node to team vertices
        for(String game_Node : map2.keySet()){
            String[] tem = game_Node.split("\\s+");
            FlowEdge e = new FlowEdge(0, map2.get(game_Node), 0.0 + against(tem[0], tem[1]));
            FlowEdge e2 = new FlowEdge(map2.get(game_Node), netMap.get(tem[0]), Double.MAX_VALUE);
            FlowEdge e3 = new FlowEdge(map2.get(game_Node), netMap.get(tem[1]), Double.MAX_VALUE);
            net.addEdge(e);
            net.addEdge(e2);
            net.addEdge(e3);
        }
        for(String currentNode : teams()){
            if(currentNode.equals(team)) continue;
            FlowEdge e = new FlowEdge(netMap.get(currentNode), net.V() - 1, wins(team) +
                    remaining(team) - wins(currentNode));
            net.addEdge(e);
        }
    }
    private double PossibleMaxFlow(String team){
        double ans = 0.0;
        for(String currentNode : teams()){
            if(currentNode.equals(team)) continue;
            for(String againstNode : teams()){
                if(!againstNode.equals(team)){
                    if(index.get(currentNode) < index.get(againstNode)){
                        ans += against(currentNode, againstNode);
                    }
                }
            }
        }
        return ans;
    }
    public Iterable<String> certificateOfElimination(String team){
        if(team == null || !map.containsKey(team)){
            throw new IllegalArgumentException();
        }
        if(!isEliminated(team)) return null;
        //if the team is eliminated, we check it's trivial or un-trivial eliminated
        if(trivialEliminated(team)){
            queue = new LinkedList<>();
            for(String x : teams()){
                if(x.equals(team)) continue;
                int cmp = wins(team) + remaining(team) - wins(x);
                if(cmp < 0){
                    queue.add(x);
                }
            }
        }
        //else condition, queue is already created in isEliminated(team)
        return queue;
    }
