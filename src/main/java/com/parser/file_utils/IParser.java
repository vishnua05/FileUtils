package com.parser.file_utils;

import java.util.Collection;

public interface IParser {
	public static final String EXCEL_FORMAT_2003 = "xls";
	public static final String EXCEL_FORMAT = "xlsx";
 
	public <T> Collection<T> read(String file, Class<T> className);
 	public <T> byte[] write(String file, Collection<T> writeData);
}
