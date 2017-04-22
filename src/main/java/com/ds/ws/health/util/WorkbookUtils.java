package com.ds.ws.health.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
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

import com.ds.ws.health.exception.HealthCheckException;

/**
 * Utility class for {@link Workbook} operations.<br>
 * Most of the methods rely on the {@link Workbook} and {@link Sheet} being
 * loaded
 * 
 * @author <a href="mailto:sarvesh.dubey@hotmail.com">Sarvesh Dubey</a>
 * @since 29/08/2016
 * @version 1.0
 *
 */
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class WorkbookUtils {

    private Workbook workbook;

    private Sheet worksheet;

    /**
     * Auto adjust the length of column corresponding to given index
     * 
     * @param colIndex
     */
    public void autoAdjustColumnWidth(int colIndex) {
	getWorksheet().autoSizeColumn(colIndex);
    }

    /**
     * Auto adjust multiple columns
     * 
     * @param colStartIndex
     * @param colEndIndex
     */
    public void autoAdjustColumnWidth(int colStartIndex, int colEndIndex) {
	Assert.isTrue(colEndIndex > colStartIndex, "colEndIndex must be greater than colStartIndex");
	for (int i = colStartIndex; i <= colEndIndex; i++) {
	    autoAdjustColumnWidth(i);
	}
    }

    /**
     * Creates hyperlink for the given URI
     * 
     * @param uri
     * @return {@link Hyperlink}
     */
    public Hyperlink createHyperlink(String uri) {
	Hyperlink link = getWorkbook().getCreationHelper()
		.createHyperlink(org.apache.poi.common.usermodel.Hyperlink.LINK_URL);
	link.setAddress(uri);
	return link;
    }

    /**
     * Makes the passed {@link Cell} a hyperlink to passed uri.<br>
     * The text of the cell is the uri passed.
     * 
     * @param cell
     * @param uri
     * @return Hyperlink cell
     */
    public Cell createHyperlinkCell(Cell cell, String uri) {
	return createHyperlinkCell(cell, uri, uri);
    }

    /**
     * Makes the passed {@link Cell} a hyperlink to passed uri.<br>
     * The text of the cell is the text passed.
     * 
     * @param cell
     * @param uri
     * @param text
     * @return
     */
    public Cell createHyperlinkCell(Cell cell, String uri, String text) {
	cell.setCellStyle(getHyperlinkCellStyle());
	cell.setHyperlink(createHyperlink(uri));
	cell.setCellValue(text);
	return cell;
    }

    /**
     * Formats the region as Table
     * 
     * @param rowStartIndex
     * @param rowEndIndex
     * @param colStartIndex
     * @param colEndIndex
     */
    public void formatAsTable(int rowStartIndex, int rowEndIndex, int colStartIndex, int colEndIndex) {
	Assert.isTrue(rowEndIndex > rowStartIndex, "rowEndIndex must be greater than rowStartIndex");
	Assert.isTrue(colEndIndex > colStartIndex, "colEndIndex must be greater than colStartIndex");

	/* Create an object of type XSSFTable */
	XSSFTable my_table = ((XSSFSheet) getWorksheet()).createTable();

	/* get CTTable object */
	CTTable cttable = my_table.getCTTable();

	/* define the required Style for the table */
	CTTableStyleInfo table_style = cttable.addNewTableStyleInfo();
	table_style.setName("TableStyleMedium9");

	table_style.setShowRowStripes(true);
	table_style.setShowColumnStripes(false);

	CTTableColumns columns = cttable.addNewTableColumns();
	columns.setCount(1 + colEndIndex - colStartIndex); // define number of
							   // columns

	/* Define the data range including headers */
	AreaReference my_data_range = new AreaReference(new CellReference(rowStartIndex, colStartIndex),
		new CellReference(rowEndIndex, colEndIndex));

	/* Set Range to the Table */
	cttable.setRef(my_data_range.formatAsString());
	cttable.setDisplayName(
		"MYTABLE"); /* this is the display name of the table */
	cttable.setName("Test"); /*
				  * This maps to "displayName" attribute in
				  * <table>, OOXML
				  */
	cttable.setId(1L); // id attribute against table as long value

	for (int j = colStartIndex; j <= colEndIndex; j++) {
	    CTTableColumn column = columns.addNewTableColumn();
	    column.setName("Column" + j);
	    column.setId(j + 1);
	}

    }

    /**
     * @return Hyperlink cell style
     */
    public CellStyle getHyperlinkCellStyle() {
	// cell style for hyperlinks
	// by default hyperlinks are blue and underlined
	CellStyle hlinkStyle = getWorkbook().createCellStyle();
	Font hlinkFont = getWorkbook().createFont();
	hlinkFont.setUnderline(Font.U_SINGLE);
	hlinkFont.setColor(IndexedColors.BLUE.getIndex());
	hlinkStyle.setFont(hlinkFont);
	return hlinkStyle;
    }

    /**
     * Gets the row corresponding to given index
     * 
     * @param rowIndex
     * @return {@link Row}
     */
    public Row getRowData(int rowIndex) {
	return getWorksheet().getRow(rowIndex);
    }

    /**
     * Gets the List of row corresponding to given index
     * 
     * @param rowStartIndex
     * @param rowEndIndex
     * @return {@link Row}
     */
    public List<Row> getRowData(int rowStartIndex, int rowEndIndex) {
	List<Row> rows = new ArrayList<>();
	for (int i = rowStartIndex; i <= rowEndIndex; i++) {
	    rows.add(getRowData(i));
	}
	return rows;
    }

    /**
     * @return The loaded Workbook
     */
    public Workbook getWorkbook() {
	Assert.notNull(workbook, "Workbook has not been loaded, load the workbook using loadWorkbook().");
	return this.workbook;
    }

    /**
     * @return The loaded {@link Sheet}
     */
    public Sheet getWorksheet() {
	Assert.notNull(workbook, "Workbook has not been loaded, load the workbook using loadWorkbook().");
	return this.worksheet;
    }

    /**
     * Checks if the sheet is empty or not.<br>
     * A sheet is considered as empty if total rows are 0.
     * 
     * @param sheet
     * @return True if the sheet is empty, False otherwise.
     */
    public boolean isWorkSheetEmpty(Sheet sheet) {
	return sheet.getLastRowNum() <= 0;
    }

    /**
     * Loads a default {@link XSSFWorkbook} and returns the same.
     * 
     * @return {@link XSSFWorkbook}
     */
    public Workbook loadWorkbook() {
	loadWorkbook(new XSSFWorkbook());
	return getWorkbook();
    }

    /**
     * Loads the given workbook
     * 
     * @param workbook
     *            to load
     * @return the loaded Workbook, same as passed workbook
     */
    public Workbook loadWorkbook(Workbook workbook) {
	this.workbook = workbook;
	return getWorkbook();
    }

    /**
     * Loads and returns a default Sheet with name sheet_0.<br>
     * This results in a new sheet being created in the loaded Workbook
     * 
     * @return the default worksheet
     */
    public Sheet loadWorksheet() {
	return loadWorksheet("sheet_0");
    }

    /**
     * Loads and returns the given sheet.<br>
     * 
     * @param Sheet
     *            existing sheet
     * @return the loaded sheet, same as the given sheet.
     */
    public Sheet loadWorksheet(Sheet sheet) {
	this.worksheet = sheet;
	return getWorksheet();
    }

    /**
     * Loads and returns sheet with given name.<br>
     * This results in a new sheet being created in the loaded Workbook
     * 
     * @param sheetName
     *            of sheet to be created
     * @return the loaded sheet
     */
    public Sheet loadWorksheet(String sheetName) {
	return loadWorksheet(getWorkbook().createSheet(sheetName));
    }

    /**
     * Once the report file is rolled over at 00:00:00, The new report file is
     * empty, to populate the new report file an explicit ping is triggered on
     * the services. However, if this service is called before the explicit ping
     * executes, the new report file will be empty causing rowEndIndex = 0 and
     * rowStartIndex = -ve; causing NullPointer in the for loop. This error
     * window is rather small lasting from file rollover time till the explicit
     * ping executes: approximately 00:00:00 to 00:00:10
     */
    public void proceedIfWorksheetNotEmpty(Sheet reportSheet) {
	if (isWorkSheetEmpty(reportSheet)) {
	    try {
		Thread.sleep(1000);
		proceedIfWorksheetNotEmpty(reportSheet);
	    } catch (InterruptedException e) {
		throw new HealthCheckException(e.getMessage());
	    }
	} else
	    return;
    }

}
