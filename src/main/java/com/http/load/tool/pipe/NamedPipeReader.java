package com.http.load.tool.pipe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.atomic.AtomicBoolean;

import static io.vertx.core.impl.Arguments.require;

public class NamedPipeReader {

    private static final int HEADER_SIZE = 4;
    private static final int INT_MASK = 0xFF;
    private static final Logger LOGGER = LoggerFactory.getLogger(NamedPipeReader.class);
    private final String pipeName;
    private final AtomicBoolean inUse = new AtomicBoolean();
    private RandomAccessFile pipe;
    private double avgTimeInMillis;
    private long totalRequests;
    private boolean kerberosReady;

    public NamedPipeReader(final String pipeName) {
        this.pipeName = pipeName;
        try {
            this.pipe = new RandomAccessFile("\\\\.\\pipe\\" + pipeName, "rw");
            this.kerberosReady = true;
        } catch (FileNotFoundException e) {
            LOGGER.error("Unable to connect to {} pipe", pipeName);
        }
    }

    public byte[] read(final String json) {
        try {
            totalRequests++;
            long pipeReadStartTime = System.currentTimeMillis();

            int jsonLength = json.length();
            byte[] requestHeader = new byte[HEADER_SIZE];
            requestHeader[0] = (byte) 1;
            requestHeader[2] = (byte) (jsonLength & INT_MASK);
            requestHeader[3] = (byte) ((jsonLength >> 8) & INT_MASK);

            pipe.write(requestHeader);
            pipe.write(json.getBytes());

            byte[] responseHeader = readFromPipe(HEADER_SIZE);
            int payloadLength = (responseHeader[2] & 0xff) | ((responseHeader[3] & 0xff) << 8);
            byte[] bytes = readFromPipe(payloadLength);

            long timeTakenByPipe = System.currentTimeMillis() - pipeReadStartTime;
            avgTimeInMillis = avgTimeInMillis + ((timeTakenByPipe - avgTimeInMillis) / (double) totalRequests);

            return bytes;
        } catch (final Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public boolean acquire() {
        return inUse.compareAndSet(false, true);
    }

    public void markAvailable() {
        inUse.set(false);
    }

    public boolean isInUse() {
        return inUse.get();
    }

    public String getPipeName() {
        return pipeName;
    }

    private byte[] readFromPipe(final int length) throws IOException {
        byte[] bytes = new byte[length];

        // Put some negative values - later used for validation.
        bytes[length - 1] = -1;
        pipe.read(bytes);

        // Ensure that the payload returned by Named Pipe is valid.
        require(bytes[length - 1] != -1, "Invalid data from pipe");
        return bytes;
    }

    public boolean isKerberosReady() {
        return kerberosReady;
    }

    public NamedPipeReader setKerberosReady(boolean kerberosReady) {
        this.kerberosReady = kerberosReady;
        return this;
    }

    public double getAvgTimeInMillis() {
        return avgTimeInMillis;
    }
}