package jexxus.common;

/**
 * Represents a connection between two computers.
 * 
 * @author Jason
 * 
 */
public interface Connection {

	/**
	 * Checks to see whether the current connection is open.
	 * 
	 * @return True if the connection is established.
	 */
	public boolean isConnected();

	/**
	 * Sends the given data over this connection.
	 * 
	 * @param data
	 *            The data to send to the other computer.
	 * @param deliveryType
	 *            The requirements for the delivery of this data.
	 */
	public void send(byte[] data, Delivery deliveryType);

	/**
	 * Closes the connection. Further data may not be transfered across this
	 * link.
	 */
	public void close();

}
