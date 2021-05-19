package com.synebula.gaea.app.component.poi

import com.synebula.gaea.app.struct.ExcelData
import org.apache.poi.hpsf.Decimal
import org.apache.poi.hssf.usermodel.HSSFCell
import org.apache.poi.hssf.usermodel.HSSFCellStyle
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.BorderStyle
import org.apache.poi.ss.usermodel.HorizontalAlignment
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.VerticalAlignment
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.web.multipart.MultipartFile
import java.util.*

/**
 * Excel操作对象
 */
object Excel {

    /**
     * 导出excel
     *
     * @param data 需要导出的数据
     * @return excel表格数据
     */
    fun export(data: ExcelData, writeTo: (wb: HSSFWorkbook) -> Unit) {
        // 第一步，创建一个HSSFWorkbook，对应一个Excel文件
        val wb = HSSFWorkbook()
        // 第二步，在workbook中添加一个sheet,对应Excel文件中的sheet，并设置默认样式
        val sheet = wb.createSheet(data.title)

        // 第四步，创建单元格，并设置值表头 设置表头居中
        val titleStyle = wb.createCellStyle()
        titleStyle.alignment = HorizontalAlignment.CENTER// 创建一个居中格式
        titleStyle.verticalAlignment = VerticalAlignment.CENTER
        val titleFont = wb.createFont()
        titleFont.bold = true
        titleStyle.setFont(titleFont)
        setBorderStyle(titleStyle, BorderStyle.THIN)

        //声明列对象
        // 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制
        var row = sheet.createRow(0)
        row.height = 25 * 20
        var cell: HSSFCell
        //创建标题
        for (col in data.columnNames.indices) {
            try {
                cell = row.createCell(col)
                cell.setCellStyle(titleStyle)
                cell.setCellValue(data.columnNames[col])
                setColumnWidth(data.columnNames[col], col, sheet)
            } catch (ex: RuntimeException) {
                throw Exception("创建索引${col}列[${data.columnNames[col]}]时出现异常", ex)
            }
        }

        val contentStyle = wb.createCellStyle()
        contentStyle.alignment = HorizontalAlignment.LEFT// 创建一个修改居左格式
        contentStyle.verticalAlignment = VerticalAlignment.CENTER
        setBorderStyle(contentStyle, BorderStyle.THIN)

        //创建内容
        var col = 0
        for (i in data.data.indices) {
            try {
                row = sheet.createRow(i + 1)
                row.height = 20 * 20
                col = 0
                while (col < data.data[i].size) {
                    cell = row.createCell(col)
                    cell.setCellStyle(contentStyle)
                    cell.setCellValue(data.data[i][col])
                    setColumnWidth(data.data[i][col], col, sheet)

                    col++
                }
            } catch (ex: RuntimeException) {
                throw Exception("创建索引${i}行[${data.columnNames[col]}]时出现异常", ex)
            }
        }
        writeTo(wb)
    }

    /**
     * 导入文件
     *
     * @param file 上传文件流
     * @param columns 文件列名称、类型定义
     * @param startRow 数据起始行，默认0
     * @param startColumn 数据起始列，默认0
     *
     * @return ExcelData
     */
    fun import(
            file: MultipartFile,
            columns: List<Pair<String, String>>,
            startRow: Int = 0,
            startColumn: Int = 0
    ): List<Map<String, Any>> {
        if (file.originalFilename?.endsWith(".xls") != true && file.originalFilename?.endsWith(".xlsx") != true)
            throw RuntimeException("无法识别的文件格式[${file.originalFilename}]")

        val workbook = if (file.originalFilename?.endsWith(".xls") == true)
            HSSFWorkbook(file.inputStream)
        else
            XSSFWorkbook(file.inputStream)
        val sheet = workbook.getSheetAt(0)

        val data = mutableListOf<Map<String, Any>>()
        for (i in startRow..sheet.lastRowNum) {
            val row = sheet.getRow(i) ?: continue
            val rowData = mutableMapOf<String, Any>()
            for (c in startColumn until columns.size + startColumn) {
                try {
                    val column = columns[c]
                    val value: Any = when (column.second) {
                        Int::class.java.name, Double::class.java.name,
                        Float::class.java.name, Decimal::class.java.name -> try {
                            row.getCell(c).numericCellValue
                        } catch (ignored: Exception) {
                            row.getCell(c).stringCellValue
                        }
                        Boolean::class.java.name -> try {
                            row.getCell(c).booleanCellValue
                        } catch (ignored: Exception) {
                            row.getCell(c).stringCellValue
                        }
                        Date::class.java.name -> try {
                            row.getCell(c).dateCellValue
                        } catch (ignored: Exception) {
                            row.getCell(c).stringCellValue
                        }
                        else -> row.getCell(c).stringCellValue
                    }
                    rowData.put(columns[c].first, value)
                } catch (ex: Exception) {
                    throw RuntimeException("解析EXCEL文件${file.originalFilename}第${i}行第${c}列出错", ex)
                }
            }
            data.add(rowData)
        }
        workbook.close()
        file.inputStream.close()
        return data
    }

    /**
     * 设置列宽
     *
     * @param content 列内容
     * @param col 列索引
     * @param sheet 需要设置的表格
     */
    private fun setColumnWidth(content: String, col: Int, sheet: Sheet) {
        //设置列宽
        val width = (content.length * 1.5 * 256).toInt()
        if (width > sheet.getColumnWidth(col))
            sheet.setColumnWidth(col, width)
    }

    /**
     * 设置cell style的边框
     */
    private fun setBorderStyle(style: HSSFCellStyle, borderStyle: BorderStyle) {
        style.borderTop = borderStyle
        style.borderRight = borderStyle
        style.borderBottom = borderStyle
        style.borderLeft = borderStyle
    }
}