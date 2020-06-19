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
 * JavaBean InitialVisit generated from database;
 * generated by DynasiteSourceGenerator, inspired by XslBeanGenerator by Klaus Berg.
 *
 * @author Chris Kelley
 *         Date: 2014-10-27
 *         Time: 17:44:30
 *         Form Name: Initial Visit Physical Exam
 *         Form Id: 77
 */

/**
 * @hibernate.joined-subclass table="initialvisit"
 * @hibernate.joined-subclass-key column="id"
 */
public class InitialVisit extends EncounterData {

private Integer field159;	//height_159
private Float field266;	//temperature_266
private Integer field161;	//heent_161
private String field162;	//heent_abnorm_162
private Integer field163;	//teeth_163
private String field164;	//teeth_other_164
private Integer field165;	//thyroid_165
private Integer field166;	//breasts_166
private Integer field167;	//respiratory_system_167
private String field1449;	//respiratory_system_other
private Integer field169;	//heart_169
private String field170;	//heart_other_170
private Integer field171;	//pulse_171
private Integer field172;	//abdomen_172
private String field173;	//abdomen_abnormal_173
private Integer field174;	//extremities_174
private String field175;	//extremities_abnormal_175
private Integer field176;	//skin_176
private String field177;	//skin_abnorm_177
private Integer field178;	//lymph_nodes_178
private Integer field179;	//rectum_179
private String field180;	//rectum_abnormal_180
private Integer field181;	//vulva_181
private String field182;	//vulva_abnormal_182
private Integer field183;	//vagina_183
private String field184;	//vagina_abnormal_184
private Integer field185;	//cervix_185
private String field186;	//cervix_abnormal_186
private Integer field187;	//uterus_187
private Integer field189;	//adnexa_189
private String field190;	//adnexa_abnormal_190
private Integer field191;	//varicosities_191
private Integer field1357;	//pallor_193
private Integer field1356;	//cns_192
private String field1922;	//comments


 /**
  * @return
  * @hibernate.property column="height_159"
  */
    public Integer getField159() {
        return this.field159;
    }

    public void setField159(Integer field159) {
        this.field159 = field159;
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
  * @hibernate.property column="heent_161"
  */
    public Integer getField161() {
        return this.field161;
    }

    public void setField161(Integer field161) {
        this.field161 = field161;
    }





 /**
  * @return
  * @hibernate.property column="heent_abnorm_162" type="text"
  */
    public String getField162() {
        return this.field162;
    }

    public void setField162(String field162) {
        this.field162 = field162;
    }





 /**
  * @return
  * @hibernate.property column="teeth_163"
  */
    public Integer getField163() {
        return this.field163;
    }

    public void setField163(Integer field163) {
        this.field163 = field163;
    }





 /**
  * @return
  * @hibernate.property column="teeth_other_164" type="text"
  */
    public String getField164() {
        return this.field164;
    }

    public void setField164(String field164) {
        this.field164 = field164;
    }





 /**
  * @return
  * @hibernate.property column="thyroid_165"
  */
    public Integer getField165() {
        return this.field165;
    }

    public void setField165(Integer field165) {
        this.field165 = field165;
    }





 /**
  * @return
  * @hibernate.property column="breasts_166"
  */
    public Integer getField166() {
        return this.field166;
    }

    public void setField166(Integer field166) {
        this.field166 = field166;
    }





 /**
  * @return
  * @hibernate.property column="respiratory_system_167"
  */
    public Integer getField167() {
        return this.field167;
    }

    public void setField167(Integer field167) {
        this.field167 = field167;
    }





 /**
  * @return
  * @hibernate.property column="respiratory_system_other" type="text"
  */
    public String getField1449() {
        return this.field1449;
    }

    public void setField1449(String field1449) {
        this.field1449 = field1449;
    }





 /**
  * @return
  * @hibernate.property column="heart_169"
  */
    public Integer getField169() {
        return this.field169;
    }

    public void setField169(Integer field169) {
        this.field169 = field169;
    }





 /**
  * @return
  * @hibernate.property column="heart_other_170" type="text"
  */
    public String getField170() {
        return this.field170;
    }

    public void setField170(String field170) {
        this.field170 = field170;
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
  * @hibernate.property column="abdomen_172"
  */
    public Integer getField172() {
        return this.field172;
    }

    public void setField172(Integer field172) {
        this.field172 = field172;
    }





 /**
  * @return
  * @hibernate.property column="abdomen_abnormal_173" type="text"
  */
    public String getField173() {
        return this.field173;
    }

    public void setField173(String field173) {
        this.field173 = field173;
    }





 /**
  * @return
  * @hibernate.property column="extremities_174"
  */
    public Integer getField174() {
        return this.field174;
    }

    public void setField174(Integer field174) {
        this.field174 = field174;
    }





 /**
  * @return
  * @hibernate.property column="extremities_abnormal_175" type="text"
  */
    public String getField175() {
        return this.field175;
    }

    public void setField175(String field175) {
        this.field175 = field175;
    }





 /**
  * @return
  * @hibernate.property column="skin_176"
  */
    public Integer getField176() {
        return this.field176;
    }

    public void setField176(Integer field176) {
        this.field176 = field176;
    }





 /**
  * @return
  * @hibernate.property column="skin_abnorm_177" type="text"
  */
    public String getField177() {
        return this.field177;
    }

    public void setField177(String field177) {
        this.field177 = field177;
    }





 /**
  * @return
  * @hibernate.property column="lymph_nodes_178"
  */
    public Integer getField178() {
        return this.field178;
    }

    public void setField178(Integer field178) {
        this.field178 = field178;
    }





 /**
  * @return
  * @hibernate.property column="rectum_179"
  */
    public Integer getField179() {
        return this.field179;
    }

    public void setField179(Integer field179) {
        this.field179 = field179;
    }





 /**
  * @return
  * @hibernate.property column="rectum_abnormal_180" type="text"
  */
    public String getField180() {
        return this.field180;
    }

    public void setField180(String field180) {
        this.field180 = field180;
    }





 /**
  * @return
  * @hibernate.property column="vulva_181"
  */
    public Integer getField181() {
        return this.field181;
    }

    public void setField181(Integer field181) {
        this.field181 = field181;
    }





 /**
  * @return
  * @hibernate.property column="vulva_abnormal_182" type="text"
  */
    public String getField182() {
        return this.field182;
    }

    public void setField182(String field182) {
        this.field182 = field182;
    }





 /**
  * @return
  * @hibernate.property column="vagina_183"
  */
    public Integer getField183() {
        return this.field183;
    }

    public void setField183(Integer field183) {
        this.field183 = field183;
    }





 /**
  * @return
  * @hibernate.property column="vagina_abnormal_184" type="text"
  */
    public String getField184() {
        return this.field184;
    }

    public void setField184(String field184) {
        this.field184 = field184;
    }





 /**
  * @return
  * @hibernate.property column="cervix_185"
  */
    public Integer getField185() {
        return this.field185;
    }

    public void setField185(Integer field185) {
        this.field185 = field185;
    }





 /**
  * @return
  * @hibernate.property column="cervix_abnormal_186" type="text"
  */
    public String getField186() {
        return this.field186;
    }

    public void setField186(String field186) {
        this.field186 = field186;
    }





 /**
  * @return
  * @hibernate.property column="uterus_187"
  */
    public Integer getField187() {
        return this.field187;
    }

    public void setField187(Integer field187) {
        this.field187 = field187;
    }





 /**
  * @return
  * @hibernate.property column="adnexa_189"
  */
    public Integer getField189() {
        return this.field189;
    }

    public void setField189(Integer field189) {
        this.field189 = field189;
    }





 /**
  * @return
  * @hibernate.property column="adnexa_abnormal_190" type="text"
  */
    public String getField190() {
        return this.field190;
    }

    public void setField190(String field190) {
        this.field190 = field190;
    }





 /**
  * @return
  * @hibernate.property column="varicosities_191"
  */
    public Integer getField191() {
        return this.field191;
    }

    public void setField191(Integer field191) {
        this.field191 = field191;
    }





 /**
  * @return
  * @hibernate.property column="pallor_193"
  */
    public Integer getField1357() {
        return this.field1357;
    }

    public void setField1357(Integer field1357) {
        this.field1357 = field1357;
    }





 /**
  * @return
  * @hibernate.property column="cns_192"
  */
    public Integer getField1356() {
        return this.field1356;
    }

    public void setField1356(Integer field1356) {
        this.field1356 = field1356;
    }





 /**
  * @return
  * @hibernate.property column="comments" type="text"
  */
    public String getField1922() {
        return this.field1922;
    }

    public void setField1922(String field1922) {
        this.field1922 = field1922;
    }





}
