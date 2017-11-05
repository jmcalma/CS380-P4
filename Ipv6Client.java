import java.net.Socket;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

public class Ipv6Client {

    public static void main(String[] args) {
        try(Socket socket = new Socket("18.221.102.182", 38004)) {
            InputStream is = socket.getInputStream();
            OutputStream os = socket.getOutputStream();
            String address = socket.getInetAddress().getHostAddress();
            byte packet[] = new byte[40];

            for(int i = 1; i <= 12; i++) {
                int dataLength = (int) Math.pow(2, i);
                byte firstHalf = (byte) ((dataLength >> 8) & 0xFF);
                byte secondHalf = (byte) (dataLength & 0xFF);

                //version
                packet[0] = (byte) 96;
                //payload length
                packet[4] = firstHalf;
                packet[5] = secondHalf;
                //next header
                packet[6] = (byte) 17;
                //hop limit
                packet[7] = (byte) 20;
                //source address
                packet[18] = (byte) 255;
                packet[19] = (byte) 255;
                packet[20] = (byte) 127;
                packet[23] = (byte) 1;
                //destination address
                String[] destAddr = address.split("\\.");
                int destination[] = new int[4];
                for(int j = 0; j < destAddr.length; j++) {
                    int val = Integer.valueOf(destAddr[j]);
                    destination[j] = val;
                }
                packet[34] = (byte) 255;
                packet[35] = (byte) 255;
                packet[36] = (byte) destination[0];
                packet[37] = (byte) destination[1];
                packet[38] = (byte) destination[2];
                packet[39] = (byte) destination[3];

                for(byte b : packet) {
                    os.write(b);
                }
                System.out.println("data length: " + dataLength);
                for(int j = 0; j < dataLength; j++) {
                    os.write(0);
                }
                System.out.print("Response: 0x");
                for(int j = 0; j < 4; j++) {
                    System.out.printf("%02X", is.read());
                }
                System.out.println("\n");
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}