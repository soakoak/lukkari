package fi.sokira.metropolialukkari.models;

import java.util.Date;
import java.util.List;

public class ReservationQuery implements MetropoliaQuery {

	private String subject;
	private Date startDate;
	private Date endDate;
	private List<String> studentGroup;
	
	public ReservationQuery() {
	}

	public String getSubject() {
		return subject;
	}

	public ReservationQuery setSubject(String subject) {
		this.subject = subject;
		return this;
	}

	public Date getStartDate() {
		return startDate;
	}

	public ReservationQuery setStartDate(Date startDate) {
		this.startDate = startDate;
		return this;
	}

	public Date getEndDate() {
		return endDate;
	}

	public ReservationQuery setEndDate(Date endDate) {
		this.endDate = endDate;
		return this;
	}

	public List<String> getStudentGroup() {
		return studentGroup;
	}

	public ReservationQuery setStudentGroup(List<String> studentGroup) {
		this.studentGroup = studentGroup;
		return this;
	}
	
	
}
