package com.byluroid.spring.beans.factory.config.annotations;

public class ConvertableTestBean {

	int property;

	public int getProperty() {
		return property;
	}

	@Property(key=PropertyAnnotationAndPlaceholderConfigurerTest.TEST_KEY, update=true)
	public void setProperty(int property) {
		this.property = property;
	}

}
