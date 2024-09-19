package csci2320;

import java.util.Iterator;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public class DLinkedSeq<E> implements Seq<E> {
  // Declare your node class for a double linked list here.
  // Put your private data here
  private static class Node<E>{
    E data;
    Node<E> next;
    Node<E> prev;
    Node(E elem, Node<E> prev, Node<E>next){
      data = elem;
      this.next = next;
      this.prev = prev;
    }
  }
  Node<E> top = null;

  // I'm giving you some helper method to make testing easier.
  @SuppressWarnings("unchecked")
  public static <E> DLinkedSeq<E> of(E... elems) {
    DLinkedSeq<E> ret = new DLinkedSeq<>();
    for (E e: elems) ret.add(e);
    return ret;
  }

  public static DLinkedSeq<Integer> ofInt(int... elems) {
    DLinkedSeq<Integer> ret = new DLinkedSeq<>();
    for (Integer e: elems) ret.add(e);
    return ret;
  }

  @Override
  public boolean equals(Object that) {
    if (that == null || !(that instanceof DLinkedSeq)) return false;
    DLinkedSeq<?> thatSeq = (DLinkedSeq<?>)that;
    if (thatSeq.size() != size()) return false;
    for (Iterator<?> iter1 = thatSeq.iterator(), iter2 = this.iterator(); iter1.hasNext();)
      if (!iter1.next().equals(iter2.next())) return false;
    return true;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("DLinkedSeq(");
    boolean first = true;
    for (E e: this) {
      if (!first) {
        sb.append(", " + e);
      } else {
        sb.append(e.toString());
        first = false;
      }
    }
    sb.append(")");
    return sb.toString();
  }

  @Override
  public Iterator<E> iterator() {
    return new Iterator<E>(){
      private Node<E> current = top;
      @Override
      public boolean hasNext(){
        return current != null;
      }
      @Override
      public E next(){
        if(!hasNext()){
          throw new java.util.NoSuchElementException();
        }
        E data = current.data;
        current = current.next;
        return data;
      }
    };
  }

  private Node<E> getLastNode(){
    Node<E> current = top;
    while (current != null && current.next!= null){
      current = current.next;
    }
    return current;
  }

  private Node<E> getNodeAtIndex(int index){
    if(index<0||index>=size()){
      throw new IndexOutOfBoundsException("Index out of bounds");
    }
    Node<E> current = top;
    for (int i = 0; i < index; i++){
      current = current.next;
    }
    return current;
  }

  @Override
  public E get(int index) {
    Node<E> current = getNodeAtIndex(index);
    return current.data;
  }

  @Override
  public void set(int index, E elem) {
    Node<E> current = getNodeAtIndex(index);
    current.data = elem;
  }

  @Override
  public void add(E elem) {
    Node<E> newNode = new Node<>(elem, null, null);
    Node<E> lastNode = getLastNode();
    if(lastNode != null){
      lastNode.next = newNode;
      newNode.prev = lastNode;
    }
    else{
      top = newNode;
    }
  }

  @Override
  public void insert(int index, E elem) {
    if (index<0||index>size()){
      throw new IndexOutOfBoundsException("Index out of bounds");
    }
    if (index == size()){
      add(elem);
      return;
    }
    Node<E> current = getNodeAtIndex(index);
    Node<E> newNode = new Node<>(elem, current.prev, current);
    if (current.prev != null){
      current.prev.next = newNode;
    }
    else{
      top = newNode;
    }
    current.prev = newNode;
  }

  @Override
  public E remove(int index) {
    if (index<0 || index>=size()){
      throw new IndexOutOfBoundsException("Index out of bounds");
    }
    Node<E> current = getNodeAtIndex(index);
    if(current.prev != null){
      current.prev.next = current.next;
    }
    else{
      top = current.next;
    }
    if(current.next != null){
      current.next.prev = current.prev;
    }
    return current.data;
  }

  @Override
  public int size() {
    int count = 0;
    Node<E> current = top;
    while (current != null){
      count ++;
      current = current.next;
    }  
    return count;
  }

  @Override
  public <E2> DLinkedSeq<E2> map(Function<E, E2> f) {
    DLinkedSeq<E2> result = new DLinkedSeq<>();
    for (E elem:this){
      result.add(f.apply(elem));
    }
    return result;
  }

  @Override
  public DLinkedSeq<E> filter(Function<E, Boolean> predicate) {
    DLinkedSeq<E> result = new DLinkedSeq<>();
    for (E elem: this){
      if(predicate.apply(elem)){
        result.add(elem);
      }
    }
    return result;
  }

  @Override
  public DLinkedSeq<E> takeWhile(Function<E, Boolean> predicate) {
    DLinkedSeq<E> result = new DLinkedSeq<>();
    Node<E> current = top;
    while (current != null && predicate.apply(current.data)){
      result.add(current.data);
      current = current.next;
    }
    return result;
  }

  @Override
  public DLinkedSeq<E> dropWhile(Function<E, Boolean> predicate) {
    DLinkedSeq<E> result = new DLinkedSeq<>();
    Node<E> current = top;
    while (current != null && !predicate.apply(current.data)){
      current = current.next;
    }
    while (current != null){
      result.add(current.data);
      current=current.next;
    }
    return result;
  }

  @Override
  public Optional<E> find(Function<E, Boolean> predicate) {
    Node<E> current = top;
    while (current != null){
      if (predicate.apply(current.data)){
        return Optional.of(current.data);
      }
      current = current.next;
    }
    return Optional.empty();
  }

  @Override
  public <A> A foldLeft(A zero, BiFunction<A, E, A> f) {
    A result = zero;
    Node<E> current = top;
    while (current != null){
      result = f.apply(result, current.data);
      current = current.next;
    }
    return result;
  }

  @Override
  public <A> A foldRight(BiFunction<E, A, A> f, A zero) {
    A result = zero;
    Node<E> current = getLastNode();
    while (current != null){
      result = f.apply(current.data, result);
      current = current.prev;
    }
    return result;
  }

  @Override
  public void mapped(Function<E, E> f) {
    DLinkedSeq<E> result = new DLinkedSeq<>();
    Node<E> current = top;
    while (current != null){
      result.add(f.apply(current.data));
      current = current.next;
    }
    top = result.top;
  }

  @Override
  public void filtered(Function<E, Boolean> predicate) {
    Node<E> current = top;
    Node<E> prev = null;
    while (current != null){
      if(!predicate.apply(current.data)){
        if (prev != null){
          prev.next = current.next;
        }
        else {
          top = current.next;
        }
      }
      else {
        prev = current;
      }
      current = current.next;
    }
  }

  @Override
  public void keepWhile(Function<E, Boolean> predicate) {
    Node<E> current = top;
    Node<E> prev = null;
    while (current != null && predicate.apply(current.data)){
      prev = current;
      current = current.next;
    }
    if (prev!=null){
      prev.next = null;
    }
    else{
      top = null;
    }
  }

  @Override
  public void removeWhile(Function<E, Boolean> predicate) {
    Node<E> current = top;
    while (current != null && predicate.apply(current.data)){
      top = current.next;
      current = top;
    }
  }
  
}
