package com.parser.file_utils;

import java.util.Collection;

public class ExcelApp {
	public static void main(String[] args) {
		IParser excelParser = new ExcelParser();
		String inputPath = "/media/vishnuvardhanreddya/EEA06B5AA06B2873/Java Softwares/Excel/student.xls";
		Collection<Student> StudentDetails = excelParser.read(inputPath, Student.class);
		System.out.println("StudentExcel Details:"+StudentDetails);
		String outputFilePath = "/media/vishnuvardhanreddya/EEA06B5AA06B2873/Java Softwares/Excel/studentoutput.xlsx";
		excelParser.write(outputFilePath, StudentDetails);
		System.out.println("Data successfully written to Excel.Thank you");
	}
}
