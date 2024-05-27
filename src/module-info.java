module NewsScrapper {
	exports model;
	exports controller;
	exports main;
	exports datamanager.searchengine;
	exports datamanager.crawler;
	requires transitive javafx.graphics;
	opens controller;
	
	requires org.jsoup;
	requires javafx.base;
	requires transitive javafx.controls;
	requires javafx.fxml;
	requires javafx.media;
	requires javafx.swing;
	requires javafx.web;
	requires org.seleniumhq.selenium.chrome_driver;
	requires org.seleniumhq.selenium.support;
	requires dev.failsafe.core;
	requires com.google.gson;
	requires com.google.common;
	requires com.fasterxml.jackson.databind;
	opens model to com.google.gson;
	requires org.apache.lucene.core;
	requires org.apache.lucene.queryparser;
	requires org.slf4j;
	requires org.apache.opennlp.tools;
	requires java.base;
}
