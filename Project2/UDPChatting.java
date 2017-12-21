import java.io.*; 
import java.net.*; 

public class UDPChatting {
	final static int MAXBUFFER = 512;
	static RcvThread rcvThread; 
	public static DatagramSocket socket;
	public static InetAddress remoteaddr;
	public InetAddress myinetaddr;
	public static int remoteport=0, myport=0; 
	static Signaling p= new Signaling(); // use signaling for timeout, acknowledge, wait
	static Timeout tclick; // Timeout Interface

	public static void main(String[] args) {
		if(args.length == 2){
			//client mode : gets IP address and port number
			remoteport = Integer.parseInt(args[1]);
			try {
				remoteaddr = InetAddress.getByName(args[0]);
			} catch (UnknownHostException e) {
				System.out.println("Error on port"+remoteport);e.printStackTrace();
			}
		} else if(args.length == 1){
		// server mode without sending first: wait for an incoming message
			myport = Integer.parseInt(args[0]); //server mode
		} else {
			System.out.println("»ç¿ë¹ý: java UDPChatting localhost port (client) or port(server)");
			System.exit(0);
		}
				
		try {
			if(myport==0) {
				socket = new DatagramSocket();
			} else {
				socket = new DatagramSocket(myport);
			}
            System.out.println("Datagram\n"
            		+ "Address : "+InetAddress.getLocalHost()+"\n"+"Port number : "+socket.getLocalPort());

            tclick = new Timeout();
            rcvThread = new RcvThread(socket, p);
			rcvThread.start();
				
			DatagramPacket send_packet;// Datagram packet to send
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
				    
			while (true) {
				//keyboard input
				System.out.print("Input Data : ");
				String data;
				data = br.readLine();
				if (data.length() == 0){ // no char input return 
					System.out.println("exit call : input is empty");
					break;
				} // else System.out.println("read line="+data);
				byte buffer[] = new byte[512];
				buffer = data.getBytes();// change string to byte array
					// data send
				if((remoteaddr!=null)) {
					for(int i=0; i<10;i++) { // 10 times restransmission
						send_packet = new DatagramPacket(buffer, buffer.length, remoteaddr, remoteport);
						socket.send (send_packet);
						
						tclick.Timeoutset(i,100,p);
						p.ACKflag = false;
						p.TIMEflag = true;
						p.waitingACK();

						if(p.ACKflag == true) {
							tclick.Timeoutcancel(i);
							p.TIMEflag = false;
							break;
						} 
							// true: ACK,  false: Timeout
						else System.out.println("Retransmission "+data);
					}
				} else   System.out.println("Server mode: unable to send until receive packet");
				
			}
				
		} catch(IOException e) {
			System.out.println(e);
		}
		rcvThread.exitout(); // exit of Receive Thread 
		System.out.println("exit called");
		socket.close();
	}
}