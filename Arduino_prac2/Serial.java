import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Serial {
	
	void connect (String portName, int baudRate) throws Exception {
		CommPortIdentifier commPortIdentifier = CommPortIdentifier.getPortIdentifier(portName);
		
		if (commPortIdentifier.isCurrentlyOwned()){
			System.out.println("Error: Port is currently in use");
		}
		else{
			// Open Communication Port
			CommPort commPort = commPortIdentifier.open("SerialWithArduino", 2000);
			
			if (commPort instanceof SerialPort) {
				SerialPort serialPort = (SerialPort) commPort;
				serialPort.setSerialPortParams(
					baudRate,
					SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1,
					SerialPort.PARITY_NONE
				);
				
				// Get Stream
				InputStream in = serialPort.getInputStream();
				OutputStream out = serialPort.getOutputStream();
				
				// Using Thread
				SerialReader reader = new SerialReader(in);
				SerialWriter writer = new SerialWriter(out);
				
				(new Thread(reader)).start();
				(new Thread(writer)).start();
				
			}else{
				System.out.println("Error: current commPort type is not supported");
			}
		}	 
	}
	
	
	public static class SerialReader implements Runnable {
		InputStream in;
		
		public SerialReader (InputStream in) {
			this.in = in;
		}
		
		public void run () {
			byte[] buffer = new byte[1024];
			int len = -1;
			try {
				while ( ( len = this.in.read(buffer)) > -1) {
					System.out.print(new String(buffer, 0, len));
				}
			}catch (IOException e){
				e.printStackTrace();
			}			
		}
	}

	public static class SerialWriter implements Runnable {
		OutputStream out;
		
		public SerialWriter (OutputStream out) {
			this.out = out;
		}
		
		public void run() {
			try{
				int c = 0;
				while ( ( c = System.in.read()) > -1 ){
					this.out.write(c);
				}				
			}
			catch (IOException e) {
				e.printStackTrace();
			}			
		}
	}
	
	
}