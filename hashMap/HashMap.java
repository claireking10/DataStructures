package csci2320;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;


public class HashMap<K, V> implements Map<K, V> {
  @SuppressWarnings("unchecked")
  private Node<K,V> [] table = new Node[10];
  private static final double threshold = 0.75;
  private int numElems = 0;
  private static class Node<K,V>{
    K key;
    V value;
    Node<K,V> next;

    private Node(K key, V value, Node<K,V> next){
      this.key = key;
      this.value = value; 
      this.next = next;
    }
  }
   // I'm giving you some helper method to make testing easier.
  @SuppressWarnings("unchecked")
  public static <K, V> HashMap<K, V> of(Map.KeyValuePair<K, V>... elems) {
    HashMap<K, V> ret = new HashMap<>();
    for (var kvp: elems) ret.put(kvp.key(), kvp.value());
    return ret;
  }

  @Override
  public boolean equals(Object that) {
    if (that == null || !(that instanceof HashMap)) return false;
    HashMap<?, ?> thatSeq = (HashMap<?, ?>)that;
    if (thatSeq.size() != size()) return false;
    for (Iterator<?> iter1 = thatSeq.iterator(), iter2 = this.iterator(); iter1.hasNext();)
      if (!iter1.next().equals(iter2.next())) return false;
    return true;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("HashMap(");
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

  @Override
  public Iterator<KeyValuePair<K, V>> iterator() {
    return new KeyValueIterator();
  }

  private class KeyValueIterator implements Iterator<KeyValuePair<K,V>>{
    private int k = -1;
    private Node<K,V> rover = null;
    public KeyValueIterator(){
      advance();
    }

    @Override
    public boolean hasNext(){
      return k < table.length;
    }

    @Override
    public KeyValuePair<K,V> next(){
      if(!hasNext()){
        throw new NoSuchElementException("No Elem");
      }
      
      KeyValuePair<K,V> valuepair = new KeyValuePair<>(rover.key, rover.value);
      advance();
      return valuepair;
    }

    private boolean advance(){
      if (rover != null && rover.next != null){
        rover = rover.next;
      }
      else{
        k++;
      while (k<table.length){
        if(table[k] !=null){
          rover = table [k];
          return true;
        }
        k++;
      }
      }
      return false;
    }
  }


  @Override
  public Optional<V> put(K key, V value) {
    if (key == null){
      throw new NoSuchElementException("Not valid");
    }
    int index = Math.abs(key.hashCode()) % table.length;
    Node<K,V> rover = table[index];
    Node<K,V> trailer = null;
    while (rover!=null){
      if(key.equals(rover.key)){
        V prevNode = rover.value;
        rover.value = value;
        return Optional.of(prevNode);
      }
      trailer = rover;
      rover = rover.next;
    }
    Node<K,V> n = new Node<K,V>(key, value, null);
    if (trailer == null){
      table[index] = n;
    }
    else{
      trailer.next = n;
    }
    numElems ++;
    if ((1.0* numElems) / table.length >= threshold){
      resize();
    }
    return Optional.empty();
  }

  private void resize() {
    int resize = (table.length*2)+1;
    @SuppressWarnings("unchecked")
    Node<K,V> [] nTable = new Node[resize];
    for (int i = 0; i<table.length; i++){
      Node<K,V> node = table[i];
      while(node != null){
        int newIndex = Math.abs(node.key.hashCode()%resize);
        Node<K,V> mover = node.next;
        if (nTable[newIndex] == null){
          nTable[newIndex] = node;
        }
        else{
          Node<K,V> current = nTable[newIndex];
          while(current.next != null){
            current = current.next;
          }
          current.next = node;
        }
        node.next = null;
        node = mover;
      }
    }
    table = nTable;
    //Implement resizing logic, create a new table and rehash elements
  }

    // Implementation suggestion.
    // Write this and test it first without growth.
    // When you add the array growth, have the actual code in a separate function
    // and do the growth after you add the new value

  @Override
  public Optional<V> get(K key) {
    int index = Math.abs(key.hashCode() % table.length);
    Node<K,V> rover = table[index];
    while(rover!=null){
      if (rover.key.equals(key)){
        return Optional.of(rover.value);
      }
      rover = rover.next;
    }
    return Optional.empty();
  }

  @Override
  public V getOrElse(K key, V defaultValue) {
    if (key == null){
      throw new NoSuchElementException("No element");
    }
    int index = Math.abs(key.hashCode()% table.length);
    Node<K,V> rover = table[index];
    while(rover!=null){
      if (key.equals(rover.key)){
        return rover.value;
      }
      rover = rover.next;
    }
    return defaultValue;
  }

  @Override
  public boolean contains(K key) {
    if (key == null){
      throw new NoSuchElementException("No Element");
    }
    int index = Math.abs(key.hashCode()%table.length);
    Node<K,V> rover = table[index];
    while (rover != null){
      if (key.equals(rover.key)){
        return true;
      }
      rover = rover.next;
    }
    return false;
  }

  @Override
  public Optional<V> remove(K key) {
    if (key == null){
      throw new NoSuchElementException("No ELement");
    }
    int index = Math.abs(key.hashCode()%table.length);
    Node<K,V> rover = table[index];
    Node<K,V> trailer = null;
    
    while(rover!=null){
      if(key.equals(rover.key)){
        if(trailer == null){
          table[index]=rover.next;
        }
        else{
          trailer.next = rover.next;
        }
        numElems--;
        return Optional.of(rover.value);
      }
      trailer = rover;
      rover = rover.next;
    }
    return Optional.empty();
  }

  @Override
  public int size() {
    return numElems;
  }

  @Override
  public Set<K> keySet() {
    return new KeySet();

    // Implementation note. This can be done with an anonymous inner class.
    // You can refer to the HashMap it is in with `HashMap.this`. So you
    // can call things like `HashMap.this.contains`.
  }
  private class KeySet implements Set<K>{
    @Override
    public Iterator<K> iterator(){
      return new KeyIterator();
    }
    private class KeyIterator implements Iterator<K>{
      private int k = -1;
      private Node<K,V> rover = null;
      public KeyIterator(){
        advance();
      }
      @Override 
      public boolean hasNext(){
        return k < table.length;
      }
      @Override 
      public K next(){
        if (!hasNext()){
          throw new NoSuchElementException("No Element");
        }
        K key = rover.key;
        advance();
        return key;
      }
      private boolean advance(){
        if (rover != null && rover.next != null){
          rover = rover.next;
        }
        else{
          k++;
          while (k < table.length){
            if (table[k] != null){
              rover = table[k];
              return true;
            }
            k++;
          }
        }
        return false;
      }
    }

  @Override 
  public boolean contains(K elem){
    return HashMap.this.contains(elem);
  }
  @Override
  public int size(){
    return HashMap.this.size();
  }
  }
  
  @Override
  public <V2> HashMap<K, V2> mapValues(Function<V, V2> f) {
    HashMap<K,V2> result = new HashMap<>();
    for (int i = 0; i< table.length; i++){
      Node<K,V> node = table[i];
      while(node != null){
        V2 Nvalue = f.apply(node.value);
        result.put(node.key, Nvalue);
        node = node.next;
      }
    }
    return result;
  }

  @Override
  public Map<K, V> filter(Function<Map.KeyValuePair<K, V>, Boolean> predicate) {
    HashMap<K,V> result = new HashMap<>();
    for (int i = 0; i< table.length; i++){
      Node<K,V> node = table[i];
      while(node != null){
        KeyValuePair<K,V> keyval = new KeyValuePair<>(node.key, node.value);
        if(predicate.apply(keyval)){
          result.put(node.key, node.value);
        }
        node = node.next;
      }
    }
    return result;
  }

  @Override
  public Optional<Map.KeyValuePair<K, V>> find(Function<Map.KeyValuePair<K, V>, Boolean> predicate) {
    for (int i = 0; i< table.length; i++){
      Node <K,V> rover = table[i];
      while(rover != null){
        KeyValuePair <K,V> keyval = new KeyValuePair<>(rover.key, rover.value);
        if (predicate.apply(keyval)){
          return Optional.of(keyval);
        }
      rover = rover.next;
      }
      }
      return Optional.empty();
    }
  

  @Override
  public <E2> E2 fold(E2 zero, BiFunction<E2, Map.KeyValuePair<K, V>, E2> f) {
    E2 k = zero;
    for(int i = 0; i < table.length; i++){
      Node<K,V> rover = table[i];
      while (rover != null){
        KeyValuePair<K,V> keyval = new KeyValuePair<>(rover.key, rover.value);
        k = f.apply(k, keyval);
        rover = rover.next;
      }
    }
    return k;
  }

  @Override
  public boolean exists(Function<Map.KeyValuePair<K, V>, Boolean> predicate) {
    for (int i = 0; i < table.length; i++){
      Node<K,V> rover = table[i];
      while(rover != null){
        KeyValuePair<K,V> keyval = new KeyValuePair<> (rover.key, rover.value);
        if(predicate.apply(keyval)){
          return true;
        }
        rover = rover.next;
    }
  }
    return false;
}

  @Override
  public boolean forall(Function<Map.KeyValuePair<K, V>, Boolean> predicate) {
    for (int i = 0; i < table.length; i++){
      Node<K,V> rover = table[i];
      while (rover != null){
        KeyValuePair<K,V> keyval = new KeyValuePair<>(rover.key, rover.value);
       if(!predicate.apply(keyval)){
         return false;
        }
        rover = rover.next;
    }
  }
    return true;
  }

  /**
   * Lets this object work as a function from K to V. Gets the value if the key exists.
   * Throws an exception if it doesn't.
   * @param key the key to look up in the collection
   * @return the associated value
   */
  @Override
  public V apply(K key) {
    if (key == null){
      throw new NoSuchElementException("Key is null");
    }
    int index = Math.abs (key.hashCode()% table.length);
    Node<K,V> rover = table[index];
    while (rover != null){
      if (key.equals(rover.key)){
        return rover.value;
      }
      rover = rover.next;
    }
    throw new NoSuchElementException("Key not found");
  }
}