package com.byluroid.spring.beans.factory.config.annotations;

import java.util.Properties;

public class MockPropertyLoader implements PropertyLoader {

	Properties properties;
	PropertyListener listener;

	public MockPropertyLoader(Properties properties) {
		this.properties = properties;
	}

	public Properties loadProperties() {
		return properties;
	}

	public void registerPropertyListener(PropertyListener listener) {
		this.listener = listener;
	}

}
