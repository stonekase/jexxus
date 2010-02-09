package jexxus.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

import jexxus.common.Connection;
import jexxus.common.ConnectionListener;
import jexxus.common.Delivery;
import jexxus.common.Encryption;
import jexxus.common.Encryption.Algorithm;

/**
 * Represents a server's connection to a client.
 * 
 * @author Jason
 * 
 */
public class ServerConnection extends Connection {

	private final Server controller;
	private final Socket socket;
	private final OutputStream tcpOutput;
	private final InputStream tcpInput;
	private boolean connected = true;
	private final String ip;
	private int udpPort = -1;
	private Encryption.Algorithm encryption;

	ServerConnection(Server controller, ConnectionListener listener, Socket socket) throws IOException {
		super(listener);

		this.controller = controller;
		this.socket = socket;
		this.ip = socket.getInetAddress().getHostAddress();
		tcpOutput = new BufferedOutputStream(socket.getOutputStream());
		tcpInput = new BufferedInputStream(socket.getInputStream());

		try {
			this.encryption = Encryption.createEncryptionAlgorithm(this);
		} catch (Exception e) {
			e.printStackTrace();
		}

		startTCPListener();
	}

	@Override
	protected Algorithm getEncryptionAlgorithm() {
		return encryption;
	}

	private void startTCPListener() {
		Thread t = new Thread(new Runnable() {
			public void run() {
				while (true) {
					try {
						byte[] ret = readTCP();
						listener.receive(ret, ServerConnection.this);
					} catch (SocketException e) {
						if (connected) {
							connected = false;
							controller.connectionDied(ServerConnection.this, false);
							listener.connectionBroken(ServerConnection.this, false);
						} else {
							controller.connectionDied(ServerConnection.this, true);
							listener.connectionBroken(ServerConnection.this, true);
						}
						break;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		t.setName("Jexxus-TCPSocketListener");
		t.start();
	}

	@Override
	public synchronized void send(byte[] data, Delivery deliveryType) {
		if (connected == false) {
			throw new RuntimeException("Cannot send message when not connected!");
		}
		if (deliveryType == Delivery.RELIABLE) {
			// send with TCP
			try {
				sendTCP(data);
			} catch (IOException e) {
				System.err.println("Error writing TCP data.");
				System.err.println(e.toString());
			}
		} else if (deliveryType == Delivery.UNRELIABLE) {
			controller.sendUDP(data, this);
		}
	}

	/**
	 * @return The IP of this client.
	 */
	public String getIP() {
		return ip;
	}

	/**
	 * Closes this connection to the client.
	 */
	public void exit() {
		connected = false;
		try {
			tcpInput.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			tcpOutput.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	InetAddress getAddress() {
		return socket.getInetAddress();
	}

	int getUDPPort() {
		return udpPort;
	}

	void setUDPPort(int port) {
		this.udpPort = port;
	}

	@Override
	public void close() {
		if (!connected) {
			throw new RuntimeException("Cannot close the connection when it is not connected.");
		} else {
			try {
				socket.close();
				tcpInput.close();
				tcpOutput.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			connected = false;
		}
	}

	@Override
	public boolean isConnected() {
		return connected;
	}

	@Override
	protected InputStream getTCPInputStream() {
		return tcpInput;
	}

	@Override
	protected OutputStream getTCPOutputStream() {
		return tcpOutput;
	}
}
