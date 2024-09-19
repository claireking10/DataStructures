package csci2320;

import java.util.Iterator;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public class BSTMap<K extends Comparable<K>, V> implements Map<K, V> {
  // Put your Node class and private data up here.
  private static class Node<K extends Comparable<K>,V> {
    K key;
    V value;
    Node<K,V> left = null, right = null, parent = null;

    Node(K key, V val, Node<K,V> parent){
      this.key = key;
      this.value = val;
      this.parent = parent;
    }

    public boolean leftSide(K newKey){
      if(newKey.compareTo(key) < 0) return true;
      return false;
    }
  }

  private Node<K,V> root = null;
  private int numElem = 0;

  // I'm giving you some helper method to make testing easier.
  @SuppressWarnings("unchecked")
  public static <K extends Comparable<K>, V> BSTMap<K, V> of(Map.KeyValuePair<K, V>... elems) {
    BSTMap<K, V> ret = new BSTMap<>();
    for (var kvp: elems) ret.put(kvp.key(), kvp.value());
    return ret;
  }

  @Override
  public boolean equals(Object that) {
    if (that == null || !(that instanceof BSTMap)) return false;
    BSTMap<?, ?> thatSeq = (BSTMap<?, ?>)that;
    if (thatSeq.size() != size()) return false;
    for (Iterator<?> iter1 = thatSeq.iterator(), iter2 = this.iterator(); iter1.hasNext();)
      if (!iter1.next().equals(iter2.next())) return false;
    return true;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("BSTMap(");
    boolean first = true;
    for (var kvp: this) {
      if (!first) {
        sb.append(", " + kvp.key() +"->"+kvp.value());
      } else {
        sb.append(kvp.key() +"->"+kvp.value());
        first = false;
      }
    }
    sb.append(")");
    return sb.toString();
  }

  private Node<K,V> min(Node<K,V> y){
    Node<K,V> current = y;
    while(current.left != null) current = current.left;
    return current;
  }

  @Override
  public Iterator<KeyValuePair<K, V>> iterator() {
    return new Iterator<KeyValuePair<K,V>>(){
      Node<K,V> current = min(root);
      @Override
      public boolean hasNext(){
        return current != null;
      }
      @Override
      public KeyValuePair<K,V> next(){
        if(!hasNext()){
          return null;
        }
        Node<K,V> temp = current;
        current = successor();
        return Map.kvp(temp.key, temp.value);
      }
      private Node<K,V> successor(){
        if(current.right != null){
          return min(current.right);
        }
        else{
          Node<K,V> currentParent = current.parent;
          while(currentParent != null && current == currentParent.right){
            current = currentParent;
            currentParent = currentParent.parent;
          }
          return currentParent;
        }
      }
    };
  }

    // Note that if you choose to put a parent in your node, you can use the pseudocode
    // in the book for this. If you don't, you are allowed to use a java.util.Stack. Both
    // approaches will need to be named so they can have a constructor that either moves
    // the Node pointer to the first value or populates the stack.
  

  @Override
  public Optional<V> put(K key, V value) {
    Node<K,V> rover = root;
    Node<K,V> trailer = null;
    while (rover!=null){
      trailer = rover;
      if(key.compareTo(rover.key)<0) rover = rover.left;
      else if (key.compareTo(rover.key) ==0){
        var temporary = rover.value;
        rover.value = value;
        return Optional.of(temporary);
      }
      else rover = rover.right;
    }
    if(trailer ==null){
      root = new Node<K,V>(key,value,null);
      numElem++;
    }
    else if (key.compareTo(trailer.key) < 0){
      trailer.left = new Node<K,V>(key, value, trailer);
      numElem++;
    }
    else{
      trailer.right = new Node<K,V>(key,value,trailer);
      numElem++;
    }
    return Optional.of(value);
    }

  @Override
  public Optional<V> get(K key) {
    Node<K, V> rover = root;
    while (rover != null && !rover.key.equals(key)){
      if (rover.leftSide(key)) rover = rover.left;
      else rover = rover.right;
    }
    if (rover == null) return Optional.empty();
    if (key.equals(rover.key)) return Optional.of(rover.value);
    return Optional.empty();
  }

  @Override
  public V getOrElse(K key, V defaultValue) {
    Node<K, V> rover = root;
    while (rover != null){
      if (rover.key.equals(key)) return rover.value;
      if (rover.leftSide(key)) rover = rover.left;
      else rover = rover.right;
    }
    return defaultValue;
  }

  @Override
  public boolean contains(K key) {
    Node<K,V> rover = root;
    while (rover != null){
      if (key.compareTo(rover.key) < 0) rover = rover.left;
      else if (key.compareTo(rover.key) == 0) return true;
      else rover = rover.right;
    }
    return false;
  }

  @Override
  public Optional<V> remove(K key) {
    Node< K,V> rover = root;
    Node<K,V> trailer = null;
    while(rover != null){
      if(key.compareTo(rover.key)==0){
        Node<K,V> temporary = rover;
        if(rover.left == null){
          transplant(rover, rover.right);
        }
        else if(rover.right == null){
          transplant(rover,rover.left);
        }
        else{
          trailer = min(rover.right);
          if(trailer!=rover.right){
            transplant(trailer,trailer.right);
            trailer.right = rover.right;
            trailer.right.parent = trailer;
          }
          transplant(rover,trailer);
          trailer.left = rover.left;
          trailer.left.parent = trailer;
        }
        numElem--;
        return Optional.of(temporary.value);
      }
      else if (key.compareTo(rover.key)<0){
        trailer =  rover;
        rover = rover.left;
      }
      else{
        trailer = rover;
        rover = rover.right;
      }
    }
    return Optional.empty();
  }

  private void transplant(Node<K,V> p, Node<K,V> v){
    if (p.parent == null) root = v;
    else if (p == p.parent.left) p.parent.left = v;
    else p.parent.right = v;
    if(v != null) v.parent = p.parent;
  }


  @Override
  public int size() {
    return numElem;
  }

  @Override
  public Set<K> keySet(){
    return new Set<K>(){
      @Override
      public Iterator<K> iterator(){
        return new Iterator<K>(){
          private Iterator<KeyValuePair<K,V>> iterator = BSTMap.this.iterator();
          @Override
          public boolean hasNext(){
            return iterator.hasNext();
          }
          @Override
          public K next(){
            return iterator.next().key();
          }
        };
      }
      @Override
      public boolean contains(K elem){
        return BSTMap.this.contains(elem);
      }
      @Override
      public int size(){
        return numElem;
      }
    };
  }

    // Implementation note. This can be done with an anonymous inner class.
    // You can refer to the BSTMap it is in with `BSTMap.this`. So you
    // can call things like `BSTMap.this.contains`.
  
  
  @Override
  public <V2> BSTMap<K, V2> mapValues(Function<V, V2> f) {
    BSTMap<K,V2> result = new BSTMap<>();
    for( KeyValuePair<K,V> kvp : this){
      V2 diffval = f.apply(kvp.value());
      result.put(kvp.key(), diffval);
    }
    return result;
}

  @Override
  public Map<K, V> filter(Function<Map.KeyValuePair<K, V>, Boolean> predicate) {
    BSTMap<K, V> result = new BSTMap<>();
    for (KeyValuePair<K,V> kvp: this) {
      if (predicate.apply(kvp)) {
        result.put(kvp.key(), kvp.value());
      }
    }
    return result;
  }

  @Override
  public Optional<Map.KeyValuePair<K, V>> find(Function<Map.KeyValuePair<K, V>, Boolean> predicate) {
    for( KeyValuePair<K,V> kvp:this) {
      if ( predicate.apply(kvp)){
        return Optional.of(kvp);
      }
    }
    return Optional.empty();
}

  @Override
  public <E2> E2 fold(E2 zero, BiFunction<E2, Map.KeyValuePair<K, V>, E2> f) {
    for( KeyValuePair<K,V> kvp : this) {
      zero = f.apply(zero, kvp);
    }
    return zero;
}

  @Override
  public boolean exists(Function<Map.KeyValuePair<K, V>, Boolean> predicate) {
    for ( KeyValuePair<K,V> kvp : this ) { 
      if ( predicate.apply(kvp)){
        return true; 
      }
  }
  return false; 
}

  @Override
  public boolean forall(Function<Map.KeyValuePair<K, V>, Boolean> predicate) {
  for (KeyValuePair<K,V> kvp: this){
    if (!predicate.apply(kvp)){
      return false;
    }
  }
  return true;
  }
  
  
  
  // ----------------------------These are potential helper functions for debugging ---------------------
  // You can modify these if you want. I don't call them in any way. They don't have to be used if you 
  // don't need them. I used them in debugging my own implementation.

   public void inorderPrint() {
     inorderPrintRecur(root);
   }

   private void inorderPrintRecur(Node<K, V> n) {
     if (n != null) {
       inorderPrintRecur(n.left);
       System.out.println(n.key+" -> "+n.value+"  ");
       inorderPrintRecur(n.right);
     }
   }

   public void preorderPrintKeys() {
     preorderPrintKeysRecur(root);
     System.out.println();
   }

   public void preorderPrintKeysRecur(Node<K, V> n) {
     if (n != null) {
       System.out.print("(" + n.key);
       preorderPrintKeysRecur(n.left);
       preorderPrintKeysRecur(n.right);
       System.out.print(")");
     }
   }

   public boolean isConsistent() {
     return isConsistentRecur(root);
   }

   private boolean isConsistentRecur(Node<K, V> n) {
     if (n == null) return true;
     if (n.parent != null && n.parent.left != n && n.parent.right != n) {
       System.out.println("Not a child of parent at "+n.key);
       return false;
     }
     if (n.left != null && n.left.key.compareTo(n.key) >= 0) {
       System.out.println("Left child not smaller at "+n.key);
       return false;
     }
     if (n.right != null && n.right.key.compareTo(n.key) <= 0) {
       System.out.println("Right child not larger at "+n.key);
       return false;
     }
     return isConsistentRecur(n.left) && isConsistentRecur(n.right);
    } }


