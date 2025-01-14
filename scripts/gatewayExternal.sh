#!/bin/bash

sudo apt install iptables-persistent

# Update /etc/network/interfaces
sudo bash -c 'cat <<EOL > /etc/network/interfaces
source /etc/network/interfaces.d/*
# The loopback network interface and sw-2 interface
auto lo eth0 eth1
iface lo inet loopback
# clientCarAdapter interface
iface eth0 inet static
        address 192.168.3.254
        netmask 255.255.255.0
# sw-2 interface
iface eth1 inet static
        address 192.168.0.254
        netmask 255.255.255.0	
EOL'
# Create /etc/sysctl.conf
echo "net.ipv4.ip_forward=1" | sudo tee /etc/sysctl.conf
# Apply the changes
sudo sysctl -p
sudo sysctl --system
# Flush existing rules
sudo iptables -F
sudo iptables -t nat -F
sudo iptables -X
sudo iptables -t nat -X
# Set default policies to DROP
sudo iptables -P INPUT DROP
sudo iptables -P FORWARD DROP
sudo iptables -P OUTPUT DROP
# Allow established and related connections
sudo iptables -A FORWARD -p tcp -m state --state ESTABLISHED,RELATED -j ACCEPT
# Allow the system to forward network traffic that is intended for the webserver
sudo iptables -A FORWARD -p tcp -d 192.168.0.1 --dport 443 -j ACCEPT
sudo iptables -A FORWARD -p tcp -d 192.168.0.1 --dport 444 -j ACCEPT
# NAT (PREROUTING) rule to redirect HTTPS traffic to VM1 (192.168.0.1)
sudo iptables -t nat -A PREROUTING -p tcp --dport 443 -j DNAT --to-destination 192.168.0.1:443
sudo iptables -t nat -A PREROUTING -p tcp --dport 444 -j DNAT --to-destination 192.168.0.1:444
sudo iptables -t nat -A POSTROUTING -j MASQUERADE
# Drop all other forwarding traffic
sudo iptables -A FORWARD -j DROP
# Check and enable forwarding on all interfaces
sudo sysctl net.ipv4.ip_forward=1
sysctl net.ipv4.conf.all.forwarding
# Restart the network service
sudo /etc/init.d/networking restart
sudo ip link set eth0 up

#Save
sudo sh -c 'iptables-save > /etc/iptables/rules.v4'
sudo systemctl enable netfilter-persistent.service
