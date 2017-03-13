--Selecting all
SELECT DISTINCT COUNT(taskid) FROM task;
SELECT DISTINCT taskid AS a1, description AS a2, timeclosed AS a3, timecreated AS a4, timemodified AS a5, timeopened AS a6, author AS a7, doc AS a8, reponsibility AS a9 FROM task LIMIT ?, ?;

SELECT DISTINCT t2.taskid AS a1, t2.description AS a2, t2.timeclosed AS a3, t2.timecreated AS a4, t2.timemodified AS a5, t2.timeopened AS a6, t2.author AS a7, t2.doc AS a8, t2.reponsibility AS a9 FROM task t0, task t2, doc t1 WHERE (t0.doc = t1.docid) LIMIT ?, ?

--Searching for text
SELECT DISTINCT COUNT(t0.taskid) FROM task t0, taskresponse t2, doc t1 WHERE (((((t1.subject LIKE ? OR t1.referencenumber LIKE ?) OR t0.description LIKE ?) OR t2.response LIKE ?) AND (t0.timeclosed IS NULL)) AND ((t1.docid = t0.doc) AND (t2.task = t0.taskid)))
SELECT DISTINCT t1.taskid AS a1, t1.description AS a2, t1.timeclosed AS a3, t1.timecreated AS a4, t1.timemodified AS a5, t1.timeopened AS a6, t1.author AS a7, t1.doc AS a8, t1.reponsibility AS a9 FROM doc t0, taskresponse t2, task t1 WHERE (((((t0.subject LIKE ? OR t0.referencenumber LIKE ?) OR t1.description LIKE ?) OR t2.response LIKE ?) AND (t1.timeclosed IS NULL)) AND ((t0.docid = t1.doc) AND (t2.task = t1.taskid))) LIMIT ?, ?

--Select by responsibility
SELECT DISTINCT taskid AS a1, description AS a2, timeclosed AS a3, timecreated AS a4, timemodified AS a5, timeopened AS a6, author AS a7, doc AS a8, reponsibility AS a9 FROM task WHERE ((reponsibility = ?) AND (timeclosed IS NULL)) LIMIT ?, ?
SELECT DISTINCT taskid AS a1, description AS a2, timeclosed AS a3, timecreated AS a4, timemodified AS a5, timeopened AS a6, author AS a7, doc AS a8, reponsibility AS a9 FROM task WHERE ((reponsibility = ?) AND (timeclosed IS NULL)) LIMIT ?, ?

--Select by responsibility and search text
SELECT DISTINCT COUNT(t0.taskid) 
FROM task t0, taskresponse t2, doc t1 
WHERE ((((((t1.subject LIKE ? OR t1.referencenumber LIKE ?) OR t0.description LIKE ?) OR t2.response LIKE ?) AND (t0.reponsibility = ?)) AND (t0.timeclosed IS NULL)) AND ((t1.docid = t0.doc) AND (t2.task = t0.taskid)))
SELECT DISTINCT t1.taskid AS a1, t1.description AS a2, t1.timeclosed AS a3, t1.timecreated AS a4, t1.timemodified AS a5, t1.timeopened AS a6, t1.author AS a7, t1.doc AS a8, t1.reponsibility AS a9 
FROM taskresponse t0, doc t2, task t1 
WHERE ((((((t2.subject LIKE ? OR t2.referencenumber LIKE ?) OR t1.description LIKE ?) OR t0.response LIKE ?) AND (t1.reponsibility = ?)) AND (t1.timeclosed IS NULL)) AND ((t2.docid = t1.doc) AND (t0.task = t1.taskid))) LIMIT ?, ?

--Search for 'Query on 21 Feb'
SELECT DISTINCT COUNT(t0.taskid) FROM task t0, taskresponse t2, doc t1 WHERE (((((t1.subject LIKE ? OR t1.referencenumber LIKE ?) OR t0.description LIKE ?) OR t2.response LIKE ?) AND (t0.timeclosed IS NULL)) AND ((t1.docid = t0.doc) AND (t2.task = t0.taskid)))
SELECT DISTINCT t1.taskid AS a1, t1.description AS a2, t1.timeclosed AS a3, t1.timecreated AS a4, t1.timemodified AS a5, t1.timeopened AS a6, t1.author AS a7, t1.doc AS a8, t1.reponsibility AS a9 FROM taskresponse t0, doc t2, task t1 WHERE (((((t2.subject LIKE ? OR t2.referencenumber LIKE ?) OR t1.description LIKE ?) OR t0.response LIKE ?) AND (t1.timeclosed IS NULL)) AND ((t2.docid = t1.doc) AND (t0.task = t1.taskid))) LIMIT ?, ?

--Select where deadline after
SELECT DISTINCT COUNT(t0.taskid) FROM task t0, taskresponse t1 WHERE (((t0.timeclosed IS NULL) AND (t1.deadline >= ?)) AND (t1.task = t0.taskid))
SELECT DISTINCT t1.taskid AS a1, t1.description AS a2, t1.timeclosed AS a3, t1.timecreated AS a4, t1.timemodified AS a5, t1.timeopened AS a6, t1.author AS a7, t1.doc AS a8, t1.reponsibility AS a9 FROM taskresponse t0, task t1 WHERE (((t1.timeclosed IS NULL) AND (t0.deadline >= ?)) AND (t0.task = t1.taskid)) LIMIT ?, ?
