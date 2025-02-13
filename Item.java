public class Item {

    private String aName;
    private String aDescription;
    private int aWeight;

    public Item(String pName, String pDescription, int pWeight) {
        this.aName = pName;
        this.aDescription = pDescription;
        this.aWeight = pWeight;
    }

    public String getName() {
        return this.aName;
    }

    public String getDescription() {
        return this.aDescription;
    }

    public String getLongDescription() {
        return "Item " + this.getName() + "\n" + this.getDescription() + "\nWeight: " + this.getWeight();
    }

    public int getWeight() {
        return this.aWeight;
    }

    public void onUse(Player player) {

    }
}
