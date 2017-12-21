import java.util.Timer;
import java.util.TimerTask;

/*  Timeout ���� �۾� (timer & TimeoutTask ����, TimeoutTask ���� �� ����) 
 *  �۽� �����尡 ���� ���� ���� ��� wait �ϸ鼭 �۽� ��Ŷ�� ���� ������ ��ٸ�.
 *  TimeoutTask ����: ���� ��Ŷ�� seq ��ȣ�� ���� timeout �ð��� �����ؼ� Timer�� ������
 *  TimeoutTask ����: Ack ���� ��Ŷ�� seq ��ȣ�� ���� timeoutTask�� Timer���� ������ 
 */

public class Timeout {
    Timer timer= new Timer();
    TimeoutTask[] myTimerTask = new TimeoutTask[16]; //MAXSIZE
    Signaling pp;
    public int timeoutlimit=0;
    boolean DEBUG=false;
    public void Timeoutset (int i, int ms, Signaling p) {
    	// TimeoutTask ����: ���� ��Ŷ�� sequence number�� ���� timeout �ð��� �����ؼ� Timer�� ������
    	pp=p;
    	myTimerTask[i]=new TimeoutTask(i);
        timer.schedule(myTimerTask[i], ms);
	}
    
    public void Timeoutcancel (int i) {
    	// TimeoutTask ����: Ack ���� ��Ŷ�� sequence number�� ���� timeoutTask�� Timer���� ������
    	int k=i;
    	if(DEBUG) System.out.println("Timer cancelled! no="+k);
        myTimerTask[k].cancel();
	}

    class TimeoutTask extends TimerTask {
    	int jj;
    	TimeoutTask(int j) {    		
    		jj=j;
    	}
    	public void run() {
            if(DEBUG) System.out.println("Time up! "+(timeoutlimit));
            pp.Timeoutnotifying();
            this.cancel(); //Terminate the timerTask thread
        }
    }
}
