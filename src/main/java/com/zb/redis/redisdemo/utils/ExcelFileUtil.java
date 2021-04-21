package com.zb.redis.redisdemo.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.monitorjbl.xlsx.StreamingReader;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.*;

@Log4j2
public class ExcelFileUtil {

    private ExcelFileUtil() {
    }

    public static final String XLS = "xls";
    public static final String XLSX = "xlsx";
    public static final String EMPTY = "";
    public static final String POINT = ".";


    /**
     * read the Excel .xlsx,.xls
     *
     * @param file        jsp中的上传文件
     * @param isdeleteOne 是否删除第一行
     * @return
     * @throws IOException
     */
    public static Result readExcel(MultipartFile file, boolean isdeleteOne) throws IOException {
        //校验并返回excel的类型 xls xlsx
        int res = checkFile(file);
        if (res == 1) {
            return Result.ok(readXLSX(file));
        } else if (res == 2) {
            return Result.ok(readXLS(file, isdeleteOne));
        } else {
            return Result.failed("文件格式错误,请导入excel文件");
        }
    }


    /**
     * 获得path的后缀名
     *
     * @param path
     * @return
     */
    public static String getPostfix(String path) {
        if (path == null || EMPTY.equals(path.trim())) {
            return EMPTY;
        }
        if (path.contains(POINT)) {
            return path.substring(path.lastIndexOf(POINT) + 1, path.length());
        }
        return EMPTY;
    }

    /**
     * 判断File文件的类型
     *
     * @param file 传入的文件
     * @return 0-文件为空，1-XLSX文件，2-XLS文件，3-其他文件
     */
    public static int checkFile(MultipartFile file) {
        if (file == null) {
            return 0;
        }
        String postfix = getPostfix(file.getOriginalFilename());
        if (postfix.endsWith(XLSX)) {
            return 1;
        }
        if (postfix.endsWith(XLS)) {
            return 2;
        }
        return 3;
    }


    /**
     * 读取XLSX文件
     *
     * @param file
     * @return
     * @throws IOException
     * @throws InvalidFormatException
     */
    public static JSONArray readXLSX(MultipartFile file) throws IOException {
        Workbook wk = null;
        JSONArray jsonArray = new JSONArray();
        try {

            wk = builderWorbook(file);

            Sheet sheet = getSheetByIndex(wk, 0);

            //遍历所有的行
            List<String> titles = new ArrayList<>();
            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    //获取第一行所有的标题
                    getAllTitle(titles, row);
                    continue;
                }

                //遍历所有的列
                JSONObject jsonObject = new JSONObject();
                for (int i = 0; i < titles.size(); i++) {
                    Cell cell = row.getCell(i);
                    String stringCellValue = null;
                    if (cell != null) {
                        stringCellValue = ExcelFileUtil.getValue(cell);
                    }

                    jsonObject.put(titles.get(i), stringCellValue);
                }

                if (row.getRowNum() > 0) {
                    jsonArray.add(jsonObject);
                }
            }
        } catch (Exception e) {
            log.error("文件解析异常");

        } finally {
            wk.close();
        }
        return jsonArray;

    }

    //获取改行所有的值
    private static void getAllTitle(List<String> titles, Row row) throws IOException {
        for (Cell cell : row) {
            String value = ExcelFileUtil.getValue(cell);
            //这一行为标题行
            titles.add(value);
        }
    }

    //根据sheet名称获取sheet
    private static Sheet getSheetByName(Workbook wk, String name) {
        return wk.getSheet(name);
    }

    //根据索引获取sheet
    private static Sheet getSheetByIndex(Workbook wk, int index) {
        return wk.getSheetAt(index);
    }

    //使用StreamingReader构建workbook对象
    private static Workbook builderWorbook(MultipartFile file) throws IOException {
        return StreamingReader.builder()
                .rowCacheSize(100)  //缓存到内存中的行数，默认是10
                .bufferSize(4096)  //读取资源时，缓存到内存的字节大小，默认是1024
                .open(file.getInputStream());  //打开资源，必须，可以是InputStream或者是File，注意：只能打开XLSX格式的文件

    }

    /**
     * 读取XLS文件
     *
     * @param file
     * @return
     * @throws IOException
     * @throws FileNotFoundException
     */
    public static JSONArray readXLS(MultipartFile file, boolean isDeleteOne) throws IOException {
        return read(new HSSFWorkbook(file.getInputStream()), isDeleteOne);
    }

    public static JSONArray read(Workbook book, boolean isDeleteOne) throws IOException {
        Sheet sheet = book.getSheetAt(0);
        return read(sheet, book, isDeleteOne);
    }

    /**
     * 解析数据
     *
     * @param sheet       表格sheet对象
     * @param book        用于流关闭
     * @param isDeleteOne 是否删除第一行
     * @return
     * @throws IOException
     */
    public static JSONArray read(Sheet sheet, Workbook book, boolean isDeleteOne) throws IOException {
        if (isDeleteOne) {//删除第一行
            Row row = sheet.getRow(0);
            sheet.removeRow(row);
        }
        int rowStart = sheet.getFirstRowNum();    // 首行下标
        int rowEnd = sheet.getLastRowNum();    // 尾行下标
        // 如果首行与尾行相同，表明只有一行，直接返回空数组
        if (rowStart == rowEnd) {
            book.close();
            return new JSONArray();
        }
        // 获取第一行JSON对象键
        Row firstRow = sheet.getRow(rowStart);
        int cellStart = firstRow.getFirstCellNum();
        int cellEnd = firstRow.getLastCellNum();
        Map<Integer, String> keyMap = new HashMap<>();
        for (int j = cellStart; j < cellEnd; j++) {
            keyMap.put(j, getValue(firstRow.getCell(j), rowStart, j, book, true));
        }
        // 获取每行JSON对象的值
        JSONArray array = new JSONArray();
        for (int i = rowStart + 1; i <= rowEnd; i++) {
            Row eachRow = sheet.getRow(i);
            JSONObject obj = new JSONObject();
            //行号
            obj.put("line", String.valueOf(i + 1));
            StringBuffer sb = new StringBuffer();
            for (int k = cellStart; k < cellEnd; k++) {
                if (eachRow != null) {
                    String val = getValue(eachRow.getCell(k), i, k, book, false);
                    if (!StringUtils.isEmpty(val)) val.trim();
                    sb.append(val);        // 所有数据添加到里面，用于判断该行是否为空
                    obj.put(keyMap.get(k), val);
                }
            }
            if (sb.toString().length() > 0) {
                array.add(obj);
            }
        }
        book.close();
        return array;
    }

    /**
     * 获取每个单元格的数据
     *
     * @param cell   单元格对象
     * @param rowNum 第几行
     * @param index  该行第几个
     * @param book   主要用于关闭流
     * @param isKey  是否为键：true-是，false-不是。 如果解析Json键，值为空时报错；如果不是Json键，值为空不报错
     * @return
     * @throws IOException
     */
    public static String getValue(Cell cell, Integer rowNum, Integer index, Workbook book, boolean isKey) throws IOException {

        // 空白或空
        if (cell == null || cell.getCellTypeEnum() == CellType.BLANK) {
            if (isKey) {
                book.close();
                throw new NullPointerException(String.format("the key on row %s index %s is null ", ++rowNum, ++index));
            } else {
                return "";
            }
        }

        // 0. 数字 类型
        if (cell.getCellTypeEnum() == CellType.NUMERIC) {
            if (HSSFDateUtil.isCellDateFormatted(cell)) {
                Date date = cell.getDateCellValue();
                return DateUtil.getTime(date, DateUtil.DEFAULT_DATE_FORMAT);
            }
            DecimalFormat df = new DecimalFormat("0.00");
            String val = df.format(cell.getNumericCellValue());
            val = val.toUpperCase();
            if (val.contains("E")) {
                val = val.split("E")[0].replace(".", "");
            }
            return val;
        }

        // 1. String类型
        if (cell.getCellTypeEnum() == CellType.STRING) {
            String val = cell.getStringCellValue();
            if (val == null || val.trim().length() == 0) {
                if (book != null) {
                    book.close();
                }
                return "";
            }
            return val.trim();
        }

        // 2. 公式 CELL_TYPE_FORMULA
        if (cell.getCellTypeEnum() == CellType.FORMULA) {
            return cell.getStringCellValue();
        }

        // 4. 布尔值 CELL_TYPE_BOOLEAN
        if (cell.getCellTypeEnum() == CellType.BOOLEAN) {
            return cell.getBooleanCellValue() + "";
        }

        // 5.	错误 CELL_TYPE_ERROR
        return "";
    }


    public static String getValue(Cell cell) throws IOException {
        return getValue(cell, null, null, null, false);
    }

    public static XSSFWorkbook export(String sheetName, String[] title, String[][] values) {

        //创建一个XSSFWorkbook 对应一个excel
        XSSFWorkbook workbook = new XSSFWorkbook();

        //添加sheet
        XSSFSheet sheet = StringUtils.isNotEmpty(sheetName) ? workbook.createSheet(sheetName) : workbook.createSheet();

        //第三步，在sheet中添加表头第0行
        XSSFRow row = sheet.createRow(0);

        //第四步，创建单元格，并设置值表头 设置表头居中
        XSSFCellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);

        XSSFCell cell = null;

        //创建标题
        for (int i = 0; i < title.length; i++) {
            cell = row.createCell(i);
            cell.setCellValue(title[i]);
            cell.setCellStyle(cellStyle);
        }

        //创建内容
        for (int i = 0; i < values.length; i++) {
            row = sheet.createRow(i + 1);
            for (int j = 0; j < values[i].length; j++) {
                //将内容按顺序赋给对应的列对象
                row.createCell(j).setCellValue(values[i][j]);
            }
        }

        return workbook;
    }


    public static void outputExcel(XSSFWorkbook wb, HttpServletResponse response) {
        //输出Excel
        OutputStream outputStream;
        try {
            outputStream = response.getOutputStream();
            wb.write(outputStream);
            outputStream.close();
        } catch (IOException e) {
            log.error("输出报表信息出错：{}", e);
        }
        try {
            wb.close();
        } catch (IOException e) {
            log.error("wb关闭异常{}", e);
        }
    }


    public static void outputExcel(XSSFWorkbook wb, HttpServletResponse response, String fileName) {
        try {
            fileName = new String(fileName.getBytes(), "ISO8859-1");
        } catch (UnsupportedEncodingException e) {
            log.error("fileName转换出错{} {}", fileName, e);
            fileName = "repsonse.xlsx";
        }
        response.setHeader("Content-type", "application/force-download");
        response.setHeader("Content-Transfer-Encoding", "Binary");
        response.setHeader("Content-Type", "application/octet-stream;charset=ISO8859-1");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        ExcelFileUtil.outputExcel(wb, response);
    }

}
