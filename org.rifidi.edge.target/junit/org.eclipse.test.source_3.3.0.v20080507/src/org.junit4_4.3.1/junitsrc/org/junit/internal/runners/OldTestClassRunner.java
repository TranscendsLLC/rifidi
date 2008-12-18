package org.junit.internal.runners;

import junit.extensions.TestDecorator;
import junit.framework.AssertionFailedError;
import junit.framework.JUnit4TestAdapter;
import junit.framework.JUnit4TestCaseFacade;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestListener;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

public class OldTestClassRunner extends Runner {
	private static final class OldTestClassAdaptingListener implements
			TestListener {
		private final RunNotifier fNotifier;

		private OldTestClassAdaptingListener(RunNotifier notifier) {
			fNotifier= notifier;
		}

		public void endTest(Test test) {
			fNotifier.fireTestFinished(asDescription(test));
		}

		public void startTest(Test test) {
			fNotifier.fireTestStarted(asDescription(test));
		}

		// Implement junit.framework.TestListener
		public void addError(Test test, Throwable t) {
			Failure failure= new Failure(asDescription(test), t);
			fNotifier.fireTestFailure(failure);
		}

		private Description asDescription(Test test) {
			if (test instanceof JUnit4TestCaseFacade) {
				JUnit4TestCaseFacade facade= (JUnit4TestCaseFacade) test;
				return facade.getDescription();
			}
			return Description.createTestDescription(test.getClass(), getName(test));
		}

		private String getName(Test test) {
			if (test instanceof TestCase)
				return ((TestCase) test).getName();
			else
				return test.toString();
		}

		public void addFailure(Test test, AssertionFailedError t) {
			addError(test, t);
		}
	}

	private Test fTest;
	
	@SuppressWarnings("unchecked")
	public OldTestClassRunner(Class<?> klass) {
		this(new TestSuite(klass.asSubclass(TestCase.class)));
	}

	public OldTestClassRunner(Test test) {
		super();
		fTest= test;
	}

	@Override
	public void run(RunNotifier notifier) {
		TestResult result= new TestResult();
		result.addListener(createAdaptingListener(notifier));
		fTest.run(result);
	}

	public static TestListener createAdaptingListener(final RunNotifier notifier) {
		return new OldTestClassAdaptingListener(notifier);
	}
	
	@Override
	public Description getDescription() {
		return makeDescription(fTest);
	}

	private Description makeDescription(Test test) {
		if (test instanceof TestCase) {
			TestCase tc= (TestCase) test;
			return Description.createTestDescription(tc.getClass(), tc.getName());
		} else if (test instanceof TestSuite) {
			TestSuite ts= (TestSuite) test;
			String name= ts.getName() == null ? "" : ts.getName();
			Description description= Description.createSuiteDescription(name);
			int n= ts.testCount();
			for (int i= 0; i < n; i++)
				description.addChild(makeDescription(ts.testAt(i)));
			return description;
		} else if (test instanceof JUnit4TestAdapter) {
			JUnit4TestAdapter adapter= (JUnit4TestAdapter) test;
			return adapter.getDescription();
		} else if (test instanceof TestDecorator) {
			TestDecorator decorator= (TestDecorator) test;
			return makeDescription(decorator.getTest());
		} else {
			// This is the best we can do in this case
			return Description.createSuiteDescription(test.getClass());
		}
	}
}
