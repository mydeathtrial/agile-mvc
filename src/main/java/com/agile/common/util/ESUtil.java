package com.agile.common.util;

import com.carrotsearch.hppc.ObjectContainer;
import org.elasticsearch.action.admin.cluster.state.ClusterStateResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.cluster.node.DiscoveryNode;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.util.List;

/**
 * @author 佟盟 on 2018/10/16
 */
public class ESUtil {

    private static Client client;

    private static Client getClient() {
        if (client == null) {
            try {
                client = FactoryUtil.getBean(Client.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return client;
    }

    /**
     * 查看集群信息
     */
    public static List<DiscoveryNode> getClusterInfo() {
        return ((PreBuiltTransportClient) getClient()).connectedNodes();
    }

    /**
     * 获取所有的索引
     */
    public static String[] getAllIndex() {
        ClusterStateResponse response = getClient().admin().cluster().prepareState().execute().actionGet();
        return response.getState().getMetaData().getConcreteAllIndices();
    }

    public static ObjectContainer<MappingMetaData> getTypeByIndex(String index) {
        ClusterStateResponse response = getClient().admin().cluster().prepareState().execute().actionGet();
        return response.getState().getMetaData().index(index).getMappings().values();
    }
}
