package spiderman;

public class DimOfNode {
    private int dimensionID;
    private int canonCount;
    private int weightValue;
    private DimOfNode nextNode;

    /*
     * Constructor
     * @param dimensionID the ID of the dimension
     * @param canonCount the number of canon events for the dimension
     * @param weightValue the weight value of the dimension
     * @param nextNode link to the next node in the list
     */
    public DimOfNode(int dimensionID, int canonCount, int weightValue, DimOfNode nextNode) {
        this.dimensionID = dimensionID;
        this.canonCount = canonCount;
        this.weightValue = weightValue;
        this.nextNode = nextNode;
    }

    public int getDimensionID() {
        return dimensionID;
    }
    public void setDimensionID(int dimensionID) {
        this.dimensionID = dimensionID;
    }
    public int getCanonCount() {
        return canonCount;
    }
    public void setCanonCount(int canonCount) {
        this.canonCount = canonCount;
    }
    public int getWeightValue() {
        return weightValue;
    }
    public void setWeightValue(int weightValue) {
        this.weightValue = weightValue;
    }
    public DimOfNode getNextNode() {
        return nextNode;
    }
    public void setNextNode(DimOfNode nextNode) {
        this.nextNode = nextNode;
    }
}