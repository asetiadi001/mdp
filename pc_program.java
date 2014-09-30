import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.*;




public class pc_program{
    public static final InetSocketAddress RES_PI_ADDR = new InetSocketAddress("192.168.18.1",5143);
    
	public static byte[] in_buf, out_buf;
	public static DatagramSocket clientSocket;
	private static InetSocketAddress targetAddr;

    //	RobotInstructionParser parser;

	public static void buildSocket() {
        
		try {
			clientSocket = new DatagramSocket();
			targetAddr = RES_PI_ADDR;
			in_buf = new byte[1024];
			out_buf = new byte[1024];
            //	parser = new RobotInstructionParser();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


    
	protected static void send(String str) {
		out_buf = str.getBytes();
		try {
			DatagramPacket packet = new DatagramPacket(out_buf, out_buf.length, targetAddr);
			clientSocket.send(packet);
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

    
	protected static String receive() {
		DatagramPacket packet = new DatagramPacket(in_buf, in_buf.length);
		try {
			clientSocket.receive(packet);
			return new String(packet.getData(), packet.getOffset(), packet.getLength());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
    
    
    public static void main(String[] args){
    	System.out.println ("Start\n");
        Scanner sc = new Scanner(System.in);
        String input;
        int choice;
        buildSocket();
        //for(int i = 0; i <20; i++);
        send("ready");
        while (true){
            System.out.print("send(1) or receive(0): ");
            choice = sc.nextInt();
            sc.nextLine();
            if(choice>0){
            	System.out.print("Say Something: ");
            	input = sc.nextLine();
            	send(input);
            }
            else{
				System.out.println("Receiving(blocking till recevive a msg): ");
            	try{
            		while(true){
            			String text=receive();
            		}
            	}catch(NoSuchElementException e){
            		System.out.println("Going back...");
            	}
            }
            //send ("sent from pc\n");
        }
    }
}