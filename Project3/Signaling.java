	/*  Signaling for Thread (������ �� �ñ׳� ����) Thread.waiting() - Thread.notify() 
	 *  �۽� �����尡 ���� ���� ���� ��� wait �ϸ鼭 �۽� ��Ŷ�� ���� ������ ��ٸ�.
	 *  wait for (keyboard input, ack packet, timeout)
	 *  notifying when (keyboard input to be sent(seqNo), ack packet received(ackNo), timeout expired(seq No)) 
	 *  return notify_state; 0: undefined, i=1:send key, i=2: receive ack, i=3: timeout 
	 */

public class Signaling {
	public static boolean ACKflag = false, TIMEflag=false;
	public static int notify_state=0,timeouttaskNo=0,rcvackNo=0,sndseqNo=0;

	public synchronized void Timeoutnotifying() {
		System.out.println("Timeout notified"); 
		TIMEflag = false; 
		notify(); 
	} 
	public synchronized void ACKnotifying() { 
		System.out.println("Ack notified"); 
		ACKflag = true; 
		notify(); 
	} 
	public synchronized void waitingACK() {//   System.out.println("Waiting for the event: ");
		System.out.println("Waiting for the event.... "); 
		try { 	
			 wait(); 	
		} catch(InterruptedException e) { 
			System.out.println("InterruptedException caught"); 
		} 
	}
}