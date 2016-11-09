import com.pi4j.io.serial.*;
import com.pi4j.util.CommandArgumentParser;
import com.pi4j.util.Console;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
/**
 * Created by admin on 2016/9/29.
 */
public class Rs485Send {
    public static boolean sendFlag=true;
    public static void main(String args[]) throws InterruptedException,IOException{
        List<Frame> frameList=new ArrayList<Frame>();
        SAXFrameService insSaxFrameService=new  SAXFrameService();
        try{
            frameList=insSaxFrameService.getFrameList();
        }catch(Exception ex){
            ex.printStackTrace();
        }
        final Console console=new Console();
        StringBuilder recvData=new StringBuilder();
        console.title("Send commandline to another pi and wait for respondse");
        console.promptForExit();
        // create gpio controller
        final GpioController gpio = GpioFactory.getInstance();
        // provision gpio pin #01 as an output pin and turn on
        final GpioPinDigitalOutput pin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_07, "RS485Control", PinState.HIGH);
        pin.setShutdownOptions(true, PinState.LOW);

        final Serial serial=SerialFactory.createInstance();
        serial.addListener(new SerialDataEventListener(){
            @Override
            public void dataReceived(SerialDataEvent event){
                try{
                    console.println("[RECEIVE HEX DATA] "+ event.getHexByteString());
                    String hexString=event.getHexByteString().replaceAll(",","").toUpperCase().toString();
                    recvData.append(hexString);
                    console.println("receive pin state"+pin.getState());
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
            int sendDataLength=frameList.size();
            int count=0;
            while(console.isRunning()){
                try{
                    if(sendFlag){
                        console.println("send pin state:"+pin.getState());
                        if(count>=sendDataLength){
                            count=0;
                        }
                        if(sendDataLength>0){
                            String hexString=frameList.get(count).getSendFrame().replaceAll(" ","").toUpperCase().toString();
                            console.println("send hexString:"+hexString);
                            serial.write(HexUtil.hexStringToByte(hexString));
                            double waitTime=hexString.length()*8*1000*1.0/115200+10;
                            Thread.sleep((long) waitTime);
                            count++;
                        }
                        sendFlag=false;
                        pin.low();
                        console.println("send pin state1:"+pin.getState());
                    }
                }catch(IllegalStateException ex){
                    ex.printStackTrace();
                }
                //wait 1 second
                //Thread.sleep(2000);
            }
        }catch(IOException ex){
            console.println("==>> SERIAL SETUP FAILED:"+ex.getMessage());
            return;
        }
    }

}
