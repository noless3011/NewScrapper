module NewsScrapper {
	exports model;
	exports controller;
	exports main;

	requires javafx.base;
	requires javafx.controls;
	requires javafx.fxml;
	requires transitive javafx.graphics;
	opens controller;
}