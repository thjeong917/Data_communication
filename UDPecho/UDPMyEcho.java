import java.net.*;
import java.io.*;

public class UDPMyEcho {
	final static int MAXBUFFER = 512;

	public static void main(String[] args) {

		if (args.length != 2) {
			System.out.println("사용법: java -jar UDPMyEcho.jar localhost port");
			System.exit(0);
		}
		

		int port = Integer.parseInt(args[1]);
		try {
			InetAddress inetaddr = InetAddress.getByName(args[0]); // localhost
			DatagramSocket socket = new DatagramSocket();
			DatagramPacket send_packet;// 송신용데이터그램패킷
			DatagramPacket recv_packet;// 수신용데이터그램패킷
			BufferedReader br = new BufferedReader(new InputStreamReader(
					System.in)); // 입력 스트림 생성
			while (true) {
				byte buffer[] = new byte[MAXBUFFER];
				System.out.print("Input Data : ");
				String data = br.readLine();
				if (data.length() == 0)
					break;
				buffer = data.getBytes();// 스트링을바이트배열로바꿈
				// 데이터송신
				send_packet = new DatagramPacket(buffer, buffer.length,
						inetaddr, port);
				socket.send(send_packet);
				//타임 아웃 설정
				socket.setSoTimeout(1000);
				// 에코데이터수신
				while (true) {
					try {
						recv_packet = new DatagramPacket(buffer, buffer.length);
						socket.receive(recv_packet);
						// 화면출력
						String result = new String(buffer);
						System.out.println("Echo Data : " + result);
						break;
					} catch (SocketTimeoutException e) {
						System.out.println(e);
						socket.send(send_packet);
						socket.setSoTimeout(1000);
					}
				}
			}
			socket.close();
		} catch (UnknownHostException ex) {
			System.out.println("Error in the host address ");
		} catch (IOException e) {
			System.out.println(e);
		}
	}
}