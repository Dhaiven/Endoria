import java.util.HashMap;

/**
 *  Cette classe représente une list contenant des items
 *
 * @author  DEBELLE Hugp
 * @version 2.0 (Février 2025)
 */
public class ItemList {

    private HashMap<String, Item> aItems;

    public ItemList() {
        this.aItems = new HashMap<>();
    }

    public Item getItemByName(String pName) {
        return this.aItems.get(pName);
    }

    /**
     * @return la somme du poids de chaque item
     */
    public int getWeight() {
        int sum = 0;
        for (Item item : this.aItems.values()) {
            sum += item.getWeight();
        }
        return sum;
    }

    /**
     * Ajoute un item
     */
    public void addItem(final Item pItem) {
        this.aItems.put(pItem.getName(), pItem);
    }

    /**
     * Supprime un item
     */
    public void removeItem(final Item pItem) {
        this.aItems.remove(pItem.getName());
    }

    /**
     * @return un String de tout les items disposables
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
