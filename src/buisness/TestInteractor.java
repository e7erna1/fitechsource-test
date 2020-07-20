package src.buisness;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import src.TestException;
import src.callback.TestCallback;

public class TestInteractor implements ITestInteractor {

  private final TestCallback testCallback;
  private final Queue<Runnable> workQueue = new ConcurrentLinkedQueue<>();
  private final Set<Double> resultSet = Collections.synchronizedSet(new HashSet<>());
  private final List<Thread> threadList = new ArrayList<>();
  private int threadNumber;
  private volatile boolean isRunning = true, isWritten = false;

  public TestInteractor(int threadNumber, TestCallback testCallback) {
    this.testCallback = testCallback;
    this.threadNumber = threadNumber;
    for (int i = 0; i < threadNumber; i++) {
      Thread thread = new Thread(new TaskWorker());
      threadList.add(thread);
      thread.start();
    }
  }

  public void execute(int iterationsNumber) {
    this.executeTask(() -> {
      try {
        Set<Double> doubles = TestCalc.calculate(iterationsNumber);
        resultSet.addAll(doubles);
      } catch (TestException e) {
        isRunning = false;
        if (!isWritten) {
          workQueue.clear();
          isWritten = true;
          threadList.forEach(Thread::interrupt);
          resultSet.clear();
          System.out.println(e.getMessage());
        }
      }
    });
  }

  private void executeTask(Runnable command) {
    if (isRunning) {
      workQueue.offer(command);
    }
  }

  private void shutdown() {
    threadNumber--;
    isRunning = false;
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
          System.out.println(workQueue.size());
          System.out.println(Thread.currentThread().getId());
          if (workQueue.size() == 0) {
            shutdown();
          }
        }
      }
    }
  }
}