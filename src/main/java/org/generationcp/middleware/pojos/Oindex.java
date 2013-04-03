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

public class Oindex implements Serializable{

    private static final long serialVersionUID = -6609291577310766245L;
    /*
    public static final String GET_FACTORID_AND_LEVELNO_OF_CONDITIONS_BY_REPRESNO = 
                "SELECT factorid, levelno " +
    		"FROM oindex " +
    		"WHERE represno = :represno " +
    		"GROUP BY factorid, levelno " +
    		"HAVING COUNT(DISTINCT ounitid) = :count";
    */
    private Integer id;

    private Integer observationUnitId;

    private Integer factorId;

    private Integer levelNumber;

    private Integer representationNumber;

    public Oindex() {
    }

    public Oindex(Integer id, Integer observationUnitId, Integer factorId, Integer levelNumber, Integer representationNumber) {
        super();
        this.id = id;
        this.observationUnitId = observationUnitId;
        this.factorId = factorId;
        this.levelNumber = levelNumber;
        this.representationNumber = representationNumber;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getObservationUnitId() {
        return observationUnitId;
    }

    public void setObservationUnitId(Integer observationUnitId) {
        this.observationUnitId = observationUnitId;
    }

    public Integer getFactorId() {
        return factorId;
    }

    public void setFactorId(Integer factorId) {
        this.factorId = factorId;
    }

    public Integer getLevelNumber() {
        return levelNumber;
    }

    public void setLevelNumber(Integer levelNumber) {
        this.levelNumber = levelNumber;
    }

    public Integer getRepresentationNumber() {
        return representationNumber;
    }

    public void setRepresentationNumber(Integer representationNumber) {
        this.representationNumber = representationNumber;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Oindex [id=");
        builder.append(id);
        builder.append(", observationUnitId=");
        builder.append(observationUnitId);
        builder.append(", factorId=");
        builder.append(factorId);
        builder.append(", levelNumber=");
        builder.append(levelNumber);
        builder.append(", representationNumber=");
        builder.append(representationNumber);
        builder.append("]");
        return builder.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Oindex)) {
            return false;
        }

        Oindex rhs = (Oindex) obj;
        return new EqualsBuilder().appendSuper(super.equals(obj)).append(id, rhs.id).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(9, 29).append(id).toHashCode();
    }

}
