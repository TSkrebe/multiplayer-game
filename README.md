multiplayer-game
================
DEMO

http://youtu.be/HUbNPIwcu_4

This repository consists of 3 tightly related projects -- client side, server side and library 
which is shared by both client and server. 


/*********Running projects************/

//Server side
To run MultiPlayerServer project you have to pass only one argument -- TCP port. This number has to be
the same as the in the client side (look below) otherwise client will not be able to connect to server.
example:
7896

//Client side
To run MultiPlayerClient project you have to pass arguments in such order
1. IP of server where server side is running
2. TCP port of server
3. UDP port through which the game play will be updated (firewalls and router might block incomming traffic)

example:
If server runs on localhost:
localhost 7896 1234 




