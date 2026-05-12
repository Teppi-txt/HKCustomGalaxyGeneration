package entities;
import interface_adapters.IObtainable;
import java.util.ArrayList;

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
    public boolean canObtain(PlayerState state) {
        return false;
    }

    @Override
    public boolean isObtained(PlayerState state) {
        int count = 0;
        for (IObtainable objective : state.getObjectives()) {
            if (collectionItems.contains(objective)) {
                count += 1;
            }
        }
        return count >= collectionCount;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public ArrayList<ObtainOption> getDependencies() {
        return new ArrayList<>();
    }

    @Override
    public PlayerStateEffect getMinimalEffect(PlayerState state) {
        return new PlayerStateEffect();
    }

    public void setCollectionCount(int collectionCount) {
        this.collectionCount = collectionCount;
    }

    public int getCollectionCount() {
        return collectionCount;
    }

    public ArrayList<IObtainable> getCollectionItems() {
        return collectionItems;
    }

    public void setCollectionItems(ArrayList<IObtainable> collectionItems) {
        this.collectionItems = collectionItems;
    }
}