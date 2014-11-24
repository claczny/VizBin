#####
# Basic Ubuntu setup
#####
# Resolve dependencies on a system-level (most convenient)
time sudo apt-get install virtualbox-guest-x11 vim subversion git openjdk-7-jre autoconf bison cmake flex g++ intltool libtool scons autopoint gperf ruby libgsl0ldbl libblas-dev libgsl0-dev libboost-all-dev clang llvm-dev automake autogen libtool patch libxml2-dev uuid-dev libssl-dev libmpc-dev libmpfr-dev libgmp-dev ant openjdk-7-jdk

#####
# Get and build cross-compiler and toolchain for OSX
#####
cd $HOME
# Get OSXcross code
git clone https://github.com/tpoechtrager/osxcross.git && cd osxcross
# Put the Mac OS X SDK into the right place
cd tarballs
#wget <PUT THE URL TO YOUR MAC OS X SDK (as tar.gz) here>
wget https://www.dropbox.com/s/ovrs70zl1yr7gg3/MacOSX10.7.sdk.tar.gz
# Do the actual build of osxcross
cd ../
time ./build.sh
time ./build_gcc.sh
# Print instructions to set the shell environment (LD_LIBRARY_PATH and PATH)
#target/bin/osxcross-env
# Prepare BOOST
cd $HOME
mkdir boost && cd boost
wget --content-disposition "http://downloads.sourceforge.net/project/boost/boost/1.55.0/boost_1_55_0.tar.gz?r=http%3A%2F%2Fsourceforge.net%2Fprojects%2Fboost%2Ffiles%2Fboost%2F1.55.0%2F&ts=1402397648&use_mirror=softlayer-ams"
tar -xzf boost_1_55_0.tar.gz

#####
# Get and build cross-compiler and toolchain for WINDOWS
#####
cd $HOME
# Get MXE
git clone https://github.com/mxe/mxe.git && cd mxe

# Set the suitable target
cat <<EOF > settings.mk
MXE_TARGETS := x86_64-w64-mingw32.static
EOF

# Build
time make -j4 gcc libgomp cblas boost

# Post install
#export PATH=`pwd`/usr/bin:$PATH
