package com.kedaireka.monitoringkjabb.utils

import android.content.Context
import android.text.format.DateFormat
import android.util.Log
import com.kedaireka.monitoringkjabb.model.Sensor
import org.apache.poi.ss.usermodel.*
import org.apache.poi.xssf.usermodel.IndexedColorMap
import org.apache.poi.xssf.usermodel.XSSFColor
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


abstract class ExcelUtils {

    companion object {

        fun createExcel(context: Context, workbook: Workbook, sensor: Sensor) {

            //Get App Director, APP_DIRECTORY_NAME is a string
            val appDirectory = context.getExternalFilesDir("KJABB")

            //Check App Directory whether it exists or not, create if not.
            if (appDirectory != null && !appDirectory.exists()) {
                appDirectory.mkdirs()
            }

            //Create excel file with extension .xlsx
            val timestamp = DateFormat.format("yyyy-MM-dd hh:mm:ss a", Date())
            val excelFile = File(appDirectory, "${sensor.name}-$timestamp.xlsx")

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

        fun createWorkbook(data: ArrayList<Sensor>): Workbook {
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
            for (i in 0 until data.size) {
                addData(i + 1, sheet, data[i])
            }


            return workbook
        }

        private fun createCell(row: Row, columnIndex: Int, value: String?) {
            val cell = row.createCell(columnIndex)
            cell?.setCellValue(value)
        }

        private fun addData(rowIndex: Int, sheet: Sheet, data: Sensor) {

            //Create row based on row index
            val row = sheet.createRow(rowIndex)
            val df = DateFormat.format("yyyy-MM-dd hh:mm:ss a", data.created_at.toDate())

            createCell(row, 0, df.toString()) //Column 1
            createCell(row, 1, data.id) //Column 2
            createCell(row, 2, data.name) //Column 3
            createCell(row, 3, data.value) //Column 4
        }

        private fun createSheetHeader(cellStyle: CellStyle, sheet: Sheet) {
            // Create sheet first row
            val row = sheet.createRow(0)

            // Header List
            val HEADER_LIST = listOf("Timestamp", "ID", "Sensor", "Nilai")

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
