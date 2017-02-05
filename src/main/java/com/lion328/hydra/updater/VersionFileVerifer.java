package com.lion328.hydra.updater;

import com.lion328.xenonlauncher.downloader.verifier.FileVerifier;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class VersionFileVerifer implements FileVerifier
{

    private final String remoteVersion;
    private final String[] arguments;

    public VersionFileVerifer(String remoteVersion, String[] arguments)
    {
        this.remoteVersion = remoteVersion;
        this.arguments = arguments;
    }

    public String getRemoteVersion()
    {
        return remoteVersion;
    }

    public String[] getArguments()
    {
        return arguments.clone();
    }

    protected String getVersion(File jarFile) throws IOException
    {
        ProcessBuilder pb = Util.createJarProcessBuilder(jarFile, null, Arrays.asList(getArguments()));
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        byte[] buffer = new byte[512];
        int len;

        Process process = pb.start();
        InputStream in = process.getInputStream();

        while((len = in.read(buffer)) > 0)
        {
            bao.write(buffer, 0, len);
        }

        process.destroy();

        return new String(bao.toByteArray(), StandardCharsets.UTF_8);
    }

    @Override
    public boolean isValid(File file) throws IOException
    {
        return file.exists() && getVersion(file).trim().equals(remoteVersion);
    }
}
