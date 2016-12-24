package com.j1tth4.excelcreator;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import jxl.Cell;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableCell;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

/**
 * Created by toy_o on 25/8/2559.
 */
public class ExcelCreator {

    private WritableWorkbook workbook;

    public void createWorkbook(File file) throws IOException {
        WorkbookSettings settings = new WorkbookSettings();
        settings.setLocale(new Locale("en", "EN"));
        workbook = Workbook.createWorkbook(file);
    }

    public void createSheet(String sheetName, int index, List<WritableCell> cells) throws WriteException{
        if (workbook != null && cells != null) {
            WritableSheet writableSheet = workbook.createSheet(sheetName, index);
            for (WritableCell cell : cells) {
                writableSheet.addCell(cell);
            }
        }
    }

    public void write() throws IOException, WriteException {
        if(workbook != null){
            workbook.write();
            workbook.close();
        }
    }

//    public static boolean createExcel(File file, String sheetName, List<WritableCell> cells) {
//        boolean isExport = false;
//        WorkbookSettings wbSettings = new WorkbookSettings();
//        wbSettings.setLocale(new Locale("en", "EN"));
//        WritableWorkbook workbook;
//        try {
//            workbook = Workbook.createWorkbook(file);
//            WritableSheet sheet = workbook.createSheet(sheetName, 0);
//            try {
//                for (WritableCell cell : cells) {
//                    sheet.addCell(cell);
//                }
//            } catch (RowsExceededException e) {
//                e.printStackTrace();
//            } catch (WriteException e) {
//                e.printStackTrace();
//            }
//            workbook.write();
//            try {
//                workbook.close();
//            } catch (WriteException e) {
//                e.printStackTrace();
//            }
//            isExport = true;
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return isExport;
//    }
}
