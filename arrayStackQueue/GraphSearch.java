package csci2320;

import java.util.List;
import java.util.Set;
import java.util.HashSet;

public class GraphSearch {
  static record VertexDistancePair(int vertex, int distance) {}

  /**
   * This method takes an adjacency list and two vertices, start and end, and returns the length of the shortest path from start to end.
   * @param adj adjacency list representation of a graph
   * @param start index of the vertex to start at
   * @param end index of the vertex to end at
   * @return the length of the shortest path from start to end
   */
  public static int bfsShortestPath(List<List<Integer>> adj, int start, int end) {
    ArrayQueue<VertexDistancePair> queue = new ArrayQueue<>();
    Set<Integer> visited = new HashSet<>();
    queue.enqueue(new VertexDistancePair(start, 0));
    visited.add(start);
    while(!queue.isEmpty()){
      VertexDistancePair current = queue.dequeue();
      int currentVertex = current.vertex;
      int currentDistance = current.distance;
      if(currentVertex == end) {
        return currentDistance;
      }
      for (int neighbor:adj.get(currentVertex)){
        if(!visited.contains(neighbor)){
          queue.enqueue(new VertexDistancePair(neighbor, currentDistance + 1));
          visited.add(neighbor);
        }
      }
    }
    return -1;  // This makes my one test pass. Change this according to your code.
  }
}
