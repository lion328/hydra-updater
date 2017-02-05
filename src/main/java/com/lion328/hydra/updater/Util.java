package com.lion328.hydra.updater;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Util
{

    private static char[] unitTable = new char[] {'\0', 'K', 'M', 'G', 'T'};
    private static File workingJar;

    public static String convertUnit(long l)
    {
        int unit = 0;
        float f = l;

        while (f >= 1024 && (unit + 1 <= unitTable.length))
        {
            f /= 1024;
            unit++;
        }

        if (unit == 0)
        {
            return String.valueOf(l);
        }

        return String.format("%.2f", f) + " " + unitTable[unit] + "i";
    }

    public static String httpGET(URL url) throws IOException
    {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        InputStream is = connection.getInputStream();
        ByteArrayOutputStream baos = new ByteArrayOutputStream(8192);

        byte[] buff = new byte[8192];
        int len;

        while ((len = is.read(buff)) != -1)
        {
            baos.write(buff, 0, len);
        }

        return new String(baos.toByteArray(), StandardCharsets.UTF_8);
    }

    public static ProcessBuilder createJarProcessBuilderIPv4(File jarFile)
    {
        return createJarProcessBuilder(jarFile, Collections.singletonList("-Djava.net.preferIPv4Stack=true"), null);
    }

    public static ProcessBuilder createJarProcessBuilder(File jarFile, List<String> jvmArgs, List<String> progArgs)
    {
        File workingJar = getWorkingJar();
        List<String> args = new ArrayList<>();

        args.add(new File(System.getProperty("java.home"), "bin/java").getAbsolutePath());

        if (jvmArgs != null)
        {
            args.addAll(jvmArgs);
        }

        args.add("-jar");
        args.add(jarFile.getAbsolutePath());

        if (progArgs != null)
        {
            args.addAll(progArgs);
        }

        ProcessBuilder pb = new ProcessBuilder(args);
        pb.directory(jarFile.getParentFile());

        return pb;
    }

    public static File getWorkingJar()
    {
        if (workingJar == null)
        {
            try
            {
                workingJar = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
            }
            catch (URISyntaxException e)
            {
                e.printStackTrace();
            }
        }

        return workingJar;
    }
}
