Multiplayer 2D Java game
========================

Youtube DEMO
http://youtu.be/HUbNPIwcu_4
 
 
This repository consists of 2 projects: client side and server side.

Data is shared through TCP and UPD protocols


Client ----> Server  | TCP

Server ----> Clients | UDP


Written in Java SE 8.

 
**Running projects**
 
 
##Server side 
 
To run MultiPlayerServer project you have to pass only one argument -- TCP port.

java -jar serverSide.jar 10000
 
 
##Client side 

Default lwjgl natives are compatible with LINUX. Change that if you use other OS. Look into lib directory.
 
To run MultiPlayerClient project you have to pass arguments in such order 

1. IP of server where server side is running 
2. TCP port of server 
3. UDP port through which the game play will be updated (firewalls and router might block incoming traffic) DEFAULT random
 

Example: 
If server runs on localhost: 

localhost 7896 1234  

