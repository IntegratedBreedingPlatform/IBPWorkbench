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
package org.generationcp.middleware.pojos.gdms;

import java.io.Serializable;
import java.util.Comparator;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * <b>Description</b>: Placeholder POJO for Allelic Value Element
 * Used by GenotypicDataManager.getAllelicValuesByGidsAndMarkerNames().
 * 
 * <br>
 * <br>
 * 
 * <b>Author</b>: Mark Agarrado <br>
 * <b>File Created</b>: Jul 13, 2012
 */
public class AllelicValueElement implements Serializable{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The Germplasm Id. */
    private Integer gid;
    
    /** The Data value. */
    private String data;
    
    /** The Marker Name. */
    private String markerName;
    
    private Integer datasetId;
    
    private String alleleBinValue;
    
    private Integer peakHeight;

    /**
     * Instantiates a AllelicValueElement object with datasetId, gid, markerName and data(char_value for table char_values, 
     * allele_bin_value for allele_values table and map_char_value for mapping_pop_values).
     * 
     * @param datasetId
     * @param gid
     * @param markerName
     * @param data
     * @param peakHeight
     */
    public AllelicValueElement(Integer datasetId, Integer gid, String markerName, String data, Integer peakHeight) {
        this.datasetId = datasetId;
        this.gid = gid;
        this.data = data;
        this.markerName = markerName;
        this.peakHeight = peakHeight;
    }

    /**
     * Instantiates a AllelicValueElement object with gid, data(char_value for table char_values, 
     * allele_bin_value for allele_values table and map_char_value for mapping_pop_values), marker name.
     * 
     * @param gid
     * @param data
     * @param markerName
     */
    public AllelicValueElement(Integer gid, String data, String markerName, Integer peakHeight) {
        this.gid = gid;
        this.data = data;
        this.markerName = markerName;
        this.peakHeight = peakHeight;
    }
    
    /**
     * Gets the Germplasm Id.
     * 
     * @return the gid
     */
    public Integer getGid() {
        return gid;
    }

    
    /**
     * Sets the Germplasm Id.
     * 
     * @param gid the gid to set
     */
    public void setGid(Integer gid) {
        this.gid = gid;
    }

    
    /**
     * Gets the Data value.
     * 
     * @return the data
     */
    public String getData() {
        return data;
    }

    
    /**
     * Sets the Data value.
     * 
     * @param data the data to set
     */
    public void setData(String data) {
        this.data = data;
    }

    
    /**
     * Gets the Marker Name.
     * 
     * @return the markerName
     */
    public String getMarkerName() {
        return markerName;
    }

    
    /**
     * Sets the Marker Name.
     * 
     * @param markerName the markerName to set
     */
    public void setMarkerName(String markerName) {
        this.markerName = markerName;
    }
    
    
    public Integer getDatasetId() {
        return datasetId;
    }

    
    public void setDatasetId(Integer datasetId) {
        this.datasetId = datasetId;
    }

    
    public String getAlleleBinValue() {
        return alleleBinValue;
    }

    
    public void setAlleleBinValue(String alleleBinValue) {
        this.alleleBinValue = alleleBinValue;
    }
    
    public Integer getPeakHeight() {
		return peakHeight;
	}

	public void setPeakHeight(Integer peakHeight) {
		this.peakHeight = peakHeight;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AllelicValueElement [gid=");
		builder.append(gid);
		builder.append(", data=");
		builder.append(data);
		builder.append(", markerName=");
		builder.append(markerName);
		builder.append(", datasetId=");
		builder.append(datasetId);
		builder.append(", alleleBinValue=");
		builder.append(alleleBinValue);
		builder.append(", peakHeight=");
		builder.append(peakHeight);
		builder.append("]");
		return builder.toString();
	}
    
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof AllelicValueElement)) {
            return false;
        }

        AllelicValueElement rhs = (AllelicValueElement) obj;
        return new EqualsBuilder().appendSuper(super.equals(obj)).append(gid, rhs.gid)
                .append(data, rhs.data)
                .append(datasetId, rhs.datasetId)
                .append(markerName, rhs.markerName).isEquals();
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 77).append(gid)
                .append(data)
                .append(datasetId)
                .append(markerName).toHashCode();
    }

    public static Comparator<AllelicValueElement> AllelicValueElementComparator 
                          = new Comparator<AllelicValueElement>() {
        @Override
        public int compare(AllelicValueElement element1, AllelicValueElement element2) {
            Integer gid1 = element1.getGid();
            Integer gid2 = element2.getGid();
            
            int gidComp = gid1.compareTo(gid2);
            
            if (gidComp != 0){
                return gidComp;
            } else {
                String markerName1 = element1.getMarkerName();
                String markerName2 = element2.getMarkerName();
                return markerName1.compareToIgnoreCase(markerName2);
            }
        }
 
    };
}
