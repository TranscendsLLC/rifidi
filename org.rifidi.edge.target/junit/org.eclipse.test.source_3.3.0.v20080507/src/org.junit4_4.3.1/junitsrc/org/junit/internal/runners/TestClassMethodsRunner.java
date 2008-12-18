package org.junit.internal.runners;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.Filterable;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runner.manipulation.Sortable;
import org.junit.runner.manipulation.Sorter;
import org.junit.runner.notification.RunNotifier;

public class TestClassMethodsRunner extends Runner implements Filterable, Sortable {
	private final List<Method> fTestMethods;
	private final Class<?> fTestClass;

	// This assumes that some containing runner will perform validation of the test methods	
	public TestClassMethodsRunner(Class<?> klass) {
		fTestClass= klass;
		fTestMethods= new TestIntrospector(getTestClass()).getTestMethods(Test.class);
	}
	
	@Override
	public void run(RunNotifier notifier) {
		if (fTestMethods.isEmpty())
			notifier.testAborted(getDescription(), new Exception("No runnable methods"));
		for (Method method : fTestMethods)
			invokeTestMethod(method, notifier);
	}

	@Override
	public Description getDescription() {
		Description spec= Description.createSuiteDescription(getName());
		List<Method> testMethods= fTestMethods;
		for (Method method : testMethods)
				spec.addChild(methodDescription(method));
		return spec;
	}

	protected String getName() {
		return getTestClass().getName();
	}
	
	protected Object createTest() throws Exception {
		return getTestClass().getConstructor().newInstance();
	}

	protected void invokeTestMethod(Method method, RunNotifier notifier) {
		Object test;
		try {
			test= createTest();
		} catch (InvocationTargetException e) {
			notifier.testAborted(methodDescription(method), e.getCause());
			return;			
		} catch (Exception e) {
			notifier.testAborted(methodDescription(method), e);
			return;
		}
		createMethodRunner(test, method, notifier).run();
	}

	protected TestMethodRunner createMethodRunner(Object test, Method method, RunNotifier notifier) {
		return new TestMethodRunner(test, method, notifier, methodDescription(method));
	}

	protected String testName(Method method) {
		return method.getName();
	}

	protected Description methodDescription(Method method) {
		return Description.createTestDescription(getTestClass(), testName(method));
	}

	public void filter(Filter filter) throws NoTestsRemainException {
		for (Iterator<Method> iter= fTestMethods.iterator(); iter.hasNext();) {
			Method method= iter.next();
			if (!filter.shouldRun(methodDescription(method)))
				iter.remove();
		}
		if (fTestMethods.isEmpty())
			throw new NoTestsRemainException();
	}

	public void sort(final Sorter sorter) {
		Collections.sort(fTestMethods, new Comparator<Method>() {
			public int compare(Method o1, Method o2) {
				return sorter.compare(methodDescription(o1), methodDescription(o2));
			}
		});
	}

	protected Class<?> getTestClass() {
		return fTestClass;
	}
}