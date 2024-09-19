package csci2320;

import java.util.Arrays;  // You will probably find Arrays.copyOf useful for this assignment.
import java.util.Iterator;

public abstract class BTVector<E> implements Iterable<E> {
  /**
   * Create a new vector with this element added to the end.
   * @param elem the new element
   * @return a new vector that is one bigger
   */
  abstract public BTVector<E> add(E elem);

  /**
   * Gets the value at a particular index.
   */
  abstract public E get(int index);

  /**
   * Returns a new vector where the value at the given location has been changed. Note that
   * the original collection is not altered in any way.
   * @param index location to change
   * @param elem value to set it to
   * @return a new vector with one value modified
   */
  abstract public BTVector<E> set(int index, E elem);

  /**
   * Returns the size of the vector.
   */
  abstract public int size();

  /**
   * Returns an iterator that runs through the elements in the vector. In the leaf, this can
   * be your normal iterator through an array with an index. In the internal nodes, the fast
   * approach keeps an index for a child and the iterator for that child. In the next() it
   * calls next() on the child iterator. Then, if the child iterator doesn't have a next after
   * that call, it moves the index forward and creates a new child iterator. It is done when
   * the index reaches the end.
   */
  abstract public Iterator<E> iterator();

  /**
   * This protected method is helpful in add because you need to be able to tell if the next add
   * would overflow your last child. Having this method allows you to know in advance if you need
   * create a new child or, if your last available child is full, if you need to create a new
   * parent.
   * @return a boolean telling if a node can hold more elements
   */
  abstract protected boolean isFull();

  /**
   * I found this protected method to be helpful. It has a node create a new sibling that
   * works with the same nibble `this` which contains the one provided element. This is
   * needed because when a parent needs a new child, it can ask the child to make a sibling
   * so it is of the right type (leaf/internal).
   * @param elem the one element in the new sibling
   * @return a new vector node with one element in it.
   */
  abstract protected BTVector<E> makeSibling(E elem);

  /**
   *  Our implementation groups bits into groups of 4. This constant is here so you don't have
   * "magic numbers" scattered through your code.
   */
  private static int BITS_IN_ALPHABET = 4;

  /**
   * Creates an empty leaf to begin construction of a vector.
   */
  public static <E> BTVector<E> empty() {
    return new Leaf<E>();
  }

  /**
   * This class represents an internal node in the binary trie.
   */
  private static class Internal<E> extends BTVector<E> {
    // Add your data here.

    // Include appropriate constructors.
    // My implementation had three constructors. One took a single child. One took an array of
    // children. The last took an array and a new child to add.
    // You can make a BTVector<E>[] by instantiating an array of the raw type and casting it
    // this look ssomething like (BTVector<E>) new BTVector[size].
    BTVector<E>[] children;
    int level;

    @SuppressWarnings("unchecked")
    public Internal(int level, BTVector<E> child){
      this.level = level;
      children = (BTVector<E>[]) new BTVector[]{child};
    }

    public Internal(int level, BTVector<E>[] child){
      this.level = level;
      children = Arrays.copyOf(child,child.length);
    }

    public Internal(int level, BTVector<E>[] child, BTVector<E> newChild){
      this.level = level;
      children = Arrays.copyOf(child, child.length + 1);
      children[child.length] = newChild;
    }

    @SuppressWarnings("unchecked")
    public Internal(int level, BTVector<E> childOne, BTVector<E> childTwo){
      children = (BTVector<E>[]) new BTVector[]{childOne, childTwo};
      this.level = level;
    }

    @Override
    public BTVector<E> add(E elem) {
      if (isFull()){
        return new Internal<E>(level+1, this, makeSibling(elem));
      }
      else if (!children[children.length - 1].isFull()){
        BTVector<E>[] newChildren = Arrays.copyOf(children, children.length);
        newChildren[newChildren.length - 1] = children[children.length - 1].add(elem);
        return new Internal<E>(level, newChildren);
      }
      else{
      return new Internal<E>(level, children, children[0].makeSibling(elem));
      }
    }

    @Override
    public E get(int index) {
      return children[(index >> (level * BITS_IN_ALPHABET))& 0xf].get(index);
    }

    @Override
    public BTVector<E> set(int index, E elem) {
      int childIndex = (index >> (level * BITS_IN_ALPHABET)) & 0xf;
      Internal<E> newChild = new Internal<E>(level, children);
      newChild.children[childIndex] = newChild.children[childIndex].set(index, elem);
      return newChild;
    }

    @Override
    public int size() {
      return ((children.length - 1)<<(level*BITS_IN_ALPHABET))+children[children.length-1].size();
    }

    @Override
    public Iterator<E> iterator() {
      return new Iterator<E>(){
        int childIndex;
        Iterator<E> childIterator = children[childIndex].iterator();

        @Override
        public boolean hasNext(){
          return childIndex < children.length;
        }

        public E next(){
          E temp = childIterator.next();
          if(!childIterator.hasNext()){
            childIndex++;
            if (hasNext()){
              childIterator = children[childIndex].iterator();
            }
          }
          return temp;
        }
      };
    }

    @Override
    protected boolean isFull() {
      return children.length == 16 && children[15].isFull();
    }

    @Override
    protected BTVector<E> makeSibling(E elem) {
      return new Internal<E>(level, children[0].makeSibling(elem));
    }
  }

  /**
   * This class represents a leaf in the binary trie.
   */
  private static class Leaf<E> extends BTVector<E> {
    // Add your array of data here. If you make the array the proper size, you don't
    // have to store a number of elements in the leaves.
  
    // Add you constructors here.
    // My implementation had four. One took no arguments and made an array of length zero.
    // One took a single element. One took an array of elements. The last took an array and 
    // the new element to add.

    // You can create an E[] the way we have in the past with (E[]) new Object[0].
    private E[] elements; 

    @SuppressWarnings("unchecked")
    public Leaf(){
      elements = (E[]) new Object[0];
    }

    @SuppressWarnings("unchecked")
    public Leaf(E elem){
      elements = (E[]) new Object[]{elem};
    }

    public Leaf(E[] elementsArray){
      elements = Arrays.copyOf(elementsArray, elementsArray.length);
    }

    public Leaf(E[] elementsArray, E elem){
      elements = Arrays.copyOf(elementsArray, elementsArray.length+1);
      elements[elementsArray.length] = elem;
    }

    @Override
    @SuppressWarnings("unchecked")
    public BTVector<E> add(E elem) {
        if(isFull()){
          return new Internal<E>(1,new BTVector[]{this, new Leaf<E>(elem)});
        }
        else{
          return new Leaf<E>(elements, elem);
        }
      }    

    @Override
    public E get(int index) {
      return elements[index & 0xf];
    }

    @Override
    public BTVector<E> set(int index, E elem) {
      E[] newElements = Arrays.copyOf(elements, elements.length);
      newElements[index & 0xf] = elem;
      return new Leaf<E>(newElements);
    }

    @Override
    public int size() {
      return elements.length;
    }

    @Override
    public Iterator<E> iterator() {
      return Arrays.asList(elements).iterator();
    }

    @Override
    protected boolean isFull() {
      return elements.length >= (1<<BITS_IN_ALPHABET);
    }

    @Override
    protected BTVector<E> makeSibling(E elem) {
      return new Leaf<E>(elem);
    }
  }
}
