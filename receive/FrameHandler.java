import java.util.ArrayList;
import java.util.List;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class FrameHandler extends DefaultHandler {
	List<Frame> frames;
	Frame frame;
	String elementTag=null;
	boolean frameFlag=false;
	public List<Frame> getFrames(){
		return frames;
	}
	
	public void startDocument() throws SAXException{
		frames=new ArrayList<Frame>();		
	}
	
	public void startElement(String uri,String localName,String qName,
			Attributes attributes) throws SAXException{
		if("frame".equals(qName)){
			frame=new Frame();
			frame.setDescribeId(attributes.getValue(0));
		}
		else if("sendFrame".equals(qName)){
			frameFlag=false;
		}
		else if("recvFrame".equals(qName)){
			frameFlag=true;
		}
		else if("commonConstSection".equals(qName) && !frameFlag){
			frame.setSendFrame(attributes.getValue(0));
		}
		else if("commonConstSection".equals(qName) && frameFlag){
			frame.setRecvFrame(attributes.getValue(0));
		}
		elementTag=null;
	}
	
	public void endElement(String uri,String localName,String qName )
			throws SAXException{
		if("frame".equals(qName)&& frame !=null){
			frames.add(frame);
			frame=null;
		}
		elementTag=null;
	}
	
	public void endDocument() throws SAXException{
		
	}

}
