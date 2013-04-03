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

public class Trait implements Serializable{

    private static final long serialVersionUID = 1L;

    private Integer id;

    private Integer traitId;

    private String name;

    private String abbreviation;

    private String descripton;

    private Integer nameStatus;

    public Trait() {
    }

    public Trait(Integer id) {
        super();
        this.id = id;
    }

    public Trait(Integer id, Integer traitId, String name, String abbreviation, String descripton, Scale mainScale, TraitMethod mainMethod,
            Integer status) {
        super();
        this.id = id;
        this.traitId = traitId;
        this.name = name;
        this.abbreviation = abbreviation;
        this.descripton = descripton;
        this.nameStatus = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTraitId() {
        return traitId;
    }

    public void setTraitId(Integer traitId) {
        this.traitId = traitId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getDescripton() {
        return descripton;
    }

    public void setDescripton(String descripton) {
        this.descripton = descripton;
    }

    public Integer getNameStatus() {
        return nameStatus;
    }

    public void setNameStatus(Integer status) {
        this.nameStatus = status;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (id == null ? 0 : id.hashCode());
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
        Trait other = (Trait) obj;
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Trait [id=");
        builder.append(id);
        builder.append(", traitId=");
        builder.append(traitId);
        builder.append(", name=");
        builder.append(name);
        builder.append(", abbreviation=");
        builder.append(abbreviation);
        builder.append(", descripton=");
        builder.append(descripton);
        builder.append(", standardScale=");
        builder.append(", nameStatus=");
        builder.append(nameStatus);
        builder.append("]");
        return builder.toString();
    }

}
