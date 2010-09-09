package com.urbanmania.spring.beans.factory.config.annotations;


public class MockPropertyListener implements PropertyListener {

    PropertyEvent event;

    public PropertyEvent getEvent() {
        return event;
    }

    public void propertyChanged(PropertyEvent event) {
        this.event = event;
    }

}
