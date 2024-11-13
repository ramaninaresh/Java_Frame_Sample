#!/bin/bash

# Set desired Chrome version
CHROME_VERSION="130.0.6723.117"
CHROMEDRIVER_VERSION="131.0.6778.69"
CHROME_DOWNLOAD_URL="https://dl.google.com/linux/direct/google-chrome-stable_${CHROME_VERSION}-1_amd64.deb"
CHROMEDRIVER_DOWNLOAD_URL="https://storage.googleapis.com/chrome-for-testing-public/131.0.6778.69/linux64/chromedriver-linux64.zip"

# Install Chrome version 130
echo "Installing Chrome version $CHROME_VERSION..."
wget -q --show-progress --https-only --timestamping "$CHROME_DOWNLOAD_URL"
sudo dpkg -i "google-chrome-stable_${CHROME_VERSION}-1_amd64.deb"
sudo apt --fix-broken install -y

# Download and install ChromeDriver version 131
echo "Installing ChromeDriver version $CHROMEDRIVER_VERSION..."
wget -q --show-progress "$CHROMEDRIVER_DOWNLOAD_URL"
unzip -q chromedriver_linux64.zip
chmod +x chromedriver
sudo mv chromedriver /usr/local/bin/

# Verify the installations
google-chrome --version
chromedriver --version

echo "Chrome version $CHROME_VERSION and ChromeDriver version $CHROMEDRIVER_VERSION installed successfully!"
