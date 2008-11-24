package jexxus.common;

/**
 * Contains some functions for manipulating data into bytes and data back to
 * bytes.
 * 
 * @author Jason
 * 
 */
public class ByteUtils {

	/**
	 * Puts the given integer into the first 4 bytes of the buffer.
	 * 
	 * @param b
	 *            The integer to convert.
	 * @param buf
	 *            The buffer to storge the 4 bytes.
	 */
	public static void pack(int b, byte[] buf) {
		buf[0] = (byte) (b >>> 24);
		buf[1] = (byte) (b >>> 16);
		buf[2] = (byte) (b >>> 8);
		buf[3] = (byte) (b);
	}

	/**
	 * Converts the first 4 bytes of the array to an integer.
	 * 
	 * @param b
	 *            The byte buffer.
	 * @return An integer from the first 4 bytes.
	 */
	public static int unpack(byte[] b) {
		return (b[0] << 24) + ((b[1] & 0xFF) << 16) + ((b[2] & 0xFF) << 8)
				+ (b[3] & 0xFF);
	}

}
