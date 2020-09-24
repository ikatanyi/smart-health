/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * Author:  Kelsas 
 * Created: Sep 24, 2020
 */
ALTER TABLE `sick_off_note` ADD COLUMN `doctor` VARCHAR(255) ;

create table company_logo (id bigint not null auto_increment, company_id varchar(38), data longblob, file_name varchar(255), file_type varchar(255), primary key (id)) engine=InnoDB

