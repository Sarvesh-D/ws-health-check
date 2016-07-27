package com.barclays.solveit.ws.health.util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFTable;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTable;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableColumn;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableColumns;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableStyleInfo;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@Scope(value=ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class WorkbookUtils {

	private Workbook workbook;

	public void loadWorkbook() {
		loadWorkbook(new XSSFWorkbook());
	}

	public void loadWorkbook(Workbook workbook) {
		this.workbook = workbook;
	}

	public Workbook getWorkbook() {
		Assert.notNull(workbook, "Workbook has not been loaded, load the workbook using loadWorkbook().");
		return this.workbook;
	}

	public CellStyle getHyperlinkCellStyle() {
		//cell style for hyperlinks
		//by default hyperlinks are blue and underlined
		CellStyle hlinkStyle = getWorkbook().createCellStyle();
		Font hlinkFont = getWorkbook().createFont();
		hlinkFont.setUnderline(Font.U_SINGLE);
		hlinkFont.setColor(IndexedColors.BLUE.getIndex());
		hlinkStyle.setFont(hlinkFont);
		return hlinkStyle;
	}

	public Hyperlink createHyperlink(String uri) {
		Hyperlink link = getWorkbook().getCreationHelper().createHyperlink(Hyperlink.LINK_URL);
		link.setAddress(uri);
		return link;
	}

	public Cell createHyperlinkCell(Cell cell, String uri) {
		cell.setCellStyle(getHyperlinkCellStyle());
		cell.setHyperlink(createHyperlink(uri));
		cell.setCellValue(uri);
		return cell;
	}

	public void formatAsTable(int tableRows, int tableCols) {
		/* Create an object of type XSSFTable */
		XSSFTable my_table = ((XSSFSheet) getWorkbook().getSheet("report")).createTable();

		/* get CTTable object*/
		CTTable cttable = my_table.getCTTable();

		/* define the required Style for the table */    
		CTTableStyleInfo table_style = cttable.addNewTableStyleInfo();
		table_style.setName("TableStyleMedium9");

		table_style.setShowRowStripes(true);
		table_style.setShowColumnStripes(false);

		CTTableColumns columns = cttable.addNewTableColumns();
		columns.setCount(tableCols); //define number of columns

		/* Define the data range including headers */
		AreaReference my_data_range = new AreaReference(new CellReference(0, 0), new CellReference(4, 6));

		/* Set Range to the Table */
		cttable.setRef(my_data_range.formatAsString());
		cttable.setDisplayName("MYTABLE");      /* this is the display name of the table */
		cttable.setName("Test");    /* This maps to "displayName" attribute in <table>, OOXML */            
		cttable.setId(1L); //id attribute against table as long value

		for (int j = 0; j < tableCols; j++) {
			CTTableColumn column = columns.addNewTableColumn();   
			column.setName("Column" + j);     
			column.setId(j+1);
		}

	}

	public void autoAdjustColumnWidth(int colNum) {
		getWorkbook().getSheetAt(0).autoSizeColumn(colNum);
	}

	public void autoAdjustColumnWidth(int colStartIndex, int colEndIndex) {
		for (int i = colStartIndex; i <= colEndIndex; i++) {
			autoAdjustColumnWidth(i);
		}
	}

	public static void main(String[] args)
			throws FileNotFoundException, IOException
	{
		/* Start with Creating a workbook and worksheet object */
		Workbook wb = new XSSFWorkbook();
		XSSFSheet sheet = (XSSFSheet)wb.createSheet();

		/* Create an object of type XSSFTable */
		XSSFTable my_table = sheet.createTable();

		/* get CTTable object*/
		CTTable cttable = my_table.getCTTable();

		/* Let us define the required Style for the table */    
		CTTableStyleInfo table_style = cttable.addNewTableStyleInfo();
		table_style.setName("TableStyleMedium9");   

		/* Set Table Style Options */
		table_style.setShowColumnStripes(false); //showColumnStripes=0
		table_style.setShowRowStripes(true); //showRowStripes=1

		/* Define the data range including headers */
		AreaReference my_data_range = new AreaReference(new CellReference(0, 0), new CellReference(4, 2));

		/* Set Range to the Table */
		cttable.setRef(my_data_range.formatAsString());
		cttable.setDisplayName("MYTABLE");      /* this is the display name of the table */
		cttable.setName("Test");    /* This maps to "displayName" attribute in <table>, OOXML */            
		cttable.setId(1L); //id attribute against table as long value

		CTTableColumns columns = cttable.addNewTableColumns();
		columns.setCount(3); //define number of columns

		/* Define Header Information for the Table */
		for (int i = 0; i < 3; i++)
		{
			CTTableColumn column = columns.addNewTableColumn();   
			column.setName("Column" + i);      
			column.setId(i+1);
		}

		/* Add remaining Table Data */
		for (int i=0;i<=4;i++) //we have to populate 4 rows
		{
			/* Create a Row */
			Row row = sheet.createRow(i);
			for (int j = 0; j < 3; j++) //Three columns in each row
			{
				Cell localXSSFCell = row.createCell(j);
				if (i == 0) {
					localXSSFCell.setCellValue("Heading" + j);
				} else {
					localXSSFCell.setCellValue(i + j);
				}   
			}
		} 

		/* Write output as File */
		FileOutputStream fileOut = new FileOutputStream("D:\\Excel_Format_As_Table.xlsx");
		WorkbookUtils w = new WorkbookUtils(); 
		w.loadWorkbook(wb);
		w.autoAdjustColumnWidth(0, 2);
		wb.write(fileOut);
		fileOut.close();
	}

	/*public static void main(String[] args)
		    throws FileNotFoundException, IOException
		  {
		     Start with Creating a workbook and worksheet object 
		        Workbook wb = new XSSFWorkbook();
		    XSSFSheet sheet = (XSSFSheet)wb.createSheet();

		     Create an object of type XSSFTable 
		    XSSFTable my_table = sheet.createTable();

		         get CTTable object
		    CTTable cttable = my_table.getCTTable();

		     Let us define the required Style for the table     
		    CTTableStyleInfo table_style = cttable.addNewTableStyleInfo();
		    table_style.setName("TableStyleMedium9");   

		         Set Table Style Options 
		    table_style.setShowColumnStripes(false); //showColumnStripes=0
		    table_style.setShowRowStripes(true); //showRowStripes=1

		     Define the data range including headers 
		   AreaReference my_data_range = new AreaReference(new CellReference(0, 0), new CellReference(4, 2));

		     Set Range to the Table 
		        cttable.setRef(my_data_range.formatAsString());
		        cttable.setDisplayName("MYTABLE");       this is the display name of the table 
		    cttable.setName("Test");     This maps to "displayName" attribute in <table>, OOXML             
		    cttable.setId(1L); //id attribute against table as long value

		    CTTableColumns columns = cttable.addNewTableColumns();
		    columns.setCount(3L); //define number of columns

		         Define Header Information for the Table 
		    for (int i = 0; i < 3; i++)
		    {
		    CTTableColumn column = columns.addNewTableColumn();   
		    column.setName("Column" + i);      
		        column.setId(i+1);
		    }

		          Add remaining Table Data 
		         for (int i=0;i<=4;i++) //we have to populate 4 rows
		         {
		          Create a Row 
		     Row row = sheet.createRow(i);
		     for (int j = 0; j < 3; j++) //Three columns in each row
		     {
		          Cell localXSSFCell = row.createCell(j);
		          if (i == 0) {
		         localXSSFCell.setCellValue("Heading" + j);
		       } else {
		         localXSSFCell.setCellValue(i + j);
		       }   
		     }
		         } 

		    Write output as File 
		    FileOutputStream fileOut = new FileOutputStream("D:\\Excel_Format_As_Table.xlsx");
		    wb.write(fileOut);
		    fileOut.close();
		  }*/

}
