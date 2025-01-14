#!/bin/bash

sudo apt install iptables-persistent

# First clear all rules
sudo iptables -F
# Set default policies
sudo iptables -P INPUT DROP
sudo iptables -P FORWARD DROP
sudo iptables -P OUTPUT DROP
# Add only the necessary forwarding rules
sudo iptables -A FORWARD -s 192.168.1.1 -d 192.168.2.1 -p tcp -i eth0 --dport 27017 -m state --state NEW -j ACCEPT
sudo iptables -A FORWARD -s 192.168.2.1 -d 192.168.1.1 -p tcp -i eth1 --sport 27017 -m state --state NEW -j ACCEPT
sudo iptables -A FORWARD -m state --state RELATED,ESTABLISHED -j ACCEPT

sudo bash -c 'cat <<EOL > /etc/network/interfaces
source /etc/network/interfaces.d/*
# The loopback network interface, sw-3 interface and sw-1 interface
auto lo eth0 eth1
iface lo inet loopback
# sw-3 interface
iface eth0 inet static         
        address 192.168.1.254
        netmask 255.255.255.0	

# sw-1 interface
iface eth1 inet static         
        address 192.168.2.254
        netmask 255.255.255.0
EOL'
# Create /etc/sysctl.conf
echo "net.ipv4.ip_forward=1" | sudo tee /etc/sysctl.conf
# Apply the changes
sudo sysctl -p
sudo sysctl --system
# Restart just in case
sudo /etc/init.d/networking restart

#Save
sudo sh -c 'iptables-save > /etc/iptables/rules.v4'
sudo systemctl enable netfilter-persistent.service




