package com.byluroid.spring.beans.factory.config.annotations;


import java.util.EventListener;

public interface PropertyListener extends EventListener {

	void propertyChanged(PropertyEvent event);

}
