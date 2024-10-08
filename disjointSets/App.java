/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package csci2320;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.Random;

public class App {
    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in)) {
            String testType = sc.next();
            if (testType.equals("speed")) {
                speed(100000);
                return;
            }
            Random rand = new Random(sc.nextLong());
            int size1 = sc.nextInt();
            int size2 = sc.nextInt();
            switch(testType) {
                case "basic":
                    basicTest(rand, size1);
                    basicTest(rand, size2);
                    break;
                case "mst":
                    mstTest(rand, size1, sc.nextDouble());
                    mstTest(rand, size2, sc.nextDouble());
                    break;
            }
        }
    }

    public static void basicTest(Random rand, int size) {
        List<DisjointSet<Integer>> sets = new ArrayList<>();
        for (int i = 0; i < size; ++i) {
            sets.add(DisjointSet.makeSet(i));
        }
        int unionCount = 0;
        for (int i = 0; i < size; ++i) {
            int s1 = rand.nextInt(size);
            int s2 = rand.nextInt(size);
            if (sets.get(s1).findSet() != sets.get(s2).findSet()) {
                sets.get(s1).union(sets.get(s2));
                unionCount++;
                if (!sets.get(s1).findSet().equals(sets.get(s2).findSet())) {
                    System.out.println("Sets don't match after union.");
                }
            }
        }
        Set<Integer> endSets = new HashSet<>();
        for (int i = 0; i < size; ++i) {
            endSets.add(sets.get(i).findSet().getElement());
        }
        System.out.println(size + " " + unionCount + " " + endSets.size());
    }

    static List<MinimumSpanningTree.Edge> randomWeightedGraph(Random rand, int numVerts, double density) {
        var ret = new ArrayList<MinimumSpanningTree.Edge>();
        for (int i = 0; i < numVerts; ++i) {
            for (int j = i+1; j < numVerts; ++j) {
                if (rand.nextDouble() < density) ret.add(new MinimumSpanningTree.Edge(i, j, rand.nextDouble()));
            }
        }
        return ret;
    }

    public static void mstTest(Random rand, int size, double density) {
        var edges = randomWeightedGraph(rand, size, density);
        var mst = MinimumSpanningTree.kruskals(size, edges);
        var cnt = 0;
        var weightSum = 0.0;
        for (var edge: mst) {
            cnt += 1;
            weightSum += edge.weight();
        }
        System.out.printf("%d %1.4f\n", cnt, weightSum);
    }

    static void speed(int size) {
        var start = System.nanoTime();
        Random rand = new Random(48283);

        basicTest(rand, size);
        System.out.println("Basic time: " + (System.nanoTime() - start)*1e-9);
        mstTest(rand, size / 10, 0.8);
        System.out.println("Final time: " + (System.nanoTime() - start)*1e-9);
    }
}
