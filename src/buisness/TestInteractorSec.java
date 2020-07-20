package src.buisness;

import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import src.callback.TestCallback;

public class TestInteractorSec implements Executor {

  private final Queue<Runnable> workQueue = new ConcurrentLinkedQueue<>();
  public volatile boolean isRunning = true;

  public TestInteractorSec(int nThreads) {
    for (int i = 0; i < nThreads; i++) {
      new Thread(new TaskWorker()).start();
    }
  }

  @Override
  public void execute(Runnable command) {
    if (isRunning) {
      workQueue.offer(command);
    }
  }

  public void shutdown() {
    isRunning = false;
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