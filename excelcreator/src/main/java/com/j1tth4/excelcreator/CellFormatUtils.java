package com.j1tth4.excelcreator;

import jxl.write.NumberFormat;
import jxl.write.WritableCellFormat;

/**
 * Created by toy_o on 2/9/2559.
 */
public class CellFormatUtils {

    public static WritableCellFormat getNumberCellFormat(String pattern){
        WritableCellFormat numberCellFormat = new WritableCellFormat(new NumberFormat(pattern));
        return numberCellFormat;
    }
}
