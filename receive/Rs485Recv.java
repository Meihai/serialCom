import com.pi4j.io.serial.*;
import com.pi4j.util.CommandArgumentParser;
import com.pi4j.util.Console;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
/**
 * Created by admin on 2016/9/29.
 */
public class Rs485Recv {

    public static boolean sendFlag=false;

    public static void main(String args[]) throws InterruptedException,IOException{
        Map<String,String> sendRecvMap;
        List<Frame> frameList;
        SAXFrameService insSaxFrameService=new  SAXFrameService();
        sendRecvMap=  insSaxFrameService.getSendRecvMap();
        try{
            frameList=insSaxFrameService.getFrameList() ;
        }catch(Exception ex){
            ex.printStackTrace();
        }
        final Console console=new Console();
        StringBuilder recvData=new StringBuilder();
        console.title("Send commandline to another pi");
        console.promptForExit();
        final GpioController gpio = GpioFactory.getInstance();
        final GpioPinDigitalOutput pin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_07, "RS485Ctrl", PinState.LOW);
        // set shutdown state for this pin
        pin.setShutdownOptions(true, PinState.HIGH);
        final Serial serial=SerialFactory.createInstance();
        serial.addListener(new SerialDataEventListener(){
            @Override
            public void dataReceived(SerialDataEvent event){
                try{
                    //console.println("[SEND ASCII DATA] "+event.getAsciiString());
                    console.println("[RECEIVE HEX DATA] "+event.getHexByteString());
                    String hexString=event.getHexByteString().replaceAll(",","").toUpperCase().toString();
                    recvData.append(hexString);
                    sendFlag=true;
                    pin.high();
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
                        Set set=sendRecvMap.entrySet();

                        String sendString=sendRecvMap.get(recvString);
                        if(sendString==null){
                            sendString=sendRecvMap.get(recvString.toLowerCase().toString());
                        }
                        if(sendString !=null){
                            String hexString=sendString.replaceAll(" ","").toUpperCase().toString();
                            serial.write(HexUtil.hexStringToByte(hexString));
                            double waitTime=hexString.length()*8*1000*1.0/115200+10;
                            Thread.sleep((long) waitTime);
                        }
                        sendFlag=false;
                        pin.low();
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
