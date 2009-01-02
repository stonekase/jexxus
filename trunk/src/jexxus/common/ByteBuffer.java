package jexxus.common;

import java.util.Arrays;

/**
 * Used for storing many forms of data into a single array of bytes. This is
 * useful for serializing objects to send them over the network or save them as
 * a file.
 * 
 * @author Jason
 * 
 */
public class ByteBuffer {

	private byte[] data;
	private int size = 0;

	/**
	 * Constructs a new buffer.
	 */
	public ByteBuffer() {
		this(16);
	}

	/**
	 * Constructs a new buffer.
	 * 
	 * @param initialCapactiy
	 *            The initial capacity of the underlying byte array.
	 */
	public ByteBuffer(int initialCapacity) {
		data = new byte[initialCapacity];
	}

	/**
	 * @return The underlying buffer which contains any data which was added.
	 */
	public byte[] getBuffer() {
		return data;
	}

	/**
	 * Adds all of the data from the given buffer to this one.
	 * 
	 * @param buffer
	 *            The buffer to transfer data from.
	 */
	public void addBuffer(ByteBuffer buffer) {
		addBytes(buffer.data, 0, buffer.size);
	}

	/**
	 * Adds all of the data from the given array to this one.
	 * 
	 * @param bytes
	 *            The array to transfer data from.
	 */
	public void addBytes(byte[] bytes) {
		addBytes(bytes, 0, bytes.length);
	}

	/**
	 * Adds all of the data from the given array to this one.
	 * 
	 * @param bytes
	 *            The buffer to transfer data from.
	 * @param offset
	 *            The offset to start adding bytes from.
	 * @param len
	 *            The number of bytes to add from the given array.
	 */
	public void addBytes(byte[] bytes, int offset, int len) {
		for (int i = offset; i < offset + len; i++) {
			add(bytes[i]);
		}
	}

	/**
	 * Adds the given byte to the underlying buffer.
	 * 
	 * @param b
	 *            The byte to add.
	 */
	public void add(byte b) {
		if (size == data.length)
			resize();
		data[size++] = b;
	}

	/**
	 * Adds the given short to the underlying buffer.
	 * 
	 * @param b
	 *            The short to add.
	 */
	public void addShort(short b) {
		add((byte) (b >>> 8));
		add((byte) (b));
	}

	/**
	 * Adds the given short to the underlying buffer.
	 * 
	 * @param b
	 *            The short to add.
	 */
	public void addInt(int b) {
		add((byte) (b >>> 24));
		add((byte) (b >>> 16));
		add((byte) (b >>> 8));
		add((byte) (b));
	}

	/**
	 * Adds the given float to the underlying buffer.
	 * 
	 * @param b
	 *            The float to add.
	 */
	public void addFloat(float f) {
		int i = Float.floatToRawIntBits(f);
		addInt(i);
	}

	/**
	 * Adds the given double to the underlying buffer.
	 * 
	 * @param b
	 *            The double to add.
	 */
	public void addDouble(double d) {
		long t = Double.doubleToRawLongBits(d);
		addLong(t);
	}

	/**
	 * Adds the given long to the underlying buffer.
	 * 
	 * @param b
	 *            The long to add.
	 */
	public void addLong(long b) {
		add((byte) b);
		b >>>= 8;
		add((byte) b);
		b >>>= 8;
		add((byte) b);
		b >>>= 8;
		add((byte) b);
		b >>>= 8;
		add((byte) b);
		b >>>= 8;
		add((byte) b);
		b >>>= 8;
		add((byte) b);
		b >>>= 8;
		add((byte) b);
		b >>>= 8;
	}

	/**
	 * Adds the given String to the underlying buffer.<br>
	 * <br>
	 * This will only work for strings smaller than 32,767 bytes.
	 * 
	 * @param b
	 *            The String to add.
	 */
	public void addString(String s) {
		addShort((short) s.length());
		addBytes(s.getBytes());
	}

	private void resize() {
		byte[] copy = new byte[data.length * 2];
		for (int i = 0; i < data.length; i++) {
			copy[i] = data[i];
		}
		data = copy;
	}

	/**
	 * @return The amount of bytes which have been used in the underlying
	 *         buffer.
	 */
	public int size() {
		return size;
	}

	/**
	 * Turns this buffer into an equivalent byte array.
	 */
	public byte[] toByteArray() {
		return Arrays.copyOf(data, size);
	}

	/**
	 * Resets this buffer so that it contains no data.
	 */
	public void reset() {
		size = 0;
	}

}
