package csci2320;

import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.NoSuchElementException;



public class TrieSet implements Iterable<String> {
  // Add your Node and private data here.
  private static class Node{
    Map<Character, Node> children;
    boolean isEndOfWord;

    Node(){
      children = new HashMap<>();
      isEndOfWord = false;
    }
  }

  private Node root;
  private Map<String, String> parent;
  private Map<String, Integer> rank;

  public TrieSet(){
    root = new Node();
    parent = new HashMap<>();
    rank = new HashMap<>();
  }

  /**
   * Adds a string to the set.
   */
  public void add(String str) {
    Node current = root;
    for(char c : str.toCharArray()){
      current.children.putIfAbsent(c, new Node());
      current = current.children.get(c);
    } 
    current.isEndOfWord = true;
    makeSet(str);
  }

  /**
   * Removes a string from the set.
   * @return true if it was removed. false if it wasn't found.
   */
  public boolean remove(String str) {
    if(!contains(str)){
      return false;
    }
    Node current = root;
    for(char c : str.toCharArray()){
      current = current.children.get(c);
    }
    current.isEndOfWord = false;
    return true;
  }

  /**
   * Tells if a value is in the set.
   */
  public boolean contains(String str) {
    Node current = root;
    for (char c : str.toCharArray()){
      if(!current.children.containsKey(c)){
        return false;
      }
      current = current.children.get(c);
    }
    return current.isEndOfWord;
  }

  // I'm giving you this iterator because you didn't really want to write one yourself.
  private class TrieIterator implements Iterator<String> {
    private StringBuilder prefix = new StringBuilder();
    private Stack<Iterator<Map.Entry<Character, Node>>> stack = new Stack<>();

    public TrieIterator() {
      pushFirst(root);
    }

    private void pushFirst(Node n) {
      var iter = n.children.entrySet().iterator();
      stack.push(iter);
      if (!n.isEndOfWord) {
        var entry = iter.next();
        prefix.append(entry.getKey());
        pushFirst(entry.getValue());
      }
    }

    private void advance() {
      if (stack.peek().hasNext()) {
        var iter = stack.peek();
        var entry = iter.next();
        prefix.append(entry.getKey());
        pushFirst(entry.getValue());
      } else {
        stack.pop();
        if (!stack.isEmpty()) {
          prefix.deleteCharAt(prefix.length() - 1);
          advance();
        }
      }
    }

    @Override
    public boolean hasNext() {
      return !stack.isEmpty();
    }

    @Override
    public String next() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      }
      var ret = prefix.toString();
      advance();
      return ret;
    }
  }

  /**
   * Returns an iterator that goes through this set.
   */
  @Override
  public Iterator<String> iterator() {
    return new TrieIterator();
  }

  /**
   * Returns the longest prefix of the given string that is in the set.
   */
  public String longestPrefix(String str) {
    Node current = root;
    StringBuilder prefix = new StringBuilder();
    for (char c : str.toCharArray()) {
      if (!current.children.containsKey(c)) {
        break;
      }
      prefix.append(c);
      current = current.children.get(c);
      }
      return prefix.toString();
    }

  /**
   * Returns a list of all suffixes of the given string for strings in the set. Only return the
   * suffix, not the complete word. So if "valid" is in the set and the string is "val" then
   * "id" should be in the returned list. If the prefix is valid, the return set should include
   * the empty string.
   */
  public Set<String> validSuffixes(String str) {
    Node prefixNode = findPrefixNode(str);
    Set<String> suffixes = new HashSet<>();
    collectSuffixes(prefixNode, "", suffixes);
    return suffixes;
  }

  private Node findPrefixNode(String prefix) {
    Node current = root;
    for (char c : prefix.toCharArray()) {
        if (!current.children.containsKey(c)) {
            return null;
        }
        current = current.children.get(c);
    }
    return current;
}

private void collectSuffixes(Node node, String suffix, Set<String> suffixes) {
    if (node == null) {
        return;
    }
    if (node.isEndOfWord) {
        suffixes.add(suffix);
    }
    for (char c : node.children.keySet()) {
        collectSuffixes(node.children.get(c), suffix + c, suffixes);
    }
}

  private void makeSet(String x) {
    parent.put(x, x);
    rank.put(x, 0);
  }

}
