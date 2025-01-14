#!/bin/bash

# Update /etc/network/interfaces
sudo bash -c 'cat <<EOL > /etc/network/interfaces
source /etc/network/interfaces.d/*

# The loopback network interface, sw-2 interface and sw-3 interface
auto lo eth0 eth1
iface lo inet loopback

# sw-2 interface
iface eth0 inet static          
        address 192.168.0.1
        netmask 255.255.255.0
        gateway 192.168.0.254

# sw-3 interface
iface eth1 inet static
		address 192.168.1.1
		netmask 255.255.255.0
		gateway 192.168.1.254

EOL'

# Restart the network service
sudo /etc/init.d/networking restart
