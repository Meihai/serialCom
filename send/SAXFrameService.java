import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class SAXFrameService {
	
	public List<Frame> getFrameList( ) throws Exception{
		InputStream inStream=this.getClass().getClassLoader().getResourceAsStream("MockSendRecvDefine-ma01c.xml");
		SAXParserFactory factory=SAXParserFactory.newInstance();
		SAXParser parser=factory.newSAXParser();
		FrameHandler handler=new FrameHandler();
		parser.parse(inStream, handler);
		List<Frame> frameList=handler.getFrames();
		inStream.close();
		return frameList;
	}

	public HashMap<String,String> getSendRecvMap( ){
		HashMap<String,String> sendRecvMap=new HashMap<String,String>();
		try{
			List<Frame> frameList=getFrameList();
			for(Frame insFrame:frameList){
				sendRecvMap.put(insFrame.getSendFrame().replaceAll(" ",""),insFrame.getRecvFrame().replaceAll(" ",""));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return sendRecvMap;
	}

}
