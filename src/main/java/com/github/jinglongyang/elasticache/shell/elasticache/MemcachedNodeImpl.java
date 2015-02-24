package com.github.jinglongyang.elasticache.shell.elasticache;

import net.spy.memcached.MemcachedNode;
import net.spy.memcached.config.NodeEndPoint;
import net.spy.memcached.ops.Operation;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Collection;

/**
 * @author: jinglongyang
 */
public class MemcachedNodeImpl implements MemcachedNode {
    private final MemcachedNode root;

    public MemcachedNodeImpl(MemcachedNode n) {
        root = n;
    }

    @Override
    public String toString() {
        return root.toString();
    }

    public void addOp(Operation op) {
        throw new UnsupportedOperationException();
    }

    public void insertOp(Operation op) {
        throw new UnsupportedOperationException();
    }

    public void connected() {
        throw new UnsupportedOperationException();
    }

    public void copyInputQueue() {
        throw new UnsupportedOperationException();
    }

    public void fillWriteBuffer(boolean optimizeGets) {
        throw new UnsupportedOperationException();
    }

    public void fixupOps() {
        throw new UnsupportedOperationException();
    }

    public int getBytesRemainingToWrite() {
        return root.getBytesRemainingToWrite();
    }

    public SocketChannel getChannel() {
        throw new UnsupportedOperationException();
    }

    public Operation getCurrentReadOp() {
        throw new UnsupportedOperationException();
    }

    public Operation getCurrentWriteOp() {
        throw new UnsupportedOperationException();
    }

    public ByteBuffer getRbuf() {
        throw new UnsupportedOperationException();
    }

    public int getReconnectCount() {
        return root.getReconnectCount();
    }

    public int getSelectionOps() {
        return root.getSelectionOps();
    }

    public SelectionKey getSk() {
        throw new UnsupportedOperationException();
    }

    public SocketAddress getSocketAddress() {
        return root.getSocketAddress();
    }

    public NodeEndPoint getNodeEndPoint() {
        return root.getNodeEndPoint();
    }

    public void setNodeEndPoint(NodeEndPoint endPoint) {
        root.setNodeEndPoint(endPoint);
    }

    public ByteBuffer getWbuf() {
        throw new UnsupportedOperationException();
    }

    public boolean hasReadOp() {
        return root.hasReadOp();
    }

    public boolean hasWriteOp() {
        return root.hasReadOp();
    }

    public boolean isActive() {
        return root.isActive();
    }

    public void reconnecting() {
        throw new UnsupportedOperationException();
    }

    public void registerChannel(SocketChannel ch, SelectionKey selectionKey) {
        throw new UnsupportedOperationException();
    }

    public Operation removeCurrentReadOp() {
        throw new UnsupportedOperationException();
    }

    public Operation removeCurrentWriteOp() {
        throw new UnsupportedOperationException();
    }

    public void setChannel(SocketChannel to) {
        throw new UnsupportedOperationException();
    }

    public void setSk(SelectionKey to) {
        throw new UnsupportedOperationException();
    }

    public void setupResend() {
        throw new UnsupportedOperationException();
    }

    public void transitionWriteItem() {
        throw new UnsupportedOperationException();
    }

    public int writeSome() throws IOException {
        throw new UnsupportedOperationException();
    }

    public Collection<Operation> destroyInputQueue() {
        throw new UnsupportedOperationException();
    }

    public void authComplete() {
        throw new UnsupportedOperationException();
    }

    public void setupForAuth() {
        throw new UnsupportedOperationException();
    }

    public int getContinuousTimeout() {
        throw new UnsupportedOperationException();
    }

    public void setContinuousTimeout(boolean isIncrease) {
        throw new UnsupportedOperationException();
    }
}
