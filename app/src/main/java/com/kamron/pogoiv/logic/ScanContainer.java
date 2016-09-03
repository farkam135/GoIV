package com.kamron.pogoiv.logic;

/**
 * Created by Johan on 2016-08-19.
 * <p/>
 * A class which keeps the 2 most recent IV scans in memory
 */
public class ScanContainer {
    public IVScanResult oneScanAgo = null;
    public IVScanResult twoScanAgo = null;

    public ScanContainer() {
    }

    /**
     * pushes the 3 scan ago out of memory, and remembers the two latest scanns
     *
     * @param res
     */
    public void addNewScan(IVScanResult res) {
        twoScanAgo = oneScanAgo;
        oneScanAgo = res;
    }
}
