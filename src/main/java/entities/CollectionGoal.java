package entities;
import interface_adapters.Obtainable;
import java.util.ArrayList;
import java.util.List;

public class CollectionGoal extends Obtainable {
    private String name;

    private ArrayList<Obtainable> collectionItems;

    private int collectionCount;
    
    public CollectionGoal(String name, ArrayList<Obtainable> items, int count) {
        this.name = name;
        this.collectionItems = items;
        this.collectionCount = count;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public ArrayList<ObtainOption> getDependencies() {
        return new ArrayList<>();
    }

    public void setCollectionCount(int collectionCount) {
        this.collectionCount = collectionCount;
    }

    public int getCollectionCount() {
        return collectionCount;
    }

    public List<Obtainable> getCollectionItems() {
        return collectionItems;
    }

    public void setCollectionItems(List<Obtainable> collectionItems) {
        this.collectionItems = (ArrayList<Obtainable>) collectionItems;
    }
}