package com.homeproject.namematcher.util;

import picocli.CommandLine.IVersionProvider;


public class VersionProvider implements IVersionProvider {
    @Override
    public String[] getVersion() {
        return new String[] {
                "version - 0.1.0",
                "artifactId - namematcher" };
    }
}
