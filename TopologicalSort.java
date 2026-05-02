import entities.Goal;
import entities.PlayerState;
import interface_adapters.IObtainable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

enum Color {
    WHITE,
    BLACK,
    GREY
}

public class TopologicalSort {
    static int time = 0;


    //implementation of DFS ordering
    // public ArrayList<Goal> sort(ArrayList<Goal> goals) {
    //     HashMap<String, ArrayList<String>> map = generateMap(goals);
    //     HashMap<String, DFSInfo> dfs = generateDFSMap(goals);

    //     ArrayList<Goal> return_list = new ArrayList<>();

    //     //run a topological sort
    //     time = 0;

    //     for (Goal g : goals) {
    //         DFSInfo node = dfs.get(g.getName());
    //         if (node.getColor() == Color.WHITE) {
    //             // start dfs

    //             int status = dfs_helper(map, dfs, g, return_list);
    //             if (status == -1) { // cycle detected, no top. ordering possible
    //                 return null;
    //             }
    //         }
    //     }
    //     return return_list;
    // }

    private int dfs_helper(HashMap<String, ArrayList<String>> map, HashMap<String, DFSInfo> dfs, Goal g, ArrayList<Goal> return_list) {
        time += 1;
        dfs.get(g.getName()).setColor(Color.GREY);
        dfs.get(g.getName()).setDiscoverTime(time);
        
        for (Goal c : g.getGoalDependencies()) {
            DFSInfo node = dfs.get(c.getName());
            if (node.getColor() == Color.WHITE) {
                if (dfs_helper(map, dfs, c, return_list) == -1) {
                    return -1;
                };
            } else if (node.getColor() == Color.GREY) {
                return -1;
            }
        }
        
        time += 1;
        dfs.get(g.getName()).setColor(Color.BLACK);
        dfs.get(g.getName()).setFinishTime(time);
        return_list.add(g);
        return 1;
    }

    private HashMap<String, ArrayList<String>> generateMap(ArrayList<IObtainable> goals) {
        HashMap<String, ArrayList<String>> map = new HashMap<>();

        // initialize all nodes
        for (IObtainable goal : goals) {
            map.put(goal.getName(), new ArrayList<>());
        }

        // build edges: dependency -> goal
        for (IObtainable goal : goals) {
            for (IObtainable dep : goal.getDependencies()) {
                if (dep instanceof Goal) {
                    String depName = ((Goal) dep).getName();

                    // ensure dependency exists in map
                    map.putIfAbsent(depName, new ArrayList<>());

                    // add edge: dep -> goal
                    map.get(depName).add(goal.getName());
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

            for (IObtainable dep : goal.getDependencies()) {
                goalInfo.addNeighbor(dep); // goal depends on dep

                KahnInfo depInfo = map.get(dep.getName());
                if (depInfo != null) {
                    depInfo.addDependent(goal); // dep is needed by goal
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

        public boolean hasDegreeZero() {
            return neighbors.isEmpty();
        }

        public IObtainable getNode() {
            return node;
        }
    }

    public ArrayList<IObtainable> sort_and_reduce(ArrayList<IObtainable> goals) {
        ArrayList<KahnInfo> q = new ArrayList<>();
        HashMap<String, KahnInfo> map = generateInMap(goals);
        ArrayList<IObtainable> return_list = new ArrayList<>();
        HashSet<String> queued = new HashSet<>();
        PlayerState state = new PlayerState();

        for (IObtainable g : goals) {
            KahnInfo k = map.get(g.getName());

            if (k.hasDegreeZero()) {
                q.add(k);
                queued.add(g.getName());
            }
        }

        while (!q.isEmpty()) {
            int index = (int) (q.size() * Math.random());
            KahnInfo top = q.remove(index);
            top.getNode().getEffects().applyToState(state);
            System.out.println(state);

            if (!return_list.contains(top.getNode())) {
                return_list.add(top.getNode());
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

                if (k2.hasDegreeZero() || !return_list.contains(k2.getNode())) {
                    q.add(k2);
                    queued.add(k2.getNode().getName());
                }
            }
        }

        return return_list;
    }
}

