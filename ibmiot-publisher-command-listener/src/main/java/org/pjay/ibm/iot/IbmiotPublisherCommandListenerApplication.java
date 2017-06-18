package org.pjay.ibm.iot;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

import com.ibm.iotf.client.app.ApplicationClient;
import com.ibm.iotf.client.device.DeviceClient;

@SpringBootApplication
public class IbmiotPublisherCommandListenerApplication {
	
	private final static String PROPERTIES_APPLICATION_FILE_NAME = "/app.properties";
	private final static String PROPERTIES_DEVICE_FILE_NAME = "/device.properties";

	public static void main(String[] args) {
		SpringApplication.run(IbmiotPublisherCommandListenerApplication.class, args);
	}
	
	// IMP:
	// http://docs.spring.io/spring-javaconfig/docs/1.0.0.M4/reference/html/ch02s02.html
	// https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/context/annotation/Bean.html
	// https://stackoverflow.com/questions/10604298/spring-component-versus-bean
	// https://docs.spring.io/spring/docs/current/spring-framework-reference/html/beans.html
	// We can write a initializer bean and connect to clients after properties set or without that can add a command line runner
	// @Bean can used for autowire thought it is default NO, if we define in @Configuration class. Like here as @SpringBootApplication is internally has @Configuration 
	@Bean("appClient")
	// Need to verify, if without bean name will it take the name from method after get or by return variable name. As per documentation full name of method is byName value
	@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
	public ApplicationClient getAppClient(){
		Properties props = new Properties();
		ApplicationClient appClient = null;
		try {
			props.load(IbmiotPublisherCommandListenerApplication.class.getResourceAsStream(PROPERTIES_APPLICATION_FILE_NAME));
			appClient = new ApplicationClient(props);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return appClient;
	}
	
	// Still autowire is without same name being used in controller, i think if byName does not work then byType is tried and gets the bean
	@Bean(autowire=Autowire.BY_NAME, name="deviceClient")
	@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
	public DeviceClient getMyDeviceClient(){
		Properties props = new Properties();
		DeviceClient myClient = null;
		try {
			//Instantiate the class by passing the properties file
			props.load(IbmiotPublisherCommandListenerApplication.class.getResourceAsStream(PROPERTIES_DEVICE_FILE_NAME));
			myClient = new DeviceClient(props);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return myClient;
	}
	
	// https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/context/annotation/Bean.html
	// https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/context/annotation/Scope.html
	// https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/context/annotation/Primary.html
	// https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/context/annotation/Lazy.html
	// https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/context/annotation/DependsOn.html
	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
	public CommandCallbackSubscriber getCommandCallbackSubscriber(){
		return new CommandCallbackSubscriber();
	}
}
