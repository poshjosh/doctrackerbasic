alter table unit add column `abbreviation` VARCHAR(100) NULL after `unit`;
update unit SET `abbreviation` = 'Office of the CAS' WHERE `unitid` = 1;
update unit SET `abbreviation` = 'Pol & Plans Branch' WHERE `unitid` = 2;
update unit SET `abbreviation` = 'Trg & Ops Branch' WHERE `unitid` = 3;
update unit SET `abbreviation` = 'AcE Branch' WHERE `unitid` = 4;
update unit SET `abbreviation` = 'Log & Comms Branch' WHERE `unitid` = 5;
update unit SET `abbreviation` = 'Admin Branch' WHERE `unitid` = 6;
update unit SET `abbreviation` = 'Stan Eval Branch' WHERE `unitid` = 7;
update unit SET `abbreviation` = 'Medical Svcs Branch' WHERE `unitid` = 8;
update unit SET `abbreviation` = 'Air Sec Branch' WHERE `unitid` = 9;
update unit SET `abbreviation` = 'A & B Branch' WHERE `unitid` = 10;
update unit SET `abbreviation` = 'SI List' WHERE `unitid` = 11;
update unit SET `abbreviation` = 'AFIT' WHERE `unitid` = 12;

alter table appointment add column `abbreviation` VARCHAR(100) NULL after `appointment`;
update appointment SET `abbreviation` = 'CAS' WHERE `appointmentid` = 1;
update appointment SET `abbreviation` = 'COPP' WHERE `appointmentid` = 2;
update appointment SET `abbreviation` = 'CTOP' WHERE `appointmentid` = 3;
update appointment SET `abbreviation` = 'CAcE' WHERE `appointmentid` = 4;
update appointment SET `abbreviation` = 'CLog & Comms' WHERE `appointmentid` = 5;
update appointment SET `abbreviation` = 'COA' WHERE `appointmentid` = 6;
update appointment SET `abbreviation` = 'COSE' WHERE `appointmentid` = 7;
update appointment SET `abbreviation` = 'CMS' WHERE `appointmentid` = 8;
update appointment SET `abbreviation` = 'Air Sec' WHERE `appointmentid` = 9;
update appointment SET `abbreviation` = 'CAB' WHERE `appointmentid` = 10;
update appointment SET `abbreviation` = 'PASO - CAS' WHERE `appointmentid` = 11;
update appointment SET `abbreviation` = 'AA - CAS' WHERE `appointmentid` = 12;
update appointment SET `abbreviation` = 'DPROC' WHERE `appointmentid` = 13;
update appointment SET `abbreviation` = 'AFIT Comdt' WHERE `appointmentid` = 14;
