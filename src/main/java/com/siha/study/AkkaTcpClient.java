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

@Slf4j
public class AkkaTcpClient extends UntypedActor {
    public static void main(String[] args) {
        ActorSystem clientActorSystem = ActorSystem.create("AkkaActorClient");
        ActorRef clientActor = clientActorSystem.actorOf(AkkaTcpClient.props(new InetSocketAddress("localhost", 9090), null), "clientAcotr");

        clientActor.tell("test", clientActor);
    }

    final InetSocketAddress remote;
    private ActorRef tcpActor;

    public static Props props(InetSocketAddress remote, ActorRef tcpActor) {
        return Props.create(AkkaTcpClient.class, remote, tcpActor);
    }

    public AkkaTcpClient(InetSocketAddress remote, ActorRef tcpActor) {
        this.remote = remote;
        this.tcpActor = tcpActor;
    }

    @Override
    public void onReceive(Object message) throws Throwable {
        if (message instanceof Tcp.CommandFailed) {
            log.info("In ClientActor - received message: failed");
            getContext().stop(getSelf());
        } else if (message instanceof Tcp.Connected) {
            log.info("In ClientActor - received message: connected");

            getSender().tell(TcpMessage.register(getSelf()), getSelf());
            getContext().become(connected(getSender()));

            getSender().tell(TcpMessage.write(ByteString.fromArray("hello".getBytes())), getSelf());
        }
    }

    private Procedure<Object> connected(final ActorRef connection) {
        return param -> {
            if(param instanceof ByteString) {
                connection.tell(TcpMessage.write((ByteString)param), getSelf());
            } else if(param instanceof Tcp.CommandFailed) {

            } else if(param instanceof Tcp.Received) {
                log.info("In ClientActor - Received message: {}", ((Tcp.Received)param).data().utf8String());
            } else if(param.equals("close")) {
                connection.tell(TcpMessage.close(), getSelf());
            } else if(param instanceof Tcp.ConnectionClosed) {
                getContext().stop(getSelf());
            }
        };
    }
}
