package utilities;

import entities.CollectionGoal;
import entities.ObtainOption;
import entities.PlayerState;
import interface_adapters.IObtainable;
import java.util.ArrayList;
import java.util.HashMap;

enum Color {
    WHITE,
    BLACK,
    GREY
}

public class TopologicalSort {
    static int time = 0;

    private HashMap<String, ArrayList<String>> generateMap(ArrayList<IObtainable> goals) {
        HashMap<String, ArrayList<String>> map = new HashMap<>();

        // initialize all nodes
        for (IObtainable goal : goals) {
            map.put(goal.getName(), new ArrayList<>());
        }

        // build edges: dependency -> goal
        for (IObtainable goal : goals) {
            for (ObtainOption deps : goal.getDependencies()) {
                for (IObtainable dep : deps.getDependencies()) {
                    if (dep instanceof CollectionGoal) {
                        String depName = ((CollectionGoal) dep).getName();

                        // ensure dependency exists in map
                        map.putIfAbsent(depName, new ArrayList<>());

                        // add edge: dep -> goal
                        map.get(depName).add(goal.getName());
                    }
                }
            }
        }

        return map;
    }

    // private HashMap<String, DFSInfo> generateDFSMap(ArrayList<IObtainable> goals) {
    //     HashMap<String, DFSInfo> map = new HashMap<>();

    //     // initialize all nodes
    //     for (Goal goal : goals) {
    //         map.put(goal.getName(), new DFSInfo());
    //     }
    //     return map;
    // }

    private HashMap<String, KahnInfo> generateInMap(ArrayList<IObtainable> goals) {
        HashMap<String, KahnInfo> map = new HashMap<>();

        for (IObtainable goal : goals) {
            map.put(goal.getName(), new KahnInfo(goal));
        }

        for (IObtainable goal : goals) {
            KahnInfo goalInfo = map.get(goal.getName());

            for (ObtainOption deps : goal.getDependencies()) {
                for (IObtainable dep : deps.getDependencies()) {
                    goalInfo.addNeighbor(dep); // goal depends on dep

                    KahnInfo depInfo = map.get(dep.getName());
                    if (depInfo != null) {
                        depInfo.addDependent(goal); // dep is needed by goal
                    }
                }
            }            
        }

        return map;
    }

    public class DFSInfo {

        private int discoverTime;
        private int finishTime;
        private Color color;

        public DFSInfo() {
            this.discoverTime = -1;
            this.finishTime = -1;
            this.color = Color.WHITE;
        }

        public int getDiscoverTime() {
            return discoverTime;
        }

        public void setDiscoverTime(int discoverTime) {
            this.discoverTime = discoverTime;
        }

        public int getFinishTime() {
            return finishTime;
        }

        public void setFinishTime(int finishTime) {
            this.finishTime = finishTime;
        }

        public Color getColor() {
            return color;
        }

        public void setColor(Color color) {
            this.color = color;
        }

        @Override
        public String toString() {
            return "Goal{" +
                "discoverTime=" + discoverTime +
                ", finishTime=" + finishTime +
                ", color=" + color +
                '}';
        }
    }

    public class KahnInfo {
        private ArrayList<IObtainable> neighbors;
        private ArrayList<IObtainable> dependents;
        private IObtainable node;

        public KahnInfo(IObtainable node) {
            this.neighbors = new ArrayList<>();
            this.dependents = new ArrayList<>();
            this.node = node;
        }

        public ArrayList<IObtainable> getNeighbors() {
            return neighbors;
        }

        public ArrayList<IObtainable> getDependents() {
            return dependents;
        }

        public void addNeighbor(IObtainable neighbor) {
            neighbors.add(neighbor);
        }

        public void addDependent(IObtainable dependent) {
            dependents.add(dependent);
        }

        public void removeNeighbor(IObtainable neighbor) {
            neighbors.remove(neighbor);
        }

        public boolean canObtain(PlayerState state) {
            return this.node.canObtain(state);
        }

        public IObtainable getNode() {
            return node;
        }
    }

    public ArrayList<IObtainable> sort_and_reduce(ArrayList<IObtainable> goals) {
        ArrayList<KahnInfo> q = new ArrayList<>();
        HashMap<String, KahnInfo> map = generateInMap(goals);
        ArrayList<IObtainable> return_list = new ArrayList<>();
        PlayerState state = new PlayerState();

        for (IObtainable g : goals) {
            KahnInfo k = map.get(g.getName());

            if (k.canObtain(state)) {
                q.add(k);
            }
        }

        while (!q.isEmpty()) {
            int index = (int) (q.size() * Math.random());
            KahnInfo top = q.remove(index);
            top.getNode().getEffects().applyToState(state);

            if (!return_list.contains(top.getNode())) {
                // add the node to completed list
                return_list.add(top.getNode());

                // System.out.println(top.getNode().getName() + ": " + top.getNode().getSuccessfulOption(state));

                // add the node to the player state
                state.obtain(top.getNode());
            }

            for (IObtainable goal : goals) {
                if (!return_list.contains(goal) && goal.isObtained(state)) {
                    return_list.add(goal);
                }
            }

            for (IObtainable dependent : top.getDependents()) {
                KahnInfo k2 = map.get(dependent.getName());
                if (k2 == null) continue;

                k2.removeNeighbor(top.getNode());

                if (k2.canObtain(state) && !return_list.contains(k2.getNode())) {
                    q.add(k2);
                }
            }
        }

        return return_list;
    }
}

