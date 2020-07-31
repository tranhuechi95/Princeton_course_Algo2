import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdOut;

import java.util.HashSet;
import java.util.Set;

public class BaseballElimination {
    private final int noOfTeams;
    private final String[] teams;
    private final int[] wins;
    private final int[] losses;
    private final int[] remaining;
    private final int[][] gameAvsB;


    // create a baseball division from given filename in format specified below
    public BaseballElimination(String filename) {
        In in = new In(filename);
        noOfTeams = Integer.parseInt(in.readLine());
        teams = new String[noOfTeams];
        wins = new int[noOfTeams];
        losses = new int[noOfTeams];
        remaining = new int[noOfTeams];
        gameAvsB = new int[noOfTeams][noOfTeams];
        int count = 0;
        while (!in.isEmpty()) {
            String line = in.readLine();
            String[] tokens = line.trim()
                                  .split("\\s+"); // trim() removes the whitespace before and \\s+ remove all the whitespaces in betw
            teams[count] = tokens[0];
            wins[count] = Integer.parseInt(tokens[1]);
            losses[count] = Integer.parseInt(tokens[2]);
            remaining[count] = Integer.parseInt(tokens[3]);
            for (int i = 4; i < tokens.length; i++) {
                gameAvsB[count][i - 4] = Integer.parseInt(tokens[i]);
            }
            count++;
        }
    }

    // number of teams
    public int numberOfTeams() {
        return noOfTeams;
    }

    // all teams
    public Iterable<String> teams() {
        Set<String> teamsSet = new HashSet<>();
        for (int i = 0; i < teams.length; i++) {
            teamsSet.add(teams[i]);
        }
        return teamsSet;
    }

    private int[] getTeamfromId(int id, int size) {
        int[] teamsId = new int[2];
        int team1 = id / size;
        int team2 = id % size;
        teamsId[0] = team1;
        teamsId[1] = team2;
        return teamsId;
    }

    // number of wins for given team
    public int wins(String team) {
        if (team == null) {
            throw new IllegalArgumentException("Cannot have null input");
        }
        Set<String> teamSet = (Set<String>) teams();
        if (!teamSet.contains(team)) {
            throw new IllegalArgumentException("Cannot input invalid team");
        }
        int location = 0;
        for (int i = 0; i < noOfTeams; i++) {
            if (team.equals(teams[i])) {
                location = i;
                break;
            }
        }
        return wins[location];
    }

    // number of losses for given team
    public int losses(String team) {
        if (team == null) {
            throw new IllegalArgumentException("Cannot have null input");
        }
        Set<String> teamSet = (Set<String>) teams();
        if (!teamSet.contains(team)) {
            throw new IllegalArgumentException("Cannot input invalid team");
        }
        int location = 0;
        for (int i = 0; i < noOfTeams; i++) {
            if (team.equals(teams[i])) {
                location = i;
                break;
            }
        }
        return losses[location];
    }

    // number of remaining games for given team
    public int remaining(String team) {
        if (team == null) {
            throw new IllegalArgumentException("Cannot have null input");
        }
        Set<String> teamSet = (Set<String>) teams();
        if (!teamSet.contains(team)) {
            throw new IllegalArgumentException("Cannot input invalid team");
        }
        int location = 0;
        for (int i = 0; i < noOfTeams; i++) {
            if (team.equals(teams[i])) {
                location = i;
                break;
            }
        }
        return remaining[location];
    }


    // number of remaining games between team1 and team2
    public int against(String team1, String team2) {
        if (team1 == null || team2 == null) {
            throw new IllegalArgumentException("Cannot have null inputs");
        }
        Set<String> teamSet = (Set<String>) teams();
        if (!teamSet.contains(team1) || !teamSet.contains(team2)) {
            throw new IllegalArgumentException("Cannot input invalid team");
        }
        int flag = 0;
        int location1 = 0;
        int location2 = 0;
        for (int i = 0; i < noOfTeams; i++) {
            if (team1.equals(teams[i])) {
                flag++;
                location1 = i;
            }
            if (team2.equals(teams[i])) {
                flag++;
                location2 = i;
            }
            if (flag == 2) {
                break;
            }
        }
        return gameAvsB[location1][location2];
    }

    // classes FlowEdge and FlowNetwork are defined alr
    // need to create a FlowNetwork for a given team x
    // turn private later
    private FlowNetwork createNetwork(String team) {
        // build the network with the games and team vertices
            /* 1. get the corresponding number of the given team
            2. get all the other teams id = row * size + col (ex team 1 is 1 vs 1)
            3. get all the games id = row * size + col (ex team 0 vs 1)
            4. add the FlowEdge and the corresponding capacity for games
            5. add the FlowEdge and the corr. capacity for teams
            */

        int teamWin = wins(team);
        int teamRemaining = remaining(team);
        FlowNetwork network = new FlowNetwork(noOfTeams * noOfTeams + 2); // n^2 + 2
        int[] teamsId = new int[noOfTeams - 1]; // get the teams other than the given one
        // source and sink vertices are set as n^2 and n^2 + 1
        int s = noOfTeams * noOfTeams;
        int t = s + 1;

        int count = 0;
        for (int i = 0; i < noOfTeams; i++) {
            if (!team.equals(teams[i])) {
                teamsId[count] = i;
                count++;
            }
        }
        // Connect the teams to sink
        for (int i = 0; i < teamsId.length; i++) { // in the form row * size + col
            double capacityOfEdge = teamWin + teamRemaining - wins[teamsId[i]];
            FlowEdge flowEdge = new FlowEdge(teamsId[i] * noOfTeams + teamsId[i], t,
                                             capacityOfEdge);
            network.addEdge(flowEdge);
        }
        // Connect the source to games and games to teams
        for (int i = 0; i < teamsId.length; i++) {
            for (int j = i + 1; j < teamsId.length; j++) {
                // capacity of edge is the game left between 2 teams
                double capacityOfEdge1 = gameAvsB[teamsId[i]][teamsId[j]];
                FlowEdge flowEdge = new FlowEdge(s, teamsId[i] * noOfTeams + teamsId[j],
                                                 capacityOfEdge1);
                network.addEdge(flowEdge);

                double capacityOfEdge2 = Double.POSITIVE_INFINITY;
                FlowEdge flowEdge1 = new FlowEdge(teamsId[i] * noOfTeams + teamsId[j],
                                                  teamsId[i] * noOfTeams + teamsId[i],
                                                  capacityOfEdge2);
                FlowEdge flowEdge2 = new FlowEdge(teamsId[i] * noOfTeams + teamsId[j],
                                                  teamsId[j] * noOfTeams + teamsId[j],
                                                  capacityOfEdge2);
                network.addEdge(flowEdge1);
                network.addEdge(flowEdge2);
            }
        }

        return network;
    }

    private int checkTrivialElimination(String team) {
        int maxWinTeam = 0;
        int maxWin = 0;
        for (int i = 0; i < noOfTeams; i++) {
            if (!team.equals(teams[i]) && wins(teams[i]) > maxWin) {
                maxWin = wins(teams[i]);
                maxWinTeam = i;
            }
        }
        if (maxWin > wins(team) + remaining(team)) {
            return maxWinTeam;
        }
        else return -1;
    }

    // is given team eliminated?
    public boolean isEliminated(String team) {
        if (team == null) {
            throw new IllegalArgumentException("Cannot have null input");
        }
        Set<String> teamSet = (Set<String>) teams();
        if (!teamSet.contains(team)) {
            throw new IllegalArgumentException("Cannot input invalid team");
        }
        // check for trivial elimination
        int check = checkTrivialElimination(team);
        if (check >= 0) {
            return true;
        }
        // check for non-trivial elimination
        else {
            int s = noOfTeams * noOfTeams;
            int t = s + 1;
            FlowNetwork network = createNetwork(team);
            FordFulkerson networkMaxFlow = new FordFulkerson(network, s, t);
            int flag = 0;
            for (FlowEdge e : network.adj(s)) {
                if ((e.capacity() - e.flow()) > 0) {
                    flag = 1; // not full -> eliminated
                    break;
                }
            }
            return (flag == 1);
        }
    }

    // subset R of teams that eliminates given team; null if not eliminated
    public Iterable<String> certificateOfElimination(String team) {
        if (team == null) {
            throw new IllegalArgumentException("Cannot have null input");
        }
        Set<String> teamSet = (Set<String>) teams();
        if (!teamSet.contains(team)) {
            throw new IllegalArgumentException("Cannot input invalid team");
        }
        if (isEliminated(team)) {
            Set<String> R = new HashSet<>();
            // check for trivial elimination
            int check = checkTrivialElimination(
                    team); // check will return the id of the team that eliminate the given team trivially
            if (check >= 0) {
                R.add(teams[check]);
            }
            else {
                Queue<FlowEdge> eliminateEdge = new Queue<FlowEdge>();
                int s = noOfTeams * noOfTeams;
                int t = s + 1;
                FlowNetwork network = createNetwork(team);
                FordFulkerson networkMaxFlow = new FordFulkerson(network, s, t);
                // all edges from source are full if value == sum of all capacity
                for (FlowEdge e : network.adj(s)) {
                    if ((e.capacity() - e.flow()) > 0) {
                        eliminateEdge.enqueue(e);
                    }
                }
                for (FlowEdge e : eliminateEdge) {
                    int id = e.to();
                    int[] eliminateTeams = getTeamfromId(id, noOfTeams);
                    for (int i = 0; i < eliminateTeams.length; i++) {
                        R.add(teams[eliminateTeams[i]]);
                    }
                }
            }
            return R;
        }
        else return null;
    }

    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            }
            else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }
}
