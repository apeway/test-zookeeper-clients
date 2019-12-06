package org.liws.zk.clients.base.impl.callback;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.data.Stat;

public class SimpDataCallback implements AsyncCallback.DataCallback {
	
	@Override
	public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
		System.out.println(rc + ", " + path + ", " + new String(data));
		System.out.println(stat.getCzxid() + "," + stat.getMzxid() + "," + stat.getVersion());
	}
	
}