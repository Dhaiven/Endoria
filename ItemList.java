import java.util.HashMap;

/**
 *  Cette classe représente une list contenant des items
 *
 * @author  DEBELLE Hugp
 * @version 2.0 (Février 2025)
 */
public class ItemList {

    private final HashMap<String, Item> aItems;

    public ItemList() {
        this.aItems = new HashMap<>();
    }

    public Item getItemByName(final String pName) {
        return this.aItems.get(pName);
    }

    /**
     * @return la somme du poids de chaque item
     */
    public int getWeight() {
        int vResult = 0;
        for (Item vItem : this.aItems.values()) {
            vResult += vItem.getWeight();
        }
        return vResult;
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

        StringBuilder vResult = new StringBuilder();
        for (Item item : this.aItems.values()) {
            vResult.append(item.getLongDescription()).append("\n");
        }

        return vResult.toString();
    }
}
