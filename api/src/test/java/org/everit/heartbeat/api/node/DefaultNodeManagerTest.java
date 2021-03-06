package org.everit.heartbeat.api.node;

/*
 * Copyright (c) 2013, Everit Kft.
 *
 * All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * Class to test the DefaultNodeManager class.
 */
public class DefaultNodeManagerTest {

    /**
     * Testing the addNode() and getAllNodes() function of the DefaultNodeManager class.
     * 
     * @throws UnknownHostException
     *             If can't get InetAddress.
     */
    @Test
    public void addAndGetNodeTest() throws UnknownHostException {
        DefaultNodeManager defaultNodeManager = new DefaultNodeManager();

        InetSocketAddress inetSocketAddress = InetSocketAddress.createUnresolved("192.168.1.1", 400);

        Node node = new Node(inetSocketAddress, 111, "111");
        Node node2 = new Node(inetSocketAddress, 222, "333");

        Assert.assertNull(defaultNodeManager.addNode(node));

        Assert.assertEquals(node, defaultNodeManager.addNode(node2));

        Assert.assertFalse(Arrays.asList(defaultNodeManager.getAllNodes()).contains(node));
        Assert.assertTrue(Arrays.asList(defaultNodeManager.getAllNodes()).contains(node2));

        Node node3 = new Node(InetSocketAddress.createUnresolved("192.168.1.2", 400), 222, "333");
        defaultNodeManager.addNode(node3);
        Node node4 =
                new Node(InetSocketAddress.createUnresolved("192.168.1.3", 400), 333, "333");
        defaultNodeManager.addNode(node4);

        Assert.assertTrue(Arrays.asList(defaultNodeManager.getAllNodes()).size() == 3);

    }

    /**
     * Testing the addNode method with null parameter. Should throw IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public void addNullTest() {
        DefaultNodeManager defaultNodeManager = new DefaultNodeManager();
        defaultNodeManager.addNode(null);
    }

    /**
     * Testing the getLiveNodes() method.
     * 
     * @throws UnknownHostException
     *             If can't get InetAddress.
     */
    @Test
    public void getLiveNodesTest() throws UnknownHostException {
        DefaultNodeManager defaultNodeManager = new DefaultNodeManager();

        InetSocketAddress inetSocketAddress = InetSocketAddress.createUnresolved("192.168.1.1", 400);
        long thresholdInMs = 2000;
        long lastHeartbeatReveivedAt = System.currentTimeMillis();

        Node node = new Node(inetSocketAddress, 100, "111");
        Node node2 = new Node(InetSocketAddress.createUnresolved("192.168.1.2", 400), lastHeartbeatReveivedAt, "333");
        Node node3 = new Node(InetSocketAddress.createUnresolved("192.168.1.3", 400), lastHeartbeatReveivedAt
                - thresholdInMs, "333");
        defaultNodeManager.addNode(node);
        defaultNodeManager.addNode(node2);
        defaultNodeManager.addNode(node3);

        List<Node> liveNodes = Arrays.asList(defaultNodeManager.getLiveNodes(thresholdInMs));
        for (Node i : liveNodes) {
            Assert.assertTrue(i.getLastHeartbeatReceivedAt() > (lastHeartbeatReveivedAt - thresholdInMs));
        }

    }

}
