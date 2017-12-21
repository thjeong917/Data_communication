import java.io.*;
import java.net.*;

public class UDPMyEchoServer {
	final static int MAXBUFFER = 512;
	
	
	public static void main(String[] args){
		if(args.length < 1)
		{
			System.out.println("»ç¿ë¹ý : java -jar UdpEchoServer.jar port");
			System.exit(0);
		}
		int arg_port = Integer.parseInt(args[0]);
		new UDPMyEchoServer().work(arg_port);
	}
	void work(int arg_port){
		int port = arg_port;
		
		String receiveString;
		try{
			DatagramSocket Dsocket = new DatagramSocket(port);
			System.out.println("Running the UDP Echo Server");
			System.out.println("IP address is "+InetAddress.getLocalHost().getHostAddress()+", Port number is "+port);
			while(true){
				byte rece_buffer[] = new byte[MAXBUFFER];
				DatagramPacket recv_packet = new DatagramPacket(rece_buffer, MAXBUFFER) ;
				Dsocket.receive(recv_packet);
				receiveString = new String(recv_packet.getData()).substring(0, recv_packet.getLength());
				
				System.out.println("Server received data:"+receiveString +" from " + recv_packet.getAddress());
				new EchoSender(Dsocket, recv_packet, receiveString).start();
			}

		}catch(IOException e){
			System.out.println(e);
		}
	}
	class EchoSender extends Thread{
		DatagramSocket Dsocket;
		DatagramPacket echo_packet;
		String object;
		public EchoSender(DatagramSocket socket, DatagramPacket packet, String object) {
			Dsocket = socket;
			echo_packet = packet;
			this.object = object;
		}
		public void run() {
			DatagramPacket send_packet = new DatagramPacket(object.getBytes(),object.getBytes().length,echo_packet.getAddress(),echo_packet.getPort());
			try {
				Dsocket.send(send_packet);
			} catch (IOException e) {
				e.printStackTrace();
			}
			super.run();
		}
	}

}
