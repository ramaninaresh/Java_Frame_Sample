#!/bin/bash

# Update the package index
echo "Updating package list..."
sudo apt-get update -y

# Install Firefox (latest version)
echo "Installing the latest version of Firefox..."
sudo apt-get install -y firefox

# Verify Firefox installation
firefox --version

echo "Firefox has been installed successfully!"
