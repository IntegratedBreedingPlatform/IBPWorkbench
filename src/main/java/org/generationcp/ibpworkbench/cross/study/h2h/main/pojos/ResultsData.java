
package org.generationcp.ibpworkbench.cross.study.h2h.main.pojos;

import java.io.Serializable;
import java.util.Map;

public class ResultsData implements Serializable {

	private static final long serialVersionUID = -879684249019712493L;

	private String groupId1;
	private Integer gid1;
	private String gid1Name;
	private String groupId2;
	private Integer gid2;
	private String gid2Name;
	private Map<String, String> traitDataMap; // key is the traitName and suffix, then val

	public ResultsData(Integer gid1, String gid1Name, Integer gid2, String gid2Name, Map<String, String> traitDataMap) {
		super();
		this.gid1 = gid1;
		this.gid2 = gid2;
		this.gid1Name = gid1Name;
		this.gid2Name = gid2Name;
		this.traitDataMap = traitDataMap;
	}

	public ResultsData(String groupId1 ,Integer gid1, String gid1Name,String groupId2 , Integer gid2, String gid2Name, Map<String, String> traitDataMap) {
		super();
		this.groupId1 = groupId1;
		this.groupId2 = groupId2;
		this.gid1 = gid1;
		this.gid2 = gid2;
		this.gid1Name = gid1Name;
		this.gid2Name = gid2Name;
		this.traitDataMap = traitDataMap;
	}

	public String getGid1Name() {
		return this.gid1Name;
	}

	public void setGid1Name(String gid1Name) {
		this.gid1Name = gid1Name;
	}

	public String getGid2Name() {
		return this.gid2Name;
	}

	public void setGid2Name(String gid2Name) {
		this.gid2Name = gid2Name;
	}

	public Integer getGid1() {
		return this.gid1;
	}

	public void setGid1(Integer gid1) {
		this.gid1 = gid1;
	}

	public Integer getGid2() {
		return this.gid2;
	}

	public void setGid2(Integer gid2) {
		this.gid2 = gid2;
	}

	public String getGroupId1() {
		return groupId1;
	}

	public void setGroupId1(String groupId1) {
		this.groupId1 = groupId1;
	}

	public String getGroupId2() {
		return groupId2;
	}

	public void setGroupId2(String groupId2) {
		this.groupId2 = groupId2;
	}

	public Map<String, String> getTraitDataMap() {
		return this.traitDataMap;
	}

	public void setTraitDataMap(Map<String, String> traitDataMap) {
		this.traitDataMap = traitDataMap;
	}

}
