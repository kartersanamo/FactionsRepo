package com.kartersanamo.network;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

final class MinecraftStatus {

    private MinecraftStatus() {
    }

    static boolean isReady(InetSocketAddress address) {
        try (Socket socket = new Socket()) {
            socket.connect(address, 2000);
            socket.setSoTimeout(2000);
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            DataInputStream in = new DataInputStream(socket.getInputStream());

            ByteArrayOutputStream handshakeBytes = new ByteArrayOutputStream();
            DataOutputStream handshake = new DataOutputStream(handshakeBytes);
            writeVarInt(handshake, 0);
            writeVarInt(handshake, 47);
            writeString(handshake, address.getHostString());
            handshake.writeShort(address.getPort());
            writeVarInt(handshake, 1);

            writeVarInt(out, handshakeBytes.size());
            out.write(handshakeBytes.toByteArray());
            writeVarInt(out, 1);
            writeVarInt(out, 0);

            int length = readVarInt(in);
            if (length <= 0) {
                return false;
            }
            int packetId = readVarInt(in);
            return packetId == 0 && readString(in) != null;
        } catch (IOException ignored) {
            return false;
        }
    }

    private static void writeString(DataOutputStream out, String value) throws IOException {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        writeVarInt(out, bytes.length);
        out.write(bytes);
    }

    private static String readString(DataInputStream in) throws IOException {
        int length = readVarInt(in);
        if (length <= 0 || length > 32767) {
            return null;
        }
        byte[] bytes = new byte[length];
        in.readFully(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    private static void writeVarInt(DataOutputStream out, int value) throws IOException {
        int remaining = value;
        while ((remaining & 0xFFFFFF80) != 0) {
            out.writeByte((remaining & 0x7F) | 0x80);
            remaining >>>= 7;
        }
        out.writeByte(remaining);
    }

    private static int readVarInt(DataInputStream in) throws IOException {
        int value = 0;
        int position = 0;
        while (true) {
            int read = in.readByte();
            value |= (read & 0x7F) << position;
            if ((read & 0x80) == 0) {
                return value;
            }
            position += 7;
            if (position >= 32) {
                throw new IOException("VarInt too big");
            }
        }
    }
}
