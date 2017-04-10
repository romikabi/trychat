package server;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.util.Enumeration;

/**
 * Created by romanabuzyarov on 10.04.17.
 */
public class Main {
    public static void main(String[] args)throws Exception{
        ServerSocket serverSocket = new ServerSocket(8080);
        while (true){
            Socket socket = serverSocket.accept();
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println(br.readLine());
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
