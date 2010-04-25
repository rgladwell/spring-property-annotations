package com.byluroid.spring.beans.factory.config.annotations;

public class NoSetterTestBean {

	@Property(key=PropertyAnnotationAndPlaceholderConfigurerTest.TEST_KEY)
	String property;

	public String getProperty() {
		return property;
	}

}
