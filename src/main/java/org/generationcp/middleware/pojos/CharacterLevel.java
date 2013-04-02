/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/

package org.generationcp.middleware.pojos;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class CharacterLevel implements Serializable{

    private static final long serialVersionUID = -7870779107873158520L;

/*    public static final String GET_BY_OUNIT_ID_LIST = 
            "SELECT oi.ounitid, oi.factorid, f.fname, lc.lvalue " + 
            "FROM oindex oi JOIN level_c lc ON lc.factorid = oi.factorid AND lc.levelno = oi.levelno " + 
                            "JOIN factor f ON f.labelid = lc.labelid " +
            "WHERE oi.ounitid IN (:ounitIdList)";
    
    public static final String GET_CONDITION_AND_VALUE = 
        "SELECT f.fname, lc.lvalue, f.traitid, f.scaleid, f.tmethid, f.ltype " +
        "FROM factor f JOIN level_c lc ON f.labelid = lc.labelid " +
        "WHERE lc.factorid = :factorid AND lc.levelno = :levelno";
    
    public static final String COUNT_STUDIES_BY_GID =
        "SELECT COUNT(DISTINCT s.studyid) "
        + "FROM oindex oi JOIN level_c lc ON lc.factorid = oi.factorid AND lc.levelno = oi.levelno "
                + "JOIN factor f ON f.labelid = lc.labelid "
                + "JOIN study s ON f.studyid = s.studyid "
        + "WHERE f.fname = 'GID' AND lc.lvalue = :gid";
    
    public static final String GET_STUDIES_BY_GID =
        "SELECT s.studyid, s.sname, s.title, s.objectiv, COUNT(DISTINCT oi.ounitid) " 
        + "FROM oindex oi JOIN level_c lc ON lc.factorid = oi.factorid AND lc.levelno = oi.levelno "
                + "JOIN factor f ON f.labelid = lc.labelid "
                + "JOIN study s ON f.studyid = s.studyid "
        + "WHERE f.fname = 'GID' AND lc.lvalue = :gid "
        + "GROUP BY s.studyid";
    
    public static final String GET_BY_FACTOR_AND_REPRESNO =
        "SELECT DISTINCT {lc.*} FROM level_c lc JOIN oindex oi ON lc.factorid = oi.factorid " 
        + "AND lc.levelno = oi.levelno "
        + "WHERE lc.factorid = :factorid AND lc.labelid = :labelid AND oi.represno = :represno";
*/
    
    protected CharacterLevelPK id;

    private String value;

    public CharacterLevel() {
    }

    public CharacterLevel(CharacterLevelPK id, String value) {
        super();
        this.id = id;
        this.value = value;
    }

    public CharacterLevelPK getId() {
        return id;
    }

    public void setId(CharacterLevelPK id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof CharacterLevel)) {
            return false;
        }

        CharacterLevel rhs = (CharacterLevel) obj;
        return new EqualsBuilder().appendSuper(super.equals(obj)).append(id, rhs.id).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(31, 77).append(id).toHashCode();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("CharacterLevel [id=");
        builder.append(id);
        builder.append(", value=");
        builder.append(value);
        builder.append("]");
        return builder.toString();
    }

}
