multiplayer-game
================
This repository consists of 3 tightly related projects -- client side, server side and library 
which is shared by both client and server. 


//setting up IPs and Ports

If you want this program to work not only on your own machine, make sure you change 
server's ip (ClientSide project, TcpConnection.java). If server side runs on LAN  
refer to it with internal ip (192.168.x.x), if server side runs outside your LAN
put down ip of that server. 

Tcp connection with server is based on 4444 port. To change that you have to change port on both
sides. (TcpConnection.java on client and server)

In client side UdpConnection.java file you can change port number though which the data about game-play will be 
updated. By defaut any available port will be assign. This makes it possible
to run multiple clients on the same pc (usefull for testing). However if you 
want client to connect with server through specific port redefine UDP_PORT 
constant(clientSide project, UdpConnection.java). Make sure that port is not 
blocked by firewalls or router(port forward it in that case) and port is
in correct range 0-65535 otherwise random port will be assigned.


