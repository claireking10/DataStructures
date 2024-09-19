package csci2320;

import java.util.Random;

public class SpeedTest {
  static final int BIG_CNT = 100000;
  static final int STEP_CNT = 1000;

  public static void run() {
    var start = System.nanoTime();
    Random rand = new Random(48283);

    var seq = new DLinkedSeq<Double>();
    for (int i = 0; i < BIG_CNT; ++i) {
      seq.add(rand.nextDouble());
    }
    for (int i = 0; i < BIG_CNT/2; ++i) {
      seq.insert(i, rand.nextDouble());
    }
    System.out.println("Adding split: " + (System.nanoTime() - start)*1e-9);
    double sum = 0.0;
    for (int i = 0; i < seq.size(); i += STEP_CNT) {
      sum += seq.get(i);
      seq.set(i, rand.nextDouble());
    }
    for (int i = 0; i < STEP_CNT; ++i) {
      int index = rand.nextInt(seq.size());
      sum -= seq.get(index);
      seq.remove(index);
    }
    System.out.println("Set/remove split: " + (System.nanoTime() - start)*1e-9);
    // map
    var seq2 = seq.map(x -> x*3.14);
    // filter
    var seq3 = seq2.filter(x -> x < 2.0);
    // takeWhile
    var seq4 = seq2.takeWhile(x -> x < 3.0);
    // dropWhile
    var seq5 = seq2.dropWhile(x -> x < 3.0);
    // find
    var big = seq2.find(x -> x > 3);
    // foldLeft
    var sumLeft = seq2.foldLeft(0.0, (x,y) -> x+y);
    // foldRight
    var prodRight = seq2.foldRight((x,y) -> x*y, 0.0);
    // mapped
    seq2.mapped(x -> x/2);
    // filtered
    seq2.filtered(x -> x < 1.0);
    // keepWhile
    seq2.keepWhile(x -> x > 0.02);
    // removeWhile
    seq2.removeWhile(x -> x < 0.98);
    System.out.println(sum+" "+seq2.size()+" "+seq3.size()+" "+seq4.size()+" "+seq5.size()+" "+big+" "+sumLeft+" "+prodRight);
    System.out.println("Final time: " + (System.nanoTime() - start)*1e-9);
  }
}
