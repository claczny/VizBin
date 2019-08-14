BACKEND_PATH = src/backend/bh_tsne
INTERFACE_PATH = src/interface/VizBin
PREFIX = src/tsne
CLEANDIRS = $(BACKEND_PATH) $(INTERFACE_PATH)
.PHONY: all backend interface clean install $(CLEANDIRS)

####
#COMPILE ALL
####
all: backend_all interface

linux: backend_linux interface
windows: backend_windows interface
macos: backend_macos interface

backend_all: backend_linux backend_windows backend_macos
	 $(MAKE) -C $(BACKEND_PATH)

backend_linux:
	 $(MAKE) -C $(BACKEND_PATH) linux
	@echo "COPYING: linux binaries into destination folder ..."
	mkdir -p $(INTERFACE_PATH)/$(PREFIX)
	install -m 0755 $(BACKEND_PATH)/pbh_tsne $(INTERFACE_PATH)/$(PREFIX)/pbh_tsne 
	@echo "done."

backend_windows:
	 $(MAKE) -C $(BACKEND_PATH) windows
	@echo "COPYING: windows binaries into destination folder ..."
	mkdir -p $(INTERFACE_PATH)/$(PREFIX)
	install -m 0755  $(BACKEND_PATH)/pbh_tsne.exe $(INTERFACE_PATH)/$(PREFIX)/pbh_tsne.exe
	@echo "done."

backend_macos:
	 $(MAKE) -C $(BACKEND_PATH) macos
	@echo "COPYING: mac OS binaries into destination folder ..."
	mkdir -p $(INTERFACE_PATH)/$(PREFIX)
	install -m 0755 $(BACKEND_PATH)/pbh_tsne_osx $(INTERFACE_PATH)/$(PREFIX)/pbh_tsne_osx
	@echo "done."

interface:
	$(MAKE) -C $(INTERFACE_PATH)

####
#CLEAN
####
clean: $(CLEANDIRS)
$(CLEANDIRS): 
	echo $@
	make -C $@ clean
clean: 
	rm -f VizBin-dist.jar
####
#INSTALL
####
install: $(INSTALLDIRS)
	install -m 0755 $(INTERFACE_PATH)/dist/VizBin-dist.jar . 
	
