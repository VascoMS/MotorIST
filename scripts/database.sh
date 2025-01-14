#!/bin/bash

sudo apt install python3
sudo apt install python3-pip


sudo apt install python3.12-venv
#Create and activate python venv
python -m venv mongo_venv
source mongo_venv/bin/activate
pip install pymongo
#Close venv
deactivate

#Docker
sudo apt install docker.io
sudo systemctl enable --now docker
docker pull mongo:4.4

# Update /etc/network/interfaces
sudo bash -c 'cat <<EOL > /etc/network/interfaces
source /etc/network/interfaces.d/*

# The loopback network interface and sw-1 interface
auto lo eth0
iface lo inet loopback

# sw-1 interface
iface eth0 inet static          
        address 192.168.2.1
        netmask 255.255.255.0
        gateway 192.168.2.254
EOL'


