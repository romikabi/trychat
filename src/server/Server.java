package server;

import java.io.*;
import java.net.*;
import java.util.Enumeration;

/**
 * Created by romanabuzyarov on 10.04.17.
 */
public class Server {
    public static void main(String[] args)throws Exception{

        ServerSocket serverSocket = new ServerSocket(8080);
        while (true){
            Socket socket = serverSocket.accept();
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter pw = new PrintWriter(socket.getOutputStream());
            System.out.println(br.readLine());
            pw.println("Done");
            socket.close();
        }
    }

    private static String myPublicIP(){
        try {
            URL url = new URL("http://bot.whatismyipaddress.com");
            String res;
            try(InputStreamReader is = new InputStreamReader(url.openStream());
                BufferedReader stream = new BufferedReader(is)){
                res = stream.readLine().trim();
            }
            if(res.length()>0)
                return res;
        }
        catch (MalformedURLException ex){

        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        return "";
    }
}
