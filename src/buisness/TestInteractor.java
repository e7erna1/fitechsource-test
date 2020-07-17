package src.buisness;

import java.lang.Thread.State;
import src.TestException;
import src.callback.TestCallback;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class TestInteractor implements ITestInteractor, Runnable {

  TestCallback testCallback;
  final Set<Double> resultSet;
  int currentIteration = 0, iterationsNumber;

  public TestInteractor(int currentIteration, Set<Double> resultSet, TestCallback testCallback) {
    this.currentIteration = currentIteration;
    this.resultSet = resultSet;
    this.testCallback = testCallback;
  }

  @Override
  public void execute(int iterationsNumber, int threadNumber) {
    this.iterationsNumber = iterationsNumber;

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
