HadoopHiveHLLCardinality
========================

Hive User-defined Aggregation Function (UDAF)


HyperLogLog approximate cardinality estimation algorithm
which involves Linear Counting


## The evolution From Hadoop Hive Query to Simple Redshift SQL Query to achieve the Cardinality solution

# HLL Explanation

We have to deal with tera bytes IoT sensor data and its benn more than billions of entries every 24 hours.
To track down the event changes in the sensors, i.e., UV level, Carbon level, Air pressure, how many shifts in data
occured, what level of threshold is transpassed and so on. 

To detect this we might write a query likely as follows,

```
SELECT DATE_TRUNC('day',event_time), COUNT(DISTINCT device_id), COUNT(DISTINCT uv_level) FROM sensor_streams GROUP BY 1;
```

But to render the Dashboard its not sufficient to be waited long due to large data set and computing capacity.

During Hadoop-Hive era last few years back we used to implement UADF and Mahout based machine learning matrix calculation
analysis to mimic HLL and minimize the calculation time in 8 hours from 24 hours. This incurs huge cost in employing Xlarge instances to speed up the computation.

Lets we have no problem with the cost but to get the query result quickkly then AWS REDSHIFT still has the solution as follows.

## REDSHIFT HLL Computation Way

```
SELECT DATE_TRUNC('day',event_time), 
  APPROXIMATE COUNT(DISTINCT device_id), 
  APPROXIMATE COUNT(DISTINCT uv_level)
FROM sensor_streams GROUP BY 1;
```

## Time Series Database or Postgres Computational Way for HLL

### HASHING Step

Lets cluster the data and pre-detemine for a portion of error say 5% more or less.

```
SELECT 
  31 - FLOOR(LOG(2, HASHTEXT(device_id) & ~(1 << 31)))) 
  AS bucket_hash 
FROM sensor_streams;
```

Here's the SQL for grouping MSBs by date and bucket:

```
SELECT 
  DATE(created_at) as created_date, 
  HASHTEXT(device_id) & (512 - 1) as bucket_num, 
  31 - floor(LOG(2, MIN(HASHTEXT(device_id) & ~(1 << 31)))) as bucket_hash 
FROM sensor_streams 
GROUP BY 1, 2 
ORGER BY 1, 2
```

### COUNTING Step

```
SELECT 
  created_date, 
  ((POW(512, 2) * (0.7213 / (1 + 1.079 / 512))) / ((512 - COUNT(1)) + SUM(POW(2, -1 * bucket_hash))))::INT AS num_uniques, 
  512 - COUNT(1) AS num_zero_buckets 
FROM bucketed_data 
GROUP BY 1 
ORDER BY 1;
```

### Hash Modeling Scenario

```
Hash     MSB Position     Hashes like this
1xxxxx   1                50%
01xxxx   2                25%
001xxx   3                12.5%
0001xx   4                6.25%
```

