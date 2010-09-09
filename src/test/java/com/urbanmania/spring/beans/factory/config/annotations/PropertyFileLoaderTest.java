package com.urbanmania.spring.beans.factory.config.annotations;

import static junit.framework.Assert.*;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

public class PropertyFileLoaderTest {

    private static final Logger log = Logger.getLogger(PropertyFileLoaderTest.class.getName());

    List<Resource> resources;
    PropertyFileLoader loader;

    @Before
    public void setUp() {
        loader = new PropertyFileLoader();
        resources = new ArrayList<Resource>();
        resources.add(new ClassPathResource("test1.properties", getClass().getClassLoader()));
        resources.add(new ClassPathResource("test2.properties", getClass().getClassLoader()));
        loader.setResources(resources);
    }

    @Test
    public void testLoadPropertyFiles() throws Exception {        
        Properties properties = loader.loadProperties();

        assertTrue(properties.containsKey("test1"));
        assertEquals("test1", properties.get("test1"));
        assertTrue(properties.containsKey("test2"));
        assertEquals("override", properties.get("test2"));
    }

    @Test
    public void testCheckResourcesForUpdates() throws Exception {
        File test3 = new File(System.getProperty("java.io.tmpdir")+File.separator+"test3.properties");
        FileWriter writer = new FileWriter(test3);
        Properties properties = new Properties();
        properties.setProperty("test3", "test3");
        properties.store(writer, null);
        resources.add(new FileSystemResource(test3));
        loader.loadProperties();
        MockPropertyListener listener = new MockPropertyListener();
        loader.registerPropertyListener(listener);

        Thread.sleep(1000);
        properties.setProperty("test3", "updated");
        properties.store(writer, null);
        loader.checkResourcesForUpdates();

        assertNotNull("property changed event not sent", listener.getEvent());
        assertEquals("property not found", "test3", listener.getEvent().getKey());
        assertEquals("property not correctly updated", "updated", listener.getEvent().getValue());
    }

}
