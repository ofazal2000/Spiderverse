package spiderman;

import java.util.*;

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
 * SpotInputFile name is passed through the command line as args[2]
 * Read from the SpotInputFile with the format:
 * Two integers (line seperated)
 *      i.    Line one: The starting dimension of Spot (int)
 *      ii.   Line two: The dimension Spot wants to go to (int)
 * 
 * Step 4:
 * TrackSpotOutputFile name is passed in through the command line as args[3]
 * Output to TrackSpotOutputFile with the format:
 * 1. One line, listing the dimenstional number of each dimension Spot has visited (space separated)
 * 
 * @author Seth Kelley
 */

public class TrackSpot {
    
    public static void main(String[] args) {

        if ( args.length < 4 ) {
            StdOut.println(
                "Execute: java -cp bin spiderman.TrackSpot <dimension INput file> <spiderverse INput file> <spot INput file> <trackspot OUTput file>");
                return;
        }


        String dimensionFile = args[0];
        String spiderverseFile = args[1];
        String spotFile = args[2];
        String outputFile = args[3];
 
        StdIn.setFile(dimensionFile);
        int dimensions = StdIn.readInt();
        int size = StdIn.readInt();
        double threshold = StdIn.readDouble();
        DimOfNode[] cluster = new DimOfNode[size];
        int currDimAdded = 0;
 
        int i = 0;
        while (!StdIn.isEmpty()) {
            int dimNum = StdIn.readInt();
            int canonEvents = StdIn.readInt();
            int dimWeight = StdIn.readInt();
            int index = (dimNum % size);
            if (currDimAdded >= threshold * size) {
                size *= 2;
                DimOfNode[] newCluster = new DimOfNode[size];
                for (DimOfNode ptr : cluster) {
                    DimOfNode node = ptr;
                    while (node != null) {
                        int newIndex = (node.getDimensionID() % size);
                        if (newCluster[newIndex] == null) {
                            newCluster[newIndex] = new DimOfNode(node.getDimensionID(), node.getCanonCount(), node.getWeightValue(), null);
                        } else {
                            DimOfNode curr = new DimOfNode(node.getDimensionID(), node.getCanonCount(), node.getWeightValue(), null);
                            curr.setNextNode(newCluster[newIndex]);
                            newCluster[newIndex] = curr;
                        }
                        node = node.getNextNode();
                    }
                }
                cluster = newCluster;
                index = (dimNum % size);
            }
            if (cluster[index] == null) {
                cluster[index] = new DimOfNode(dimNum, canonEvents, dimWeight, null);
            } else {
                DimOfNode curr = new DimOfNode(dimNum, canonEvents, dimWeight, null);
                curr.setNextNode(cluster[index]);
                cluster[index] = curr;
            }
            currDimAdded++;
            i++;
        }
 
        for (int j = 0; j < size; j++) {
            DimOfNode prev1 = cluster[(j - 1 + size) % size];
            DimOfNode prev2 = cluster[(j - 2 + size) % size];
 
            DimOfNode newNode1 = new DimOfNode(prev1.getDimensionID(), prev1.getCanonCount(), prev1.getWeightValue(), null);
            DimOfNode newNode2 = new DimOfNode(prev2.getDimensionID(), prev2.getCanonCount(), prev2.getWeightValue(), null);
 
            DimOfNode curr = cluster[j];
            while (curr.getNextNode() != null) {
                curr = curr.getNextNode();
            }
            curr.setNextNode(newNode1);
            newNode1.setNextNode(newNode2);
        }
 
        HashMap<Integer, List<Integer>> adjList = new HashMap<>();
 
        int k = 0;
        while (k < cluster.length) {
            DimOfNode ptr = cluster[k];
            if (ptr != null) {
                int firstDimension = ptr.getDimensionID();
                adjList.putIfAbsent(firstDimension, new ArrayList<>());
 
                while (ptr != null) {
                    int dimension2 = ptr.getDimensionID();
                    adjList.putIfAbsent(dimension2, new ArrayList<>());
                    if (firstDimension != dimension2) {
                        adjList.get(firstDimension).add(dimension2);
                        adjList.get(dimension2).add(firstDimension);
                    }
                    ptr = ptr.getNextNode();
                }
            }
            k++;
        }
 
        StdIn.setFile(spiderverseFile);
        int numOfPeople = StdIn.readInt();
        Map<Integer, List<String>> spiderVerse = new HashMap<>();
        int j = 0;
        while (j < numOfPeople) {
            int dimension3 = StdIn.readInt();
            String name = StdIn.readString();
            int signature = StdIn.readInt();
            spiderVerse.putIfAbsent(dimension3, new ArrayList<>());
            spiderVerse.get(dimension3).add(name + " " + signature);
            j++;
        }
 
        StdIn.setFile(spotFile);
        int start = StdIn.readInt();
        int end = StdIn.readInt();
        HashSet<Integer> visited = new HashSet<>();
        Stack<Integer> path = new Stack<>();
        LinkedHashSet<Integer> fullPath = new LinkedHashSet<>();
        path.push(start);
        visited.add(start);
        boolean found = false;
 
        while (!path.isEmpty()) {
            int current = path.peek();
            fullPath.add(current);
            if (current == end) {
                found = true;
                break;
            }
            List<Integer> neighbors = adjList.get(current);
            boolean allVisited = true;
            for (int neighbor : neighbors) {
                if (!visited.contains(neighbor)) {
                    allVisited = false;
                    path.push(neighbor);
                    visited.add(neighbor);
                    break;
                }
            }
            if (allVisited) {
                path.pop();
            }
        }
 
        StdOut.setFile(outputFile);
        for (Integer dimension5 : fullPath) {
            StdOut.print(dimension5 + " ");
        }
    }
}