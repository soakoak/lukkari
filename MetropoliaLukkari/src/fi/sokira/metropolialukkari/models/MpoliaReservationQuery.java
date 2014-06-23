package fi.sokira.metropolialukkari.models;

import java.util.Date;
import java.util.List;

public class MpoliaReservationQuery implements MpoliaQuery {

	private String subject;
	private Date startDate;
	private Date endDate;
	private List<String> studentGroup;
	
	public MpoliaReservationQuery() {
	}

	public String getSubject() {
		return subject;
	}

	public MpoliaReservationQuery setSubject(String subject) {
		this.subject = subject;
		return this;
	}

	public Date getStartDate() {
		return startDate;
	}

	public MpoliaReservationQuery setStartDate(Date startDate) {
		this.startDate = startDate;
		return this;
	}

	public Date getEndDate() {
		return endDate;
	}

	public MpoliaReservationQuery setEndDate(Date endDate) {
		this.endDate = endDate;
		return this;
	}

	public List<String> getStudentGroup() {
		return studentGroup;
	}

	public MpoliaReservationQuery setStudentGroup(List<String> studentGroup) {
		this.studentGroup = studentGroup;
		return this;
	}
	
	
}
