package net.netzgut.integral.mongo.configuration;

import java.util.function.Consumer;

import com.mongodb.event.ServerHeartbeatFailedEvent;
import com.mongodb.event.ServerHeartbeatStartedEvent;
import com.mongodb.event.ServerHeartbeatSucceededEvent;
import com.mongodb.event.ServerMonitorListener;

public class ServerMonitorBuilder {

    private Consumer<ServerHeartbeatStartedEvent>   started;
    private Consumer<ServerHeartbeatSucceededEvent> succeeded;
    private Consumer<ServerHeartbeatFailedEvent>    failed;

    public ServerMonitorBuilder() {
        // Start a new builder
    }

    public ServerMonitorBuilder started(Consumer<ServerHeartbeatStartedEvent> started) {
        this.started = started;

        return this;
    }

    public ServerMonitorBuilder succeeded(Consumer<ServerHeartbeatSucceededEvent> succeeded) {
        this.succeeded = succeeded;

        return this;
    }

    public ServerMonitorBuilder failed(Consumer<ServerHeartbeatFailedEvent> failed) {
        this.failed = failed;

        return this;
    }

    public ServerMonitorListener build() {
        return new ServerMonitorListener() {

            @Override
            public void serverHearbeatStarted(ServerHeartbeatStartedEvent event) {
                if (ServerMonitorBuilder.this.started == null) {
                    return;
                }

                ServerMonitorBuilder.this.started.accept(event);
            }

            @Override
            public void serverHeartbeatSucceeded(ServerHeartbeatSucceededEvent event) {
                if (ServerMonitorBuilder.this.succeeded == null) {
                    return;
                }

                ServerMonitorBuilder.this.succeeded.accept(event);
            }

            @Override
            public void serverHeartbeatFailed(ServerHeartbeatFailedEvent event) {
                if (ServerMonitorBuilder.this.failed == null) {
                    return;
                }

                ServerMonitorBuilder.this.failed.accept(event);
            }
        };
    }

}
