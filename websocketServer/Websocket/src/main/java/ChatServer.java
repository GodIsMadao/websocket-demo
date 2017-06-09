import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Iterator;

/**
 * Created by Saintyun on 2017/6/9.
 */
public class ChatServer extends WebSocketServer{

    public ChatServer() throws UnknownHostException {
    }

    public ChatServer(InetSocketAddress address) {
        super(address);
    }

    public ChatServer(InetSocketAddress address, int decoders) {
        super(address, decoders);
    }

    public ChatServer(int port) {
        this(new InetSocketAddress(port));
    }

    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        final String info = webSocket.getRemoteSocketAddress().getAddress().getHostAddress() + "接入!";
        showInfo(info);

        send2All(info);
        showInfo("发送：" + info);
    }

    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
        final String info = webSocket.getRemoteSocketAddress().getAddress().getHostAddress() + " 断开!，reason=" + s;
        showInfo(info);

        send2All(info);
        showInfo("发送：" + info);
    }

    public void onMessage(WebSocket webSocket, String s) {
        final String info = webSocket.getRemoteSocketAddress().getAddress().getHostAddress() + ", msg=>" + s;
        showInfo("读取：" + info);

        send2All(info);
        showInfo("发送：" + info);
    }

    public void onError(WebSocket webSocket, Exception e) {
        final String info = webSocket.getRemoteSocketAddress().getAddress().getHostAddress() + ", error=>" + e;

        showInfo(info);
    }

    /**
     * @param info
     */
    protected void send2All(String info) {
        Iterator<WebSocket> interator = connections().iterator();
        while(interator.hasNext()) {
            interator.next().send(info);
        }
    }

    protected void showInfo(String info) {
        System.out.println(info);
    }

    public static void main(String[] args) throws IOException {
        ChatServer server = new ChatServer(8889);
        server.start();

        BufferedReader buffer = new BufferedReader(new InputStreamReader(System.in));
        while(true){
            String msg = buffer.readLine();
//            这样就是群聊
            server.send2All(msg);
            server.showInfo("获取："+msg);
        }
    }
}
