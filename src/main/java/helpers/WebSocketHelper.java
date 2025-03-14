package helpers;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Slf4j
public class WebSocketHelper extends WebSocketClient {
    @Getter
    private final List<String> messages = new ArrayList<>();
    private final CountDownLatch connectionLatch = new CountDownLatch(1);

    public WebSocketHelper(String address) throws URISyntaxException {
        super(new URI("ws://" + address + "/ws"));
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        log.info("WebSocket connection opened");
        connectionLatch.countDown();
    }

    @Override
    public void onMessage(String message) {
        log.info("Received message: {}", message);
        messages.add(message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        log.info("WebSocket connection closed");
    }

    @Override
    public void onError(Exception ex) {
        log.error("WebSocket error: {}", ex.getMessage());
    }

    public boolean waitForConnection(long timeout, TimeUnit unit) throws InterruptedException {
        return connectionLatch.await(timeout, unit);
    }

    public void clear() {
        messages.clear();
    }
}
