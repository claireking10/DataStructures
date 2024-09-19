package csci2320;
import java.util.EmptyStackException;

public class ArrayStack<E> implements Stack<E> {
  private E[] arr;
  private int len;
  
  public ArrayStack() {
    len = 0;
    arr = (E[]) new Object[10];
  }

  @Override
  public void push(E elem) {
    if(arr.length == len) {
      E[] tmp = (E[])new Object[len*2];
      for(int i=0;i<len;i++){
        tmp[i] = arr[i];
      }
      arr = tmp;
    }
    arr[len] = elem;
    len++;
  }

  @Override
  public E pop() {
    if(this.isEmpty()) { throw new EmptyStackException();}
    len--;
    return arr[len];
  }

  @Override
  public E peek() {
    if(this.isEmpty()) { throw new EmptyStackException();}
    return arr[len-1];
  }

  @Override
  public boolean isEmpty() {
    return len ==0;
  }
  
}
