package clientservercommunication;


import static org.mockito.Mockito.mock;

import java.io.IOException;

import jexxus.client.ClientConnection;
import jexxus.common.ConnectionListener;
import jexxus.server.Server;

import org.junit.Test;

public class TestClient
{
    private final static int PORT = 50163;

    @Test(expected = IOException.class)
    public void clientConnectNoSsl() throws Exception
    {
        ConnectionListener clientConnectionListenerMock = mock(ConnectionListener.class);

        ClientConnection clientConnection = new ClientConnection(clientConnectionListenerMock, "localhost", PORT, false);
        clientConnection.connect();
    }

    @Test(expected = IOException.class)
    public void clientConnectNoServer() throws Exception
    {
        ConnectionListener clientConnectionListenerMock = mock(ConnectionListener.class);

        ClientConnection clientConnection = new ClientConnection(clientConnectionListenerMock, "localhost", PORT);
        clientConnection.connect();
    }

    @Test(expected = IOException.class)
    public void clientConnectServerNotStartedYet() throws Exception
    {
        ConnectionListener clientConnectionListenerMock = mock(ConnectionListener.class);
        ConnectionListener serverConnectionListenerMock = mock(ConnectionListener.class);

        Server server = new Server(serverConnectionListenerMock, PORT);

        ClientConnection clientConnection = new ClientConnection(clientConnectionListenerMock, "localhost", PORT);
        clientConnection.connect();

        server.shutdown();
    }
}
