package DBProxy.Plugins.proxy;

import DBProxy.Core.CMySQLLoadBalancer;
import DBProxy.Core.Engine;
import DBProxy.MySQL.Protocol.Com_Initdb;
import DBProxy.MySQL.Protocol.Com_Query;
import DBProxy.MySQL.Protocol.Flags;
import DBProxy.MySQL.Protocol.Handshake;
import DBProxy.MySQL.Protocol.HandshakeResponse;
import DBProxy.MySQL.Protocol.Packet;
import DBProxy.MySQL.Protocol.ResultSet;
import java.net.Socket;
import java.net.UnknownHostException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.BufferedInputStream;
import org.apache.log4j.Logger;
import DBProxy.Plugins.Base;
import Publisher.CPublisherManager;
import java.util.regex.Pattern;

public class Proxy extends Base {

    public Pattern regUpdateIncident = Pattern.compile("UPDATE Incident [ (=),\'a-zA-Z0-9]* incidentStatus = 'On-going'");

    public Pattern regInsertIncident = Pattern.compile("INSERT INTO Incident [ (),\'a-zA-Z0-9]* VALUES [ (),\'a-zA-Z0-9]* 'On-going'");

    public Logger logger = Logger.getLogger("Plugin.Proxy");

    // MySql server stuff
    public String mysqlHost = "";
    public int mysqlPort = 0;
    public Socket mysqlSocket = null;
    public InputStream mysqlIn = null;
    public OutputStream mysqlOut = null;

    public void init(Engine context) throws IOException, UnknownHostException {
        this.logger.trace("init");

        if (context.query.startsWith("SELECT") || context.query.startsWith("SHOW")) {
            this.mysqlSocket = CMySQLLoadBalancer.getDataStorage();
        } else {
            this.mysqlSocket = CMySQLLoadBalancer.getMasterStorage();
        }

        // Connect to the mysql server on the other side
        this.logger.info("Connected to mysql server at " + this.mysqlHost + ":" + this.mysqlPort);
        this.mysqlIn = new BufferedInputStream(this.mysqlSocket.getInputStream(), 16384);
        this.mysqlOut = this.mysqlSocket.getOutputStream();

    }

    public void read_handshake(Engine context) throws IOException {
        this.logger.trace("read_handshake");
        byte[] packet = Packet.read_packet(this.mysqlIn);

        context.handshake = Handshake.loadFromPacket(packet);

        // Remove some flags from the reply
        context.handshake.removeCapabilityFlag(Flags.CLIENT_COMPRESS);
        context.handshake.removeCapabilityFlag(Flags.CLIENT_SSL);
        context.handshake.removeCapabilityFlag(Flags.CLIENT_LOCAL_FILES);

        // Set the default result set creation to the server's character set
        ResultSet.characterSet = context.handshake.characterSet;

        // Set Replace the packet in the buffer
        context.buffer.add(context.handshake.toPacket());
    }

    public void send_handshake(Engine context) throws IOException {
        this.logger.trace("send_handshake");
        Packet.write(context.clientOut, context.buffer);
        context.clear_buffer();
    }

    public void read_auth(Engine context) throws IOException {
        this.logger.trace("read_auth");
        byte[] packet = Packet.read_packet(context.clientIn);
        context.buffer.add(packet);

        context.authReply = HandshakeResponse.loadFromPacket(packet);

        if (!context.authReply.hasCapabilityFlag(Flags.CLIENT_PROTOCOL_41)) {
            this.logger.fatal("We do not support Protocols under 4.1");
            context.halt();
            return;
        }

        context.authReply.removeCapabilityFlag(Flags.CLIENT_COMPRESS);
        context.authReply.removeCapabilityFlag(Flags.CLIENT_SSL);
        context.authReply.removeCapabilityFlag(Flags.CLIENT_LOCAL_FILES);

        context.schema = context.authReply.schema;
    }

    public void send_auth(Engine context) throws IOException {
        this.logger.trace("send_auth");
        Packet.write(this.mysqlOut, context.buffer);
        context.clear_buffer();
    }

    public void read_auth_result(Engine context) throws IOException {
        this.logger.trace("read_auth_result");
        byte[] packet = Packet.read_packet(this.mysqlIn);
        context.buffer.add(packet);
        if (Packet.getType(packet) != Flags.OK) {
            this.logger.fatal("Auth is not okay!");
        }
    }

    public void send_auth_result(Engine context) throws IOException {
        this.logger.trace("read_auth_result");
        Packet.write(context.clientOut, context.buffer);
        context.clear_buffer();
    }

    public void read_query(Engine context) throws IOException {
        this.logger.trace("read_query");
        context.bufferResultSet = false;

        byte[] packet = Packet.read_packet(context.clientIn);
        context.buffer.add(packet);

        context.sequenceId = Packet.getSequenceId(packet);
        this.logger.trace("Client sequenceId: " + context.sequenceId);

        switch (Packet.getType(packet)) {
            case Flags.COM_QUIT:
                this.logger.trace("COM_QUIT");
                context.halt();
                break;

            // Extract out the new default schema
            case Flags.COM_INIT_DB:
                this.logger.trace("COM_INIT_DB");
                context.schema = Com_Initdb.loadFromPacket(packet).schema;
                break;

            // Query
            case Flags.COM_QUERY:
                this.logger.trace("COM_QUERY");
                context.query = Com_Query.loadFromPacket(packet).query;
                break;

            default:
                break;
        }
    }

    public void send_query(Engine context) throws IOException {
        this.logger.trace("send_query");
        Packet.write(this.mysqlOut, context.buffer);
        context.clear_buffer();
    }

    public void read_query_result(Engine context) throws IOException {
        this.logger.trace("read_query_result");

        byte[] packet = Packet.read_packet(this.mysqlIn);
        context.buffer.add(packet);

        context.sequenceId = Packet.getSequenceId(packet);

        switch (Packet.getType(packet)) {
            case Flags.OK:
            case Flags.ERR:
                break;

            default:
                context.buffer = Packet.read_full_result_set(this.mysqlIn, context.clientOut, context.buffer, context.bufferResultSet);
                break;
        }
        if (this.regUpdateIncident.matcher(context.query).find()) {
            CPublisherManager.publishOngoingIncident(context.query);
        } else if (this.regInsertIncident.matcher(context.query).find()) {
            CPublisherManager.publishOngoingIncident(context.query);
        }

    }

    public void send_query_result(Engine context) throws IOException {
        this.logger.trace("send_query_result");

        Packet.write(context.clientOut, context.buffer);
        context.clear_buffer();
    }

    public void cleanup(Engine context) {
        this.logger.trace("cleanup");
        if (this.mysqlSocket == null) {
            return;
        }

        try {
            this.mysqlSocket.close();
        } catch (IOException e) {
        }
    }
}
