
JAR = dist/VizBin-dist.jar
.PHONY: VizBin clean

VizBin:
	mvn compile assembly:single

install: $(JAR)
install:
	@echo "COPYING: jar into root folder ..."
	install -m 0755 $(JAR) ../../../
	@echo "done."

clean:
	mvn clean
	rm -rf src/tsne
