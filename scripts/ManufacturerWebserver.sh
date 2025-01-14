#!/bin/bash

#Add the static route
sudo ip route add 192.168.2.0/24 via 192.168.1.254 
#Restart just in case
sudo /etc/init.d/networking restart
#run the server
java -jar ManufacturerWebServer-0.0.1-SNAPSHOT.jar 
