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
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * POJO for country table.
 * 
 * @author klmanansala
 */
@Entity
@Table(name = "cntry")
@NamedQueries({ @NamedQuery(name = "getAllCountry", query = "FROM Country") })
public class Country implements Serializable, Comparable<Country>{

    private static final long serialVersionUID = 1L;
    public static final String GET_ALL = "getAllCountry";

    @Id
    @Basic(optional = false)
    @Column(name = "cntryid")
    private Integer cntryid;

    @Column(name = "isonum")
    private Integer isonum;

    @Basic(optional = false)
    @Column(name = "isotwo")
    private String isotwo;

    @Basic(optional = false)
    @Column(name = "isothree")
    private String isothree;

    @Basic(optional = false)
    @Column(name = "faothree")
    private String faothree;

    @Basic(optional = false)
    @Column(name = "fips")
    private String fips;

    @Basic(optional = false)
    @Column(name = "wb")
    private String wb;

    @Basic(optional = false)
    @Column(name = "isofull")
    private String isofull;

    @Basic(optional = false)
    @Column(name = "isoabbr")
    private String isoabbr;

    @Basic(optional = false)
    @Column(name = "cont")
    private String cont;

    @Column(name = "scntry")
    private Integer scntry;

    @Column(name = "ecntry")
    private Integer ecntry;

    @Column(name = "cchange")
    private Integer cchange;

    public Country() {
    }

    public Country(Integer cntryid) {
        super();
        this.cntryid = cntryid;
    }

    public Country(Integer cntryid, Integer isonum, String isotwo, String isothree, String faothree, String fips, String wb,
            String isofull, String isoabbr, String cont, Integer scntry, Integer ecntry, Integer cchange) {
        super();
        this.cntryid = cntryid;
        this.isonum = isonum;
        this.isotwo = isotwo;
        this.isothree = isothree;
        this.faothree = faothree;
        this.fips = fips;
        this.wb = wb;
        this.isofull = isofull;
        this.isoabbr = isoabbr;
        this.cont = cont;
        this.scntry = scntry;
        this.ecntry = ecntry;
        this.cchange = cchange;
    }

    public Integer getCntryid() {
        return cntryid;
    }

    public void setCntryid(Integer cntryid) {
        this.cntryid = cntryid;
    }

    public Integer getIsonum() {
        return isonum;
    }

    public void setIsonum(Integer isonum) {
        this.isonum = isonum;
    }

    public String getIsotwo() {
        return isotwo;
    }

    public void setIsotwo(String isotwo) {
        this.isotwo = isotwo;
    }

    public String getIsothree() {
        return isothree;
    }

    public void setIsothree(String isothree) {
        this.isothree = isothree;
    }

    public String getFaothree() {
        return faothree;
    }

    public void setFaothree(String faothree) {
        this.faothree = faothree;
    }

    public String getFips() {
        return fips;
    }

    public void setFips(String fips) {
        this.fips = fips;
    }

    public String getWb() {
        return wb;
    }

    public void setWb(String wb) {
        this.wb = wb;
    }

    public String getIsofull() {
        return isofull;
    }

    public void setIsofull(String isofull) {
        this.isofull = isofull;
    }

    public String getIsoabbr() {
        return isoabbr;
    }

    public void setIsoabbr(String isoabbr) {
        this.isoabbr = isoabbr;
    }

    public String getCont() {
        return cont;
    }

    public void setCont(String cont) {
        this.cont = cont;
    }

    public Integer getScntry() {
        return scntry;
    }

    public void setScntry(Integer scntry) {
        this.scntry = scntry;
    }

    public Integer getEcntry() {
        return ecntry;
    }

    public void setEcntry(Integer ecntry) {
        this.ecntry = ecntry;
    }

    public Integer getCchange() {
        return cchange;
    }

    public void setCchange(Integer cchange) {
        this.cchange = cchange;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj instanceof Country) {
            Country param = (Country) obj;
            if (this.getCntryid().equals(param.getCntryid())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public int hashCode() {
        return this.getCntryid();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Country [cntryid=");
        builder.append(cntryid);
        builder.append(", isonum=");
        builder.append(isonum);
        builder.append(", isotwo=");
        builder.append(isotwo);
        builder.append(", isothree=");
        builder.append(isothree);
        builder.append(", faothree=");
        builder.append(faothree);
        builder.append(", fips=");
        builder.append(fips);
        builder.append(", wb=");
        builder.append(wb);
        builder.append(", isofull=");
        builder.append(isofull);
        builder.append(", isoabbr=");
        builder.append(isoabbr);
        builder.append(", cont=");
        builder.append(cont);
        builder.append(", scntry=");
        builder.append(scntry);
        builder.append(", ecntry=");
        builder.append(ecntry);
        builder.append(", cchange=");
        builder.append(cchange);
        builder.append("]");
        return builder.toString();
    }
    

    public int compareTo(Country compareCountry) {
 
        String compareName = ((Country) compareCountry).getIsoabbr(); 
 
        //ascending order
        return this.getIsoabbr().compareTo(compareName);
 
    }
 

}
