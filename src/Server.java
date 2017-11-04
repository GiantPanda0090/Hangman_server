import com.sun.security.ntlm.Client;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {
    private static final int LINGER_TIME=5000;
    private static int portNo=9000;


    private  void start(){
        try{
            ServerSocket listendingSocket= new ServerSocket(portNo);
            System.out.println("Waiting for connection...");
            while(true){
                Scanner out = new Scanner(System.in);
                Socket clientSocket= listendingSocket.accept();
                Socket socket = new Socket("localhost", 8001);//backdoor
                PrintWriter clientPrint = new PrintWriter(socket.getOutputStream());//backdoor send
                BufferedReader clientReceive = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                clientPrint.println("proceed");
                clientPrint.flush();//all sent out
                BufferedReader receive = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter print = new PrintWriter(clientSocket.getOutputStream());
                System.out.println("Socket open!");
                clientSocket.setSoLinger(true,LINGER_TIME);// set linger time( wait till certain time before close)
                Thread handler= new Thread(new Hanger(clientSocket,receive,print,clientPrint));
                handler.setPriority(Thread.MAX_PRIORITY);
                handler.start();
                //backdoor monitor
                Thread stop= new Thread(new Stop(clientPrint,clientReceive,handler));
                stop.start();

            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }
}
