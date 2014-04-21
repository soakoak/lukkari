package fi.sokira.metropolialukkari.models;

import java.util.Date;
import java.util.List;

public class RealizationQuery implements MetropoliaQuery {
	
	private String name;
	private List<String> codes;
	private Date startDate;
	private Date endDate;
	private List<String> studentGroups;
	
	public RealizationQuery() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getCodes() {
		return codes;
	}

	public void setCodes(List<String> codes) {
		this.codes = codes;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public List<String> getStudentGroups() {
		return studentGroups;
	}

	public void setStudentGroups(List<String> studentGroups) {
		this.studentGroups = studentGroups;
	}

}
