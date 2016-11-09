# serialCom
在树莓派pi3 b+上使用串口通信的系统设置和软件调试
1 设置树莓派串口的uart使能
设置方法可以参考链接：
http://ukonline2000.com/?p=880
2 在树莓派上安装pi4j
安装方法参考：
1)下载pi4j-1.1.deb
2)切换到pi4j-1.1.deb目录, 安装 pi4j-1.1.deb
sudo dpkg –i pi4j-1.1.deb

3 编译串口的接收和发送代码
切换到串口发送测试程序文件夹send
运行以下指令编译所有java文件
javac -classpath .:classes:/opt/pi4j/lib/'*' -d . *.java

运行232串口发送程序:
sudo java  -classpath .:classes:/opt/pi4j/lib/'*' PiSerialSend

若运行485发送测试程序，则运行
sudo java  -classpath .:classes:/opt/pi4j/lib/'*' Rs485Send

在另一个pi3(已安装pi4j，串口功能已设置)上切换到串口接收测试程序文件夹receive
运行以下指令编译所有java文件
javac -classpath .:classes:/opt/pi4j/lib/'*' -d . *.java

运行232串口接收程序:
sudo java  -classpath .:classes:/opt/pi4j/lib/'*' PiSerialRecv

若运行485串口接收程序:
sudo java  -classpath .:classes:/opt/pi4j/lib/'*' Rs485Recv
