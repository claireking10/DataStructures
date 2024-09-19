package csci2320;

import java.util.function.BiPredicate;

public class BinaryHeap<E> implements PriorityQueue<E> {
  // Add your data here.
  private BiPredicate<E,E> higherPriority;
  private Object[] heapArray;
  private int size;
  private int parent(int index){
    return (index -1)/2;
  }

  /**
   * Constructs a binary heap with a predicate that tells if the first argument is
   * higher priority than the second argument.
   * @param higherPriority the priority predicate
   */
  public BinaryHeap(BiPredicate<E, E> higherPriority) {
    // Add your constructor code here.
    this.higherPriority = higherPriority;
    this.heapArray = new Object[10]; // initial size, might need to be adjusted idk i just chose my fave number
    this.size = 0;
    }
  

  @Override
  public void enqueue(E elem) {
    if (size == heapArray.length){
      resizeHeapArray();
    }
    heapArray[size++] = elem;
    bubbleUp(size - 1);
  }

  private void bubbleUp(int index){
    E newItem = (E) heapArray[index];
    while (index > 0 && higherPriority.test(newItem, (E) heapArray[parent(index)])){
      swap(index, parent(index));
      index = parent(index);
    }
  }

  private void resizeHeapArray(){
    Object[] newArray = new Object[heapArray.length*2];
    for (int i = 0; i < heapArray.length; i++){
      newArray[i] = heapArray[i];
    }
    heapArray = newArray;
  }

  private void swap(int index1, int index2){
    Object temp = heapArray[index1];
    heapArray[index1] = heapArray[index2];
    heapArray[index2] = temp;
  }

  @Override
  public E dequeue() {
    if (isEmpty()) {
      throw new IllegalStateException("Priority queue is empty");
    }
    E removedItem = peek();
    heapArray[0] = heapArray[--size];
    heapArray[size] = null;
    bubbleDown(0);
    return removedItem;
  }

private void bubbleDown(int index){
  while (leftChild(index)<size){
    int child = minChild(index);
    if (higherPriority.test((E) heapArray[child], (E) heapArray[index])){
      swap(child, index);
      index = child;
    }
    else{
      break;
    }
  }
}

private int leftChild(int index){
  return index * 2 + 1;
}

private int minChild(int index){
  int leftChildIndex = leftChild(index);
  int rightChildIndex = leftChildIndex + 1;
  if (rightChildIndex< size && higherPriority.test((E) heapArray[rightChildIndex], (E) heapArray[leftChildIndex])){
    return rightChildIndex;
  }
  else{
    return leftChildIndex;
  }
}

  @Override
  public E peek() {
    if (isEmpty()){
      throw new IllegalStateException("Priority Queue is empty");
    }
    return (E) heapArray[0];
  }

  @Override
  public boolean isEmpty() {
    return size == 0;
  }

  @Override
  public int size() {
    return size;
  }
  
}
