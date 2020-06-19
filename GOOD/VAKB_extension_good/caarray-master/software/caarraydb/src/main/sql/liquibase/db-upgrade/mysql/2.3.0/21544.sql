-- GF 21544 - fix biomaterial search
-- update the biomaterial filter
update csm_filter_clause set generated_sql_user = concat('ID in (select b.ID from biomaterial b left join experiment ex on b.experiment_id = ex.id left join project p on ex.id = p.experiment left join sampleextract se on b.id = se.extract_id left join biomaterial b2 on b2.id = se.sample_id left join extractlabeledextract ele on b.id = ele.labeledextract_id left join sampleextract se2 on ele.extract_id = se2.extract_id left join biomaterial b3 on b3.id = se2.sample_id where ',
'b.discriminator=''SO'' and p.id in (select __caarray_filter_alias__.attribute_value from (select pe.attribute_value from csm_protection_group pg, csm_protection_element pe, csm_pg_pe pgpe, csm_user_group_role_pg ugrpg, csm_user u, csm_role_privilege rp, csm_role r, csm_privilege p, csm_user_group ug where pe.object_id= ''gov.nih.nci.caarray.domain.project.Project'' and pe.attribute=''id'' and u.login_name=:USER_NAME and pe.application_id=:APPLICATION_ID and ugrpg.role_id = r.role_id and ugrpg.group_id = ug.group_id and ug.user_id = u.user_id and ugrpg.protection_group_id = pg.protection_group_id and pg.protection_group_id = pgpe.protection_group_id and pgpe.protection_element_id = pe.protection_element_id and r.role_id = rp.role_id and rp.privilege_id = p.privilege_id and p.privilege_name=''READ'') __caarray_filter_alias__) or ',
'b.discriminator = ''SA'' and b.ID in (select __caarray_filter_alias__.attribute_value from (select pe.attribute_value from csm_protection_group pg, csm_protection_element pe, csm_pg_pe pgpe, csm_user_group_role_pg ugrpg, csm_user u, csm_role_privilege rp, csm_role r, csm_privilege p, csm_user_group ug where pe.object_id= ''gov.nih.nci.caarray.domain.sample.Sample'' and pe.attribute=''id'' and u.login_name=:USER_NAME and pe.application_id=:APPLICATION_ID and ugrpg.role_id = r.role_id and ugrpg.group_id = ug.group_id and ug.user_id = u.user_id and ugrpg.protection_group_id = pg.protection_group_id and pg.protection_group_id = pgpe.protection_group_id and pgpe.protection_element_id = pe.protection_element_id and r.role_id = rp.role_id and rp.privilege_id = p.privilege_id and p.privilege_name=''READ'') __caarray_filter_alias__) OR ',
'b2.discriminator = ''SA'' and b2.ID in (select __caarray_filter_alias__.attribute_value from (select pe.attribute_value from csm_protection_group pg, csm_protection_element pe, csm_pg_pe pgpe, csm_user_group_role_pg ugrpg, csm_user u, csm_role_privilege rp, csm_role r, csm_privilege p, csm_user_group ug where pe.object_id= ''gov.nih.nci.caarray.domain.sample.Sample'' and pe.attribute=''id'' and u.login_name=:USER_NAME and pe.application_id=:APPLICATION_ID and ugrpg.role_id = r.role_id and ugrpg.group_id = ug.group_id and ug.user_id = u.user_id and ugrpg.protection_group_id = pg.protection_group_id and pg.protection_group_id = pgpe.protection_group_id and pgpe.protection_element_id = pe.protection_element_id and r.role_id = rp.role_id and rp.privilege_id = p.privilege_id and p.privilege_name=''READ'') __caarray_filter_alias__) OR ',
'b3.discriminator=''SA'' and b3.id in (select __caarray_filter_alias__.attribute_value from (select pe.attribute_value from csm_protection_group pg, csm_protection_element pe, csm_pg_pe pgpe, csm_user_group_role_pg ugrpg, csm_user u, csm_role_privilege rp, csm_role r, csm_privilege p, csm_user_group ug where pe.object_id= ''gov.nih.nci.caarray.domain.sample.Sample'' and pe.attribute=''id'' and u.login_name=:USER_NAME and pe.application_id=:APPLICATION_ID and ugrpg.role_id = r.role_id and ugrpg.group_id = ug.group_id and ug.user_id = u.user_id and ugrpg.protection_group_id = pg.protection_group_id and pg.protection_group_id = pgpe.protection_group_id and pgpe.protection_element_id = pe.protection_element_id and r.role_id = rp.role_id and rp.privilege_id = p.privilege_id and p.privilege_name=''READ'') __caarray_filter_alias__))')
    where class_name = 'gov.nih.nci.caarray.domain.sample.AbstractBioMaterial';


alter table biomaterial add column experiment_id bigint;

update biomaterial b join experimentsource es on b.id = es.source_id set b.experiment_id = es.experiment_id; 
update biomaterial b join experimentsample es on b.id = es.sample_id set b.experiment_id = es.experiment_id;
update biomaterial b join experimentextract ee on b.id = ee.extract_id set b.experiment_id = ee.experiment_id; 
update biomaterial b join experimentlabeledextract el on b.id = el.labeled_extract_id set b.experiment_id = el.experiment_id;

alter table biomaterial add index biomaterial_experiment_fk (experiment_id), add constraint biomaterial_experiment_fk foreign key (experiment_id) references experiment (id);

drop table experimentsource;
drop table experimentsample;
drop table experimentextract;
drop table experimentlabeledextract;
