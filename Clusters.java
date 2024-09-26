package spiderman;

import java.util.ArrayList;
import java.util.LinkedList;

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
 * 
 * Step 2:
 * ClusterOutputFile name is passed in through the command line as args[1]
 * Output to ClusterOutputFile with the format:
 * 1. n lines, listing all of the dimension numbers connected to 
 *    that dimension in order (space separated)
 *    n is the size of the cluster table.
 * 
 * @author Seth Kelley
 */

public class Clusters {

    public static void main(String[] args) {

        if ( args.length < 2 ) {
            StdOut.println(
                "Execute: java -cp bin spiderman.Clusters <dimension INput file> <collider OUTput file>");
                return;
        }

        StdIn.setFile(args[0]);
        int numDimensions = StdIn.readInt();
        int initialSize = StdIn.readInt();
        double threshold = StdIn.readDouble();

        ArrayList<LinkedList<Integer>> clusterTable = new ArrayList<>(initialSize);
        for (int i = 0; i < initialSize; i++) {
            clusterTable.add(new LinkedList<>());
        }

        for (int i = 0; i < numDimensions; i++) {
            int dimensionNumber = StdIn.readInt();
            StdIn.readInt();
            StdIn.readInt();

            int index = dimensionNumber % clusterTable.size();
            clusterTable.get(index).addFirst(dimensionNumber);

            if ((i + 1.0) / clusterTable.size() >= threshold) {
                rehash(clusterTable);
            }
        }

        for (int i = 0; i < clusterTable.size(); i++) {
            int prevIndex = (i - 1 + clusterTable.size()) % clusterTable.size();
            int prevPrevIndex = (i - 2 + clusterTable.size()) % clusterTable.size();

            if (!clusterTable.get(prevIndex).isEmpty()) {
                clusterTable.get(i).addLast(clusterTable.get(prevIndex).getFirst());
            }
            if (!clusterTable.get(prevPrevIndex).isEmpty()) {
                clusterTable.get(i).addLast(clusterTable.get(prevPrevIndex).getFirst());
            }
        }

        StdOut.setFile(args[1]);
        for (LinkedList<Integer> cluster : clusterTable) {
            for (int dimension : cluster) {
                StdOut.print(dimension + " ");
            }
            StdOut.println();
        }
    }

    private static void rehash(ArrayList<LinkedList<Integer>> clusterTable) {
        ArrayList<LinkedList<Integer>> newTable = new ArrayList<>(clusterTable.size() * 2);
        for (int i = 0; i < clusterTable.size() * 2; i++) {
            newTable.add(new LinkedList<>());
        }

        for (LinkedList<Integer> cluster : clusterTable) {
            for (int dimension : cluster) {
                int index = dimension % newTable.size();
                newTable.get(index).addFirst(dimension);
            }
        }

        clusterTable.clear();
        clusterTable.addAll(newTable);
    }
}