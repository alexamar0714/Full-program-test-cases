package org.cidrz.project.zeprs.valueobject.report.gen;

import org.cidrz.project.zeprs.valueobject.EncounterData;
import org.cidrz.webapp.dynasite.valueobject.Patient;
import java.sql.Date;
import java.util.Set;
import java.sql.Time;
import java.sql.Timestamp;
import org.cidrz.webapp.dynasite.valueobject.AuditInfo;
import java.util.TreeSet;

/**
 * JavaBean LiverfunctionReport generated from database;
 * generated by DynasiteSourceGenerator, inspired by XslBeanGenerator by Klaus Berg.
 *
 * @author Chris Kelley
 *         Date: 2014-10-27
 *         Time: 17:44:31
 *         Form Name: Liver Function
 *         Form Id: 103
 */

/**
 * @hibernate.joined-subclass table="liverfunction"
 * @hibernate.joined-subclass-key column="id"
 */
public class LiverfunctionReport extends EncounterData {

private transient Integer alt;	//alt
private String altR;
private transient Integer ast;	//ast
private String astR;
private transient Integer alk_phos;	//alk_phos
private String alk_phosR;
private transient Float tbili;	//tbili
private String tbiliR;
private transient Float dbili;	//dbili
private String dbiliR;
private transient Integer ggt;	//ggt
private String ggtR;
private transient Long labtest_id;	//labtest_id
private String labtest_idR;


 /**
  * @return
  * @hibernate.property column="alt"
  */
    public Integer getAlt() {
        return this.alt;
    }

    public void setAlt(Integer alt) {
        this.alt = alt;
    }



    public String getAltR() {
        return this.altR;
    }

    public void setAltR(String altR) {
        this.altR = altR;
    }



 /**
  * @return
  * @hibernate.property column="ast"
  */
    public Integer getAst() {
        return this.ast;
    }

    public void setAst(Integer ast) {
        this.ast = ast;
    }



    public String getAstR() {
        return this.astR;
    }

    public void setAstR(String astR) {
        this.astR = astR;
    }



 /**
  * @return
  * @hibernate.property column="alk_phos"
  */
    public Integer getAlk_phos() {
        return this.alk_phos;
    }

    public void setAlk_phos(Integer alk_phos) {
        this.alk_phos = alk_phos;
    }



    public String getAlk_phosR() {
        return this.alk_phosR;
    }

    public void setAlk_phosR(String alk_phosR) {
        this.alk_phosR = alk_phosR;
    }



 /**
  * @return
  * @hibernate.property column="tbili"
  */
    public Float getTbili() {
        return this.tbili;
    }

    public void setTbili(Float tbili) {
        this.tbili = tbili;
    }



    public String getTbiliR() {
        return this.tbiliR;
    }

    public void setTbiliR(String tbiliR) {
        this.tbiliR = tbiliR;
    }



 /**
  * @return
  * @hibernate.property column="dbili"
  */
    public Float getDbili() {
        return this.dbili;
    }

    public void setDbili(Float dbili) {
        this.dbili = dbili;
    }



    public String getDbiliR() {
        return this.dbiliR;
    }

    public void setDbiliR(String dbiliR) {
        this.dbiliR = dbiliR;
    }



 /**
  * @return
  * @hibernate.property column="ggt"
  */
    public Integer getGgt() {
        return this.ggt;
    }

    public void setGgt(Integer ggt) {
        this.ggt = ggt;
    }



    public String getGgtR() {
        return this.ggtR;
    }

    public void setGgtR(String ggtR) {
        this.ggtR = ggtR;
    }



 /**
  * @return
  * @hibernate.property column="labtest_id"
  */
    public Long getLabtest_id() {
        return this.labtest_id;
    }

    public void setLabtest_id(Long labtest_id) {
        this.labtest_id = labtest_id;
    }



    public String getLabtest_idR() {
        return this.labtest_idR;
    }

    public void setLabtest_idR(String labtest_idR) {
        this.labtest_idR = labtest_idR;
    }



}
