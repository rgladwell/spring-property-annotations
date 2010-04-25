package com.byluroid.spring.beans.factory.config.annotations;

public class UpdateableTestBean {

	String property;

	public String getProperty() {
		return property;
	}

	@Property(key=PropertyAnnotationAndPlaceholderConfigurerTest.TEST_KEY, update=true)
	public void setProperty(String property) {
		this.property = property;
	}

}
