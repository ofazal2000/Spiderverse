package spiderman;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

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
 * ColliderOutputFile name is passed in through the command line as args[2]
 * Output to ColliderOutputFile with the format:
 * 1. e lines, each with a different dimension number, then listing
 *       all of the dimension numbers connected to that dimension (space separated)
 * 
 * @author Seth Kelley
 */

public class Collider {

    public static void main(String[] args) {

        if ( args.length < 3 ) {
            StdOut.println(
                "Execute: java -cp bin spiderman.Collider <dimension INput file> <spiderverse INput file> <collider OUTput file>");
                return;
        }

        
       String dimensionFilePath = args[0];
       String spiderverseFilePath = args[1];
       String outputFilePath = args[2];

       StdIn.setFile(dimensionFilePath);
       StdIn.readInt();
       int initialSize = StdIn.readInt();
       DimOfNode[] dimensionClusters = new DimOfNode[initialSize];
       double loadThreshold = StdIn.readDouble();
       int dimensionsAdded = 0;

       while (!StdIn.isEmpty()) {
           int dimensionID = StdIn.readInt();
           int canonCount = StdIn.readInt();
           int weightValue = StdIn.readInt();
           int index = dimensionID % initialSize;
           double capacityLimit = initialSize * loadThreshold;

           if (dimensionsAdded >= capacityLimit) {
               initialSize *= 2;
               DimOfNode[] temp = new DimOfNode[initialSize];

               for (DimOfNode node : dimensionClusters) {
                   while (node != null) {
                       int newIndex = node.getDimensionID() % initialSize;
                       DimOfNode newNode = new DimOfNode(node.getDimensionID(), node.getCanonCount(), node.getWeightValue(), temp[newIndex]);
                       temp[newIndex] = newNode;
                       node = node.getNextNode();
                   }
               }
               dimensionClusters = temp;
               index = dimensionID % initialSize;
           }

           dimensionClusters[index] = new DimOfNode(dimensionID, canonCount, weightValue, dimensionClusters[index]);
           dimensionsAdded++;
       }
       linkDimensionClusters(dimensionClusters, initialSize);

       HashMap<Integer, List<Integer>> adjacencyMap = buildAdjacencyList(dimensionClusters);
       StdIn.setFile(spiderverseFilePath);
       int peopleCount = StdIn.readInt();
       Map<Integer, List<String>> spiderVerse = createSpiderVerse(peopleCount, spiderverseFilePath);

       printColliderOutput(adjacencyMap, spiderVerse, outputFilePath);
   }

   public static void linkDimensionClusters(DimOfNode[] dimensionClusters, int size) {
       for (int i = 0; i < dimensionClusters.length; i++) {
           DimOfNode prevPrevNode = dimensionClusters[(i - 2 + size) % size];
           DimOfNode prevNode = dimensionClusters[(i - 1 + size) % size];

           DimOfNode newNode1 = new DimOfNode(prevNode.getDimensionID(), prevNode.getCanonCount(), prevNode.getWeightValue(), null);
           DimOfNode newNode2 = new DimOfNode(prevPrevNode.getDimensionID(), prevPrevNode.getCanonCount(), prevPrevNode.getWeightValue(), null);

           DimOfNode node = dimensionClusters[i];
           while (node.getNextNode() != null) {
               node = node.getNextNode();
           }
           node.setNextNode(newNode1);
           newNode1.setNextNode(newNode2);
       }
   }

   public static HashMap<Integer, List<Integer>> buildAdjacencyList(DimOfNode[] dimensionClusters) {
       HashMap<Integer, List<Integer>> adjacencyMap = new HashMap<>();
       for (DimOfNode firstNode : dimensionClusters) {
           if (firstNode != null) {
               List<Integer> firstNodeConnections = adjacencyMap.computeIfAbsent(firstNode.getDimensionID(), k -> new ArrayList<>());
               for (DimOfNode node = firstNode.getNextNode(); node != null; node = node.getNextNode()) {
                   if (firstNode.getDimensionID() != node.getDimensionID()) {
                       List<Integer> currentNodeConnections = adjacencyMap.computeIfAbsent(node.getDimensionID(), k -> new ArrayList<>());
                       firstNodeConnections.add(node.getDimensionID());
                       currentNodeConnections.add(firstNode.getDimensionID());
                   }
               }
           }
       }
       return adjacencyMap;
   }

   public static Map<Integer, List<String>> createSpiderVerse(int peopleCount, String spiderverseFilePath) {
       Map<Integer, List<String>> spiderVerse = new HashMap<>();
       for (int j = 0; j < peopleCount; j++) {
           int dimensionNum = StdIn.readInt();
           String name = StdIn.readString();
           int dimensionalSignature = StdIn.readInt();
           spiderVerse.computeIfAbsent(dimensionNum, k -> new ArrayList<>()).add(name + " " + dimensionalSignature);
       }
       return spiderVerse;
   }

   public static void printColliderOutput(HashMap<Integer, List<Integer>> adjacencyMap, Map<Integer, List<String>> spiderVerse, String outputFilePath) {
       StdOut.setFile(outputFilePath);
       adjacencyMap.forEach((dimension, linkedDimensions) -> {
           StdOut.print(dimension);
           for (int linkedDimension : linkedDimensions) {
               StdOut.print(" " + linkedDimension);
           }
           StdOut.println();
       });
   }
}