import java.io.IOException;
import java.util.zip.CRC32;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

class RcvThread extends Thread {
	DatagramSocket socket;
	boolean	state=true;
	DatagramPacket rcv_packet;// 수신용 데이터그램 패킷
	DatagramPacket send_packet;
	Signaling p;
	int LENGTH;
	static int seq = 0;
	
	RcvThread (DatagramSocket s, Signaling pp) {
		socket = s;
		p=pp; // should be defined (null point exception can be occurred)
	}	
	public void run() {
		byte buff[] = new byte[518];
		rcv_packet = new DatagramPacket(buff,buff.length);		
		
		while (state) {
			try {
		       socket.receive(rcv_packet);
		       RUDPChatting.remoteport = rcv_packet.getPort(); // port number
		       RUDPChatting.remoteaddr = rcv_packet.getAddress();; //IP address
			} catch(IOException e) {
				System.out.println("Thread exception "+e);
			}			
			//byte[] dest = RUDPChatting.remoteaddr.getAddress();
			
			//receive U Frame
			if((((byte)buff[16] & LLC.U) == LLC.U)){
				/*
				if(((byte)buff[16]&LLC.disc) == LLC.disc){
					System.out.println("Disc received!");
					exitout(); // exit of Receive Thread 
					socket.close();
				}
				*/
				
				for(int i = 0; i<6; i++){
					RUDPChatting.destaddr[i] = buff[i+6];
				}
				
				if(RUDPChatting.SERVER == 0){
					System.out.println("Access Complete");
				}
				else{
					System.out.println("Client Access");
					
					byte[] reply = LLC.makeUFrame(RUDPChatting.destaddr, LLC.access);
					reply[16] = 0x00;
					reply[16] |= LLC.U;
					reply[16] |= LLC.access;
					
					System.out.println("UA sent!");
					send_packet = new DatagramPacket(reply, reply.length, RUDPChatting.remoteaddr, RUDPChatting.remoteport);
					System.out.println(reply[16]&0xff);
					try{
						socket.send(send_packet);
					} catch(IOException e) {
						System.out.println(e);
					}
					
				}
			}
			//receive S Frame
			else if((buff[16] & LLC.S) == LLC.S){
				
				//NAK Receive
				if((buff[16] & LLC.NAK) == LLC.NAK){
					System.out.println("Nak Receive");
				}
				//Ack Receive
				else if((buff[16] & LLC.ACK) ==LLC.ACK){
					if(buff[17]-1 == RUDPChatting.seq){
						System.out.println("ACK Receive");
						//System.out.println("seq : "+(buff[17]));
						p.ACKnotifying();
					}
					else{
						System.out.println("seq : "+(buff[17]));
						System.out.println("seq false");
					}
				}
				
			}
			//receive I Frame
			else if((buff[16] & LLC.I) == LLC.I){
				
				int temp1 = (buff[12] & 0xff);
				int temp2 = (buff[13] & 0xff);
				int length = temp1*256 + temp2;
				
				try{
					byte[] result = new byte[length-22];
				
					for(int i=0; i<length-22; i++){
						result[i] = buff[i+18];
					}
					System.out.println("receive : " + new String(result));
					
				} catch(NegativeArraySizeException e){
					System.out.println("sssss");
				}
				System.out.println("length : "+length);
				
				byte[] CRC = new byte[4];
				for(int i=0; i<4; i++){
					CRC[i] = buff[length-4+i];
					buff[length-4+i]=0;
				}
				
				CRC32 CRC_ck = new CRC32();
				CRC_ck.reset();
				CRC_ck.update(buff, 0, length-5);
				int ck_value = (int) CRC_ck.getValue();
				int crc_value = (int) TypeCast.bytesToLong(CRC);
				
				if(ck_value != crc_value){
					System.out.println("CRC ERROR");
					System.out.println("Send Nak");
					
					byte[] NakFrame = LLC.makeNAKFrame(RUDPChatting.destaddr);

					DatagramPacket NakPacket = new DatagramPacket(NakFrame, NakFrame.length, RUDPChatting.remoteaddr, RUDPChatting.remoteport);
					
					try{
						socket.send(NakPacket);
					} catch(IOException e) {
						System.out.println(e);
					}
					
				}
				else{
					System.out.println("CRC OK");
					System.out.println("Send Ack");
					
					byte[] AckFrame = LLC.makeACKFrame(RUDPChatting.destaddr, buff[17]);
					
					send_packet = new DatagramPacket(AckFrame, AckFrame.length, RUDPChatting.remoteaddr, RUDPChatting.remoteport);

					DatagramPacket AckPacket = new DatagramPacket(AckFrame, AckFrame.length, RUDPChatting.remoteaddr, RUDPChatting.remoteport);
					
					try{
						socket.send(AckPacket);
					} catch(IOException e) {
						System.out.println(e);
					}
				}
			}
		}
		
		System.out.println("exit called");
	}
	public void exitout(){
		state=false;
	}
			
} // end ReceiverThread class
