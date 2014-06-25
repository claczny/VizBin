# This Makefile is based on the setup that is created when using the setup.sh script (i.e. steps 01-03)
# Maybe setting the compiler etc. could be made even nicer. However, the current way matches the setup created using the setup.sh script.

#####
# COMPILER SETTINGS
#####
# For Linux
CXX = g++
# For Windows
MXE = $(HOME)/mxe/usr
MXETARGET = x86_64-w64-mingw32.static
MXECROSS = $(MXE)/$(MXETARGET)
WIN_CXX = $(MXETARGET)-g++
# For Mac OS X
OSXCROSS = $(HOME)/osxcross/target
OSX_CXX = o64-g++

#####
# SOURCES
#####
SOURCES = quadtree.cpp tsne.cpp
PSOURCES = quadtree.cpp ptsne.cpp

#####
# C++ COMPILER FLAGS
#####
#CXXFLAGS = -O3 -pedantic -Wall -Wextra
CXXFLAGS = -O3
CXXFLAGS_LINUX = $(CXXFLAGS) -static -I$(HOME)/boost/boost_1_55_0
CXXFLAGS_WIN = $(CXXFLAGS) -static -I$(MXECROSS)/include
CXXFLAGS_OSX = $(CXXFLAGS) -I$(HOME)/boost/boost_1_55_0 -I$(OSXCROSS)/SDK/MacOSX10.7.sdk/System/Library/Frameworks/vecLib.framework/Versions/A/Headers

#####
# LINKER FLAGS
#####
#LDFLAGS_LINUX =
LDLIBS_LINUX = -lgslcblas 

LDFLAGS_WIN = -L$(MXECROSS)/lib
LDLIBS_WIN = -lcblas -lblas -lgfortran -lquadmath

LDFLAGS_OSX = -L$(OSXCROSS)/SDK/MacOSX10.7.sdk/usr/lib
LDLIBS_OSX = -lcblas

.PHONY:	all install linux windows osx clean clean_linux clean_windows clean_osx

all: linux windows osx

#####
# LINUX TARGET
#####
linux: pbh_tsne

bh_tsne: $(SOURCES)
	@echo "BUILDING: serial version for LINUX ..."
	$(CXX) $(CXXFLAGS_LINUX) -o $@ $? $(LDFLAGS) $(LDLIBS_LINUX)
	@echo "done."

pbh_tsne: CXXFLAGS_LINUX += -fopenmp 
pbh_tsne: $(PSOURCES)
	@echo "BUILDING: parallel version for LINUX ..."
	$(CXX) $(CXXFLAGS_LINUX) -o $@ $? $(LDFLAGS) $(LDLIBS_LINUX)
	@echo "done."

#####
#  WINDOWS TARGET
#####
windows: export PATH := $(MXE)/bin/:$(PATH)
windows: pbh_tsne.exe

bh_tsne.exe: $(SOURCES)
	@echo "BUILDING: serial version for WINDOWS ..."
	$(WIN_CXX) $(CXXFLAGS_WIN) -o $@ $? $(LDFLAGS_WIN) $(LDLIBS_WIN)
	@echo "done."

pbh_tsne.exe: CXXFLAGS_WIN += -fopenmp
pbh_tsne.exe: $(PSOURCES)
	@echo "BUILDING: parallel version for WINDOWS ..."
	$(WIN_CXX) $(CXXFLAGS_WIN) -o $@ $? $(LDFLAGS_WIN) $(LDLIBS_WIN)
	@echo "done."

#####
# MAC OS X TARGET
#####
osx: export PATH := $(OSXCROSS)/bin:$(PATH)
osx: export LD_LIBRARY_PATH := $(OSXCROSS)/lib:$(LD_LIBRARY_PATH)
osx: pbh_tsne_osx

bh_tsne_osx: $(SOURCES)
	@echo "BUILDING: serial (dynamic) version for OSX ..."
	$(OSX_CXX) $(CXXFLAGS_OSX) -o $@ $? $(LDFLAGS_OSX) $(LDLIBS_OSX)
	@echo "done."

pbh_tsne_osx: CXXFLAGS_OSX += -fopenmp 
pbh_tsne_osx: $(PSOURCES)
	@echo "BUILDING: parallel (dynamic) version for OSX ..."
	$(OSX_CXX) $(CXXFLAGS_OSX) -o $@ $? $(LDFLAGS_OSX) $(LDLIBS_OSX)
	@echo "done."

#####
# CLEAN
#####
clean: clean_linux clean_windows clean_osx

clean_linux:
	rm -f bh_tsne pbh_tsne 

clean_windows:
	rm -f bh_tsne.exe pbh_tsne.exe

clean_osx:
	rm -f bh_tsne_osx pbh_tsne_osx
