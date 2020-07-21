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
  private final Queue<Runnable> taskQueue = new ConcurrentLinkedQueue<>();
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
    this.addTask(() -> {
      try {
        resultSet.addAll(TestCalc.calculate(iterationsNumber));
      } catch (TestException exception) {
        exceptionHandler(exception);
      }
    });
  }

  private void addTask(Runnable command) {
    taskQueue.offer(command);
  }

  private void shutdown() {
    threadNumber--;
    isRunning = false;
    if (threadNumber == 0) {
      testCallback.showResult(resultSet);
    }
  }

  private void exceptionHandler(TestException exception) {
    isRunning = false;
    taskQueue.clear();
    if (!isWritten) {
      isWritten = true;
      threadList.forEach(Thread::interrupt);
      resultSet.clear();
      System.out.println(exception.getMessage());
    }
  }

  private final class TaskWorker implements Runnable {

    @Override
    public void run() {
      while (isRunning) {
        Runnable nextTask = taskQueue.poll();
        if (nextTask != null) {
          nextTask.run();
          if (taskQueue.size() == 0) {
            shutdown();
          }
        }
      }
    }
  }
}