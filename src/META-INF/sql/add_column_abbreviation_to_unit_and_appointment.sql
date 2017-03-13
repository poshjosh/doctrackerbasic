alter table `doctrackerbasic_db1`.unit add column `abbreviation` VARCHAR(100) NULL after `unit`;
update `doctrackerbasic_db1`.unit SET `abbreviation` = 'Office of the CAS' WHERE `unitid` = 1;
update `doctrackerbasic_db1`.unit SET `abbreviation` = 'Pol & Plans Branch' WHERE `unitid` = 2;
update `doctrackerbasic_db1`.unit SET `abbreviation` = 'Trg & Ops Branch' WHERE `unitid` = 3;
update `doctrackerbasic_db1`.unit SET `abbreviation` = 'AcE Branch' WHERE `unitid` = 4;
update `doctrackerbasic_db1`.unit SET `abbreviation` = 'Log & Comms Branch' WHERE `unitid` = 5;
update `doctrackerbasic_db1`.unit SET `abbreviation` = 'Admin Branch' WHERE `unitid` = 6;
update `doctrackerbasic_db1`.unit SET `abbreviation` = 'Stan Eval Branch' WHERE `unitid` = 7;
update `doctrackerbasic_db1`.unit SET `abbreviation` = 'Medical Svcs Branch' WHERE `unitid` = 8;
update `doctrackerbasic_db1`.unit SET `abbreviation` = 'Air Sec Branch' WHERE `unitid` = 9;
update `doctrackerbasic_db1`.unit SET `abbreviation` = 'A & B Branch' WHERE `unitid` = 10;
update `doctrackerbasic_db1`.unit SET `abbreviation` = 'SI List' WHERE `unitid` = 11;

alter table `doctrackerbasic_db1`.appointment add column `abbreviation` VARCHAR(100) NULL after `appointment`;
update `doctrackerbasic_db1`.appointment SET `abbreviation` = 'CAS' WHERE `appointmentid` = 1;
update `doctrackerbasic_db1`.appointment SET `abbreviation` = 'COPP' WHERE `appointmentid` = 2;
update `doctrackerbasic_db1`.appointment SET `abbreviation` = 'CTOP' WHERE `appointmentid` = 3;
update `doctrackerbasic_db1`.appointment SET `abbreviation` = 'CAcE' WHERE `appointmentid` = 4;
update `doctrackerbasic_db1`.appointment SET `abbreviation` = 'CLog & Comms' WHERE `appointmentid` = 5;
update `doctrackerbasic_db1`.appointment SET `abbreviation` = 'COA' WHERE `appointmentid` = 6;
update `doctrackerbasic_db1`.appointment SET `abbreviation` = 'COSE' WHERE `appointmentid` = 7;
update `doctrackerbasic_db1`.appointment SET `abbreviation` = 'CMS' WHERE `appointmentid` = 8;
update `doctrackerbasic_db1`.appointment SET `abbreviation` = 'Air Sec' WHERE `appointmentid` = 9;
update `doctrackerbasic_db1`.appointment SET `abbreviation` = 'CAB' WHERE `appointmentid` = 10;
update `doctrackerbasic_db1`.appointment SET `abbreviation` = 'PASO - CAS' WHERE `appointmentid` = 11;
update `doctrackerbasic_db1`.appointment SET `abbreviation` = 'AA - CAS' WHERE `appointmentid` = 12;
update `doctrackerbasic_db1`.appointment SET `abbreviation` = 'DPROC' WHERE `appointmentid` = 13;

