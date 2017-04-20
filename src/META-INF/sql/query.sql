SELECT DISTINCT t1.taskid AS a1, t1.description AS a2, t1.timeclosed AS a3, t1.timecreated AS a4, 
t1.timemodified AS a5, t1.timeopened AS a6, t1.author AS a7, t1.doc AS a8, t1.reponsibility AS a9 
FROM task t1 LEFT OUTER JOIN taskresponse t2 ON (t2.task = t1.taskid), doc t0 
WHERE (((((t1.description LIKE '%Query on 12 Apr%' OR t0.subject LIKE '%Query on 12 Apr%') 
OR t0.referencenumber LIKE '%Query on 12 Apr%') OR t2.response LIKE '%Query on 12 Apr%') 
AND (t1.timeclosed IS NULL)) AND (t0.docid = t1.doc)) ORDER BY t1.taskid DESC LIMIT 0, 100;

SELECT DISTINCT t1.taskid, t1.description, t1.timeclosed, t1.timecreated, 
t1.timemodified, t1.timeopened, t1.author, t1.doc, t1.reponsibility 
FROM task t1 LEFT OUTER JOIN taskresponse t0 ON (t0.task = t1.taskid), doc t2 
WHERE ((((((t0.response LIKE ? AND (t1.reponsibility = ?)) AND (t1.timeclosed IS NULL)) AND (t0.deadline IS NOT NULL)) 
AND EXISTS (SELECT t3.taskresponseid FROM taskresponse t3 WHERE (t1.taskid = t3.task) HAVING (MAX(t3.deadline) >= ?)) ) 
AND EXISTS (SELECT t4.taskresponseid FROM taskresponse t4 WHERE (t1.taskid = t4.task) HAVING (MAX(t4.deadline) < ?)) ) 
AND (t2.docid = t1.doc)) ORDER BY t1.taskid DESC

SELECT DISTINCT COUNT(taskid) FROM task t1 LEFT OUTER JOIN taskresponse t2 ON (t2.task = t1.taskid), doc t0

--Search for 'Query on 3 Mar'
SELECT DISTINCT COUNT(t0.taskid) FROM task t0 LEFT OUTER JOIN taskresponse t2 ON (t2.task = t0.taskid), doc t1 WHERE (((((t0.description LIKE ? OR t1.subject LIKE ?) OR t1.referencenumber LIKE ?) OR t2.response LIKE ?) AND (t0.timeclosed IS NULL)) AND (t1.docid = t0.doc))
SELECT DISTINCT t1.taskid AS a1, t1.description AS a2, t1.timeclosed AS a3, t1.timecreated AS a4, t1.timemodified AS a5, t1.timeopened AS a6, t1.author AS a7, t1.doc AS a8, t1.reponsibility AS a9 FROM task t1 LEFT OUTER JOIN taskresponse t2 ON (t2.task = t1.taskid), doc t0 WHERE (((((t1.description LIKE ? OR t0.subject LIKE ?) OR t0.referencenumber LIKE ?) OR t2.response LIKE ?) AND (t1.timeclosed IS NULL)) AND (t0.docid = t1.doc)) ORDER BY t1.taskid DESC LIMIT ?, ?

--Query for 'query_today' report
SELECT DISTINCT taskid AS a1, description AS a2, timeclosed AS a3, timecreated AS a4, timemodified AS a5, timeopened AS a6, author AS a7, doc AS a8, reponsibility AS a9 FROM task WHERE (timeclosed IS NULL) ORDER BY taskid DESC LIMIT ?, ?

