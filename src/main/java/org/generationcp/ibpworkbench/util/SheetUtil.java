package org.generationcp.ibpworkbench.util;

import au.com.bytecode.opencsv.CSVWriter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class SheetUtil {
	/**
	 * Given a sheet, this method deletes a column from a sheet and moves
	 * all the columns to the right of it to the left one cell.
	 * 
	 * Note, this method will not update any formula references.
	 * 
	 * @param sheet
	 * @param column
	 */
	public static void deleteColumn( Sheet sheet, int columnToDelete ){
		int maxColumn = 0;
		for ( int r=0; r < sheet.getLastRowNum()+1; r++ ){
			Row	row	= sheet.getRow( r );
			
			// if no row exists here; then nothing to do; next!
			if ( row == null )
				continue;
			
			// if the row doesn't have this many columns then we are good; next!
			int lastColumn = row.getLastCellNum();
			if ( lastColumn > maxColumn )
				maxColumn = lastColumn;
			
			if ( lastColumn < columnToDelete )
				continue;
			
			for ( int x=columnToDelete+1; x < lastColumn + 1; x++ ){
				Cell oldCell	= row.getCell(x-1);
				if ( oldCell != null )
					row.removeCell( oldCell );
				
				Cell nextCell	= row.getCell( x );
				if ( nextCell != null ){
					Cell newCell	= row.createCell( x-1, nextCell.getCellType() );
					cloneCell(newCell, nextCell);
				}
			}
		}

		
		// Adjust the column widths
		for ( int c=0; c < maxColumn; c++ ){
			sheet.setColumnWidth( c, sheet.getColumnWidth(c+1) );
		}
	}
	
	/*
	 * Takes an existing Cell and merges all the styles and forumla
	 * into the new one
	 */
	private static void cloneCell( Cell cNew, Cell cOld ){
		cNew.setCellComment( cOld.getCellComment() );
		cNew.setCellStyle( cOld.getCellStyle() );
		
		switch ( cNew.getCellType() ){
			case Cell.CELL_TYPE_BOOLEAN:{
				cNew.setCellValue( cOld.getBooleanCellValue() );
				break;
			}
			case Cell.CELL_TYPE_NUMERIC:{
				cNew.setCellValue( cOld.getNumericCellValue() );
				break;
			}
			case Cell.CELL_TYPE_STRING:{
				cNew.setCellValue( cOld.getStringCellValue() );
				break;
			}
			case Cell.CELL_TYPE_ERROR:{
				cNew.setCellValue( cOld.getErrorCellValue() );
				break;
			}
			case Cell.CELL_TYPE_FORMULA:{
				cNew.setCellFormula( cOld.getCellFormula() );
				break;
			}
		}
	}

	/**
	 * 
	 * @param sheet - excel sheet to be converted
	 * @param csvFile - file where csv is written
	 * @throws IOException 
	 */
	public static void sheetToCSV(Sheet sheet,File csvFile) throws IOException {
		CSVWriter csvWriter = new CSVWriter(new FileWriter(csvFile), CSVWriter.DEFAULT_SEPARATOR , CSVWriter.NO_QUOTE_CHARACTER, "\r\n");
		
		Row row = null;
        for (int i = 0; i <= sheet.getLastRowNum(); i++) {
            row = sheet.getRow(i);
            
            ArrayList<String> rowStr = new ArrayList<String>();
            
            for (int j = 0; j < row.getLastCellNum(); j++) {
            	Cell cell = row.getCell(j);
            	
            	if (cell != null) {
            		cell.setCellType(Cell.CELL_TYPE_STRING);
                    rowStr.add(cell.getStringCellValue());
                	
            	} else
            		rowStr.add("");
            }
            
            csvWriter.writeNext(rowStr.toArray(new String[rowStr.size()]));
        }
        
        csvWriter.flush();
        csvWriter.close();
	}
}