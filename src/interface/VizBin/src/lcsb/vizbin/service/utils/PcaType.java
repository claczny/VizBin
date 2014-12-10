package lcsb.vizbin.service.utils;

public enum PcaType {
	MTJ("Mtj"), MTJ_OPTIMIZED("Mtj optimized"), EJML("EJML");

	String	commonName	= null;

	private PcaType(String name) {
		commonName = name;
	}

	public String getName() {
		return commonName;
	}
}
