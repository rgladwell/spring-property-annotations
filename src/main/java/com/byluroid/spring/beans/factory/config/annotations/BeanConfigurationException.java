package com.byluroid.spring.beans.factory.config.annotations;


import org.springframework.beans.BeansException;

/**
 * @author ricardo
 */
public class BeanConfigurationException extends BeansException {

	private static final long serialVersionUID = -3492599598206122669L;

	public BeanConfigurationException(String s) {
        super(s);
    }

    public BeanConfigurationException(String s, Throwable throwable) {
        super(s, throwable);
    }

}
