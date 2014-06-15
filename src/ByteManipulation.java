import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

public class ByteManipulation {

    public static byte[] doubleToBytes(double value)
    {

        byte[] result = new byte[8];
        ByteBuffer.wrap(result).putDouble(value);
        return result;
    }
    
    public static double bytesToDouble(byte[] bytes) {
    	/*long bits = 0L;
    	for (int i = 0; i < 8; i++) {
    		bits |= (bytes[i] & 0xFFL) << (8 * i);
    		 or the other way around, depending on endianness
    	} */
    	return  ByteBuffer.wrap(bytes).getDouble();
    }

    public static String bytesToString(byte[] bytes) throws UnsupportedEncodingException {
            return new String(bytes,"UTF-8");
    }
}
