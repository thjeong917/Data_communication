import java.net.*;
import java.io.*;

public class UDPMyEcho {
	final static int MAXBUFFER = 512;

	public static void main(String[] args) {

		if (args.length != 2) {
			System.out.println("����: java -jar UDPMyEcho.jar localhost port");
			System.exit(0);
		}
		

		int port = Integer.parseInt(args[1]);
		try {
			InetAddress inetaddr = InetAddress.getByName(args[0]); // localhost
			DatagramSocket socket = new DatagramSocket();
			DatagramPacket send_packet;// �۽ſ뵥���ͱ׷���Ŷ
			DatagramPacket recv_packet;// ���ſ뵥���ͱ׷���Ŷ
			BufferedReader br = new BufferedReader(new InputStreamReader(
					System.in)); // �Է� ��Ʈ�� ����
			while (true) {
				byte buffer[] = new byte[MAXBUFFER];
				System.out.print("Input Data : ");
				String data = br.readLine();
				if (data.length() == 0)
					break;
				buffer = data.getBytes();// ��Ʈ��������Ʈ�迭�ιٲ�
				// �����ͼ۽�
				send_packet = new DatagramPacket(buffer, buffer.length,
						inetaddr, port);
				socket.send(send_packet);
				//Ÿ�� �ƿ� ����
				socket.setSoTimeout(1000);
				// ���ڵ����ͼ���
				while (true) {
					try {
						recv_packet = new DatagramPacket(buffer, buffer.length);
						socket.receive(recv_packet);
						// ȭ�����
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