package com.siha.study;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.io.Tcp;
import akka.io.TcpMessage;
import akka.japi.Procedure;
import akka.util.ByteString;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * Created by 1002375 on 2017. 1. 24..
 */
public class AkkaTcpServer {
    public static void main(String[] args) {
        ActorSystem serverActorSystem = ActorSystem.create("AkkaActorServer");
        ActorRef serverActor = serverActorSystem.actorOf(ServerActor.props(null), "serverActor");
        serverActorSystem.awaitTermination();
    }

    @Slf4j
    public static class ServerActor extends UntypedActor {
        private ActorRef tcpActor;

        public static Props props(ActorRef tcpActor) {
            return Props.create(ServerActor.class, tcpActor);
        }

        public ServerActor(ActorRef tcpActor) {
            this.tcpActor = tcpActor;
        }

        @Override
        public void preStart() throws Exception {
            if(tcpActor == null) {
                tcpActor = Tcp.get(getContext().system()).manager();
            }

            tcpActor.tell(TcpMessage.bind(getSelf(), new InetSocketAddress("localhost", 9090), 100), getSelf());
        }

        @Override
        public void onReceive(Object message) throws Throwable {
            if(message instanceof Tcp.Bound) {
                log.info("In ServerActor - received message: bound");
            } else if(message instanceof Tcp.CommandFailed) {
                getContext().stop(getSelf());
            } else if(message instanceof Tcp.Received) {
                final Tcp.Connected conn = (Tcp.Connected)message;
                log.info("In ServerActor - received message: connected");

                final ActorRef handler = getContext().actorOf(Props.create(SimpleHandlerActor.class));

                getSender().tell(TcpMessage.register(handler), getSelf());
            }
        }
    }

    @Slf4j
    public static class SimpleHandlerActor extends UntypedActor {
        @Override
        public void onReceive(Object message) throws Throwable {
            if(message instanceof Tcp.Received) {
                final String data = ((Tcp.Received)message).data().utf8String();
                log.info("In SimpleHandlerActor - Received message: {}", data);
                getSender().tell(TcpMessage.write(ByteString.fromArray(("result: " + data).getBytes())), getSelf());
            } else if(message instanceof Tcp.ConnectionClosed) {
                getContext().stop(getSelf());
            }
        }
    }
}
