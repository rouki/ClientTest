import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created with IntelliJ IDEA.
 * User: roee
 * Date: 5/2/14
 * Time: 8:06 AM
 * To change this template use File | Settings | File Templates.
 */
public class Client {

        private ClientConnection connection;

        public ClientConnection getConnection() { return connection; }

        public void connect(String serverName, int port)
        {
            try {
                Socket socket = new Socket(serverName,port);
                connection = new ClientConnection(socket);
                connection.start();
            }
            catch(IOException e)
            {
            }
        }

        public void send(byte[] bytes)
        {
            connection.sendMsg(bytes);
        }

        public static void main(String[] args)
        {
            Client c1 = new Client();
            c1.connect("127.0.0.1",1300);

        }

}