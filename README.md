# Setting up the minimal build environment
This process is expected to take about 1 hour, depending on the available hardware. Only minor assistance is needed.

## Setting up the virtual machine
The current minimal build environment is based on an Ubuntu 14.04 LTS 64bit installation in a VirtualBox VM.
Simply download [VirtualBox](https://www.virtualbox.org/) and [Ubuntu 14.04 LTS 64bit](http://releases.ubuntu.com/14.04/ubuntu-14.04-desktop-amd64.iso).
Then create a VM with at least 20GB of disk space (just to make sure) and 4GB (if possible 8GB) of memory, and run the installation of Ubuntu.

N.B. Maybe get yourself some more cores in the VM than the default of one core (Right-click on your VM -> "Settings" -> "System" -> "Processor").

P.S. We recommend running

```sudo apt-get update```

after the succesful installation of ubuntu to make sure all the ubuntu repositories etc. are up-to-date. 

## Setting up the ubuntu installation
Open a terminal window and make sure that you are in `$HOME`. Run

```svn co svn+ssh://tsternal@gforge.uni.lu/svn/binning/trunk```

to create your local copy of the repository in your `$HOME`.

In `trunk/`, we provide a setup script, `setupUbuntu.sh`, that installs the requirements. It uses apt-get as well as downloads and installs required packages ([MXE](http://mxe.cc/) and [OSXCross](https://github.com/tpoechtrager/osxcross) ).
Please specify a link where to get the Mac OS X SDK in `trunk/setupUbuntu.sh` as we do not provide this by default. We usually put this as a `.tar.gz`ipped tarball on Dropbox and specify the respective link in `trunk/setupUbuntu.sh`.

Calling the setup script will download and install the requirements. Hence, assuming you are in `$HOME`, simply run

```bash trunk/setupUbuntu.sh```.

This _will_ require root privileges which should not be a problem since we are working in a VM.

`trunk/setupUbuntu.sh` will install `virtualbox-guest-x11` additions. This will allow to use the full display resolution also for the VM.

---

# Building the dimension reduction binary
VizBin is based on C/C++ code which is compiled into a binary to allow fast computation of the two-dimensional embeddding.
If you have just downloaded the repository, precompiled binaries for Linux, Windows and Mac OS X are provided.
Should you be interested in modifying the underlying source code, you will simply have to run `make` in the `trunk/bh_tsne` directory to recompile the code integrating your modifications.

(**TODO**) Running `make install` will put the recompiled binaries into the right destination for later packaging along the Java GUI.

---

# Building the Java GUI
(**TODO**) Should you have made changes to the Java code and would now like to create a distributable `.jar` file, you simply have to run `XYZ ANT CMD`.
