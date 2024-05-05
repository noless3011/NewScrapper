package controller;

public class MainControllerSingleton {
	private static MainController controller = new MainController();
	private MainControllerSingleton() {
		// TODO Auto-generated constructor stub
	}
	public static void setController(MainController controller) {
		MainControllerSingleton.controller = controller;
	}
	public static MainController getMainController() {
		return controller;
	}
}
