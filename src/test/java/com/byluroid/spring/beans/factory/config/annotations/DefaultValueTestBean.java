package com.byluroid.spring.beans.factory.config.annotations;

public class DefaultValueTestBean {

	String property;

	public String getProperty() {
		return property;
	}

	@Property(key=PropertyAnnotationAndPlaceholderConfigurerTest.TEST_KEY, defaultValue=PropertyAnnotationAndPlaceholderConfigurerTest.TEST_DEFAULT_VALUE)
	public void setProperty(String property) {
		this.property = property;
	}

}
