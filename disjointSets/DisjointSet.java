package csci2320;

public class DisjointSet<E> {
  /**
   * Makes a new disjoint set that represents the provided element type.
   * @param <E> The element type.
   * @param elem The representative element for the set.
   * @return A new disjoint set whose only element in the one provided.
   */

  public static <E> DisjointSet<E> makeSet(E elem) {
    DisjointSet<E> set = new DisjointSet<>();
    set.element = elem;
    set.parent = set;
    set.rank = 0;
    return set;
  }

  // Add any member data/constructors here.
  private E element;
  private DisjointSet<E> parent;
  private int rank;

  /**
   * Returns the representative element for the current set. This should be
   * the <code>elem</code> that the set was created with.
   * @return
   */
  public E getElement() {
    return element;
  }

  /**
   * Union <code>this</code> to <code>that</code>.
   * @param that the other set to union with the current one.
   */
  public void union(DisjointSet<E> that) {
    DisjointSet<E> thisRoot = this.findSet();
    DisjointSet<E> thatRoot = that.findSet();

    if (thisRoot == thatRoot){
      return;
    }

    if (thisRoot.rank < thatRoot.rank){
      thisRoot.parent = thatRoot;
    } else if (thisRoot.rank > thatRoot.rank){
      thatRoot.parent = thisRoot;
    } else {
      thatRoot.parent = thisRoot;
      thisRoot.rank++;
    }
  }

  /**
   * Finds the representative set for the current set. Note that this is not
   * the element data. That is done with <code>getElement</code>.
   * @return the root of the tree this set is part of.
   */
  public DisjointSet<E> findSet() {
    if (this != parent){
      parent = parent.findSet();
    }
    return parent;
  }
}
