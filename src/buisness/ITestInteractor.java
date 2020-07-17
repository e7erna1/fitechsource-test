package src.buisness;

import src.callback.TestCallback;

public interface ITestInteractor {

  void execute(int iterationsNumber, int threadNumber, TestCallback testCallback);
}
