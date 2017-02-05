package com.lion328.hydra.updater;

import com.lion328.xenonlauncher.downloader.Downloader;
import com.lion328.xenonlauncher.downloader.DownloaderCallback;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

public class HydraUpdaterUI
{

    private Downloader downloader;
    private JProgressBar progressBar;
    private Thread thread;
    private JFrame frame;
    private boolean state;
    private boolean forceStop;

    public HydraUpdaterUI(Downloader downloader)
    {
        this.downloader = downloader;

        init();
    }

    private void init()
    {
        forceStop = false;

        progressBar = new JProgressBar();
        progressBar.setPreferredSize(new Dimension(400, 25));
        progressBar.setStringPainted(true);
        progressBar.setValue(0);

        JLabel label = new JLabel("กำลังดาวน์โหลด...");
        label.setForeground(Color.DARK_GRAY);

        JButton cancelButton = new JButton("ยกเลิก");
        cancelButton.addMouseListener(new MouseAdapter()
        {

            @Override
            public void mouseClicked(MouseEvent e)
            {
                stop();
            }
        });

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(Color.WHITE);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(10, 10, 10, 10);

        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 2;
        panel.add(progressBar, constraints);

        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.anchor = GridBagConstraints.WEST;
        panel.add(label, constraints);

        constraints.gridx = 1;
        constraints.anchor = GridBagConstraints.EAST;
        panel.add(cancelButton, constraints);

        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        frame.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                stop();
            }
        });

        frame.setLocationRelativeTo(null);
        frame.setSize(400, 100);
        frame.setContentPane(panel);
        frame.setResizable(false);

        try
        {
            frame.setIconImage(ImageIO.read(this.getClass().getResourceAsStream("/com/lion328/hydra/updater/resources/favicon.png")));
        }
        catch (IOException ignore)
        {

        }

        frame.setTitle("กำลังปรับปรุง Launcher");
        frame.pack();
        frame.setVisible(true);

        downloader.registerCallback(new DownloaderCallback()
        {

            private boolean updatingStatus = false;

            @Override
            public void onPercentageChange(File file, final int overallPercentage, final long fileSize, final long fileDownloaded)
            {
                if (updatingStatus)
                {
                    return;
                }

                SwingUtilities.invokeLater(new Runnable()
                {

                    @Override
                    public void run()
                    {
                        StringBuilder sb = new StringBuilder();
                        sb.append(overallPercentage);
                        sb.append("%");

                        if (fileDownloaded > 0 && fileSize > 0)
                        {
                            sb.append(", ");
                            sb.append(Util.convertUnit(fileDownloaded));
                            sb.append("B/");
                            sb.append(Util.convertUnit(fileSize));
                            sb.append("B");
                        }

                        progressBar.setString(sb.toString());
                        progressBar.setValue(overallPercentage);
                    }
                });
            }
        });
    }

    public void start()
    {
        thread = new Thread()
        {

            @Override
            public void run()
            {
                try
                {
                    downloader.download();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "ไม่สามารถปรับปรุง Launcher ได้", "เกิดข้อผิดพลาด", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                frame.setVisible(false);
                state = !forceStop;
            }
        };

        thread.start();
    }

    public void stop()
    {
        if (!isRunning())
        {
            return;
        }

        forceStop = true;
        downloader.stop();
    }

    public JFrame getFrame()
    {
        return frame;
    }

    public boolean isRunning()
    {
        return downloader.isRunning();
    }

    public boolean waitFor() throws InterruptedException
    {
        thread.join();
        boolean state = this.state;
        this.state = false;
        return state;
    }
}
