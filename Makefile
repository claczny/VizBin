BACKEND_PATH = src/backend/bh_tsne
INTERFACE_PATH = src/interface/VizBin
PREFIX = src/tsne
CLEANDIRS = $(BACKEND_PATH) $(INTERFACE_PATH)
.PHONY: all backend interface clean install $(CLEANDIRS)

####
#COMPILE ALL
####
all: backend interface

backend:
	 $(MAKE) -C $(BACKEND_PATH)

interface:
	@echo "COPYING: binaries into destination folder ..."
	install -m 0755 $(BACKEND_PATH)/pbh_tsne $(INTERFACE_PATH)/$(PREFIX)/pbh_tsne 
	install -m 0755  $(BACKEND_PATH)/pbh_tsne.exe $(INTERFACE_PATH)/$(PREFIX)/pbh_tsne.exe
	install -m 0755 $(BACKEND_PATH)/pbh_tsne_osx $(INTERFACE_PATH)/$(PREFIX)/pbh_tsne_osx
	@echo "done."
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
	
