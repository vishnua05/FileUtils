package com.parser.file_utils;

import java.util.Collection;

public class CSVApp {
	public static void main(String[] args) {
		IParser csvParser = new CSVFileParser();
		String inputCSV = "/media/vishnuvardhanreddya/EEA06B5AA06B2873/Java Softwares/Excel/student.csv";
		Collection<Student> studentCSVDetails = csvParser.read(inputCSV, Student.class);
		System.out.println("StudentCSVDetails:"+studentCSVDetails);
	}
}
