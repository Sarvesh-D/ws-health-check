package com.barclays.solveit.ws.health.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.record.CFRuleBase.ComparisonOperator;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ConditionalFormattingRule;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.PatternFormatting;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.SheetConditionalFormatting;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellRangeAddress;
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

	private Sheet worksheet;

	public Workbook loadWorkbook() {
		loadWorkbook(new XSSFWorkbook());
		return getWorkbook();
	}

	public Workbook loadWorkbook(Workbook workbook) {
		this.workbook = workbook;
		return getWorkbook();
	}

	public Sheet loadWorksheet() {
		return loadWorksheet("sheet_0");
	}

	public Sheet loadWorksheet(String sheetName) {
		return loadWorksheet(getWorkbook().createSheet(sheetName));
	}

	public Sheet loadWorksheet(Sheet sheet) {
		this.worksheet = sheet;
		return getWorksheet();
	}

	public Workbook getWorkbook() {
		Assert.notNull(workbook, "Workbook has not been loaded, load the workbook using loadWorkbook().");
		return this.workbook;
	}

	public Sheet getWorksheet() {
		Assert.notNull(workbook, "Worksheet has not been loaded, load the worksheet using loadWorksheet().");
		return this.worksheet;
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

	public void formatAsTable(int rowStartIndex, int rowEndIndex, int colStartIndex, int colEndIndex) {
		Assert.isTrue(rowEndIndex > rowStartIndex, "rowEndIndex must be greater than rowStartIndex");
		Assert.isTrue(colEndIndex > colStartIndex, "colEndIndex must be greater than colStartIndex");

		/* Create an object of type XSSFTable */
		XSSFTable my_table = ((XSSFSheet) getWorksheet()).createTable();

		/* get CTTable object*/
		CTTable cttable = my_table.getCTTable();

		/* define the required Style for the table */    
		CTTableStyleInfo table_style = cttable.addNewTableStyleInfo();
		table_style.setName("TableStyleMedium9");

		table_style.setShowRowStripes(true);
		table_style.setShowColumnStripes(false);

		CTTableColumns columns = cttable.addNewTableColumns();
		columns.setCount(1 + colEndIndex-colStartIndex); //define number of columns

		/* Define the data range including headers */
		AreaReference my_data_range = new AreaReference(new CellReference(rowStartIndex, colStartIndex),
				new CellReference(rowEndIndex, colEndIndex));

		/* Set Range to the Table */
		cttable.setRef(my_data_range.formatAsString());
		cttable.setDisplayName("MYTABLE");      /* this is the display name of the table */
		cttable.setName("Test");    /* This maps to "displayName" attribute in <table>, OOXML */            
		cttable.setId(1L); //id attribute against table as long value

		for (int j = colStartIndex; j <= colEndIndex; j++) {
			CTTableColumn column = columns.addNewTableColumn();   
			column.setName("Column" + j);
			column.setId(j+1);
		}

	}

	public void createConditionalFormattingRule() {
		SheetConditionalFormatting sheetCF = getWorksheet().getSheetConditionalFormatting();
		ConditionalFormattingRule successRule = sheetCF.createConditionalFormattingRule(ComparisonOperator.EQUAL,"PASSED");
		ConditionalFormattingRule failureRule = sheetCF.createConditionalFormattingRule(ComparisonOperator.EQUAL,"FAILED");

		PatternFormatting patternFmtSuccess = successRule.createPatternFormatting();
		patternFmtSuccess.setFillBackgroundColor(IndexedColors.GREEN.index);

		PatternFormatting patternFmtFailure = failureRule.createPatternFormatting();
		patternFmtFailure.setFillBackgroundColor(IndexedColors.RED.index);

		ConditionalFormattingRule [] cfRules = {successRule, failureRule};

		CellRangeAddress[] regions = {CellRangeAddress.valueOf("F2")};
		
		sheetCF.addConditionalFormatting(regions, cfRules);

	}

	public void autoAdjustColumnWidth(int colIndex) {
		getWorksheet().autoSizeColumn(colIndex);
	}

	public void autoAdjustColumnWidth(int colStartIndex, int colEndIndex) {
		Assert.isTrue(colEndIndex > colStartIndex, "colEndIndex must be greater than colStartIndex");
		for (int i = colStartIndex; i <= colEndIndex; i++) {
			autoAdjustColumnWidth(i);
		}
	}
	
	public Row getRowData(int rowIndex) {
		return getWorksheet().getRow(rowIndex);
	}
	
	public List<Row> getRowData(int rowStartIndex, int rowEndIndex) {
		List<Row> rows = new ArrayList<>();
		for (int i = rowStartIndex; i <= rowEndIndex; i++) {
			rows.add(getRowData(i));
		}
		return rows;
	}

}
