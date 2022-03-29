package git.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Collections;

class ClientEcho {

    private String host;
    private int port;
    private PrintWriter out;
    private BufferedReader in;
    private ArrayList<String> clientTimeTable;
    private int number;

    ClientEcho(String h, int p, ArrayList<String> tTable) {
        host = h;
        port = p;
        clientTimeTable = tTable;
        out = null;
        in = null;
    }

    void initBuffers(Socket s) throws IOException {
        out = new PrintWriter(s.getOutputStream(), true);
        InputStreamReader ins = new InputStreamReader(s.getInputStream());
        in = new BufferedReader(ins);
    }

    void communicateWithServerAddToTimeTable(PrintWriter out, BufferedReader in) throws IOException {
        Scanner scan = new Scanner(System.in);

        String line = null;
        line = scan.nextLine();

        out.println(line);
        out.flush();

        String data = in.readLine();
        System.out.println("An hour has been added to the schedule: " + data);

        if(!data.equals("Error(can not add) - visit not exist"))
            clientTimeTable.add(data);
        // scan.close();
    }

    void communicateWithServerDeleteFromTimeTable(PrintWriter out, BufferedReader in) throws IOException {
        Scanner scan = new Scanner(System.in);
        String line = null;
        line = scan.nextLine();

        out.println(line);
        out.flush();

        String data = in.readLine();
        System.out.println("An hour has been removed from the schedule: " + data);

        if(!data.equals("Error(can not delete) - visit not exist"))
            clientTimeTable.remove(data);
        // scan.close();
    }

    void showSchedule(BufferedReader in) throws IOException {
        String schedule = in.readLine();
        StringBuilder sb = new StringBuilder(schedule);

        sb.deleteCharAt(schedule.length()-1);
        sb.deleteCharAt(0);

        String output = sb.toString();
        String newOutput = output.replaceAll(", ", "\n");

        String[] timeTable = newOutput.split("\n");
        for (String s : timeTable) {
            System.out.println(s);
        }
    }

    void showClientTimeTable() {
        if(clientTimeTable.isEmpty())
            System.out.println("Empty timetable!");
        else {
            Collections.sort(clientTimeTable);
            for (String s : clientTimeTable) {
                System.out.println(s);
            }
        }
    }

    boolean checkOption(int number) throws IOException{
        switch(number) {
            case 0:
                System.out.println("Enter the date to be reported: ");
                out.println(number);
                this.communicateWithServerAddToTimeTable(out, in);
                return false;
            case 1:
                if(clientTimeTable.isEmpty()) {
                    System.out.println("Your timetable is empty! Enter your option again.");
                    return false;
                }
                else
                    System.out.println("Enter the date to be removed: ");
                    out.println(number);
                    this.communicateWithServerDeleteFromTimeTable(out, in);
                    return false;
            case 2:
                System.out.println("Whole schedule (free hours): ");
                out.println(number);
                showSchedule(in);
                return false;
            case 3:
                out.println(number);
                showClientTimeTable();
                return false;
            case 4:
                out.println(number);
                return true;
            default:
                System.out.println("Enter the correct option!");
                return false;
        }
    }

    void connectWithServer()
    {
        try{
            Socket s = new Socket(host, port);

            this.initBuffers(s);

            System.out.println("Salon schedule: ");
            showSchedule(in);

            Scanner scan = new Scanner(System.in);
            boolean check = false;
            number = 0;

            while(!check) {
                System.out.println("What you want to do " +
                        "[0 - add a visit, 1 - remove a visit, 2 - refresh schedule, 3 - show your schedule, 4 - exit]: ");
                number = scan.nextInt();
                check = checkOption(number);
            }

            System.out.println("Your schedule: ");
            Collections.sort(clientTimeTable);
            System.out.println(clientTimeTable);

            scan.close();
            s.close();
        }
        catch(UnknownHostException e1)
        {
            System.out.println("Unknown host - error");
        }
        catch (IOException e2)
        {
            System.out.println("IO - error");
        }
    }
}

public class Client
{
    public static void main(String[] args)
    {
        String host = "127.0.0.1"; //standardowy lokalny adres
        int port = 4000;
        ArrayList<String> schedule = new ArrayList<String>();
        ClientEcho var = new ClientEcho(host, port, schedule);
        var.connectWithServer();
        System.out.println("Disabling a client...");
    }
}