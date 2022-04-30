package com.kamron.pogoiv.updater;


import androidx.annotation.NonNull;

public class SemVer implements Comparable<SemVer> {
    private final int major;
    private final int minor;
    private final int patch;

    private SemVer(int major, int minor, int patch) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }

    public static SemVer parse(String versionString) {
        versionString = versionString.trim().replace("v", "");
        String[] versionParts = versionString.split("\\.");
        int majorVersion = Integer.parseInt(versionParts[0]);
        int minorVersion = Integer.parseInt(versionParts[1]);
        int patchVersion = Integer.parseInt(versionParts[2].charAt(0) + "");
        return new SemVer(majorVersion, minorVersion, patchVersion);
    }

    @Override
    public int compareTo(@NonNull SemVer other) {
        int result = major - other.major;
        if (result == 0) {
            result = minor - other.minor;
            if (result == 0) {
                result = patch - other.patch;
            }
        }
        return result;
    }

    @Override
    public boolean equals(Object other) {
        return this == other || other instanceof SemVer && compareTo((SemVer) other) == 0;
    }

    @Override
    public String toString() {
        return String.format("v%d.%d.%d", major, minor, patch);
    }
}
