package jexxus.common;

/**
 * Represents a connection between two computers.
 * 
 * @author Jason
 * 
 */
public interface Connection {

	/**
	 * Sends the given data over this connection.
	 * 
	 * @param data
	 *            The data to send to the other computer.
	 * @param deliveryType
	 *            The requirements for the delivery of this data.
	 */
	public void send(byte[] data, Delivery deliveryType);

}
