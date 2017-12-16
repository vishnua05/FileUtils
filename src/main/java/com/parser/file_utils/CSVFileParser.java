package com.parser.file_utils;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class CSVFileParser implements IParser {

	@Override
	public <T> Collection<T> read(String file, Class<T> className) {
		FileReader fileReader = null;
		CSVParser csvParser = null;
		Collection<CSVRecord> collectionOfRecords = null;
		T object = null;
		Field field = null;
		try {
			fileReader = new FileReader(new File(file));
			csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT);
			collectionOfRecords = csvParser.getRecords();

		} catch (Exception e) {
			e.printStackTrace();
		}
		Iterator<CSVRecord> iterator = collectionOfRecords.iterator();
		List<String> headers = new ArrayList<>();
		Collection<T> dataRecords = new ArrayList<>();
		CSVRecord record = null;

		while (iterator.hasNext()) {
			record = iterator.next();
			int i = 0;
			Iterator<String> list = record.iterator();
			String recordDetails = null;
			while (list.hasNext()) {
				recordDetails = list.next();
				try {
					if (record.getRecordNumber() == 1) {
						headers.add(recordDetails);
					} else {
						object = className.newInstance();
						// recordDetails = list.next();
						for (int j = 0; j < headers.size(); j++) {
							field = className.getDeclaredField(headers.get(j));
							Class<? extends Object> type = field.getType();
							Object value = null;
							if (field != null) {
								switch (type.getSimpleName()) {
								case "int":
									value = Integer.parseInt(recordDetails);
									break;
								case "double":
									value = Double.parseDouble(recordDetails);
									break;
								case "float":
									value = Float.parseFloat(recordDetails);
									break;
								case "String":
									value = recordDetails.toString();
									break;
								case "Timestamp":
									Date format = null;
									try {
										format = new SimpleDateFormat("dd/MM/yy").parse(recordDetails);
									} catch (ParseException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}

									value = new Timestamp(format.getTime());
									break;
								default:
									break;
								}
							}
							field.set(object, value);
							if (list.hasNext()) {
								recordDetails = list.next();
							}
						}
						dataRecords.add(object);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		}
		return dataRecords;
	}

	@Override
	public <T> byte[] write(String file, Collection<T> writeData) {
		return null;
	}

}
