package clientservercommunication;

import static org.mockito.Mockito.mock;
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

        server.shutdown(true);
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
