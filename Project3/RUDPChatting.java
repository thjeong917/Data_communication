import java.io.*; 
import java.net.*;

public class RUDPChatting {
	final static int MAXBUFFER = 500;
	static RcvThread rcvThread; 
	public static DatagramSocket socket;
	public static InetAddress remoteaddr;
	public static InetAddress myinetaddr;
	public static byte[] destaddr = new byte[] {0,0,0,0,0,0}; //destination MAC address
	public static int remoteport=0, myport=0; 
	static Signaling p= new Signaling(); // Object를 생성해서 argument로 패싱해야 waiting/notify가 됨
	static Timeout tclick; // Timeout Interface
	static int seq=0;
	static int SERVER = 0;

	public static void main(String[] args) {
		if(args.length == 2){
			//client mode : gets IP address and port number
			remoteport = Integer.parseInt(args[1]);
			try {
				remoteaddr = InetAddress.getByName(args[0]);
				//destaddr = remoteaddr.getAddress(); // get destination address for LLC
			} catch (UnknownHostException e) {
				System.out.println("Error on port"+remoteport);
				e.printStackTrace();
			}
		} else if(args.length == 1){
		// server mode without sending first: wait for an incoming message
			myport = Integer.parseInt(args[0]); //server mode
			SERVER = 1;
		} else {
			System.out.println("사용법: java UDPChatting localhost port or port");
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
			
			if(args.length == 2){
			
				byte[] request_buff = LLC.makeUFrame(destaddr, LLC.request);
				send_packet = new DatagramPacket(request_buff, request_buff.length, remoteaddr, remoteport);
				socket.send(send_packet);
				System.out.println("SABME sent!!");
			}
	
			while (true) {
				if(args.length == 2){
					// 키보드 입력 읽기
					System.out.print("Input Data : ");
					String data;
					data = br.readLine();
//					if (data.length() == 0){ // no char input return 
//						System.out.println("exit call : input is empty");
//						break;
//					}  else System.out.println("Your Input : "+data);
					System.out.println("Your Input : "+data);
					byte buffer[] = new byte[data.length()];	
					buffer = data.getBytes(); //change string to byte array
					
					if(data.equals("DISC")){
						byte[] disc_buff = LLC.makeUFrame(destaddr, LLC.disc);
						send_packet = new DatagramPacket(disc_buff, disc_buff.length);
						socket.send(send_packet);
						System.out.println("send DISC!");
						rcvThread.exitout(); // exit of Receive Thread 
						System.out.println("exit called");
						socket.close();
					}
					
					byte[] send_buffer = LLC.makeIFrame(destaddr, buffer, (byte)(seq));
						// data send
					
					if((remoteaddr!=null)) {
						for(int i=0; i<10;i++) { // 10 times restransmission
							send_packet = new DatagramPacket(send_buffer, send_buffer.length, remoteaddr, remoteport)/* Fill in the Blank to make DatagramPacket */;
							socket.send (send_packet);
							
							tclick.Timeoutset(i,1000,p);
							p.ACKflag = false;
							p.TIMEflag = true;
							p.waitingACK();
	
							if(p.ACKflag == true) {
								tclick.Timeoutcancel(i);
								p.TIMEflag = false;
								seq ++;
								break;
							} 
								// true: ACK,  false: Timeout
							else System.out.println("Retransmission "+data);
						}
					} else   System.out.println("Server mode: unable to send until receive packet");
				}
			}
				
		} catch(IOException e) {
			System.out.println(e);
		}
		rcvThread.exitout(); // exit of Receive Thread 
		System.out.println("exit called");
		socket.close();
	}
}

