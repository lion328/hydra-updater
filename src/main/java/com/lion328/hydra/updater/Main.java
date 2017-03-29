package com.lion328.hydra.updater;

import com.lion328.xenonlauncher.downloader.FileDownloader;
import com.lion328.xenonlauncher.downloader.URLFileDownloader;
import com.lion328.xenonlauncher.downloader.VerifiyFileDownloader;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.io.IOException;

public class Main
{

    public static void main(String[] args)
    {
        Settings settings = Settings.getInstance();

        if (settings == null)
        {
            JOptionPane.showMessageDialog(null, "ไม่สามารถใช้งาน Launcher ได้", "เกิดข้อผิดพลาด", JOptionPane.ERROR_MESSAGE);

            return;
        }

        if (settings.isLaunchWithIPv4() && !System.getProperty("java.net.preferIPv4Stack", "false").equalsIgnoreCase("true"))
        {
            try
            {
                Util.createJarProcessBuilderIPv4(Util.getWorkingJar()).start();
            }
            catch (IOException e)
            {
                e.printStackTrace();

                JOptionPane.showMessageDialog(null, "ไม่สามารถเปิด Launcher ได้ (" + e.getMessage() + ")", "เกิดข้อผิดพลาด", JOptionPane.ERROR_MESSAGE);
            }

            return;
        }

        String remoteVersion;

        try
        {
            remoteVersion = Util.httpGET(settings.getRemoteVersionUrl()).trim();
        }
        catch (IOException e)
        {
            e.printStackTrace();

            JOptionPane.showMessageDialog(null, "ไม่สามารถปรับปรุง Launcher ได้ (" + e.getMessage() + ")", "เกิดข้อผิดพลาด", JOptionPane.ERROR_MESSAGE);

            return;
        }

        System.out.println("Remote launcher version: " + remoteVersion);

        FileDownloader downloader = new URLFileDownloader(settings.getRemoteLauncherUrl(), settings.getLocalLauncherFile());
        downloader = new VerifiyFileDownloader(downloader, new VersionFileVerifer(remoteVersion, settings.getGetVersionLauncherArguments()));

        HydraUpdaterUI updater = new HydraUpdaterUI(downloader);

        JFrame updaterFrame = updater.getFrame();
        updaterFrame.setLocationRelativeTo(null);
        updaterFrame.setVisible(true);

        updater.start();

        try
        {
            updater.waitFor();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        ProcessBuilder pb = null;

        if (settings.isLaunchWithIPv4())
        {
            pb = Util.createJarProcessBuilderIPv4(settings.getLocalLauncherFile());
        }
        else
        {
            pb = Util.createJarProcessBuilder(settings.getLocalLauncherFile(), null, null);
        }

        try
        {
            pb.start();
        }
        catch (IOException e)
        {
            e.printStackTrace();

            JOptionPane.showMessageDialog(null, "ไม่สามารถเปิด Launcher ได้ (" + e.getMessage() + ")", "เกิดข้อผิดพลาด", JOptionPane.ERROR_MESSAGE);
        }

        System.exit(0);
    }
}
