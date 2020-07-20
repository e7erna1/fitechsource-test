package src.buisness;

import java.util.Set;
import src.TestException;
import src.callback.TestCallback;

public class TestInteractor implements ITestInteractor, Runnable {

  TestCallback testCallback;
  final Set<Double> resultSet;
  int currentIteration, iterationsNumber, threadNumber;

  public TestInteractor(int currentIteration, Set<Double> resultSet, TestCallback testCallback) {
    this.currentIteration = currentIteration;
    this.resultSet = resultSet;
    this.testCallback = testCallback;
  }

  @Override
  public void execute(int iterationsNumber, int threadNumber) {
    this.iterationsNumber = iterationsNumber;
    this.threadNumber = threadNumber;

    for (int i = 0; i < iterationsNumber; i++) {
      Thread thread = new Thread(new TestInteractor(i, resultSet,  testCallback));
      thread.start();
    }
  }

  @Override
  public void run() {
    try {
      Set<Double> buf = TestCalc.calculate(currentIteration);
      resultSet.addAll(buf);
      System.out.println(buf);
      System.out.println(currentIteration);
      System.out.println();
      if (currentIteration == iterationsNumber) {
        testCallback.showResult(resultSet);
      }
    } catch (TestException e) {
      e.printStackTrace();
    }
  }
}
