package com.urbanmania.spring.beans.factory.config.annotations;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.core.io.Resource;

public class PropertyFileLoader implements PropertyLoader {

    private static final Logger log = Logger.getLogger(PropertyFileLoader.class.getName());

    private Properties properties;
    private List<PropertyListener> listeners = new ArrayList<PropertyListener>();
    private List<Resource> resources;
    private Map<String, Long> lastModified = new HashMap<String, Long>();

    public void setResources(List<Resource> resources) {
        this.resources = resources;
    }

    public Properties loadProperties() throws IOException {
        properties = new Properties();
        for(Resource resource : resources) {
            properties.load(new FileReader(resource.getFile()));
        }
        
        for (Resource resource : resources) {
            updateLastModified(resource);
        }

        return properties;
    }

    public void registerPropertyListener(PropertyListener listener) {
        listeners.add(listener);
    }

    public void checkResourcesForUpdates() throws IOException {
        for (Resource resource : resources) {
            if(log.isLoggable(Level.FINE)) log.fine("checking file=[" + resource.getFile() + ",lastModified="+resource.getFile().lastModified()+"] for updates since=["+lastModified.get(resource.getFilename())+"]");
            if (resource.getFile().lastModified() > lastModified.get(resource.getFilename())) {
                Properties updatedProperies = new Properties();
                updatedProperies.load(new FileReader(resource.getFile()));
                for (Object o : updatedProperies.keySet()) {
                    String key = (String) o;
                    if (!updatedProperies.get(key).equals(properties.get(key))) {
                        log.info("changed property=[" + key + "] in file=[" + resource.getFile() + "]: " + properties.get(key) + " -> " + updatedProperies.get(key));
                        String value = (String) updatedProperies.get(key);
                        properties.put(key, value);
                        PropertyEvent event = new PropertyEvent(this, key, value);
                        for (PropertyListener listener : listeners) {
                            listener.propertyChanged(event);
                        }
                    }
                }
                updateLastModified(resource);
            }
        }
    }

    private void updateLastModified(Resource resource) throws IOException {
        lastModified.put(resource.getFilename(), resource.getFile().lastModified());
    }

}
