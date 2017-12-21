public class SerialWithArduino {

	public static void main ( String[] args ) {
		Serial conn = new Serial();
		
		try {
			conn.connect("/dev/tty.usbmodem14511", 9600);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
