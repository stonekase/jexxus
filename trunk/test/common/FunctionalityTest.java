package common;

import jexxus.client.ClientConnection;
import jexxus.client.ClientConnectionListener;
import jexxus.common.Delivery;
import jexxus.server.Server;
import jexxus.server.ServerConnection;
import jexxus.server.ServerConnectionListener;

import org.junit.Assert;
import org.junit.Test;

public class FunctionalityTest implements ServerConnectionListener, ClientConnectionListener {

	String message = "This is a test of how well our compression works! This is a test of how well our compression works!"
			+ " This is a test of how well our compression works! This is a test of how well our compression works!"
			+ " This is a test of how well our compression works! This is a test of how well our compression works! This is a test "
			+ "of how well our compression works! This is a test of how well our compression works! This is a test of how well our co"
			+ "mpression works! This is a test of how well our compression works! This is a test of how well our compression works! ";
	String serverMessage = "Server: " + message;
	String clientMessage = "Client: " + message;

	@Test
	public void doTest() {
		int port = 3252;
		Server server;
		server = new Server(this, port);
		server.startServer();
		try {
			ClientConnection conn = new ClientConnection(this, "localhost", port);
			conn.connect();
			conn.send(clientMessage.getBytes(), Delivery.RELIABLE);
			conn.send(clientMessage.getBytes(), Delivery.RELIABLE);
			conn.send(clientMessage.getBytes(), Delivery.RELIABLE);
			conn.send(clientMessage.getBytes(), Delivery.RELIABLE);
			try {
				Thread.sleep(4000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} finally {
			server.shutdown(true);
		}

	}

	@Override
	public void clientConnected(ServerConnection connected) {
		// connected.send(serverMessage.getBytes(), Delivery.RELIABLE);
	}

	@Override
	public void clientDisconnected(ServerConnection disconnected, boolean forced) {

	}

	@Override
	public void receive(byte[] ret, ServerConnection sender) {
		Assert.assertArrayEquals(clientMessage.getBytes(), ret);
	}

	@Override
	public void connectionBroken(boolean forced) {

	}

	@Override
	public void receive(byte[] data) {
		Assert.assertArrayEquals(serverMessage.getBytes(), data);
	}
}
