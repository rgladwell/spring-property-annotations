#Spring Property Annotations

Extended PropertyPlaceHolderConfigurer that injects configuration properties into your Spring components using annotations.

*Note*: Spring 3 now has support for [annotated property configuration](http://docs.spring.io/spring/docs/3.0.x/spring-framework-reference/html/beans.html#beans-factorybeans-annotations) of containers using the @Value annotation. This project is only for Spring 2.5.x support.

##Getting Started

Spring Property Annotations defines a new sub-class of the [PropertyPlaceHolderConfigurer](http://docs.spring.io/spring/docs/2.5.x/api/org/springframework/beans/factory/config/PropertyPlaceholderConfigurer.html), the PropertyAnnotationAndPlaceholderConfigurer? which can be used as follows:

###Maven

First, if you're using Maven 2/3 please add the following dependency to your pom.xml:

```xml
<dependency>
    <groupId>com.urbanmania</groupId>
    <artifactId>spring-property-annotations</artifactId>
    <version>0.1.8</version>
</dependency>
```

###Spring Configuration

Then you can add something like the following XML fragment to your Spring application context:

```xml
<bean
class="com.urbanmania.spring.beans.factory.config.annotations.
PropertyAnnotationAndPlaceholderConfigurer">
    <property name="locations">
        <value>classpath:application.properties</value>
    </property>
</bean>

<bean id="dataSource" destroy-method="close"
      class="org.apache.commons.dbcp.BasicDataSource">
    <property name="driverClassName" value="${jdbc.driverClassName}"/>
    <property name="url" value="${jdbc.url}"/>
    <property name="username" value="${jdbc.username}"/>
    <property name="password" value="${jdbc.password}"/>
</bean>
```

Alternatively you can use the more concise, dedicated XML configuration element as follows:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns:spa="http://code.google.com/p/spring-property-annotations"
        xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://code.google.com/p/spring-property-annotations
http://code.google.com/p/spring-property-annotations/spring-property-annotations-1.0.xsd">

        <spa:property-placeholder-annotations
                locations="classpath:application.properties" />

</beans>
```

###Property Annotation

However, in addition to using the placeholders, you can now also inject properties into your Spring components using the Property annotation as follows:

```java
package com.example;

import org.springframework.stereotype.Component;

import com.urbanmania.spring.beans.factory.config.annotations.Property;

@Component
public class ExampleComponent {

    String defaultEmailDomain;

    @Property(key="com.example.defaultEmailDomain")
    public void setDefaultEmailDomain(String defaultEmailDomain) {
        this.defaultEmailDomain = defaultEmailDomain;
    }

}
```

You can also annotate fields like so:

```java
@Component
public class ExampleComponent {

    @Property(key="com.example.defaultEmailDomain")
    String defaultEmailDomain;

    public void setDefaultEmailDomain(String defaultEmailDomain) {
        this.defaultEmailDomain = defaultEmailDomain;
    }

}
```

However, you must have a property setter for your field to use the above convention. The following will fail:

```java
@Component
public class ExampleComponent {

    @Property(key="com.example.defaultEmailDomain")
    String defaultEmailDomain;

}
```

You can also use expression language substitution in @Property annotation value strings to compose new properties on the fly. For example:

```java
    @Property(value="ricardo@${com.example.defaultEmailDomain}")
    String defaultEmailAddress;
```

Would create a default email address for the user "ricardo" under an email domain specified by the "com.example.defaultEmailDomain" configuration property value.

#Updating properties at runtime

You can also use Spring Annotation Properties to watch property files and update your Spring 2.5 components at run time with any updates.

**Warning**: This is an experimental feature.

To enable this feature you need to create a PropertyFileLoader? in your Spring application context as follows:


```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:batch="http://www.springframework.org/schema/batch" 
       xsi:schemalocation="http://www.springframework.org/schema/batch
         http://www.springframework.org/schema/batch/spring-batch-2.0.xsd
         http://www.springframework.org/schema/beans
         http://www.springframework.org/schema/beans/spring-beans-2.5.xsd">

<bean class="org.springframework.scheduling.quartz.SchedulerFactoryBean">
    <property name="triggers">
        <bean id="cronTrigger"
class="org.springframework.scheduling.quartz.CronTriggerBean">
             <property name="jobDetail" ref="jobDetail" />
             <property name="cronExpression" value="0 0/2 * * * ?" />
        </bean>
     </property>
</bean>

<bean id="jobDetail"
class="org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean">
    <property name="targetObject" ref="propertyLoader" />
    <property name="targetMethod" value="checkResourcesForUpdates" />
    <property name="concurrent" value="false" />
</bean>

<bean name="propertyLoader"
class="com.urbanmania.spring.beans.factory.config.annotations.
PropertyFileLoader">
    <property name="resources">
        <value>classpath:application.properties</value>
     </property>
</bean>

<bean
class="com.urbanmania.spring.beans.factory.config.annotations.
PropertyAnnotationAndPlaceholderConfigurer">
    <property name="propertyLoaders">
       <ref bean="propertyLoader"/>
    </property>
</bean>

</beans>
```

You need to let Spring Property Annotations know which properties you want updated. You can do this by setting the @Property update attribute to true as follows:

```java
    @Property(key="com.example.defaultEmailDomain", update=true)
    String defaultEmailDomain;
```

**Note**: Spring components with up-datable properties should be thread-safe, as calls to update properties occur in a separate thread. 