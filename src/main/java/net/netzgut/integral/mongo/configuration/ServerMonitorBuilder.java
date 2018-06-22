/**
 * Copyright 2018 Netzgut GmbH <info@netzgut.net>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
