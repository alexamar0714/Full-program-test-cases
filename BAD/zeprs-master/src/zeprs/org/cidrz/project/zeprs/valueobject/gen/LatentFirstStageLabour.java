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
 * JavaBean LatentFirstStageLabour generated from database;
 * generated by DynasiteSourceGenerator, inspired by XslBeanGenerator by Klaus Berg.
 *
 * @author Chris Kelley
 *         Date: 2014-10-27
 *         Time: 17:44:30
 *         Form Name: Observations for Latent Phase of 1st Stage of Labour
 *         Form Id: 17
 */

/**
 * @hibernate.joined-subclass table="latentfirststagelabour"
 * @hibernate.joined-subclass-key column="id"
 */
public class LatentFirstStageLabour extends EncounterData {

private Time field1960;	//time_exam
private Integer field230;	//foetal_heart_rate_230
private Integer field171;	//pulse_171
private Integer field224;	//bp_systolic_224
private Integer field225;	//bp_diastolic_225
private Float field266;	//temperature_266
private Integer field240;	//urinalysis_240
private Integer field242;	//urinalysis_alb_242
private Integer field243;	//urinalysis_glu_243
private Integer field244;	//urinalysis_ace_244
private Integer field367;	//contractions_367
private Integer field368;	//contraction_freq_10_mins_368
private Integer field325;	//cervix_dilatation325
private String field369;	//remarks_369
private Integer field1761;	//diagnosis
private Integer field1349;	//priority_of_referral
private Integer field1620;	//transport


 /**
  * @return
  * @hibernate.property column="time_exam"
  */
    public Time getField1960() {
        return this.field1960;
    }

    public void setField1960(Time field1960) {
        this.field1960 = field1960;
    }





 /**
  * @return
  * @hibernate.property column="foetal_heart_rate_230"
  */
    public Integer getField230() {
        return this.field230;
    }

    public void setField230(Integer field230) {
        this.field230 = field230;
    }





 /**
  * @return
  * @hibernate.property column="pulse_171"
  */
    public Integer getField171() {
        return this.field171;
    }

    public void setField171(Integer field171) {
        this.field171 = field171;
    }





 /**
  * @return
  * @hibernate.property column="bp_systolic_224"
  */
    public Integer getField224() {
        return this.field224;
    }

    public void setField224(Integer field224) {
        this.field224 = field224;
    }





 /**
  * @return
  * @hibernate.property column="bp_diastolic_225"
  */
    public Integer getField225() {
        return this.field225;
    }

    public void setField225(Integer field225) {
        this.field225 = field225;
    }





 /**
  * @return
  * @hibernate.property column="temperature_266"
  */
    public Float getField266() {
        return this.field266;
    }

    public void setField266(Float field266) {
        this.field266 = field266;
    }





 /**
  * @return
  * @hibernate.property column="urinalysis_240"
  */
    public Integer getField240() {
        return this.field240;
    }

    public void setField240(Integer field240) {
        this.field240 = field240;
    }





 /**
  * @return
  * @hibernate.property column="urinalysis_alb_242"
  */
    public Integer getField242() {
        return this.field242;
    }

    public void setField242(Integer field242) {
        this.field242 = field242;
    }





 /**
  * @return
  * @hibernate.property column="urinalysis_glu_243"
  */
    public Integer getField243() {
        return this.field243;
    }

    public void setField243(Integer field243) {
        this.field243 = field243;
    }





 /**
  * @return
  * @hibernate.property column="urinalysis_ace_244"
  */
    public Integer getField244() {
        return this.field244;
    }

    public void setField244(Integer field244) {
        this.field244 = field244;
    }





 /**
  * @return
  * @hibernate.property column="contractions_367"
  */
    public Integer getField367() {
        return this.field367;
    }

    public void setField367(Integer field367) {
        this.field367 = field367;
    }





 /**
  * @return
  * @hibernate.property column="contraction_freq_10_mins_368"
  */
    public Integer getField368() {
        return this.field368;
    }

    public void setField368(Integer field368) {
        this.field368 = field368;
    }





 /**
  * @return
  * @hibernate.property column="cervix_dilatation325"
  */
    public Integer getField325() {
        return this.field325;
    }

    public void setField325(Integer field325) {
        this.field325 = field325;
    }





 /**
  * @return
  * @hibernate.property column="remarks_369" type="text"
  */
    public String getField369() {
        return this.field369;
    }

    public void setField369(String field369) {
        this.field369 = field369;
    }





 /**
  * @return
  * @hibernate.property column="diagnosis"
  */
    public Integer getField1761() {
        return this.field1761;
    }

    public void setField1761(Integer field1761) {
        this.field1761 = field1761;
    }





 /**
  * @return
  * @hibernate.property column="priority_of_referral"
  */
    public Integer getField1349() {
        return this.field1349;
    }

    public void setField1349(Integer field1349) {
        this.field1349 = field1349;
    }





 /**
  * @return
  * @hibernate.property column="transport"
  */
    public Integer getField1620() {
        return this.field1620;
    }

    public void setField1620(Integer field1620) {
        this.field1620 = field1620;
    }





}
