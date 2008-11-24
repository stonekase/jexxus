package jexxus.common;

/**
 * Used for reading many forms of data. This is useful for reading objects from
 * over a network or from a file which have been previously serialized.
 * 
 * @author Jason
 * 
 */
public class ByteReader {

	private final byte[] b;
	private int c = 0;

	/**
	 * Constructs a new reader for the given data.
	 * 
	 * @param data
	 *            The data to read from.
	 */
	public ByteReader(byte[] data) {
		this.b = data;
	}

	/**
	 * Checks to see if all the data has been read from this reader.
	 * 
	 * @return False if there is still unread data.
	 */
	public boolean isDone() {
		return c >= b.length;
	}

	/**
	 * @return The next byte.
	 */
	public byte readByte() {
		return b[c++];
	}

	/**
	 * @return The next short.
	 */
	public short readShort() {
		return (short) (((b[c++]) << 8) + (b[c++] & 0xFF));
	}

	/**
	 * @return The next int.
	 */
	public int readInt() {
		return (b[c++] << 24) + ((b[c++] & 0xFF) << 16)
				+ ((b[c++] & 0xFF) << 8) + (b[c++] & 0xFF);
	}

	/**
	 * @return The next float.
	 */
	public float readFloat() {
		int i = readInt();
		return Float.intBitsToFloat(i);
	}

	/**
	 * @return The next long.
	 */
	public long readLong() {
		return ((((long) b[c++]) & 0xFF) + ((((long) b[c++]) & 0xFF) << 8)
				+ ((((long) b[c++]) & 0xFF) << 16)
				+ ((((long) b[c++]) & 0xFF) << 24)
				+ ((((long) b[c++]) & 0xFF) << 32)
				+ ((((long) b[c++]) & 0xFF) << 40)
				+ ((((long) b[c++]) & 0xFF) << 48) + ((((long) b[c++]) & 0xFF) << 56));
	}

	/**
	 * @return The next double.
	 */
	public double readDouble() {
		long t = readLong();
		return Double.longBitsToDouble(t);
	}

	/**
	 * @return The next String.
	 */
	public String readString() {
		short len = readShort();
		String ret = new String(b, c, len);
		c += len;
		return ret;
	}

}
