import com.pi4j.io.serial.*;
import com.pi4j.util.CommandArgumentParser;
import com.pi4j.util.Console;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Created by admin on 2016/9/14.串口发送数据
 */
public class PiSerialRecv {
    public static boolean sendFlag=false;

    public static void main(String args[]) throws InterruptedException,IOException{
        Map<String,String> sendRecvMap;
        List<Frame> frameList;
        SAXFrameService insSaxFrameService=new  SAXFrameService();
//        String hexString="01 03 08 00 01 00 0B 3F 01 74 07 1B C0";
//        String hexString1=hexString.replaceAll(" ","").toUpperCase().toString();
//        insPiSerialRecv.hexStringToByte(hexString1);
//        System.out.println(hexString1);
        sendRecvMap=  insSaxFrameService.getSendRecvMap();
        try{
            frameList=insSaxFrameService.getFrameList() ;
        }catch(Exception ex){
            ex.printStackTrace();
        }
        final Console console=new Console();
        StringBuilder recvData=new StringBuilder();
        console.title("Receive commandline from another pi and responsd to");
        console.promptForExit();
        final Serial serial=SerialFactory.createInstance();
        serial.addListener(new SerialDataEventListener(){
            @Override
            public void dataReceived(SerialDataEvent event){
                try{
                    console.println("[SEND ASCII DATA] "+event.getAsciiString());
                    console.println("[SEND HEX DATA] "+event.getHexByteString());
                    String hexString=event.getHexByteString().replaceAll(",","").toUpperCase().toString();
                    recvData.append(hexString);
                    sendFlag=true;
                } catch(IOException e){
                    e.printStackTrace();
                }
            }
        });
        try{
            SerialConfig config = new SerialConfig();
            config.device(SerialPort.getDefaultPort())
                    .baud(Baud._115200)
                    .dataBits(DataBits._8)
                    .parity(Parity.NONE)
                    .stopBits(StopBits._1)
                    .flowControl(FlowControl.NONE);
            if(args.length>0){
                config=CommandArgumentParser.getSerialConfig(config,args);
            }
            console.box("Connecting to:"+config.toString(),
                    "We are sending ASCII data on the serial port every 1 second.",
                    "Data received on serial port will be displayed below.");
            serial.open(config);
            while(console.isRunning()){
                try{
                    if(sendFlag){
                        String recvString=recvData.toString();
                       // Set set=sendRecvMap.entrySet();
                       // Iterator   iterator=set.iterator();
                       // while (iterator.hasNext()) {
                       //     Map.Entry  mapentry = (Map.Entry) iterator.next();
                       //     console.println(mapentry.getKey()+"/"+ mapentry.getValue());
                       // }
                        String sendString=sendRecvMap.get(recvString);
                        if(sendString==null){
                            sendString=sendRecvMap.get(recvString.toLowerCase().toString());
                        }
                        if(sendString !=null){
                            String hexString=sendString.replaceAll(" ","").toUpperCase().toString();
                            serial.write(HexUtil.hexStringToByte(hexString));
                        }
                        sendFlag=false;
                        recvData.delete(0,recvData.length());
                    }
                }catch(IllegalStateException ex){
                    ex.printStackTrace();
                }
                //wait 1 second
                Thread.sleep(1000);
            }
        }catch(IOException ex){
            console.println("==>> SERIAL SETUP FAILED:"+ex.getMessage());
            return;
        }
    }


}
