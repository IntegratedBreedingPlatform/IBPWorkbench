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

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The primary identifier of {@link ScaleDiscrete}.
 * 
 */
@Embeddable
public class ScaleDiscretePK implements Serializable{

    private static final long serialVersionUID = 1L;

    @Basic(optional = false)
    @Column(name = "scaleid")
    private Integer scaleId;

    @Basic(optional = false)
    @Column(name = "value")
    private String value;

    public ScaleDiscretePK() {
    }

    public ScaleDiscretePK(Integer scaleId, String value) {
        super();
        this.scaleId = scaleId;
        this.value = value;
    }

    public Integer getScaleId() {
        return scaleId;
    }

    public void setScaleId(Integer scaleId) {
        this.scaleId = scaleId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (scaleId == null ? 0 : scaleId.hashCode());
        result = prime * result + (value == null ? 0 : value.hashCode());
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
        ScaleDiscretePK other = (ScaleDiscretePK) obj;
        if (scaleId == null) {
            if (other.scaleId != null) {
                return false;
            }
        } else if (!scaleId.equals(other.scaleId)) {
            return false;
        }
        if (value == null) {
            if (other.value != null) {
                return false;
            }
        } else if (!value.equals(other.value)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ScaleDiscretePK [scaleId=");
        builder.append(scaleId);
        builder.append(", value=");
        builder.append(value);
        builder.append("]");
        return builder.toString();
    }


    
    
}
