package org.liws.zk.clients.base.async;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

/**
 * 检测节点是否存在，使用异步(async)接口。
 */
public class T6_exist_ASync {

	private static ZooKeeper zk;

	public static void main(String[] args) throws Exception {
		AsyncCallback.StatCallback statCallback = new AsyncCallback.StatCallback() {
			@Override
			public void processResult(int rc, String path, Object ctx, Stat stat) {
				// TODO Auto-generated method stub
				
			}
		}; 
		
//		zk.exists(path, watch, statCallback, "I am context.");
	}

}