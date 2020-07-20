package src.buisness;

import java.util.Collections;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import src.callback.TestCallback;
import src.view.Test;

public class TestInteractorSec {

  private final TestCallback testCallback;
  private final Queue<Runnable> workQueue = new ConcurrentLinkedQueue<>();
  private final Set<Double> resultSet = Collections.synchronizedSet(new HashSet<>());
  private Integer threadNumber;
  public volatile boolean isRunning = true;

  public TestInteractorSec(int threadNumber, TestCallback testCallback) {
    this.testCallback = testCallback;
    this.threadNumber = threadNumber;
    for (int i = 0; i < threadNumber; i++) {
      new Thread(new TaskWorker()).start();
    }
  }

  public void execute(int i) {
    this.execute(() -> {
      try {
        Set<Double> doubles = TestCalc.calculate(i);
        resultSet.addAll(doubles);
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
  }

  public void execute(Runnable command) {
    if (isRunning) {
      workQueue.offer(command);
    }
  }

  public void shutdown() {
    synchronized (threadNumber) {
      threadNumber--;
      isRunning = false;
    }
    if (threadNumber == 0) {
      testCallback.showResult(resultSet);
    }
  }

  private final class TaskWorker implements Runnable {

    @Override
    public void run() {
      while (isRunning) {
        Runnable nextTask = workQueue.poll();
        if (nextTask != null) {
          nextTask.run();
          if (workQueue.size() == 0) {
            shutdown();
          }
        }
      }
    }
  }
}