package com.parser.file_utils;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


	public class ExcelParser implements IParser {

		private static Workbook readFile(String filename) throws IOException {
			try (FileInputStream fis = new FileInputStream(filename)) {
				return new HSSFWorkbook(fis); // NOSONAR - should not be closed here
			}
		}

		/*private static void testCreateSampleSheet(String outputFilename) throws IOException {
			try (Workbook wb = new HSSFWorkbook()) {
				Sheet s = wb.createSheet();
				CellStyle cs = wb.createCellStyle();
				CellStyle cs2 = wb.createCellStyle();
				CellStyle cs3 = wb.createCellStyle();
				Font f = wb.createFont();
				Font f2 = wb.createFont();

				f.setFontHeightInPoints((short) 12);
				f.setColor((short) 0xA);
				f.setBold(true);
				f2.setFontHeightInPoints((short) 10);
				f2.setColor((short) 0xf);
				f2.setBold(true);
				cs.setFont(f);
				cs.setDataFormat(HSSFDataFormat.getBuiltinFormat("($#,##0_);[Red]($#,##0)"));
				cs2.setBorderBottom(BorderStyle.THIN);
				cs2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				cs2.setFillForegroundColor((short) 0xA);
				cs2.setFont(f2);
				wb.setSheetName(0, "HSSF Test");
				int rownum;
				for (rownum = 0; rownum < 300; rownum++) {
					Row r = s.createRow(rownum);
					if ((rownum % 2) == 0) {
						r.setHeight((short) 0x249);
					}

					for (int cellnum = 0; cellnum < 50; cellnum += 2) {
						Cell c = r.createCell(cellnum);
						c.setCellValue(rownum * 10000 + cellnum + (((double) rownum / 1000) + ((double) cellnum / 10000)));
						if ((rownum % 2) == 0) {
							c.setCellStyle(cs);
						}
						c = r.createCell(cellnum + 1);
						c.setCellValue(new HSSFRichTextString("TEST"));
						// 50 characters divided by 1/20th of a point
						s.setColumnWidth(cellnum + 1, (int) (50 * 8 / 0.05));
						if ((rownum % 2) == 0) {
							c.setCellStyle(cs2);
						}
					}
				}

				// draw a thick black border on the row at the bottom using BLANKS
				rownum++;
				rownum++;
				Row r = s.createRow(rownum);
				cs3.setBorderBottom(BorderStyle.THICK);
				for (int cellnum = 0; cellnum < 50; cellnum++) {
					Cell c = r.createCell(cellnum);
					c.setCellStyle(cs3);
				}
				s.addMergedRegion(new CellRangeAddress(0, 3, 0, 3));
				s.addMergedRegion(new CellRangeAddress(100, 110, 100, 110));

				// end draw thick black border
				// create a sheet, set its title then delete it
				wb.createSheet();
				wb.setSheetName(1, "DeletedSheet");
				wb.removeSheetAt(1);

				// end deleted sheet
				try (FileOutputStream out = new FileOutputStream(outputFilename)) {
					wb.write(out);
				}
			}
		}
*/
		@Override
		public <T> Collection<T> read(String file, Class<T> className) {
			Collection<T> records = new ArrayList<>();
			try {
				T object = null;
				Workbook workbook = ExcelParser.readFile(file);
				System.out.println("Data dump:\n");
				FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
				for (int k = 0; k < workbook.getNumberOfSheets(); k++) {
					Sheet sheet = workbook.getSheetAt(k);
					int rows = sheet.getPhysicalNumberOfRows();
					System.out.println("Sheet " + k + " \"" + workbook.getSheetName(k) + "\" has " + rows + " row(s).");
					List<String> headers = new ArrayList<>();
					for (int r = 0; r < rows; r++) {
						Row row = sheet.getRow(r);
						if (row == null) {
							continue;
						}

						System.out.println(
								"\nROW " + row.getRowNum() + " has " + row.getPhysicalNumberOfCells() + " cell(s).");

						Field[] fields = className.getDeclaredFields();
						Collection<Field> fields2 = null;
						Cell cell = null;
						if(r==0) {
							for (int c = 0; c < row.getLastCellNum(); c++) {
								cell = row.getCell(c);
								headers.add(cell.getStringCellValue());
								System.out.println("CELL col=" + cell.getColumnIndex() + " Header=" + cell);
							}
						} else {
							object = className.newInstance();
							if(headers.size() == row.getLastCellNum()) {
								for (int c = 0; c < row.getLastCellNum(); c++) {
									cell = row.getCell(c);
									Object value = null;
									Field field = className.getDeclaredField(headers.get(c));
									Class<? extends Object> type = field.getType();
									if (field != null && cell != null) {

										switch (evaluator.evaluateInCell(cell).getCellTypeEnum()) {

										case FORMULA:
											//field.set(object, cell.getNumericCellValue());
											value =  cell.getNumericCellValue();
											break;

										case NUMERIC:
											if (DateUtil.isCellDateFormatted(cell)) {
												java.util.Date dateValue = cell.getDateCellValue();
												if(type.getSimpleName().equalsIgnoreCase(Timestamp.class.getSimpleName())) {
													value = new Timestamp(dateValue.getTime());
												}
											} else {
												value = cell.getNumericCellValue();
												Double doubleValue = cell.getNumericCellValue();
												if("int".equalsIgnoreCase(type.getSimpleName())) {
													value = doubleValue.intValue();
												} else {
													value = doubleValue;
												}
												
											}
											break;

										case STRING:
											value = cell.getStringCellValue();
											break;

										case BLANK:
											value = "";
											break;

										case BOOLEAN:
											value = cell.getBooleanCellValue();
											break;

										case ERROR:
											value = cell.getErrorCellValue();
											break;

										default:
											value = "UNKNOWN " + cell.getCellType();
										}
										field.set(object, value);
										System.out.println("CELL col=" + cell.getColumnIndex() + " VALUE=" + value);


									}
								}
								records.add(object);					
							}
						}
					}
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			return records;
		}

		@Override
		public <T> byte[] write(String file, Collection<T> writeData) {
			long time = System.currentTimeMillis();
			byte[] byteData = null;
			try (OutputStream outputStream = new FileOutputStream(file)) {
				if (FilenameUtils.getExtension(file).equalsIgnoreCase(IParser.EXCEL_FORMAT_2003))
					byteData = ExcelParser.createXLSXFile(writeData, file);
				else
					byteData = ExcelParser.createXLSFile(writeData, file);
				IOUtils.write(byteData, outputStream);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("" + (System.currentTimeMillis() - time) + " ms generation time");
			return byteData;
		}

		private static <T> byte[] createXLSFile(Collection<T> writeData, String file) throws IOException {
			byte[] byteData = null;
			try (Workbook wb = new HSSFWorkbook()) {
				createExcel(wb, writeData); // end deleted sheet
				try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
					wb.write(out);
					byteData = out.toByteArray();
				}
			}
			return byteData;
		}

		private static <T> byte[] createXLSXFile(Collection<T> writeData, String file) throws IOException {
			byte[] byteData = null;
			try (Workbook wb = new XSSFWorkbook()) {
				createExcel(wb, writeData); // end deleted sheet
				try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
					wb.write(out);
					byteData = out.toByteArray();
				}
			}
			return byteData;
		}

		private static <T> void createExcel(Workbook wb, Collection<T> writeData) {
			Sheet s = wb.createSheet();
			wb.setSheetName(0, "HSSF Test");
			int rownum = 0;
			int cellnum = 0;
			Row row = null;
			Cell cell = null;
			Iterator<T> iterator = writeData.iterator();
			Class<? extends Object> class1 = iterator.next().getClass();

			Field[] fields = class1.getDeclaredFields();

			row = s.createRow(rownum++);
			Collection<Field> fieldNames = new ArrayList<>();
			for (Field field : fields) {
				fieldNames.add(field);
			}
			for (Field fieldName1 : fieldNames) {
				cell = row.createCell(cellnum++);
				cell.setCellValue(fieldName1.getName());
			}

			for (T object : writeData) {
				row = s.createRow(rownum++);
				cellnum = 0;

				for (Field fieldName : fieldNames) {
					cell = row.createCell(cellnum);
					Method method = null;
					Object value = null;
					try {
						fieldName.setAccessible(true);
						value = fieldName.get(object);
					} catch (Exception e) {
						e.printStackTrace();
					}

					if (value != null) {
						if (value instanceof String) {
							cell.setCellValue((String) value);
						} else if (value instanceof Long) {
							cell.setCellValue((Long) value);
						} else if (value instanceof Integer) {
							cell.setCellValue((Integer) value);
						} else if (value instanceof Double) {
							cell.setCellValue((Double) value);
						} else if (value instanceof Timestamp) {
							cell.setCellValue(value.toString());
						}
					}
					cellnum++;
				}
			}

		}

		private static String capitalize(final Object fieldName) {
			// TODO Auto-generated method stub
			String name = (String) fieldName;
			if (name.length() == 0) {
				return name;
			}

			return name.substring(0, 1).toUpperCase() + name.substring(1);
		}

	}

