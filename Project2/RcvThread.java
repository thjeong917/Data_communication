import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;

class RcvThread extends Thread {
	DatagramSocket socket;
	boolean	state=true;
	DatagramPacket rcv_packet;// 수신용 데이터그램 패킷
	Signaling p;
	
	RcvThread (DatagramSocket s, Signaling pp) {
		socket = s;
		p=pp; // should be defined (null point exception can be occurred)
	}	
	public void run() {
			byte buff[] = new byte[100];
			rcv_packet = new DatagramPacket(buff,buff.length);		
		while (state) {
			try {
		       socket.receive(rcv_packet);  
		       UDPChatting.remoteport = rcv_packet.getPort();    // port number
		       UDPChatting.remoteaddr = rcv_packet.getAddress(); // IP address
			} catch(IOException e) {
				System.out.println("Thread exception "+e);
			}
			
			if(p.TIMEflag == false){
			   String result = new String(buff);
			   System.out.println("\n Receive Data : " + result);
			   SocketAddress ip = rcv_packet.getSocketAddress();
			   rcv_packet.setSocketAddress(ip);
			   System.out.print("Input Data : ");
			   
			   try{
				   socket.send(rcv_packet);
			   } catch(IOException e) {
					System.out.println(e);
			   }
			}
			else{
			   p.ACKnotifying();
			}
		}
		
		System.out.println("exit called");
	}
	public void exitout(){
		state=false;
	}
			
}
