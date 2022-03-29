package git.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;

class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private int numberOfClient;
    ArrayList<String> timeTable;

    ClientHandler(Socket socket, int number, ArrayList<String> tTable) {
        clientSocket = socket;
        numberOfClient = number;
        timeTable = tTable;
        out = null;
        in = null;
    }

    public int getNumberOfClient() {
        return numberOfClient;
    }

    void initBuffers() throws IOException {
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        InputStreamReader ins = new InputStreamReader(clientSocket.getInputStream());
        in = new BufferedReader(ins);
    }

    void takeAndSendDataWhenAdd() throws IOException {
        String data = in.readLine();
        if(!timeTable.contains(data)) {
            System.out.println("Visit not exist");
            out.println("Error(can not add) - visit not exist");
            out.flush();
        }
        else {
            System.out.println("Visit given by a client " + this.getNumberOfClient() + " to add: " + data);
            timeTable.remove(data);
            out.println(data);

            System.out.println("----DELETE FROM SCHEDULE----");
            Collections.sort(timeTable);
            System.out.println(timeTable);
        }
    }

    void takeAndSendDataWhenDelete() throws IOException {
        String data = in.readLine();
        if(timeTable.contains(data)) {
            System.out.println("Visit not exist");
            out.println("Error(can not delete) - visit not exist");
            out.flush();
        }
        else {
            System.out.println("Visit given by a client " + this.getNumberOfClient() + " to remove: " + data);

            timeTable.add(data);
            out.println(data);

            System.out.println("----ADD TO SCHEDULE----");
            Collections.sort(timeTable);
            System.out.println(timeTable);
        }
    }

    void showTimeTable() {
        out.println(timeTable);
    }

    ArrayList<String> getTimeTable() {
        return timeTable;
    }

    public void run() {
        try {

            this.initBuffers();

            String number = null;
            while ((number = in.readLine()) != null) {
                if(number.equals("4"))
                    break;
                switch(number) {
                    case "0":
                        this.takeAndSendDataWhenAdd();
                        break;
                    case "1":
                        this.takeAndSendDataWhenDelete();
                        break;
                    case "2":
                        this.showTimeTable();
                        break;
                    case "3":
                        break;
                }
            }

        } catch (IOException e1) {
            System.out.println("Error IO");
        } finally {
            try {
                if (out != null)
                    out.close();
                if (in != null)
                    in.close();
                System.out.println("Client " + this.getNumberOfClient() + " closed.");
                clientSocket.close();
            } catch (IOException e1) {
                System.out.println("Error IO");
            }
        }
    }
}

class ServerHandler {
    private int port;
    private ServerSocket server;
    private int numberOfClient;
    static ArrayList<String> tTable;

    ServerHandler(int p, ArrayList<String> timeTable) {
        port = p;
        numberOfClient = 0;
        server = null;
        tTable = timeTable;
    }

    void addNewClient() {
        numberOfClient++;
    }

    int getNumberOfClient() {
        return numberOfClient;
    }

    ArrayList<String> gettTable() {
        return tTable;
    }

    void serverLaunch() {
        try {
            server = new ServerSocket(port);
            while (true) {
                Socket client = server.accept();
                this.addNewClient();
                System.out.println("A new client joined the salon: " + "Client " + this.getNumberOfClient());

                PrintWriter sendHarmonogram = new PrintWriter(client.getOutputStream(), true);
                sendHarmonogram.println(tTable);

                ClientHandler clientThread = new ClientHandler(client, this.getNumberOfClient(), tTable);
                new Thread(clientThread).start();
            }
        } catch (IOException e) {
            System.out.println("Error IO from server");
        } finally {
            if (server != null) {
                try {
                    server.close();
                } catch (IOException e1) {
                    System.out.println("Error IO from server/while closing");
                }
            }
        }
    }
}

public class Server {
    ArrayList<String> timetable;

    void putRecordsIntoArrayList() {
        timetable.add("10-11");
        timetable.add("11-12");
        timetable.add("12-13");
        timetable.add("13-14");
        timetable.add("14-15");
        timetable.add("15-16");
        timetable.add("16-17");
        timetable.add("17-18");
    }

    void showTimetable() {
        for (int i = 0; i < timetable.size(); i++) {
            System.out.println(timetable.get(i));
        }
    }

    ArrayList<String> getTimeTable() {
        return timetable;
    }

    public static void main(String[] args) {

        System.out.println("Server - start");
        int port = 4000;

        Server server = new Server();
        server.timetable = new ArrayList<String>();
        server.putRecordsIntoArrayList();

        System.out.println("\nAvailable salon hours: ");
        server.showTimetable();
        System.out.println();

        ServerHandler var = new ServerHandler(port, server.timetable);
        var.serverLaunch();

    }
}