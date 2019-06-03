package top.ageofelysian.scoreboardsaver;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class ScoreboardSaver extends JavaPlugin {

    private static ScoreboardSaver instance = null;

    @Override
    public void onEnable() {
        instance = this;

        //Creating the scoreboard-backups folder
        File to = new File(getServer().getWorldContainer().getPath() + File.separator + "scoreboard-backups");
        if (!to.exists()) {
            to.mkdirs();
        }

        //Deciding if the plugin should automatically backup
        Bukkit.getLogger().info("Starting the automatic scoreboard backup procedure.");
        if (shouldBackup()) {
            Bukkit.getLogger().info("No backups were made this week. Backing up!");
            backupScoreboard(false);
        } else {
            Bukkit.getLogger().info("An automatic backup were made this week. Not backing up.");
        }

        this.getCommand("scoreboard-backup").setExecutor(new Commands());
    }



    private boolean shouldBackup() {
        final File folder = new File(getServer().getWorldContainer().getPath() + File.separator + "scoreboard-backups");
        final File[] files = folder.listFiles();
        if (files == null || files.length == 0) return true;

        //Getting the current date
        final SimpleDateFormat sdf = new SimpleDateFormat("-ww");
        String date = sdf.format(new Date());

        //Checking if a backup is made in the current week
        boolean boo = false;
        for (final File file : files) {
            if (file.getName().contains(date) && !file.getName().contains("MANUAL")) boo = true;
        }
        return boo;
    }

    boolean backupScoreboard(boolean boo) {
        //Getting the current date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-ww");
        String date = sdf.format(new Date());

        //The actual backing up process
        File from = new File(getServer().getWorldContainer().getPath() + File.separator + "world" + File.separator + "data" + File.separator + "scoreboard.dat");
        File to;

        //IF THIS WAS A FORCED BACKUP
        if (boo) {
            File folder = new File(getServer().getWorldContainer().getPath() + File.separator + "scoreboard-backups");
            File[] files = folder.listFiles();

            //IF THERE ARE NO FILES THEN BACK UP AS MANUAL1
            if (files == null || files.length == 0) {
                to = new File(getServer().getWorldContainer().getPath() + File.separator + "scoreboard-backups" + File.separator + "scoreboard(" + date + "MANUAL1).dat");
            } else {
                List<File> list = Arrays.asList(files);
                List<File> list1 = new LinkedList<>(Arrays.asList(files));
                //LIST WILL ONLY CONTAIN THE MANUAL ONES THAT WERE MADE IN THE CURRENT WEEK
                for (File file : list) {
                    boolean boo1 = false;
                    if (!file.getName().contains(date)) {
                        boo1 = true;
                    }
                    if (!file.getName().contains("MANUAL")) {
                        boo1 = true;
                    }
                    if (boo1) list1.remove(file);
                }
                //SORTING THE LAST VERSION SO WE CAN FIND THE LATEST MANUAL BACK UP EASILY
                Collections.sort(list1);

                //THE LAST FILE IS THE LATEST FILE
                File file;
                //if (list1.size() == 0)to = new File(getServer().getWorldContainer().getPath() + File.separator + "scoreboard-backups" + File.separator + "scoreboard(" + date + "MANUAL1).dat");
                file = list1.get(list1.size() - 1);
                String afterDate = file.getName().substring(24);
                String[] parts = afterDate.split("\\)");
                int i = Integer.valueOf(parts[0]) + 1;
                to = new File(getServer().getWorldContainer().getPath() + File.separator + "scoreboard-backups" + File.separator + "scoreboard(" + date + "MANUAL" + i + ").dat");
            }

        //IF THIS WAS NOT A FORCED BACKUP
        } else {
            to = new File(getServer().getWorldContainer().getPath() + File.separator + "scoreboard-backups" + File.separator + "scoreboard(" + date + ").dat");
        }

        try {
            if (!to.exists()) {
                to.createNewFile();
            }

            copyFileUsingStream(from, to);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    private void copyFileUsingStream(File source, File dest) throws IOException {
        try (InputStream is = new FileInputStream(source); OutputStream os = new FileOutputStream(dest)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        }
    }

    static ScoreboardSaver getInstance() {
        return instance;
    }
}
