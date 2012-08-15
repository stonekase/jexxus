package clientservercommunication;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;

import jexxus.client.ClientConnection;
import jexxus.common.Connection;
import jexxus.common.ConnectionListener;
import jexxus.common.Delivery;
import jexxus.server.Server;
import jexxus.server.ServerConnection;

import org.junit.Assert;
import org.junit.Test;

public class TestClientServerCommunication
{
    private final static int PORT = 50163;

    @Test
    public void clientsSendMessagesToServer() throws Exception
    {
        ConnectionListener clientConnectionListenerMock = mock(ConnectionListener.class);
        ConnectionListener serverConnectionListenerMock = mock(ConnectionListener.class);

        Server server = new Server(serverConnectionListenerMock, PORT);
        server.startServer();

        final int NR_OF_CLIENTS = 100;

        for( int i = 0; i < NR_OF_CLIENTS; i++ )
        {
            ClientConnection clientConnection = new ClientConnection(clientConnectionListenerMock, "localhost", PORT);
            clientConnection.connect();
            clientConnection.send("message1".getBytes(), Delivery.RELIABLE);
            clientConnection.send("message2".getBytes(), Delivery.RELIABLE);
        }

        final int NR_OF_MESSAGES_SEND_PER_CLIENT = 2;
        final int expectedRecievedMessages = NR_OF_CLIENTS * NR_OF_MESSAGES_SEND_PER_CLIENT;
        verify(serverConnectionListenerMock, times(expectedRecievedMessages)).receive(any(byte[].class),
                any(ServerConnection.class));

        server.shutdown();
    }

    @Test
    public void clientsConnectToServer() throws Exception
    {
        ConnectionListener clientConnectionListenerMock = mock(ConnectionListener.class);
        ConnectionListener serverConnectionListenerMock = mock(ConnectionListener.class);

        Server server = new Server(serverConnectionListenerMock, PORT);
        server.startServer();

        final int NR_OF_CLIENTS = 100;

        for( int i = 0; i < NR_OF_CLIENTS; i++ )
        {
            ClientConnection clientConnection = new ClientConnection(clientConnectionListenerMock, "localhost", PORT);
            clientConnection.connect();
        }

        verify(serverConnectionListenerMock, times(NR_OF_CLIENTS)).clientConnected(any(ServerConnection.class));

        server.shutdown();
    }

    @Test
    public void clientDisconnectsFromServer() throws Exception
    {
        ConnectionListener clientConnectionListenerMock = mock(ConnectionListener.class);
        ConnectionListener serverConnectionListenerMock = mock(ConnectionListener.class);

        Server server = new Server(serverConnectionListenerMock, PORT);
        server.startServer();

        ClientConnection clientConnection = new ClientConnection(clientConnectionListenerMock, "localhost", PORT);
        clientConnection.connect();
        clientConnection.send("a message".getBytes(), Delivery.RELIABLE);
        clientConnection.close();

        Thread.sleep(100);

        boolean isConnected = clientConnection.isConnected();
        Assert.assertFalse(isConnected);

        verify(serverConnectionListenerMock, times(1)).connectionBroken(any(ServerConnection.class), anyBoolean());

        server.shutdown();
    }

    @Test
    public void serverKnowsIpFromClients() throws Exception
    {
        ConnectionListener clientConnectionListenerMock = mock(ConnectionListener.class);
        MyConnectionListener serverConnectionListener = new MyConnectionListener();

        Server server = new Server(serverConnectionListener, PORT);
        server.startServer();

        ClientConnection clientConnection = new ClientConnection(clientConnectionListenerMock, "localhost", PORT);
        clientConnection.connect();
        clientConnection.send("a message".getBytes(), Delivery.RELIABLE);
        clientConnection.close();

        Thread.sleep(100);

        Assert.assertEquals(serverConnectionListener.mIpFromConnectingClient,
                serverConnectionListener.mIpFromSendingClient);
        Assert.assertEquals(serverConnectionListener.mIpFromSendingClient, serverConnectionListener.mIpFromLostClient);

        server.shutdown();
    }

    @Test
    public void reuseServer() throws Exception
    {
        ConnectionListener clientConnectionListenerMock = mock(ConnectionListener.class);
        ConnectionListener serverConnectionListenerMock = mock(ConnectionListener.class);

        Server server = new Server(serverConnectionListenerMock, PORT);

        final int NR_OF_SERVER_REUSES = 10;

        for( int i = 0; i < NR_OF_SERVER_REUSES; i++ )
        {
            server.startServer();

            ClientConnection clientConnection = new ClientConnection(clientConnectionListenerMock, "localhost", PORT);
            clientConnection.connect();
            clientConnection.send("a message".getBytes(), Delivery.RELIABLE);
            boolean isClientConnected = clientConnection.isConnected();
            Assert.assertTrue(isClientConnected);

            Thread.sleep(200);

            List<Connection> connectedClients = server.getClients();
            Assert.assertEquals(1, connectedClients.size());

            server.shutdown();

            int nr_of_times_clients_connected_and_sent_msg = i + 1;

            verify(serverConnectionListenerMock, times(nr_of_times_clients_connected_and_sent_msg)).clientConnected(
                    any(ServerConnection.class));
            verify(serverConnectionListenerMock, times(nr_of_times_clients_connected_and_sent_msg)).receive(
                    any(byte[].class), any(ServerConnection.class));
        }
    }

    class MyConnectionListener
            implements ConnectionListener
    {
        public String mIpFromConnectingClient = "";
        public String mIpFromSendingClient    = "";
        public String mIpFromLostClient       = "";

        @Override
        public void connectionBroken( Connection pBroken, boolean pForced )
        {
            mIpFromLostClient = pBroken.getIP();
        }

        @Override
        public void receive( byte[] pData, Connection pFrom )
        {
            mIpFromSendingClient = pFrom.getIP();
        }

        @Override
        public void clientConnected( ServerConnection pConn )
        {
            mIpFromConnectingClient = pConn.getIP();
        }

    }
}
