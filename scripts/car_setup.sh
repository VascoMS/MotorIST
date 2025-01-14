#!/bin/bash



sudo bash -c 'cat <<EOL > /etc/network/interfaces
source /etc/network/interfaces.d/*
# The loopback network interface, sw-3 interface
auto lo eth0 eth1
iface lo inet loopback
# sw-3 interface
iface eth0 inet static         
        address 192.168.3.2
        netmask 255.255.255.0
EOL'
# Create /etc/sysctl.conf
echo "net.ipv4.ip_forward=1" | sudo tee /etc/sysctl.conf
# Apply the changes
sudo sysctl -p
sudo sysctl --system
sudo /etc/init.d/networking restart

