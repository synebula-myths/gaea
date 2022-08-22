package com.synebula.gaea.app.component.poi

import com.synebula.gaea.app.struct.ExcelData
import com.synebula.gaea.data.date.DateTime
import com.synebula.gaea.exception.NoticeUserException
import org.apache.poi.hpsf.Decimal
import org.apache.poi.hssf.usermodel.HSSFCell
import org.apache.poi.hssf.usermodel.HSSFCellStyle
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.formula.BaseFormulaEvaluator
import org.apache.poi.ss.usermodel.*
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator
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
     * @param rowStart 数据起始行，默认0
     * @param columnStart 数据起始列，默认0
     *
     * @return ExcelData
     */
    fun import(
        file: MultipartFile,
        columns: List<Pair<String, String>>,
        rowStart: Int = 0,
        columnStart: Int = 0,
        emptyRowFilter: (row: Row) -> Boolean =
            { row ->
                row.getCell(
                    0,
                    Row.MissingCellPolicy.RETURN_NULL_AND_BLANK
                ) == null
            }
    ): List<Map<String, Any?>> {
        if (file.originalFilename?.endsWith(".xls") != true && file.originalFilename?.endsWith(".xlsx") != true)
            throw RuntimeException("无法识别的文件格式[${file.originalFilename}]")

        val evaluator: BaseFormulaEvaluator
        val workbook = if (file.originalFilename?.endsWith(".xls") == true) {
            val wb = HSSFWorkbook(file.inputStream)
            evaluator = HSSFFormulaEvaluator(wb)
            wb
        } else {
            val wb = XSSFWorkbook(file.inputStream)
            evaluator = XSSFFormulaEvaluator(wb)
            wb
        }

        val sheet = workbook.getSheetAt(0)

        val data = mutableListOf<Map<String, Any?>>()
        for (r in rowStart..sheet.lastRowNum) {
            val row = sheet.getRow(r) ?: continue
            if (emptyRowFilter(row)) continue //空行不处理
            val rowData = mutableMapOf<String, Any?>()
            for (c in columnStart until columns.size + columnStart) {
                try {
                    val column = columns[c]
                    val cell = row.getCell(c, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL)
                    if (cell == null) {
                        rowData[columns[c].first] = ""
                        continue
                    }
                    val value: Any? = when (column.second) {
                        Int::class.java.name -> {
                            if (cell.cellType == CellType.NUMERIC) {
                                cell.numericCellValue.toInt()
                            } else {
                                this.getCellValue(cell, evaluator).toString().toIntOrNull()
                            }
                        }
                        Double::class.java.name -> {
                            if (cell.cellType == CellType.NUMERIC) {
                                cell.numericCellValue
                            } else {
                                this.getCellValue(cell, evaluator).toString().toDoubleOrNull()
                            }
                        }
                        Float::class.java.name -> {
                            if (cell.cellType == CellType.NUMERIC) {
                                cell.numericCellValue.toFloat()
                            } else {
                                this.getCellValue(cell, evaluator).toString().toFloatOrNull()
                            }
                        }
                        Decimal::class.java.name -> {
                            if (cell.cellType == CellType.NUMERIC) {
                                cell.numericCellValue.toBigDecimal()
                            } else {
                                this.getCellValue(cell, evaluator).toString().toBigDecimalOrNull()
                            }
                        }
                        Boolean::class.java.name -> {
                            if (cell.cellType == CellType.BOOLEAN) {
                                cell.booleanCellValue
                            } else {
                                this.getCellValue(cell, evaluator).toString().toBoolean()
                            }
                        }
                        Date::class.java.name -> try {
                            cell.dateCellValue
                        } catch (ignored: Exception) {
                            DateTime(cell.stringCellValue).date
                        }
                        else -> cell.stringCellValue
                    }
                    rowData[columns[c].first] = value
                } catch (ex: Exception) {
                    throw RuntimeException("解析EXCEL文件${file.originalFilename}第${c + 1}行第${c + 1}列出错", ex)
                }
            }
            data.add(rowData)
        }
        workbook.close()
        file.inputStream.close()
        return data
    }

    /**
     * 导入文件
     *
     * @param file 上传文件流
     * @param rowStartIndex 数据起始行，默认0
     * @param columnStartIndex 数据起始列，默认0
     *
     * @return ExcelData
     */
    fun import(
        file: MultipartFile,
        columnSize: Int = 0,
        rowStartIndex: Int = 0,
        columnStartIndex: Int = 0,
        emptyRowFilter: (row: Row) -> Boolean = { row ->
            row.getCell(
                0,
                Row.MissingCellPolicy.RETURN_NULL_AND_BLANK
            ) == null
        }
    ): List<Map<String, String>> {
        if (file.originalFilename?.endsWith(".xls") != true && file.originalFilename?.endsWith(".xlsx") != true)
            throw NoticeUserException("无法识别的文件格式[${file.originalFilename}]")
        val evaluator: BaseFormulaEvaluator
        val workbook = if (file.originalFilename?.endsWith(".xls") == true) {
            val wb = HSSFWorkbook(file.inputStream)
            evaluator = HSSFFormulaEvaluator(wb)
            wb
        } else {
            val wb = XSSFWorkbook(file.inputStream)
            evaluator = XSSFFormulaEvaluator(wb)
            wb
        }
        val sheet = workbook.getSheetAt(0)

        val titles = mutableListOf<String>()
        val titleRow = sheet.getRow(rowStartIndex)
        val size = if (columnSize != 0) columnSize else titleRow.physicalNumberOfCells //列数
        for (i in columnStartIndex until size) {
            titles.add(titleRow.getCell(i).stringCellValue)
        }

        val data = mutableListOf<Map<String, String>>()
        for (r in (rowStartIndex + 1)..sheet.lastRowNum) {
            val row = sheet.getRow(r) ?: continue
            if (emptyRowFilter(row)) continue //空行不处理
            val rowData = mutableMapOf<String, String>()
            for (c in columnStartIndex until size + columnStartIndex) {
                try {
                    val title = titles[c]
                    val cell = row.getCell(c, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL)
                    if (cell == null) {
                        rowData[title] = ""
                        continue
                    }
                    val value = getCellValue(cell, evaluator)
                    rowData[title] = value.toString()
                } catch (ex: Exception) {
                    throw NoticeUserException("解析EXCEL文件${file.originalFilename}第${r + 1}行第${c + 1}列出错", ex)
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

    private fun getCellValue(cell: Cell, evaluator: BaseFormulaEvaluator): Any {
        return when (cell.cellType) {
            CellType.BOOLEAN -> cell.booleanCellValue
            CellType.ERROR -> cell.errorCellValue
            CellType.NUMERIC -> {
                val numericCellValue: Double = cell.numericCellValue
                if (DateUtil.isCellDateFormatted(cell)) {
                    DateUtil.getLocalDateTime(numericCellValue)
                } else {
                    numericCellValue
                }
            }
            CellType.STRING -> cell.richStringCellValue.string
            CellType.BLANK -> ""
            CellType.FORMULA -> evaluator.evaluate(cell).toString()
            else -> throw Exception("匹配类型错误")
        }
    }
}