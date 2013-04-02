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

public class CharacterDataPK implements Serializable{

    private static final long serialVersionUID = 1L;

    private Integer observationUnitId;

    private Integer variateId;

    public CharacterDataPK() {
    }

    public CharacterDataPK(Integer observationUnitId, Integer variateId) {
        super();
        this.observationUnitId = observationUnitId;
        this.variateId = variateId;
    }

    public Integer getObservationUnitId() {
        return observationUnitId;
    }

    public void setObservationUnitId(Integer observationUnitId) {
        this.observationUnitId = observationUnitId;
    }

    public Integer getVariateId() {
        return variateId;
    }

    public void setVariateId(Integer variateId) {
        this.variateId = variateId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (observationUnitId == null ? 0 : observationUnitId.hashCode());
        result = prime * result + (variateId == null ? 0 : variateId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        CharacterDataPK other = (CharacterDataPK) obj;
        if (observationUnitId == null) {
            if (other.observationUnitId != null) {
                return false;
            }
        } else if (!observationUnitId.equals(other.observationUnitId)) {
            return false;
        }
        if (variateId == null) {
            if (other.variateId != null) {
                return false;
            }
        } else if (!variateId.equals(other.variateId)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("CharacterDataPK [observationUnitId=");
        builder.append(observationUnitId);
        builder.append(", variateId=");
        builder.append(variateId);
        builder.append("]");
        return builder.toString();
    }

}
