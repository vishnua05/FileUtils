package com.parser.file_utils;

import java.sql.Timestamp;

public class Student {
	int rollNo;
	String studentName;
	int sub1;
	int sub2;
	int sub3;
	int total;
	double percentage;
	Timestamp addOnDate;

	public int getRollNo() {
		return rollNo;
	}

	public void setRollNo(int rollNo) {
		this.rollNo = rollNo;
	}

	public String getStudentName() {
		return studentName;
	}

	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}

	public int getSub1() {
		return sub1;
	}

	public void setSub1(int sub1) {
		this.sub1 = sub1;
	}

	public int getSub2() {
		return sub2;
	}

	public void setSub2(int sub2) {
		this.sub2 = sub2;
	}

	public int getSub3() {
		return sub3;
	}

	public void setSub3(int sub3) {
		this.sub3 = sub3;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public double getPercentage() {
		return percentage;
	}

	public void setPercentage(double percentage) {
		this.percentage = percentage;
	}

	public Timestamp getAddOnDate() {
		return addOnDate;
	}

	public void setAddOnDate(Timestamp addOnDate) {
		this.addOnDate = addOnDate;
	}

	@Override
	public String toString() {
		return "Student [rollNo=" + rollNo + ", studentName=" + studentName + ", sub1=" + sub1 + ", sub2=" + sub2
				+ ", sub3=" + sub3 + ", total=" + total + ", percentage=" + percentage + ", addOnDate=" + addOnDate
				+ "]";
	}

}
