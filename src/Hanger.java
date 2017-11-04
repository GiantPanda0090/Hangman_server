import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Hanger implements Runnable{
    private static Socket clientSocket=null;
    //private static StringBuilder content =null;
    private static ArrayList<String> libraryLst= null;
    private static int score;
    private static BufferedReader receive=null;
    private static PrintWriter print = null;
    private static PrintWriter clientPrint=null;
    public Hanger(Socket clientSocket,BufferedReader receive,PrintWriter print,PrintWriter clientPrint) throws IOException{
        this.clientSocket= clientSocket;
        this.receive=receive;
        this.print =print;
        this.clientPrint=clientPrint;
        score=0;
    }

    public static boolean loadDictionary(String path)throws IOException{
        libraryLst= new ArrayList();
        BufferedReader fromFile = new BufferedReader(new FileReader(path));
        while(!fromFile.ready()) {
    }
    while(fromFile.readLine()!=null) {
        libraryLst.add(fromFile.readLine().toString());
    }
    System.out.println("Dictionary loaded!");
    fromFile.close();
    //System.out.print(content);//debug make it http base later

return false;
    }
    private void send(String content){
                print.println(content);
            print.flush();//all sent out

    }
private String receive(){
        try {
            String in =receive.readLine();
            return in;
        }catch (IOException e){
            System.err.println(e);
        }
        return null;
}
    @Override
    public void run()  {
        try {
            try {
                String in = receive();
                System.out.println("Command received: " + in);
                while(!(in.compareTo("start") == 0)&&!(in.compareTo("quit") == 0)){
                    send("wrongcommand");
                    System.err.println("Wrong input command!");
                    in = receive();
                    System.out.println("Command received: " + in);
                }
                if (in.compareTo("start") == 0) {
                    send("session");
                    loadDictionary("Resources/words.txt");
                    //reset point
                    while (true) {
                        //random take a word from array
                        Random b = new Random();
                        String currentQuestion = libraryLst.get(b.nextInt(libraryLst.size() - 1));
                        System.out.println("Currnt word: " + currentQuestion);
                        int strLength = currentQuestion.length();
                        send(new Integer(strLength).toString());
                        int currentChance = strLength;
                        //start testing answeres
                        while (currentChance != 0) {
                            send("gamestart");
                            String currentAnswer = receive();
                            System.out.println("User Input: " + currentAnswer);
                            //currect answer
                            try {
                                if (currentAnswer.length() > 1 && currentQuestion.compareTo(currentAnswer) == 0) {
                                    currentQuestion = "";
                                    strLength = currentQuestion.length();
                                    System.out.println("User guessed out the whole word!");
                                    send("KO");
                                    break;
                                } else if (currentQuestion.contains(currentAnswer)) {
                                    currentQuestion = removeCharAt(currentQuestion, currentQuestion.indexOf(currentAnswer));
                                    System.out.println("The answer is correct!");
                                    send("correct");
                                } else {
                                    System.out.println("The answer is wrong!");
                                    send("wrong");
                                }
                            } catch (NullPointerException e) {
                                return;
                            }
                            System.out.println("Remained Questions: " + currentQuestion);
                            strLength = currentQuestion.length();
                            currentChance--;

                        }
                        send("end");

                        if (strLength != 0) {
                            send("gameover");
                            send(new Integer(score).toString());
                        } else {
                            score++;
                            send("win");
                            send(new Integer(score).toString());
                            System.out.println("User won!");

                        }
                        System.out.println("Session reseted!");
                    }
                }else if (in.compareTo("quit") == 0) {
                    send("quittrigger");
                    return;
                }
            }catch (NullPointerException e){
                System.err.println();
                System.err.println("======================================================================");
                System.err.println("Server has terminate the connection due to client not reachable issue!");
                System.err.println("======================================================================");
            }
        }catch(IOException e){
e.printStackTrace();
        }
    }

    /*
    Reference from:
    https://www.tutorialspoint.com/javaexamples/string_removing_char.htm
     */
    public static String removeCharAt(String s, int pos) {
        return s.substring(0, pos) + s.substring(pos + 1);
    }


    //debug
   /* public static void main(String[] args) {
        try{
            loadDictionary("Resources/words.txt");
        }catch (IOException e){
            System.err.println(e);
        }
    }*/
}
