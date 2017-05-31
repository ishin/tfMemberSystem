package com.organ.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class XlsUtils {
	private XlsUtils() {
	}

	private static class Inner {
		private static final XlsUtils XU = new XlsUtils();
	}

	public static XlsUtils getInstance() {
		return Inner.XU;
	}

	public void createSimpleExcel(String sheetName, ArrayList<String[]> content, OutputStream os)
			throws WriteException, IOException {
		
		// 创建工作薄
		WritableWorkbook workbook = Workbook.createWorkbook(os);
		// 创建新的一页
		WritableSheet sheet = workbook.createSheet(sheetName, 0);
		// 创建要显示的内容,创建一个单元格，第一个参数为列坐标，第二个参数为行坐标，第三个参数为内容
		int line = content.size();

		for (int i = 0; i < line; i++) {
			String[] field = content.get(i);
			for (int j = 0; j < field.length; j++) {
				Label label = new Label(Integer.parseInt(field[0]), Integer
						.parseInt(field[1]), field[2]);
				sheet.addCell(label);
			}
		}
		// 把创建的内容写入到输出流中，并关闭输出流
		workbook.write();
		workbook.close();
		os.close();
	}
	
	public void createTitleExcel(String sheetName, int fields, ArrayList<String[]> content, OutputStream os) throws WriteException, IOException {
		//创建工作薄
        WritableWorkbook workbook = Workbook.createWorkbook(os);
        //创建新的一页
        WritableSheet sheet = workbook.createSheet(sheetName, 0);
        //构造表头
        int line = content.size();
        
        for (int i = 0; i < line; i++) {
			String[] field = content.get(i);
		
			if (i == 0) {
				//添加合并单元格，第一个参数是起始列，第二个参数是起始行，第三个参数是终止列，第四个参数是终止行
				sheet.mergeCells(0, 0, fields - 1, 0);
			    //设置字体种类和黑体显示,字体为Arial,字号大小为10,采用黑体显示
				WritableFont bold = new WritableFont(WritableFont.ARIAL,10,WritableFont.BOLD);
				//生成一个单元格样式控制对象
				WritableCellFormat titleFormate = new WritableCellFormat(bold);
				//单元格中的内容水平方向居中
				titleFormate.setAlignment(jxl.format.Alignment.CENTRE);
				//单元格的内容垂直方向居中
				titleFormate.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);
				Label title = new Label(0, 0, field[2], titleFormate);
				//设置第一行的高度
				sheet.setRowView(0, 400, false);
				sheet.addCell(title);
			} else {
				int fieldLen = field.length;
				
				for (int j = 0; j < fieldLen; j++) {
					Label label = new Label(Integer.parseInt(field[0]), Integer
							.parseInt(field[1]), field[2]);
					sheet.addCell(label);
				}
			}
		}
        workbook.write();
        workbook.close();
        os.close();
	}

}
