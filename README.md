&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;![VizBin - Seeing is ... binning](http://claczny.github.io/VizBin/images/vizbin_logo.png)

# DISCLAIMER

VizBin and its code are published under the BSD License (4-clause).
For more details, s.a. the [sample template](https://spdx.org/licenses/BSD-4-Clause).

THIS SOFTWARE IS PROVIDED BY LCSB AND CONTRIBUTORS "AS IS" AND ANY
EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL LCSB OR ITS CONTRIBUTORS BE
LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN
IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

This product includes software developed by the Delft University of Technology.

# BUILD

Pre-built files are available on the [Release page](https://github.com/claczny/VizBin/releases) so you can use VizBin right away and you do **not** need to build it.
However, should you wish to build VizBin on your own, e.g., because you introduced some modifications, you are free to do so!
Please take a look at platform-specific installation instructions as the whole build process has several requirements.
Moreover, you might want to have a look at the wiki, specifically the [Minimal Build Environment](https://github.com/claczny/VizBin/wiki/Minimal-Build-Environment) for additional information.

Generally, the build process can be triggered by running
```
$ make all
$ make install
```
.
This will build the `VizBin-dist.jar` file that can be run on Linux, Windows and OSX.

## Linux

Please install the following dependencies or make sure they are installed on your system:
```
sudo apt-get update
sudo apt-get install -y libblas-dev libboost-all-dev libgsl0-dev maven
```

Now we can build VizBin:
```
$ make linux
$ make install
```

This is included in travis' continuous integration script - `build-linux` job in `.travis.yml`.

## Windows

To create a binary file for Windows on Linux, we need to cross-compile the application.
We use [mxe](https://github.com/mxe/mxe) for that.
All requirements can be installed as debian dependencies:

```
sudo apt-get update
sudo apt-get install -y maven apt-transport-https
sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys 86B72ED9
sudo add-apt-repository 'deb [arch=amd64] http://mirror.mxe.cc/repos/apt trusty main'
sudo apt-get update
sudo apt-get install -y mxe-x86-64-w64-mingw32.static-gcc \
  mxe-x86-64-w64-mingw32.static-cblas \
  mxe-x86-64-w64-mingw32.static-boost \
  mxe-x86-64-w64-mingw32.static-blas
export PATH=$PATH:/usr/lib/mxe/usr/bin
```

Now we can build VizBin:
```
$ make linux
$ make install
```

This is included in travis' continuous integration script - `build-windows` job in `.travis.yml`.

## OSX

To create a binary file for OSX on Linux, we need to cross-compile the application.
We use [osxcross](https://github.com/tpoechtrager/osxcross) for that.
Unfortunately, the installation is not as straightforward as for the Windows binary because we require the Mac OS X SDK.
However, you can download it after accepting the [SDK license](https://download.developer.apple.com/Developer_Tools/Xcode_10.3/Xcode_10.3.xip). 
The setup can be finished by executing (assuming that `Xcode_10.3.xip` file is in your `$HOME` directory):

```
sudo apt-get update
sudo apt-get install -y clang make libssl-devel lzma-devel libxml2-devel

cd $HOME
git clone https://github.com/tpoechtrager/osxcross.git
cd osxcross
./tools/gen_sdk_package_pbzx.sh $HOME/Xcode_10.3.xip
mv MacOSX10.14.sdk.tar.xz tarballs/
./build.sh
./build_gcc.sh
cd $HOME
mkdir boost && cd boost
wget --content-disposition "http://downloads.sourceforge.net/project/boost/boost/1.55.0/boost_1_55_0.tar.gz?r=http%3A%2F%2Fsourceforge.net%2Fprojects%2Fboost%2Ffiles%2Fboost%2F1.55.0%2F&ts=1402397648&use_mirror=softlayer-ams"
tar -xzf boost_1_55_0.tar.gz
```

Now we can build VizBin:
```
$ make linux
$ make install
```

------
![University of Luxembourg](http://claczny.github.io/VizBin/images/Logo_Uni_quadri_88px.jpg) &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; ![Fonds National de la Recherche](http://claczny.github.io/VizBin/images/fnr.gif) &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; ![Luxembourg Centre for Systems Biomedicine](http://claczny.github.io/VizBin/images/LCSB_short_large_RGB_88px.jpg)
