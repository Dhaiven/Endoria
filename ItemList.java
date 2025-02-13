import java.util.HashMap;

public class ItemList {

    private HashMap<String, Item> aItems;

    public ItemList() {
        this.aItems = new HashMap<>();
    }

    public Item getItemByName(String pName) {
        return this.aItems.get(pName);
    }

    public int getWeight() {
        int sum = 0;
        for (Item item : this.aItems.values()) {
            sum += item.getWeight();
        }
        return sum;
    }

    public void addItem(final Item pItem) {
        this.aItems.put(pItem.getName(), pItem);
    }

    public void removeItem(final Item pItem) {
        this.aItems.remove(pItem.getName());
    }

    /**
     * @return un String de touts les items disposables
     */
    public String getItemString() {
        if (this.aItems.isEmpty()) {
            return "No item.";
        }

        StringBuilder result = new StringBuilder();
        for (Item item : this.aItems.values()) {
            result.append(item.getLongDescription()).append("\n");
        }

        return result.toString();
    }
}
