package com.bytesvc.main;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ProviderMain {

	static ClassPathXmlApplicationContext context = null;

	public static void main(String... args) throws Throwable {
		context = new ClassPathXmlApplicationContext("application.xml");
		context.start();

		System.out.println("sample-provider started!");
	}

}
