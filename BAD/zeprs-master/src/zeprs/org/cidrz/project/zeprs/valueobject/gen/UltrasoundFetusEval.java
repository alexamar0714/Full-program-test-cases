package org.cidrz.project.zeprs.valueobject.gen;

import org.cidrz.project.zeprs.valueobject.gen.*;
import org.cidrz.project.zeprs.valueobject.EncounterData;
import org.cidrz.webapp.dynasite.valueobject.Patient;
import java.sql.Date;
import java.util.Set;
import java.sql.Time;
import java.sql.Timestamp;
import org.cidrz.webapp.dynasite.valueobject.AuditInfo;
import java.util.TreeSet;

/**
 * JavaBean UltrasoundFetusEval generated from database;
 * generated by DynasiteSourceGenerator, inspired by XslBeanGenerator by Klaus Berg.
 *
 * @author Chris Kelley
 *         Date: 2014-10-27
 *         Time: 17:44:30
 *         Form Name: Ultrasound Fetus Evaluation
 *         Form Id: 93
 */

/**
 * @hibernate.joined-subclass table="ultrasoundfetuseval"
 * @hibernate.joined-subclass-key column="id"
 */
public class UltrasoundFetusEval extends EncounterData {

private Integer field1916;	//exam_sequence_number
private Integer field1915;	//sequence_number_fetus
private Integer field964;	//condition_of_foetus_964
private Integer field313;	//lie_313
private Integer field314;	//presentation_314
private String field1508;	//presentation_other
private Float field955;	//biparietal_diameter_955
private Integer field956;	//femur_length_956
private Integer field957;	//fetal_abdomi_957
private Float field1947;	//weight


 /**
  * @return
  * @hibernate.property column="exam_sequence_number"
  */
    public Integer getField1916() {
        return this.field1916;
    }

    public void setField1916(Integer field1916) {
        this.field1916 = field1916;
    }





 /**
  * @return
  * @hibernate.property column="sequence_number_fetus"
  */
    public Integer getField1915() {
        return this.field1915;
    }

    public void setField1915(Integer field1915) {
        this.field1915 = field1915;
    }





 /**
  * @return
  * @hibernate.property column="condition_of_foetus_964"
  */
    public Integer getField964() {
        return this.field964;
    }

    public void setField964(Integer field964) {
        this.field964 = field964;
    }





 /**
  * @return
  * @hibernate.property column="lie_313"
  */
    public Integer getField313() {
        return this.field313;
    }

    public void setField313(Integer field313) {
        this.field313 = field313;
    }





 /**
  * @return
  * @hibernate.property column="presentation_314"
  */
    public Integer getField314() {
        return this.field314;
    }

    public void setField314(Integer field314) {
        this.field314 = field314;
    }





 /**
  * @return
  * @hibernate.property column="presentation_other" type="text"
  */
    public String getField1508() {
        return this.field1508;
    }

    public void setField1508(String field1508) {
        this.field1508 = field1508;
    }





 /**
  * @return
  * @hibernate.property column="biparietal_diameter_955"
  */
    public Float getField955() {
        return this.field955;
    }

    public void setField955(Float field955) {
        this.field955 = field955;
    }





 /**
  * @return
  * @hibernate.property column="femur_length_956"
  */
    public Integer getField956() {
        return this.field956;
    }

    public void setField956(Integer field956) {
        this.field956 = field956;
    }





 /**
  * @return
  * @hibernate.property column="fetal_abdomi_957"
  */
    public Integer getField957() {
        return this.field957;
    }

    public void setField957(Integer field957) {
        this.field957 = field957;
    }





 /**
  * @return
  * @hibernate.property column="weight"
  */
    public Float getField1947() {
        return this.field1947;
    }

    public void setField1947(Float field1947) {
        this.field1947 = field1947;
    }





}
