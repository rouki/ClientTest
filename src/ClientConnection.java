import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: roee
 * Date: 5/2/14
 * Time: 9:26 AM
 * To change this template use File | Settings | File Templates.
 */

public class ClientConnection extends  Thread{
    private Socket socket;
    private InputStream is ;
    private OutputStream os  ;
    private static final int bufferSize = 256;

    public ClientConnection(Socket socket)
    {
        this.socket = socket;

        try {
            is = socket.getInputStream();
            os = socket.getOutputStream();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    public void turnOffSocket() {
        socket = null;
    }

    public ClientConnection(Socket socket, String id)
    {
        this.socket = socket;
        try {
            is = socket.getInputStream();
            os = socket.getOutputStream();
            DataOutputStream dis = new DataOutputStream(os);
        }
        catch(IOException e)
        {
            socket = null;
        }
    }

    public void sendMsg(byte[] bytes)
    {
        try {
            DataOutputStream oos = new DataOutputStream(os);
            oos.write(bytes);
        }
        catch(IOException e)
        {

        }
    }

    public PackageDetails parseLine(String line) {

        String[] sepDetails = line.split(",");

        String toId = sepDetails[1];
        double lat = Double.parseDouble(sepDetails[2]);
        double lng = Double.parseDouble(sepDetails[3]);
        int numberOfFiles = Integer.parseInt(sepDetails[4]);
        String[] files = Arrays.copyOfRange(sepDetails, 5, 5 + numberOfFiles);

        return new PackageDetails(toId, lat, lng, files);
    }

    public void run () {

        DataInputStream dis = new DataInputStream(is);
        DataOutputStream dos = new DataOutputStream(os);
        byte buffer[] = new byte[bufferSize];
        List<String> lineList = null;
        try {
            Path filePath = new File("test").toPath();
            lineList = Files.readAllLines(filePath, Charset.defaultCharset());
        }catch(IOException e) {

        }
        boolean first = true;
        for (String line : lineList) {
            try {
                if (first) {
                    System.out.println(line);
                    byte[] idBytes = line.getBytes();
                    System.arraycopy(idBytes, 0, buffer, 0, idBytes.length);
                    buffer[idBytes.length] = '\\';
                    dos.write(buffer);
                    first = false;
                } else {
                    if (line.charAt(0) == 'p') {
                        PackageDetails details = parseLine(line);
                        byte[] toIdBytes = details.to().getBytes();
                        buffer[0] = 'p';
                        System.arraycopy(toIdBytes, 0, buffer, 1, toIdBytes.length);
                        buffer[toIdBytes.length + 1] ='\\';
                        dos.write(buffer);
                        byte[] latBytes = ByteManipulation.doubleToBytes(details.lat());
                        byte[] lngBytes = ByteManipulation.doubleToBytes(details.lng());
                        System.arraycopy(latBytes, 0, buffer, 0, latBytes.length);
                        dos.write(buffer);
                        System.arraycopy(lngBytes, 0, buffer, 0, lngBytes.length);
                        dos.write(buffer);
                        buffer[0] = (byte)details.fileList().length;
                        dos.write(buffer);
                        Vector<RandomAccessFile> files = new Vector<RandomAccessFile>();
                        for (String fileName : details.fileList()) {
                            System.out.println(fileName);
                            RandomAccessFile raf = new RandomAccessFile(fileName,"rw");
                            files.add(raf);
                            byte[] fileNameBytes = fileName.getBytes();
                            System.arraycopy(fileNameBytes, 0, buffer, 0, fileNameBytes.length);
                            buffer[fileNameBytes.length] = '\\';
                            long fileSize = raf.length();
                            byte[] fileSizeBytes = ByteBuffer.allocate(8).putLong(fileSize).array();
                            System.arraycopy(fileSizeBytes, 0, buffer, fileNameBytes.length + 1, fileSizeBytes.length);
                            buffer[fileSizeBytes.length + fileNameBytes.length + 1] = '\\';
                            dos.write(buffer);
                        }
                        dis.read(buffer);
                        if (buffer[0] == 'd') {
                            for (RandomAccessFile raf : files) {
                                long fileSize = raf.length();
                                byte[] buffer2 = new byte[bufferSize];
                                while (fileSize > 0) {
                                    int bytesRead = raf.read(buffer, 0, 120);
                                    buffer2[0] = (byte) bytesRead;
                                    System.arraycopy(buffer, 0, buffer2, 1, bytesRead);
                                    dos.write(buffer2);
                                    fileSize -= bytesRead;
                                }
                                dis.read(buffer);
                                System.out.println("Here... ");
                                System.out.println((char)buffer[0]);
                                if (buffer[0] == 'd')  {
                                    System.out.println("DONE!");
                                    continue;
                                }
                            }
                        }

                    }
                }
            } catch(IOException e) {

            }
        }
    }

}
