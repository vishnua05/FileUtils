package com.parser.file_utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
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
import org.apache.commons.csv.CSVPrinter;
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
	public <T> byte[] write(String filePath, Collection<T> writeData) {
		try {
			String name = null;
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filePath));
			CSVPrinter csvPrinter = new CSVPrinter(bufferedWriter, CSVFormat.DEFAULT);
			Iterator iterator = writeData.iterator();
			Class<? extends Object> class1 = iterator.next().getClass();
			Field[] fields = class1.getDeclaredFields();
			Collection<Field> listOfFields = new ArrayList<Field>();
			for (Field field : fields) {
				listOfFields.add(field);
			}

			for (Field fieldName : listOfFields) {
				name = fieldName.getName();
				bufferedWriter.append(name);
				bufferedWriter.append(",");
			}
			bufferedWriter.newLine();
			new CSVPrinter(bufferedWriter, CSVFormat.DEFAULT);
			for (Object object : writeData) {
				for (Field fieldName : listOfFields) {
					Object value = null;
					try {
						fieldName.setAccessible(true);
						value = fieldName.get(object);
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (value != null) {
						if (value instanceof String) {
							bufferedWriter.write(value.toString());
						} else if (value instanceof Long) {
							bufferedWriter.append(value.toString());
						} else if (value instanceof Integer) {
							bufferedWriter.append(value.toString());
						} else if (value instanceof Double) {
							bufferedWriter.append(value.toString());
						} else if (value instanceof Timestamp) {
							bufferedWriter.append(value.toString());
						}
						bufferedWriter.append(",");
					}

				}
				new CSVPrinter(bufferedWriter, CSVFormat.DEFAULT);
				bufferedWriter.newLine();
			}
			/*
			 * while (iterator.hasNext()) { Object object = (Object) iterator.next();
			 * 
			 * }
			 */
			csvPrinter.flush();

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;

	}

}
