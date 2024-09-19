package csci2320;

public class ArrayQueue<E> implements Queue<E> {
  private E[] arr;
  private int len;
  private int beg;
  
  public ArrayQueue(){
    arr = (E[]) new Object[10];
    len = 0;
    beg = 0;
  }

  @Override
  public void enqueue(E elem) {
    if (len == arr.length){
      E[] tmp = (E[]) new Object[len*2];
      for(int i=0; i<len; i++){
        tmp[i] = arr[(beg + i)%arr.length];
      }
      arr = tmp;
      beg = 0;
    }
    arr[(beg + len) % arr.length] = elem;
    len++;
  }

  @Override
  public E dequeue() {
    E retVal = arr[beg];
    beg = (beg + 1) % arr.length;
    len--;
    return retVal;
  }

  @Override
  public E peek() {
    return arr[beg];
  }

  @Override
  public boolean isEmpty() {
    return len ==0;
  }
  
}
