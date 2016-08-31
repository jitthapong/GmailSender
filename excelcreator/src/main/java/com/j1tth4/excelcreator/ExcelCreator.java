package com.j1tth4.excelcreator;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

/**
 * Created by toy_o on 25/8/2559.
 */
public class ExcelCreator {

    public static boolean createExcel(File file, String sheet, List<Label> labels){
        boolean isExport = false;
        WorkbookSettings wbSettings = new WorkbookSettings();
        wbSettings.setLocale(new Locale("en", "EN"));
        try {
            WritableWorkbook workbook = Workbook.createWorkbook(file);
            WritableSheet wbSheet = workbook.createSheet(sheet, 0);
            try {
                for (Label label : labels) {
                    wbSheet.addCell(label);
                }
            } catch (WriteException e) {
                e.printStackTrace();
            }
            workbook.write();
            isExport = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return isExport;
    }
}
