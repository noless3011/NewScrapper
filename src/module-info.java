module NewScrapper {
	exports model;
	exports controller;
	exports main;
	requires transitive javafx.graphics;
	opens controller;
	requires org.jsoup;
	requires javafx.base;
	requires javafx.controls;
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
	opens model;
	requires org.apache.lucene.core;
	requires org.apache.lucene.queryparser;
	requires org.slf4j;
	requires org.apache.opennlp.tools;
	requires java.base;
}
