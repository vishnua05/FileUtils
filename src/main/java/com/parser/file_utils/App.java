package com.parser.file_utils;

import java.util.Collection;

public class App {
	public static void main(String[] args) {
		IParser parser = new ExcelParser();
		String inputPath = "/media/vishnuvardhanreddya/EEA06B5AA06B2873/Java Softwares/Excel/student.xls";
		Collection<Student> StudentDetails = parser.read(inputPath, Student.class);
		System.out.println(StudentDetails);
		String outputFilePath = "/media/vishnuvardhanreddya/EEA06B5AA06B2873/Java Softwares/Excel/studentoutput.xlsx";
		parser.write(outputFilePath, StudentDetails);
		System.out.println("Data successfully written to Excel.Thank you");
	}
}
