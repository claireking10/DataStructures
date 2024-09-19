package csci2320;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public class AVLMap<K extends Comparable<K>, V> implements Map<K, V> {
  // If you use this, you don't have to do null checks on nodes when you
  // get the height. It is static so it can be called from the Node as well.
  // private static <K extends Comparable<K>, V> int height(Node<K, V> n) {
  //   if (n == null) return 0;
  //   return n.height;
  // }
  private Node< K,V> root = null;
  private int numElems = 0; 
  private static class Node<K,V> {
    K key; 
    V value; 
    Node< K,V> left,right;
    Node<K,V> parent;
    int height;

    Node( K key, V value , Node<K,V> parent, Node<K,V> left,Node<K,V> right){
      this.key = key;
      this.value = value;
      this.parent = parent;
      this.left = null;
      this.right = null;
      this.height = 1; 
    }
  }
  private static <K extends Comparable<K> ,V> int height(Node<K,V> n){
     if (n == null) return 0;
     return n.height;
  }
  // Put your Node class and private data up here.

  // I'm giving you some helper method to make testing easier.
  @SuppressWarnings("unchecked")
  public static <K extends Comparable<K>, V> AVLMap<K, V> of(Map.KeyValuePair<K, V>... elems) {
    AVLMap<K, V> ret = new AVLMap<>();
    for (var kvp: elems) ret.put(kvp.key(), kvp.value());
    return ret;
  }

  @Override
  public boolean equals(Object that) {
    if (that == null || !(that instanceof AVLMap)) return false;
    AVLMap<?, ?> thatSeq = (AVLMap<?, ?>)that;
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

  /**
   * Return the height of the tree. This is just for testing purposes, but my tests call it.
   * @return The height of the tree.
   */
  public int treeHeight() {
    // return heigh(root);  // Suggested implementation.
    return height(root);
  }

  @Override
  public Iterator<KeyValuePair<K, V>> iterator() {
    return new AVLiterator();

    // Note that if you choose to put a parent in your node, you can use the pseudocode
    // in the book for this. If you don't, you are allowed to use a java.util.Stack. Both
    // approaches will need to be named so they can have a constructor that either moves
    // the Node pointer to the first value or populates the stack.
  }
  private class AVLiterator implements Iterator<KeyValuePair<K,V>>{
    private Node<K, V> currNode = minNode(root);
    private Node<K, V> trailer = null;

    @Override
    public boolean hasNext() {
      return currNode != null;
    }
    @Override
    public KeyValuePair<K, V> next() {
       if( !hasNext()){
        throw new NoSuchElementException("no elems more elems");
      }
      trailer = currNode;
      currNode = nextNode(currNode);
      return new KeyValuePair<>(trailer.key,trailer.value);
    }
    public Node<K,V> minNode(Node<K,V> node){
      while( node != null && node.left != null){
        node = node.left;
      }
      return node;
    }
    private Node<K,V> nextNode(Node<K,V> node){
      if(node.right != null){
        return minNode(node.right);
      }else {
        while(node.parent != null && node == node.parent.right){
          node = node.parent;
        }
        return node.parent;
      }

    }
    
  } 

  @Override
  public Optional<V> put(K key, V value) {
    if (key == null) {
      throw new IllegalArgumentException( "no null keys");
  }
   if( root == null){ 
    root = new Node<K,V>( key,value,null,null,null);
    numElems++;
    return Optional.empty();
   }
   Node<K,V> rover = root; 
   Node<K,V> trailer = null;

   while( rover != null){
         int comp = key.compareTo(rover.key);
          if( comp ==0){
          V keyval = rover.value;
          rover.value = value; 
          return Optional.of( keyval);

     }
     else if(comp < 0 ){
      trailer = rover ; 
      rover = rover.left;  

     }
     else{
      trailer = rover; 
      rover = rover.right;
     }
   }

   Node<K,V> newNode = new Node<K,V> ( key,value, trailer, null, null);
   int compT = key.compareTo(trailer.key);
   if( compT < 0 ){
    trailer.left = newNode;
   }
   else { 
    trailer.right = newNode;
    }
   numElems++;
   updateTree(newNode);
   return Optional.empty();
    
  }



  private void updateHeight( Node<K,V> node){
    if( node != null){
       int leftHeight = 0;
       int rightHeight = 0;  
     if( node.left != null ) {
      leftHeight = node.left.height;
     }
     if( node.right != null){
      rightHeight = node.right.height;
     }
     node.height = 1 + Math.max( leftHeight,rightHeight);
     } 
   }




     private Node<K,V> rotate ( Node<K,V> node){
      int balance = balanceFact(node);

      if( balance<-1){
              if( balanceFact(node.right) > 0){
              node.right = rightRot(node.right);
      }
          node = leftRot(node);
     
    }
        else if ( balance > 1){
      if( balanceFact(node.left) < 0){
       node.left = leftRot(node.left);
      }
      node = rightRot(node);
    }
    if( node.left != null){
      updateHeight(node.left);
    }
    if( node.right != null){
      updateHeight(node.right);
    }
    return node;
  }

     private int balanceFact( Node<K,V> node){
      if( node == null){
        return 0;
      }
      return height(node.left) - height(node.right);
     
    } 
    private void updateTree( Node<K,V> node){
      Node<K,V> n = node;
      while( n != null){
        n = rotate(n);
      updateHeight(n);
        n = n.parent;
      } 
    }



    private Node<K, V> leftRot( Node<K,V> node){
      Node<K,V> rightChild = node.right;
      if( rightChild != null){
      node.right = rightChild.left;
      if( node.right != null){
        node.right.parent = node;
      }  
      rightChild.left = node;
      rightChild.parent = node.parent;

      if( node == root){
        root = rightChild;
      }
      else if( node == node.parent.left){
        node.parent.left = rightChild;
      }
      else {
        node.parent.right = rightChild;
      }
      node.parent = rightChild;
      updateHeight(node);
      updateHeight(rightChild);
      return rightChild;
      
    }

    updateHeight(node);
    return node; 
  }





  private Node<K, V> rightRot(Node<K, V> node) {
   Node<K, V> leftChild = node.left;
   if (leftChild != null) { 
    node.left = leftChild.right;
    if( node.left != null){
      node.left.parent = node;
    }    
    leftChild.right = node;
    leftChild.parent = node.parent;
    
    if( node == root){
      root = leftChild;
    }
    else if( node == node.parent.right){
      node.parent.right = leftChild;
    }
    else {
      node.parent.left = leftChild;
    }
    node.parent = leftChild;
    updateHeight(node);
    updateHeight(leftChild);
    return leftChild; 
  
    }
    updateHeight(node);
    return node; 
}

  @Override
  public Optional<V> get(K key) {
    Node<K, V> node = findNode(root, key);
    if (node != null) {
        return Optional.of(node.value);
    } else {
        return Optional.empty();
    }
}

private Node<K, V> findNode(Node<K, V> node, K key) {
  if (node == null) {
      return null; 
  }

  int comp = key.compareTo(node.key);

  if (comp < 0) {
      return findNode(node.left, key);
  } else if (comp > 0) {
      return findNode(node.right, key);
  } else {
      return node; 
  }
}



  @Override
  public V getOrElse(K key, V defaultValue) {
    Node< K,V> node = findNode(root, key);
    if( node != null){
      return node.value;
    }
    else { 
      return defaultValue;
    }
  }

  @Override
  public boolean contains(K key) {
    Node<K,V> node = findNode(root, key);
    return ( node != null);
  }

  @Override
  public Optional<V> remove(K key) {
    Node< K,V> remNode = findNode(key);
  if( remNode != null){
    V remVal = remNode.value;
    repNode(remNode);
    numElems--;
    return Optional.of( remVal);
  }
  return Optional.empty();
  }

  private Node<K,V> nextnode( Node<K,V> node){
    Node<K,V> next = node.right;
    while( next.left != null){
      next = next.left;
    }
    return next;
  } 

  public void repNode( Node<K,V>node){
  if( node.left == null){
    replaceNode( node,node.right);
      updateHeight(node);
      
  }
  else if ( node.right == null){
        replaceNode( node,node.left);
        updateHeight(node);
    }
    else {
      Node<K,V> next = nextnode(node);
      Node<K,V> repParent = next.parent;
      if( next != node.right){
        repParent = next.right;
        replaceNode(next,next.right);
        next.right = node.right;
        next.right.parent = next;
      }
      replaceNode( node, next);
      next.left = node.left;
      next.left.parent = next;

    }
  
    
}

private void replaceNode( Node<K,V> u, Node<K,V> v){
  if( u.parent == null){
    root = v;
  } else if ( u == u.parent.left){
    u.parent.left = v; 

  } else{
    u.parent.right = v;

  } 
  if( v != null){
    v.parent = u.parent;
  }
}
    


  private Node<K,V> findNode ( K key){
    Node<K,V> currNode = root; 
    while ( currNode != null){
      int comp = key.compareTo(currNode.key);
      if( comp < 0){
        currNode = currNode.left;
      }
      else if ( comp > 0){
        currNode = currNode.right;
      }
      else {
        return currNode;
      }
    }
    return null;
  }

  @Override
  public int size() {
    return numElems;
  }
  
  @Override
  public Set<K> keySet() {
    // Implementation note. This can be done with an anonymous inner class.
    // You can refer to the BSTMap it is in with `BSTMap.this`. So you
    // can call things like `BSTMap.this.contains`.
      return new KeySet();
  }

  private class KeySet implements Set<K>{

    @Override
    public Iterator<K> iterator() {
      return new Keyiterator();
    }
    private class Keyiterator implements Iterator<K> {
      private Node<K, V> currNode = minNode(root);
      private Node<K, V> trailer = null;
      @Override
      public boolean hasNext() {
        return currNode != null;
      }

      @Override
      public K next() {
        if (!hasNext()) {
          throw new NoSuchElementException("No more elements");
      }

      trailer = currNode;
      currNode = nextNode(currNode);
      return trailer.key;
      }
       public Node<K,V> minNode(Node<K,V> node){
      while( node != null && node.left != null){
        node = node.left;
      }
      return node;
    }
    private Node<K,V> nextNode(Node<K,V> node){
      if(node.right != null){
        return minNode(node.right);
      }else {
        while(node.parent != null && node == node.parent.right){
          node = node.parent;
        }
        return node.parent;
      }
    }
  }

    @Override
    public boolean contains(K elem) {
      return AVLMap.this.contains(elem);
    }

    @Override
    public int size() {
      return AVLMap.this.size();
    }

    
  }
  
  @Override
  public <V2> AVLMap<K, V2> mapValues(Function<V, V2> f) {
    AVLMap<K,V2> result = new AVLMap<>();
    for( KeyValuePair<K,V> kvp : this){
      K key = kvp.key();
      V value = kvp.value();
      V2 diffval = f.apply(value);

      result.put(key, diffval);
    }
    return result;
  }

  @Override
  public Map<K, V> filter(Function<Map.KeyValuePair<K, V>, Boolean> predicate) {
    AVLMap<K,V> result = new AVLMap<>();
    for( KeyValuePair<K,V> kvp : this){
     if(predicate.apply(kvp)){
       result.put(kvp.key(),kvp.value());
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
    E2 result = zero;
    for( KeyValuePair<K,V> kvp : this) {
     result = f.apply(result, kvp);
    }
    return result;
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
    return forallHelp(root,predicate);
  
  }
  private boolean forallHelp(Node<K,V> node, Function<Map.KeyValuePair<K,V> , Boolean> predicate){
    if ( node == null){
      return true;
    }
  
    KeyValuePair<K,V> keyval = new KeyValuePair<>(node.key, node.value);
    if(!predicate.apply(keyval)){
      return false;
    }
    boolean leftnod = forallHelp(node.left, predicate);
    boolean righnod = forallHelp((node.right), predicate);
    return leftnod && righnod;
  }

  // ----------------------------These are potential helper functions for debugging ---------------------
  // You can modify these if you want. I don't call them in any way. They don't have to be used if you 
  // don't need them. I used them in debugging my own implementation.

  // public void inorderPrint() {
  //   inorderPrintRecur(root);
  // }

  // private void inorderPrintRecur(Node<K, V> n) {
  //   if (n != null) {
  //     inorderPrintRecur(n.left);
  //     System.out.println(n.key+" -> "+n.value+"  ");
  //     inorderPrintRecur(n.right);
  //   }
  // }

  // public void preorderPrintKeys() {
  //   preorderPrintKeysRecur(root);
  //   System.out.println();
  // }

  // public void preorderPrintKeysRecur(Node<K, V> n) {
  //   if (n != null) {
  //     System.out.print("(" + n.key);
  //     preorderPrintKeysRecur(n.left);
  //     preorderPrintKeysRecur(n.right);
  //     System.out.print(")");
  //   }
  // }

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
   }

}
