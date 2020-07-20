package src.view;

import java.util.HashSet;
import java.util.Set;
import src.TestConsts;
import src.TestException;
import src.buisness.TestCalc;
import src.buisness.TestInteractor;
import src.callback.TestCallback;

/**
 * Should be improved to reduce calculation time.
 *
 * 1. Change this file or create new one using parallel calculation mode. 2. Do not use `executors`,
 * only plain threads  (max threads count which should be created for calculations is
 * com.fitechsource.test.java.TestConsts#MAX_THREADS) 3. Try to provide simple solution, do not
 * implement frameworks. 4. Don't forget that calculation method can throw exception, process it
 * right way. (Stop calculation process and print error message. Ignore already calculated
 * intermediate results, user doesn't need it.)
 *
 * Please attach code files to email - skhisamov@fitechsource.com
 */

public class Test implements TestCallback {

  public static void main(String[] args) throws TestException {

    Set<Double> res = new HashSet<>();
    for (int i = 0; i < TestConsts.N; i++) {
      res.addAll(TestCalc.calculate(i));
    }
    System.out.println(res);

    Test test = new Test();
    test.method();
  }

  private void method() {
    TestInteractor testInteractorSec = new TestInteractor(TestConsts.MAX_THREADS, this);
    for (int i = 0; i < TestConsts.N; i++) {
      testInteractorSec.execute(i);
    }
  }

  @Override
  public void showResult(Set<Double> set) {
    if (set.size() != 0) {
      System.out.println(set);
    }
  }
}
