package csci2320;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * DO NOT EDIT THIS FILE!
 * This is the file that is used for running the speed test.
 */
public class SpeedTest {
  static final int BIG_CNT = 10000000;
  static final int GRAPH_CNT = 10000;
  public static void main(String[] args) {
    run();
  }

  public static void run() {
    var start = System.nanoTime();
    Random rand = new Random(48283);

    // Stack
    {
      var stack = new ArrayStack<Double>();
      for (int i = 0; i < BIG_CNT; ++i) {
        stack.push(rand.nextDouble());
      }
      while (!stack.isEmpty()) stack.pop();
      for (int i = 0; i < BIG_CNT; ++i) {
        stack.push(rand.nextDouble());
      }
      for (int i = 0; i < BIG_CNT; ++i) {
        switch (rand.nextInt() % 2) {
          case 0:
            stack.peek();
            break;
          case 1:
            stack.pop();
            break;
          case 2:
            stack.push(rand.nextDouble());
            break;
        }
      }
      System.out.println("Stack split: " + (System.nanoTime() - start)*1e-9);
    }

    // Queue
    {
      var queue = new ArrayQueue<Double>();
      for (int i = 0; i < BIG_CNT; ++i) {
        queue.enqueue(rand.nextDouble());
      }
      while (!queue.isEmpty()) queue.dequeue();
      for (int i = 0; i < BIG_CNT; ++i) {
        queue.enqueue(rand.nextDouble());
      }
      for (int i = 0; i < BIG_CNT; ++i) {
        switch (rand.nextInt() % 2) {
          case 0:
            queue.peek();
            break;
          case 1:
            queue.dequeue();
            break;
          case 2:
            queue.enqueue(rand.nextDouble());
            break;
        }
      }
      System.out.println("Queue split: " + (System.nanoTime() - start)*1e-9);
    }

    var bigGraph = new ArrayList<List<Integer>>();
    for (int i = 0; i < GRAPH_CNT; ++i) {
      var row = new ArrayList<Integer>();
      bigGraph.add(row);
      for (int j = 0; j < i + 1; ++j) {
        if (rand.nextBoolean()) row.add(j);
      }
      if (i < GRAPH_CNT - 1) row.add(i + 1);
    }

    int[][] maze = {
      {0,0,0,0,0,0,0,0,0,0},
      {0,1,1,1,1,0,1,1,1,0},
      {0,1,0,0,0,0,0,0,1,0},
      {0,1,0,1,1,1,1,0,1,0},
      {0,1,0,1,0,0,0,0,1,1},
      {0,0,0,0,0,0,1,0,0,0},
      {0,1,0,1,1,1,1,0,1,0},
      {0,1,0,0,0,0,0,0,1,0},
      {0,1,1,1,1,0,1,1,1,0},
      {0,0,0,0,1,0,0,0,0,0}
    };
    var mazeGraph = MazeToGraph.mazeToAdjacencyList(maze);

    // BFS
    {
      GraphSearch.bfsShortestPath(bigGraph, 0, GRAPH_CNT-1);
      GraphSearch.bfsShortestPath(mazeGraph.graph(), 0, mazeGraph.graph().size()-1);
      System.out.println("BFS split: " + (System.nanoTime() - start)*1e-9);
    }

    // RPC
    {
      int size = 1000000;
      HashMap<String, Double> varMap = new HashMap<>();
      for (char c = 'a'; c <= 'z'; c++) {
          varMap.put(Character.toString(c), rand.nextDouble());
      }
      int stack = 0;
      StringBuilder sb = new StringBuilder();
      String[] ops = {"+ ", "- ", "* ", "/ "};
      for (int i = 0; i < size || stack > 1; ++i) {
          if (i >= size || (stack >= 2 && rand.nextDouble() < 0.3)) {
              sb.append(ops[rand.nextInt(ops.length)]);
              stack -= 1;
          } else {
              stack += 1;
              if (rand.nextDouble() < 0.5) {
                  sb.append(String.format("%1.3f", rand.nextDouble()) + " ");
              } else {
                  sb.append((char)('a' + rand.nextInt(26)) + " ");
              }
          }
      }
      String test = sb.toString().trim();
      RPCalc.eval(test, varMap);
      System.out.println("RPC split: " + (System.nanoTime() - start)*1e-9);
    }

    System.out.println("Total time: " + (System.nanoTime() - start)*1e-9);
  }
}
