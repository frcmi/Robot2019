# Initial Provisioning of the Jetson TX2

Follow these steps to prepare a new Jetson TX2 Board for hosting our components, or to reinitialize an existing board to a clean state.
## Restoring to factory Image

The Jetson TX2 ships with [Jetson Jetpack 3.3](https://developer.nvidia.com/embedded/downloads#?search=jetpack%203.3) preinstalled, including  Ubuntu 16.04 and NVIDIA drivers. If you are provisioning a new TX2 board, you can skip this step and proceed to *Installing Standard Prerequisites*, below.

If you wish to start over and restore a TX2 to the initial Jetpack 3.3 image, you can do so. Unfortunately, the tooling required to reimage the board is only supported for host computers running Ubuntu 14.04 or 16.04. This means it cannot be done directly from a Mac or Windows machine. However, it *is* possible to do it from a virtual machine running Ubuntu 16.04. The instructions to follow generally are derived from those [here](https://devtalk.nvidia.com/default/topic/1023934/jetson-tx1/jetpack-install-from-an-ubuntu-vm-host-on-a-mac/). In summary, you have to create an Ubuntu 16.04 VM with bridge-mode networking and at least 64GB of storage, pass the USB Jetson board device through to the VM, and run the remaining steps as if you were on native Ubuntu. NOTE: These instructions were tested on Ubuntu 16.04 running under VMWare Fusion on a MacBook Pro.

### Connect the Jetson board to the Ubuntu host for imaging
* Disconnect power from Jetson
* Connect both the host machine and the Jetson board to the same   Ethernet/IP router subnet, with access to the internet (the MIHS firewall will probably not work, though this has not been tested).
* Using a USB to micro-USB cable, connect the Jetson board's micro-USB port to a USB port on the host machine
Start the Ubuntu Host machine or VM
* Reconnect power to the Jetson
* On the Jetson board, enable Force Recovery mode by doing the following in quick succession:
    * Press and release the POWER button (last from edge)
    * Press and hold the RECOVERY FORCE button (third from edge)
    * While holding the RECOVERY FORCE button, press and release the RESET button (first from edge)
    * Continue to hold the RECOVERY FORCE button for two seconds
    * Release the RECOVERY FORCE button
* At this point, if using a VM, there may be a popup on the host asking if the new USB device should be assigned to the host or the Linux VM. Make sure it is assigned to the Linux VM. If not prompted, ensure "NVidia Corp. APX..." Is on the list of USB devices passed to the guest VM.
* In the Ubuntu host or VM, run `lsusb` and ensure that  `NVidia Corp.` is listed as a USB device.

### Download Jetpack 3.3
In the Ubuntu host or VM, browse to [Jetpack 3](https://developer.nvidia.com/embedded/downloads#?search=jetpack%203.3) and download it. You will need to login with an NVIDIA account (they are free to create).

### Run the installer you downloaded
Basically, `chmod +x` the file you just downloaded, and then run it. It's fairly self-explanatory. Choose a full install. Detailed instructions can be found [here](https://docs.nvidia.com/jetpack-l4t/3.1/index.html#developertools/mobile/jetpack/l4t/3.1/jetpack_l4t_install.htm). If you are prompted for a username/password to log into the Jetson board, use `nvidia`, `nvidia`.

Doing all this from scratch takes from time, but if you leave the intermediate results installed on your host-side Ubuntu VM, you can reimagine the Jetson board in a reasonably short time.

After successfully completing the installation:
* Disconnect the USB cable between the Ubuntu host or VM and the Jetson board
* 
After you have successfully completed this step, the Jetson TX2 is in factory-reset state and you can proceed with provisioning MIHS robotics-specific stuff.

## Installing Standard Prerequisites

