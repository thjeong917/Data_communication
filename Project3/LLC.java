import java.nio.ByteBuffer;
import java.util.zip.CRC32;
import java.net.*;

public class LLC {
	public static InetAddress myaddr;
	public static NetworkInterface myMAC;
	// static byte flag = 0x7e;
	public static byte I = 0x00;
	public static byte S = (byte) (1 << 7);
	public static byte U = (byte) 192;

	public static byte NAK = (byte) (16);
	public static byte ACK;

	public static byte request = (byte) (32 + 16 + 4 + 2); // SABME
	public static byte access = (byte) (4 + 2); // UA
	public static byte not_access = (byte) (16 + 2);
	//static byte seq_make = (byte) (7);
	//static byte seq_make2 = (byte) (64 + 32 + 16);
	public static byte disc = (byte) 2; // DISC

	public static byte[] makeIFrame(byte[] Dest, byte[] data, byte seqNum) {
		try {
			myaddr = InetAddress.getLocalHost();
			try {
				myMAC = NetworkInterface.getByInetAddress(myaddr);
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			System.out.println("Error on getlocalhost");
			e.printStackTrace();
		}
		
		System.out.println("make I frame!");
		
		byte[] temp = new byte[data.length];

		for (int i = 0; i < data.length; i++) {
			temp[i] = data[i];
		}
		byte[] Frame = new byte[6+6+2+4+data.length+4];
		byte[] Source = null;
		
		try {
			Source = myMAC.getHardwareAddress();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		byte Control = 0x00;
		Control |= I;
		Control |= seqNum;

		// for Destination address and Source address
		for (int i = 0; i < 6; i++) {
			Frame[i] = Dest[i];
		}

		for (int i = 0; i < 6; i++) {
			Frame[i+6] = Source[i];
		}

		// Length PDU = 518
		Frame[12] = (byte) ((byte)(data.length+22) >>> 8);
		Frame[13] = (byte) ((byte)(data.length+22) & 0xff);

		// DSAP SSAP
		Frame[14] = 0x00;
		Frame[15] = 0x00;

		// control
		Frame[16] = Control;
		Frame[17] = (byte)((int)Control+1);

		for (int i = 0; i < temp.length; i++) {
			Frame[18 + i] = temp[i];
		}

		CRC32 crc = new CRC32();
		crc.reset();
		crc.update(Frame, 0, 18+data.length-1);
		int crclong = (int) crc.getValue();

		byte[] crcByte = ByteBuffer.allocate(4).putInt(crclong).array();

		for (int i = 0; i < 4; i++) {
			Frame[18 + data.length + i] = crcByte[i];
		}

		return Frame;
	}

	public static byte[] makeACKFrame(byte[] Dest, byte seqNum) {
		try {
			myaddr = InetAddress.getLocalHost();
			try {
				myMAC = NetworkInterface.getByInetAddress(myaddr);
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			System.out.println("Error on getlocalhost");
			e.printStackTrace();
		}
		System.out.println("make S frame!");

		byte[] Frame = new byte[22];
		byte[] Source = null;
		
		try {
			Source = myMAC.getHardwareAddress();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		byte Control = 0x00;
		Control |= S;
		//Ack = 00 이므로 check 안해줌
		byte Control2 = 0x00;
		Control2 |= seqNum;
		
		// for Destination address and Source address
		for (int i = 0; i < 6; i++) {
			Frame[i] = Dest[i];
		}
		for (int i = 0; i < 6; i++) {
			Frame[i+6] = Source[i];
		}
		// Length PDU = 22
		Frame[12] = 0x00;
		Frame[13] = 22;

		// DSAP SSAP
		Frame[14] = 0x00;
		Frame[15] = 0x00;

		// control
		Frame[16] = Control;
		Frame[17] = Control2;
		
		CRC32 crc = new CRC32();
		crc.reset();
		crc.update(Frame, 0, 17);
		int crclong = (int) crc.getValue();

		byte[] crcByte = ByteBuffer.allocate(4).putInt(crclong).array();

		for (int i = 0; i < 4; i++) {
			Frame[18 + i] = crcByte[i];
		}

		return Frame;
	}

	public static byte[] makeNAKFrame(byte[] Dest) {
		try {
			myaddr = InetAddress.getLocalHost();
			try {
				myMAC = NetworkInterface.getByInetAddress(myaddr);
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			System.out.println("Error on getlocalhost");
			e.printStackTrace();
		}
		
		System.out.println("make S frame!");

		byte[] Frame = new byte[22];
		byte[] Source = null;
		
		try {
			Source = myMAC.getHardwareAddress();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		byte Control = 0x00;
		Control |= S;
		Control |= NAK;
		
		byte Control2 = 0x00;
		//Control2 |= seqNum;
		
		// for Destination address and Source address
		for (int i = 0; i < 6; i++) {
			Frame[i] = Dest[i];
		}
		for (int i = 0; i < 6; i++) {
			Frame[i+6] = Source[i];
		}
		// Length PDU = 22
		Frame[12] = 0x00;
		Frame[13] = 22;

		// DSAP SSAP
		Frame[14] = 0x00;
		Frame[15] = 0x00;

		// control
		Frame[16] = Control;
		Frame[17] = Control2;
		
		CRC32 crc = new CRC32();
		crc.reset();
		crc.update(Frame, 0, 17);
		int crclong = (int) crc.getValue();

		byte[] crcByte = ByteBuffer.allocate(4).putInt(crclong).array();

		for (int i = 0; i < 4; i++) {
			Frame[18 + i] = crcByte[i];
		}

		return Frame;
	}

	public static byte[] makeUFrame(byte[] Dest, byte state) {
		try {
			myaddr = InetAddress.getLocalHost();
			try {
				myMAC = NetworkInterface.getByInetAddress(myaddr);
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			System.out.println("Error on getlocalhost");
			e.printStackTrace();
		}
		
		System.out.println("make U frame!");
		
		byte[] Frame = new byte[21];
		byte[] Source = null;
		
		try {
			Source = myMAC.getHardwareAddress();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		byte Control = 0x00;
		Control |= U;
		Control |= state;

		// for Destination address and Source address
		for (int i = 0; i < 6; i++) {
			Frame[i] = Dest[i];
		}
		for (int i = 0; i < 6; i++) {
			Frame[i+6] = Source[i];
		}
		
		// Length PDU = 21
		Frame[12] = 0x00;
		Frame[13] = 21;

		// DSAP SSAP
		Frame[14] = 0x00;
		Frame[15] = 0x00;

		// control
		Frame[16] = Control;
		
		CRC32 crc = new CRC32();
		crc.reset();
		crc.update(Frame, 0, 16);
		int crclong = (int) crc.getValue();

		byte[] crcByte = ByteBuffer.allocate(4).putInt(crclong).array();

		for (int i = 0; i < 4; i++) {
			Frame[16 + i] = crcByte[i];
		}

		return Frame;
	}

	public static byte[] cut(byte[] input, int start) {
		byte[] buf = new byte[input.length - 2];
		for (int i = 0; i < buf.length; i++) {
			buf[i] = input[i + start];
		}
		return buf;
	}
}
