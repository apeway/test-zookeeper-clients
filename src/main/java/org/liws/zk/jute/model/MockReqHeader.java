package org.liws.zk.jute.model;

import org.apache.jute.InputArchive;
import org.apache.jute.OutputArchive;
import org.apache.jute.Record;

/**
 * MockReqHeader表示一个简单的请求头类，通过实现Record来支持序列化和反序列化
 */
public class MockReqHeader implements Record {
	
	private long sessionId;
	private String type;

	public MockReqHeader() {
	}
	public MockReqHeader(long sessionId, String type) {
		this.sessionId = sessionId;
		this.type = type;
	}

	public long getSessionId() {
		return sessionId;
	}
	public void setSessionId(long sessionId) {
		this.sessionId = sessionId;
	}
	public String getType() {
		return type;
	}
	public void setType(String m_) {
		type = m_;
	}

	@Override
	public void serialize(OutputArchive outputArchive, String tag) throws java.io.IOException {
		outputArchive.startRecord(this, tag);
		
		outputArchive.writeLong(sessionId, "sessionId");
		outputArchive.writeString(type, "type");
		
		outputArchive.endRecord(this, tag);
	}

	@Override
	public void deserialize(InputArchive inputArchive, String tag) throws java.io.IOException {
		inputArchive.startRecord(tag);
		
		sessionId = inputArchive.readLong("sessionId");
		type = inputArchive.readString("type");
		
		inputArchive.endRecord(tag);
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (sessionId ^ (sessionId >>> 32));
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MockReqHeader other = (MockReqHeader) obj;
		if (sessionId != other.sessionId)
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}
}
