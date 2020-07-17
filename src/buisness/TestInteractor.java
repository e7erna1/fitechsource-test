package src.buisness;

import src.TestException;
import src.callback.TestCallback;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class TestInteractor implements ITestInteractor, Runnable {

  TestCallback testCallback;
  final Set<Double> resultSet = Collections.synchronizedSet(new HashSet<>());
  int currentIteration = 0, iterationsNumber;

  @Override
  public void execute(int iterationsNumber, int threadNumber, TestCallback testCallback) {
    this.testCallback = testCallback;
    this.iterationsNumber = iterationsNumber;

    for (int i = 0; i < iterationsNumber; i++) {
      Thread thread = new Thread(this);
      thread.start();
    }
  }

  @Override
  public void run() {
    try {
      synchronized (resultSet) {
        currentIteration++;
        resultSet.addAll(TestCalc.calculate(currentIteration));
      }
      if (currentIteration == iterationsNumber) {
        testCallback.showResult(resultSet);
      }
    } catch (TestException e) {
      e.printStackTrace();
    }
  }
}
