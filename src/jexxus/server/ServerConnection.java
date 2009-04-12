package jexxus.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

import jexxus.common.ByteUtils;
import jexxus.common.Connection;
import jexxus.common.Delivery;

/**
 * Represents a server's connection to a client.
 * 
 * @author Jason
 * 
 */
public class ServerConnection implements Connection {

	private final Server controller;
	private final ServerConnectionListener listener;
	private final Socket socket;
	private final OutputStream tcpOutput;
	private final InputStream tcpInput;
	private final byte[] headerInput = new byte[4];
	private final byte[] headerOutput = new byte[4];
	private boolean connected = true;
	private final String ip;
	private int udpPort = -1;

	ServerConnection(Server controller, ServerConnectionListener listener,
			Socket socket) throws IOException {
		this.controller = controller;
		this.listener = listener;
		this.socket = socket;
		this.ip = socket.getInetAddress().getHostAddress();
		tcpOutput = new BufferedOutputStream(socket.getOutputStream());
		tcpInput = new BufferedInputStream(socket.getInputStream());
		startTCPListener();
	}

	private void startTCPListener() {
		Thread t = new Thread(new Runnable() {
			public void run() {
				while (true) {
					try {
						tcpInput.read(headerInput);
						int len = ByteUtils.unpack(headerInput);
						byte[] ret = new byte[len];
						int count = 0;
						while (count < len) {
							count += tcpInput.read(ret, count, len - count);
						}
						listener.receive(ret, ServerConnection.this);
					} catch (IOException e) {
						if (connected) {
							connected = false;
							controller.connectionDied(ServerConnection.this,
									false);
						} else {
							controller.connectionDied(ServerConnection.this,
									true);
						}
						break;
					}
				}
			}
		});
		t.start();
	}

	@Override
	public synchronized void send(byte[] data, Delivery deliveryType) {
		if (connected == false) {
			System.err.println("Cannot send message when not connected!");
			return;
		}
		if (deliveryType == Delivery.RELIABLE) {
			// send with TCP
			ByteUtils.pack(data.length, headerOutput);
			try {
				tcpOutput.write(headerOutput);
				tcpOutput.write(data);
				tcpOutput.flush();
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
		if (connected) {
			System.err
					.println("Cannot close the connection when it is not connected.");
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

	public String getTCPPort() {
		// TODO Auto-generated method stub
		return null;
	}
}
