public class Frame {
	private String describeId;
	private String sendFrame;
	private String recvFrame;
	
	public Frame(String describeId,String sendFrame,String recvFrame){
		super();
		this.describeId=describeId;
		this.sendFrame=sendFrame;
		this.recvFrame=recvFrame;
	}
	
	public Frame(){
		
	}
	
	public String getDescribeId(){
		return this.describeId;
	}
	
	public void setDescribeId(String describeId){
		this.describeId=describeId;
	}
	
	public String getSendFrame(){
		return this.sendFrame;
	}
	
	public void setSendFrame(String sendFrame){
		this.sendFrame=sendFrame;
		
	}
	
	public String getRecvFrame(){
		return this.recvFrame;
	}
	
	public void setRecvFrame(String recvFrame){
		this.recvFrame=recvFrame;
		
	}
	
	public String toString(){
		return "Frame[desc="+describeId+",sendFrame="+sendFrame+",recvFrame="+recvFrame+"]";
	}

}
