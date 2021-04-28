package community.hlresort.holomatsuri.commands;

import community.hlresort.holomatsuri.pointCounter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class export implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        String path = pointCounter.plugin.getDataFolder() + "/exports/" + new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date()) + ".xlsx";
        HashMap<String, Integer> buildPoints = new HashMap<>();
        HashMap<String, Integer> deliveryPoints = new HashMap<>();

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet buildSheet = workbook.createSheet("build");
        XSSFSheet deliverySheet = workbook.createSheet("chest");
        XSSFSheet buildPointsSheet = workbook.createSheet("buildPoints");
        XSSFSheet deliveryPointsSheet = workbook.createSheet("chestPoints");

        try {
            Statement statement = pointCounter.conn.createStatement();
            ResultSet buildResult = statement.executeQuery("SELECT * FROM builds");

            Row buildSheetHeaderRow = buildSheet.createRow(0);
            for(int i = 1; i <= buildResult.getMetaData().getColumnCount(); i++) {
                buildSheetHeaderRow.createCell(i-1).setCellValue(buildResult.getMetaData().getColumnName(i));
            }

            Row buildPointsSheetHeaderRow = buildPointsSheet.createRow(0);
            buildPointsSheetHeaderRow.createCell(0).setCellValue("uuid");
            buildPointsSheetHeaderRow.createCell(1).setCellValue("name");
            buildPointsSheetHeaderRow.createCell(2).setCellValue("points");

            int buildRowCount = 1;

            while(buildResult.next()) {
                Row row = buildSheet.createRow(buildRowCount++);
                int points = 0;

                for(int i = 1; i <= buildResult.getMetaData().getColumnCount(); i++) {
                    Cell cell = row.createCell(i-1);
                    if(i == 1) {
                        cell.setCellValue(buildResult.getString(i));
                    } else {
                        if(pointCounter.config.get("blocks.build." + buildResult.getMetaData().getColumnName(i)) != null) points = points + ((int) pointCounter.config.get("blocks.build." + buildResult.getMetaData().getColumnName(i)) * buildResult.getInt(i));
                        cell.setCellValue(buildResult.getInt(i));
                    }
                }
                buildPoints.put(buildResult.getString(1), points);
            }

            ResultSet deliveryResult = statement.executeQuery("SELECT * FROM delivery");

            Row deliverySheetHeaderRow = deliverySheet.createRow(0);
            for(int i = 1; i <= buildResult.getMetaData().getColumnCount(); i++) {
                deliverySheetHeaderRow.createCell(i-1).setCellValue(deliveryResult.getMetaData().getColumnName(i));
            }

            Row deliveryPointsSheetHeaderRow = deliveryPointsSheet.createRow(0);
            deliveryPointsSheetHeaderRow.createCell(0).setCellValue("uuid");
            deliveryPointsSheetHeaderRow.createCell(1).setCellValue("name");
            deliveryPointsSheetHeaderRow.createCell(2).setCellValue("points");

            int deliveryRowCount = 1;
            while(deliveryResult.next()) {
                Row row = deliverySheet.createRow(deliveryRowCount++);
                int points = 0;

                for(int i = 1; i <= deliveryResult.getMetaData().getColumnCount(); i++) {
                    Cell cell = row.createCell(i-1);
                    if(i == 1) {
                        cell.setCellValue(deliveryResult.getString(i));
                    } else {
                        if(pointCounter.config.get("blocks.chest." + deliveryResult.getMetaData().getColumnName(i)) != null) points = points + ((int) pointCounter.config.get("blocks.chest." + deliveryResult.getMetaData().getColumnName(i)) * deliveryResult.getInt(i));
                        cell.setCellValue(deliveryResult.getInt(i));
                    }
                }
                deliveryPoints.put(buildResult.getString(1), points);
            }
            statement.close();

            int buildPointsCounter = 1;
            for(Map.Entry<String, Integer> entry : buildPoints.entrySet()) {
                OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(entry.getKey()));
                Row row = buildPointsSheet.createRow(buildPointsCounter++);
                row.createCell(0).setCellValue(entry.getKey());
                row.createCell(1).setCellValue(player.getName());
                row.createCell(2).setCellValue(entry.getValue());
            }

            int deliveryPointCounter = 1;
            for(Map.Entry<String, Integer> entry : deliveryPoints.entrySet()) {
                OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(entry.getKey()));
                Row row = deliveryPointsSheet.createRow(deliveryPointCounter++);
                row.createCell(0).setCellValue(entry.getKey());
                row.createCell(1).setCellValue(player.getName());
                row.createCell(2).setCellValue(entry.getValue());
            }

            File file = new File(path);
            file.getParentFile().mkdirs();
            file.createNewFile();

            FileOutputStream outputStream = new FileOutputStream(path, false);
            workbook.write(outputStream);
            workbook.close();

            sender.sendMessage("Exported");
        } catch (SQLException | IOException e) {
            sender.sendMessage("Something went wrong, please try again.");
            System.out.println(e.getMessage());
        }
        return true;
    }
}
