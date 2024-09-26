package spiderman;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Steps to implement this class main method:
 * 
 * Step 1:
 * DimensionInputFile name is passed through the command line as args[0]
 * Read from the DimensionsInputFile with the format:
 * 1. The first line with three numbers:
 *      i.    a (int): number of dimensions in the graph
 *      ii.   b (int): the initial size of the cluster table prior to rehashing
 *      iii.  c (double): the capacity(threshold) used to rehash the cluster table 
 * 2. a lines, each with:
 *      i.    The dimension number (int)
 *      ii.   The number of canon events for the dimension (int)
 *      iii.  The dimension weight (int)
 * 
 * Step 2:
 * SpiderverseInputFile name is passed through the command line as args[1]
 * Read from the SpiderverseInputFile with the format:
 * 1. d (int): number of people in the file
 * 2. d lines, each with:
 *      i.    The dimension they are currently at (int)
 *      ii.   The name of the person (String)
 *      iii.  The dimensional signature of the person (int)
 * 
 * Step 3:
 * HubInputFile name is passed through the command line as args[2]
 * Read from the HubInputFile with the format:
 * One integer
 *      i.    The dimensional number of the starting hub (int)
 * 
 * Step 4:
 * CollectedOutputFile name is passed in through the command line as args[3]
 * Output to CollectedOutputFile with the format:
 * 1. e Lines, listing the Name of the anomaly collected with the Spider who
 *    is at the same Dimension (if one exists, space separated) followed by 
 *    the Dimension number for each Dimension in the route (space separated)
 * 
 * @author Seth Kelley
 */
/* Because of Kingpin’s Collider experiments in Miles’ home dimension, anomalies are appearing in other Dimensions. An anomaly is someone who is in a Dimension that they do not belong to (in accordance with their dimensional signature and the Dimensions’ dimensional number).

The leader of the inter-dimensional Spider-Society, named Miguel O’Hara and known as Spider-man 2099, is trying to stop any anomalies who are wreaking havoc in the Dimensions they do not belong in. He believes it is his responsibility to protect these Dimensions, as enough damage from anomalies could cause the Dimension to cease to exist, or even the Spider-Verse as a whole.

For Miguel to accomplish his goal, he has created a hub in his home dimension, and recruits Spiders from other Dimensions to help him track down these anomalies, bring them back to the hub, and return them to their own Dimensio
This Java class will take four command line arguments in the following order: a dimension list input file name, a spiderverse input file name, a hub input file name and an output file name.
The dimension list input file and spiderverse input file will be formatted exactly as the ones from Collider.
The hub input file will be formatted as follows:
1 line, containing dimensional number of the starting hub
You and Miguel want to stop and bring in any anomalies you find, but are not sure which dimensions they may reside, or the best route to take. To solve this problem, you and Miguel have decided to use a Breadth First Search (BFS) to find the best routes which contain anomalies.

A Spider is someone whose Dimensional Signature matches the Dimension Number of where they are located.
An Anomaly is someone whose Dimensional Signature DOES NOT match the Dimension Number of where they are located.
Find the best route from the hub, to any anomaly, and back to the hub. You can do this for all anomalies in the Spider-Verse using BFS. If there is a Spider at that dimension with the anomaly, return ONLY the route going back to the hum (reverse of the route from hub –> anomaly). In both instances, the current Dimension of these anomalies and Spiders will be changed to the source where the hub is located to be sent back home in a later method. Recall that BFS finds the shortest path from a source vertex to every other vertex with respect to the number of edges (hops). */
public class CollectAnomalies {
    
    private static Map<Integer, List<Integer>> BFS(HashMap<Integer, List<Integer>> adjList, int start, Map<Integer, List<String>> spiderVerse) {
        Map<Integer, List<Integer>> shortestPaths = new HashMap<>();
        Queue<Integer> queue = new LinkedList<>();
        Set<Integer> visited = new HashSet<>();
    
        queue.add(start);
        visited.add(start);
        shortestPaths.put(start, new ArrayList<>());
    
        while (!queue.isEmpty()) {
            int dimension = queue.poll();
            List<Integer> neighbors = adjList.get(dimension);
            if (neighbors != null) { // Add this null check
                for (int neighbor : neighbors) {
                    if (!visited.contains(neighbor)) {
                        queue.add(neighbor);
                        visited.add(neighbor);
                        shortestPaths.put(neighbor, new ArrayList<>(shortestPaths.get(dimension)));
                        shortestPaths.get(neighbor).add(dimension);
                    }
                }
            }
        }
        return shortestPaths;
    }
    public static void main(String[] args) {

        if (args.length < 4) {
            StdOut.println("Execute: java -cp bin spiderman.CollectAnomalies <dimension INput file> <spiderverse INput file> <hub INput file> <collected OUTput file>");
            return;
        }

        String dimension = args[0];
        String spiderverse = args[1];
        String hub = args[2];
        String outputFile = args[3];
        // initiate the cluster
        StdIn.setFile(dimension);
        StdOut.setFile(outputFile); // Set the output file

        // ... (rest of the code) ...

        StdIn.setFile(hub);
        int hubDimension = StdIn.readInt();
        Map<Integer, List<Integer>> adjList = new HashMap<>(); // Declare and initialize the adjList variable
        Map<Integer, List<String>> spiderVerse = new HashMap<>(); // Declare and initialize the spiderVerse variable
        Map<Integer, List<Integer>> shortestPaths = BFS(new HashMap<>(adjList), hubDimension, spiderVerse);

        for (Map.Entry<Integer, List<String>> entry : spiderVerse.entrySet()) {
            int dimension3 = entry.getKey();
            if (dimension3 == hubDimension) {
                continue; // Skip anomalies already at the hub
            }
            boolean hasSpider = false;
            String anomalyName = null;
            String spiderName = null; // Store the name of the spider
            for (String person : entry.getValue()) {
                String[] parts = person.split(" ");
                int signature = Integer.parseInt(parts[1]);
                if (signature != dimension3) {
                    anomalyName = parts[0];
                } else {
                    hasSpider = true;
                    spiderName = parts[0]; // Store the name of the spider
                }
            }
            if (anomalyName != null) {
                List<Integer> path = new ArrayList<>(shortestPaths.get(dimension3));
                
                if (hasSpider) {
                    Collections.reverse(path);
                    StdOut.println(anomalyName + " " + spiderName + " " + dimension3 + " " + String.join(" ", path.stream().map(Object::toString).collect(Collectors.toList())));
                } else {
                    List<Integer> pathBack = new ArrayList<>(path);
                    Collections.reverse(pathBack);
                    path.add(dimension3);
                    path.addAll(pathBack); // add the path back to the hub
                    StdOut.println(anomalyName + " " + String.join(" ", path.stream().map(Object::toString).collect(Collectors.toList())));
                }
            }
        }
    }
}