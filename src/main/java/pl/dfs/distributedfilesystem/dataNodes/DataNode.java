package pl.dfs.distributedfilesystem.dataNodes;

import org.springframework.beans.factory.BeanCreationException;

import java.io.*;
import java.net.*;

public class DataNode {

    public DataNode(String server,int port) throws BeanCreationException {
        this.server = server;
        this.port = port;

        try {
            InetAddress addr = InetAddress.getByName(server);
            SocketAddress sockaddr = new InetSocketAddress(addr, port);
            socket = new Socket();

            int timeout = 2000;
            socket.connect(sockaddr, timeout);
            socket.setTcpNoDelay(true);


         //   outputStream = socket.getOutputStream();
         //   inputStream = socket.getInputStream();

            bufferedOutputStream = new BufferedOutputStream(socket.getOutputStream());
            bufferedInputStream = new BufferedInputStream(socket.getInputStream());


        } catch (Exception e) {
            throw new BeanCreationException("Cannot create socket");
        }
    }

    public int writeString(String content) {
        try {
            byte[] bytes = content.getBytes();
            int count = content.length();
            bufferedOutputStream.write(bytes,0, count);
        } catch (Exception e){
            return -1;
        }
        return 0;
    }
    public int writeFile(File file) {
        try {
            FileInputStream fileStream = null;
            fileStream = new FileInputStream(file);
            byte[] bytes = new byte[10 * 1024 * 1024];
            int count;
            try {
                while ((count = fileStream.read(bytes)) > 0) {
                    try {
                        bufferedOutputStream.write(bytes, 0, count);
                    } catch (IOException e5) {
                        System.out.println("e5");
                    }
                }
            }
            catch (IOException e4) {
                System.out.println("e4");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }



        return 0;
    }
    public String readRespnse(){
        byte[] bytes = new byte[10 * 1024 * 1024];
        try {
            int count = bufferedInputStream.read(bytes);
            String response = new String(bytes).substring(0,count);
            return response;
        } catch (Exception e) {

        }
        return null;
    }

    public void writeFlush(){
        try {
            bufferedOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private BufferedOutputStream bufferedOutputStream;
    private BufferedInputStream bufferedInputStream;

   // private OutputStream outputStream;
   // private BufferedOutputStream bufferedOutputStream;
   // private InputStream inputStream;
    private Socket socket;
    private String server;
    private int port;

}
