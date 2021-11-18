package com.kedaireka.monitoringkjabb.utils

import android.content.Context
import android.util.Log
import org.apache.poi.ss.usermodel.*
import org.apache.poi.xssf.usermodel.IndexedColorMap
import org.apache.poi.xssf.usermodel.XSSFColor
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException


abstract class ExcelUtils {

    companion object {

        fun createExcel(context: Context, workbook: Workbook) {

            //Get App Director, APP_DIRECTORY_NAME is a string
            val appDirectory = context.getExternalFilesDir("KJABB")

            //Check App Directory whether it exists or not, create if not.
            if (appDirectory != null && !appDirectory.exists()) {
                appDirectory.mkdirs()
            }

            //Create excel file with extension .xlsx
            val excelFile = File(appDirectory, "Coba.xlsx")

            //Write workbook to file using FileOutputStream
            try {
                val fileOut = FileOutputStream(excelFile)
                workbook.write(fileOut)
                fileOut.close()

                Log.d("Excel Utils", "location: $excelFile")
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        fun createWorkbook(): Workbook {
            System.setProperty("org.apache.poi.javax.xml.stream.XMLInputFactory", "com.fasterxml.aalto.stax.InputFactoryImpl")
            System.setProperty("org.apache.poi.javax.xml.stream.XMLOutputFactory", "com.fasterxml.aalto.stax.OutputFactoryImpl")
            System.setProperty("org.apache.poi.javax.xml.stream.XMLEventFactory", "com.fasterxml.aalto.stax.EventFactoryImpl")

            val workbook = XSSFWorkbook()

            // Creating first sheet inside workbook
            val sheet: Sheet = workbook.createSheet("Rekap Data")

            // Create Header Cell Style
            val cellStyle = getHeaderStyle(workbook)

            // Creating sheet header row
            createSheetHeader(cellStyle, sheet)

            // Adding data to the sheet
            addData(1, sheet)

            return workbook
        }

        private fun createCell(row: Row, columnIndex: Int, value: String?) {
            val cell = row.createCell(columnIndex)
            cell?.setCellValue(value)
        }

        private fun addData(rowIndex: Int, sheet: Sheet) {

            //Create row based on row index
            val row = sheet.createRow(rowIndex)

            //Add data to each cell
            createCell(row, 0, "value 1") //Column 1
            createCell(row, 1, "value 2") //Column 2
            createCell(row, 2, "value 3") //Column 3
        }

        private fun createSheetHeader(cellStyle: CellStyle, sheet: Sheet) {
            // Create sheet first row
            val row = sheet.createRow(0)

            // Header List
            val HEADER_LIST = listOf("column_1", "column_2", "column_3")

            // Loop to populate each column of header row
            for ((index, value) in HEADER_LIST.withIndex()) {

                val columnWidth = (15 * 500)

                //index represents the column number
                sheet.setColumnWidth(index, columnWidth)

                //Create cell
                val cell = row.createCell(index)

                //value represents the header value from HEADER_LIST
                cell?.setCellValue(value)

                //Apply style to cell
                cell.cellStyle = cellStyle
            }
        }

        private fun getHeaderStyle(workbook: Workbook): CellStyle {
            // Cell style for header row
            val cellStyle = workbook.createCellStyle()

            // Apply cell color
            val colorMap: IndexedColorMap = (workbook as XSSFWorkbook).stylesSource.indexedColors
            var color = XSSFColor(IndexedColors.ORANGE, colorMap).indexed
            cellStyle.fillForegroundColor = color
            cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND)

            // Apply font style on cell text
            val whiteFont = workbook.createFont()
            color = XSSFColor(IndexedColors.WHITE, colorMap).indexed
            whiteFont.color = color
            whiteFont.bold = true
            cellStyle.setFont(whiteFont)

            return cellStyle
        }
    }

}
