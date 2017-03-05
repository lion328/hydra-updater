package com.lion328.hydra.updater;

import com.lion328.xenonlauncher.util.OS;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

public class Settings
{

    private static Settings instance;

    private final URL remoteVersionUrl;
    private final URL remoteLauncherUrl;
    private final File localLauncherFile;
    private final String[] getVersionLauncherArguments;
    private final boolean launchWithIPv4;

    public Settings(URL remoteVersionUrl, URL remoteLauncherUrl, File localLauncherFile, String[] getVersionLauncherArguments, boolean launchWithIPv4)
    {
        this.remoteVersionUrl = remoteVersionUrl;
        this.remoteLauncherUrl = remoteLauncherUrl;
        this.localLauncherFile = localLauncherFile;
        this.getVersionLauncherArguments = getVersionLauncherArguments;
        this.launchWithIPv4 = launchWithIPv4;
    }

    public URL getRemoteVersionUrl()
    {
        return remoteVersionUrl;
    }

    public URL getRemoteLauncherUrl()
    {
        return remoteLauncherUrl;
    }

    public File getLocalLauncherFile()
    {
        return localLauncherFile;
    }

    public String[] getGetVersionLauncherArguments()
    {
        return getVersionLauncherArguments.clone();
    }

    public boolean isLaunchWithIPv4()
    {
        return launchWithIPv4;
    }

    public static Settings getInstance()
    {
        if (instance == null)
        {
            Properties properties = new Properties();

            try
            {
                properties.load(Settings.class.getResourceAsStream("/com/lion328/hydra/updater/resources/config.properties"));
            }
            catch (IOException e)
            {
                e.printStackTrace();

                return null;
            }

            URL remoteVersionUrl, remoteLauncherUrl;

            try
            {
                remoteVersionUrl = Util.handleRedirectedURL(new URL(replaceParameters(properties.getProperty("remoteVersionUrl"))));
                remoteLauncherUrl = Util.handleRedirectedURL(new URL(replaceParameters(properties.getProperty("remoteLauncherUrl"))));
            }
            catch (IOException e)
            {
                e.printStackTrace();

                return null;
            }

            File localLauncherFile = new File(replaceParameters(properties.getProperty("localLauncherFile")));
            String[] getVersionLauncherArguments = properties.getProperty("getVersionLauncherArguments").split(" ");
            boolean launchWithIPv4 = properties.getProperty("launchWithIPv4").equalsIgnoreCase("true");

            instance = new Settings(remoteVersionUrl, remoteLauncherUrl, localLauncherFile, getVersionLauncherArguments, launchWithIPv4);
        }

        return instance;
    }

    private static String replaceParameters(String s) {
        s = s.replace("${timestamp}", String.valueOf(System.currentTimeMillis()));
        s = s.replace("${appdata}", OS.getApplicationDataDirectory().getAbsolutePath());

        return s;
    }
}
