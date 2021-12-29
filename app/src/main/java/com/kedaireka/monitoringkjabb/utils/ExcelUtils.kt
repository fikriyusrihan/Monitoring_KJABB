package com.kedaireka.monitoringkjabb.utils

import android.content.Context
import android.text.format.DateFormat
import android.util.Log
import com.kedaireka.monitoringkjabb.model.Sensor
import org.apache.poi.ss.usermodel.*
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.xssf.usermodel.IndexedColorMap
import org.apache.poi.xssf.usermodel.XSSFColor
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.*


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
            System.setProperty(
                "org.apache.poi.javax.xml.stream.XMLInputFactory",
                "com.fasterxml.aalto.stax.InputFactoryImpl"
            )
            System.setProperty(
                "org.apache.poi.javax.xml.stream.XMLOutputFactory",
                "com.fasterxml.aalto.stax.OutputFactoryImpl"
            )
            System.setProperty(
                "org.apache.poi.javax.xml.stream.XMLEventFactory",
                "com.fasterxml.aalto.stax.EventFactoryImpl"
            )

            val workbook = XSSFWorkbook()

            // Creating first sheet inside workbook
            val sheet: Sheet = workbook.createSheet("Rekap Data")

            // Set column width for every cell
            sheet.setColumnWidth(0, 3000)

            // Create Cell Style
            val cellHeaderStyle = getHeaderStyle(workbook)
            val cellTitleStyle = getTitleStyle(workbook)

            // Creating sheet title row
            createSheetTitle(cellTitleStyle, sheet, data[0], data[data.size - 1])

            // Creating sheet header row
            createSheetSensorInformation(cellHeaderStyle, sheet, data[0])
            createSheetHeader(cellHeaderStyle, sheet)

            // Adding data to the sheet
            for (i in 0 until data.size) {
                addData(i + 12, sheet, data[i])
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

            createCell(row, 0, (rowIndex - 11).toString()) //Column 1
            createCell(row, 1, df.toString()) //Column 2
            sheet.addMergedRegion(CellRangeAddress(rowIndex, rowIndex, 1, 7))
            createCell(row, 8, data.value) //Column 4
        }

        private fun createSheetHeader(cellStyle: CellStyle, sheet: Sheet) {

            // Create sheet 11th row
            val row = sheet.createRow(11)

            // Header List
            val HEADER_LIST = listOf("No", "Timestamp", "Nilai")

            //Create cell
            val cellNo = row.createCell(0)
            val cellTimestamp = row.createCell(1)
            sheet.addMergedRegion(CellRangeAddress(11, 11, 1, 7))
            val cellNilai = row.createCell(8)

            //value represents the header value from HEADER_LIST
            cellNo.setCellValue(HEADER_LIST[0])
            cellTimestamp.setCellValue(HEADER_LIST[1])
            cellNilai.setCellValue(HEADER_LIST[2])

            //Apply style to cell
            cellNo.cellStyle = cellStyle
            cellTimestamp.cellStyle = cellStyle
            cellNilai.cellStyle = cellStyle
        }

        private fun createSheetSensorInformation(
            cellStyle: CellStyle,
            sheet: Sheet,
            sensor: Sensor
        ) {
            // Create sheet 6th row
            val row = sheet.createRow(5)
            val cell = row.createCell(0)
            sheet.addMergedRegion(CellRangeAddress(5, 5, 0, 8))
            cell.setCellValue("INFORMASI SENSOR")
            cell.cellStyle = cellStyle

            // Sensor Information
            val rowID = sheet.createRow(6)
            val cellID = rowID.createCell(0)
            cellID.setCellValue("ID")
            val cellIDValue = rowID.createCell(1)
            sheet.addMergedRegion(CellRangeAddress(6, 6, 1, 8))
            cellIDValue.setCellValue(sensor.id)

            val rowNama = sheet.createRow(7)
            val cellNama = rowNama.createCell(0)
            cellNama.setCellValue("Nama")
            val cellNameValue = rowNama.createCell(1)
            sheet.addMergedRegion(CellRangeAddress(7, 7, 1, 8))
            cellNameValue.setCellValue(sensor.name)

            val rowSatuan = sheet.createRow(8)
            val cellSatuan = rowSatuan.createCell(0)
            cellSatuan.setCellValue("Satuan")
            val cellSatuanValue = rowSatuan.createCell(1)
            sheet.addMergedRegion(CellRangeAddress(8, 8, 1, 8))
            cellSatuanValue.setCellValue(sensor.unit)

        }

        private fun createSheetTitle(
            cellStyle: CellStyle,
            sheet: Sheet,
            start: Sensor,
            end: Sensor
        ) {
            // Create sheet
            val rowFirstTitle = sheet.createRow(1)
            val cellFirstTitle = rowFirstTitle.createCell(0)
            cellFirstTitle.setCellValue("LAPORAN DATA SENSOR KJABB-IMTA")
            sheet.addMergedRegion(CellRangeAddress(1, 1, 0, 8))

            val rowSecondTitle = sheet.createRow(2)
            val cellSecondTitle = rowSecondTitle.createCell(0)

            val dfStart = DateFormat.format("dd MMMM yyyy", start.created_at.toDate())
            val dfEnd = DateFormat.format("dd MMMM yyyy", end.created_at.toDate())

            cellSecondTitle.setCellValue("PERIODE DATA: ${dfStart.toString().uppercase()} - ${dfEnd.toString().uppercase()}")
            sheet.addMergedRegion(CellRangeAddress(2, 2, 0, 8))

            cellFirstTitle.cellStyle = cellStyle
            cellSecondTitle.cellStyle = cellStyle

        }

        private fun getHeaderStyle(workbook: Workbook): CellStyle {
            // Cell style for header row
            val cellStyle = workbook.createCellStyle()

            // Apply cell color
            val colorMap: IndexedColorMap = (workbook as XSSFWorkbook).stylesSource.indexedColors
            var color = XSSFColor(IndexedColors.LIGHT_BLUE, colorMap).indexed
            cellStyle.fillForegroundColor = color
            cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND)

            // Apply font style on cell text
            val whiteFont = workbook.createFont()
            color = XSSFColor(IndexedColors.WHITE, colorMap).indexed
            whiteFont.color = color
            whiteFont.bold = true
            cellStyle.setFont(whiteFont)

            cellStyle.setAlignment(HorizontalAlignment.CENTER)

            return cellStyle
        }

        private fun getTitleStyle(workbook: Workbook): CellStyle {
            // Cell Style for title
            val cellStyle = workbook.createCellStyle()

            // Apply font style on cell text
            val font = workbook.createFont()
            font.bold = true
            font.fontHeightInPoints = 14
            cellStyle.setFont(font)
            cellStyle.setAlignment(HorizontalAlignment.CENTER)

            return cellStyle
        }
    }

}
