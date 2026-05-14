package entities;
import interface_adapters.IObtainable;
import java.util.ArrayList;
import java.util.List;

public class CollectionGoal implements IObtainable{
    private String name;

    private ArrayList<IObtainable> collectionItems;

    private int collectionCount;
    
    public CollectionGoal(String name, ArrayList<IObtainable> items, int count) {
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

    public List<IObtainable> getCollectionItems() {
        return collectionItems;
    }

    public void setCollectionItems(List<IObtainable> collectionItems) {
        this.collectionItems = (ArrayList<IObtainable>) collectionItems;
    }
}