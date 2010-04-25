package com.byluroid.spring.beans.factory.config.annotations;


import java.util.Properties;

public interface PropertyLoader {

	public Properties loadProperties();
	public void registerPropertyListener(PropertyListener listener);

}
