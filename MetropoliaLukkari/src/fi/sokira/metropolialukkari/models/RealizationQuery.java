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

	public RealizationQuery setName(String name) {
		this.name = name;
		return this;
	}

	public List<String> getCodes() {
		return codes;
	}

	public RealizationQuery setCodes(List<String> codes) {
		this.codes = codes;
		return this;
	}

	public Date getStartDate() {
		return startDate;
	}

	public RealizationQuery setStartDate(Date startDate) {
		this.startDate = startDate;
		return this;
	}

	public Date getEndDate() {
		return endDate;
	}

	public RealizationQuery setEndDate(Date endDate) {
		this.endDate = endDate;
		return this;
	}

	public List<String> getStudentGroups() {
		return studentGroups;
	}

	public RealizationQuery setStudentGroups(List<String> studentGroups) {
		this.studentGroups = studentGroups;
		return this;
	}

}
