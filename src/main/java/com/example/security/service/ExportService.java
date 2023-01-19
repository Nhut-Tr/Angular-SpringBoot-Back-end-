package com.example.security.service;

import com.example.dto.request.BestSaleDTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class ExportService {
  public static ByteArrayInputStream excelReport(List<BestSaleDTO> bestSaleDTOList) throws IOException {
    String[] columns = {"Name","Price","Sold"};
    try(Workbook workbook = new XSSFWorkbook();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
    ){
      CreationHelper creationHelper = workbook.getCreationHelper();
      Sheet sheet = workbook.createSheet("Top 10 Best-Selling Products");
      Sheet sheet1 = workbook.getSheet("Top 10 Best-Selling Products");
      sheet1.autoSizeColumn(columns.length);

      Font headerFont = workbook.createFont();
      headerFont.setBold(true);
      headerFont.setColor(IndexedColors.BLUE.getIndex());

      CellStyle cellStyle = workbook.createCellStyle();
      cellStyle.setFont(headerFont);

      Row headerRow = sheet1.createRow(0);
      for(int col =0; col<columns.length; col++){
        Cell cell = headerRow.createCell(col);
        cell.setCellValue(columns[col]);
        cell.setCellStyle(cellStyle);
      }

      CellStyle cellStyle1 = workbook.createCellStyle();
      cellStyle1.setDataFormat(creationHelper.createDataFormat().getFormat("#"));

      int rowIndex =1;
      for(BestSaleDTO bestSaleDTO : bestSaleDTOList){
        Row row = sheet.createRow(rowIndex++);
        row.createCell(0).setCellValue(bestSaleDTO.getName());
        row.createCell(1).setCellValue(bestSaleDTO.getPrice());
        row.createCell(2).setCellValue(bestSaleDTO.getSold());
      }
      workbook.write(out);
      return new ByteArrayInputStream(out.toByteArray());

    }
  }
}
