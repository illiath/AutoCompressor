package autocompressor;

public class DebugOut {
	public static void debugException(String function, Exception errorE) {
		System.out.println("--------------------------------------------");
		System.out.println("Debug:     "+function);
		System.out.println("Exception: "+errorE.toString());
		errorE.printStackTrace();
		System.out.println("--------------------------------------------");
	}
	
	public static void debugMessage(String function, String message) {
		System.out.println("--------------------------------------------");
		System.out.println("Function: "+function+", Message: "+message);
		System.out.println("--------------------------------------------");
	}	
}
