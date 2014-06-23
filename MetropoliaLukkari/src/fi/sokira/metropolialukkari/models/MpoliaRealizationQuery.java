package fi.sokira.metropolialukkari.models;

import java.util.Date;
import java.util.List;

public class MpoliaRealizationQuery implements MpoliaQuery {
	
	private String name;
	private List<String> codes;
	private Date startDate;
	private Date endDate;
	private List<String> studentGroups;
	
	public MpoliaRealizationQuery() {
	}

	public String getName() {
		return name;
	}

	public MpoliaRealizationQuery setName(String name) {
		this.name = name;
		return this;
	}

	public List<String> getCodes() {
		return codes;
	}

	public MpoliaRealizationQuery setCodes(List<String> codes) {
		this.codes = codes;
		return this;
	}

	public Date getStartDate() {
		return startDate;
	}

	public MpoliaRealizationQuery setStartDate(Date startDate) {
		this.startDate = startDate;
		return this;
	}

	public Date getEndDate() {
		return endDate;
	}

	public MpoliaRealizationQuery setEndDate(Date endDate) {
		this.endDate = endDate;
		return this;
	}

	public List<String> getStudentGroups() {
		return studentGroups;
	}

	public MpoliaRealizationQuery setStudentGroups(List<String> studentGroups) {
		this.studentGroups = studentGroups;
		return this;
	}

}
