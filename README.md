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

---
# Tutorial on using VizBin
You can find the tutorial [here](doc/tutorial/tutorial.md).

---

#LICENSES

1. ejml-0.23.jar:
 - Download page: http://code.google.com/p/efficient-java-matrix-library/
 - License type: Apache License 2.0
 - License page: http://www.apache.org/licenses/LICENSE-2.0

2. log4j-1.2.17.jar: 
 - Download page: http://logging.apache.org/log4j/1.2/download.html
 - License type: Apache License 2.0
 - License page: http://www.apache.org/licenses/LICENSE-2.0

3. commons-io-2.4.jar:
 - Download page: http://commons.apache.org/proper/commons-io/download_io.cgi
 - License type: Apache License 2.0
 - License page: http://www.apache.org/licenses/LICENSE-2.0

4. blas.jar (originally blas-0.8.jar):
 - Download page: http://sourceforge.net/projects/f2j/
 - License type: BSD License (3-clause)
 - License page: http://icl.cs.utk.edu/f2j/software/index.html#license

5. Boost:
 - Downlad page: http://www.boost.org/users/download/
 - License type: Boost Software License
 - License page: http://www.boost.org/users/license.html

6. BH-SNE:
 - Download page: http://homepage.tudelft.nl/19j49/t-SNE.html
 - License type: BSD License (4-clause)
 - License page: N/A (sample template: https://spdx.org/licenses/BSD-4-Clause)

The above list is potentially incomplete and represents the main external libraries/binaries used. It may be updated anytime in the future.
