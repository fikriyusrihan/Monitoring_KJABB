package com.kedaireka.monitoringkjabb.utils

import android.content.Context
import android.util.Log
import org.apache.poi.hssf.usermodel.HSSFCellStyle
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.hssf.util.HSSFColor
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.Sheet
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


abstract class ExcelUtils {

    companion object {
        private const val TAG = "ExcelUtils"
        private const val EXCEL_SHEET_NAME = "Rekap Sensor"
    }

    private var cell: Cell? = null
    private var sheet: Sheet? = null
    private var workbook: HSSFWorkbook? = null

    fun createExcelWorkbook(context: Context, filename: String) {
        // New Workbook
        val workbook = HSSFWorkbook()

        // Cell style for header
        val cellStyle = workbook.createCellStyle()
        cellStyle.fillForegroundColor = HSSFColor.AQUA.index
        cellStyle.fillPattern = HSSFCellStyle.SOLID_FOREGROUND
        cellStyle.alignment = CellStyle.ALIGN_CENTER

        // New Sheet
        workbook.createSheet(EXCEL_SHEET_NAME).also { sheet = it }

        // Generate Column Heading
        val row = sheet?.createRow(0)

        cell = row?.createCell(0)
        cell?.setCellValue("Timestamp")
        cell?.cellStyle = cellStyle

        cell = row?.createCell(1)
        cell?.setCellValue("Nilai")
        cell?.cellStyle = cellStyle

        storeExcelInStorage(context, filename)
    }

    fun storeExcelInStorage(context: Context, filename: String): Boolean {
        val file = File(context.getExternalFilesDir(null), filename)
        var fileOutputStream: FileOutputStream? = null
        var isSuccess = false

        try {
            fileOutputStream = FileOutputStream(file)
            workbook!!.write(fileOutputStream)
            Log.e(TAG, "Writing file$file")
            isSuccess = true
        } catch (e: IOException) {
            Log.e(TAG, "Error writing Exception: ", e)
            isSuccess = false
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save file due to Exception: ", e)
            isSuccess = false
        } finally {
            try {
                fileOutputStream?.close()
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        return isSuccess
    }

}
