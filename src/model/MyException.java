package model;

public class MyException extends Exception{
	private static final long serialVersionUID = 1L;
	public MyException(String message) {
		super(message);
	}
	public void PrintMessage() {
		System.out.println(this.getMessage());
	}
}
