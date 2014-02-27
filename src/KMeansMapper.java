import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

public class KMeansMapper implements
		Mapper<LongWritable, Text, IntWritable, Text> {
	String clusterFilePath;
	HashMap<String, Integer> clusterMap;

	@Override
	public void configure(JobConf conf) {
		System.out.println(conf.getNumReduceTasks());
		clusterFilePath = conf.get("ClusterPath");
		clusterMap = new HashMap<String, Integer>();
		Path p = new Path(clusterFilePath);
		FileSystem fs = null;
		BufferedReader br = null;
		try {
			fs = FileSystem.get(conf);
			br = new BufferedReader(new InputStreamReader(fs.open(p)));
			int count = 0;
			String line;
			line = br.readLine();
			while (line != null) {
				if (line.split("\t").length == 2) {
					line = line.split("\t")[1];
					System.out.println(line);
				}
				clusterMap.put(line, count);
				count++;
				line = br.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				// br.close();
				// fs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void close() throws IOException {

	}

	@Override
	public void map(LongWritable key, Text datapoint,
			OutputCollector<IntWritable, Text> output, Reporter reporter)
			throws IOException {
		String[] coordinates = datapoint.toString().split(",");
		double minDist = Double.MAX_VALUE;
		String minDistCentroid = null;
		Iterator<String> centroids = clusterMap.keySet().iterator();
		while (centroids.hasNext()) {
			String currentCentroid = centroids.next();
			String[] centroidcoordinates = currentCentroid.split(",");
			double dist = 0;
			for (int i = 0; i < centroidcoordinates.length; i++) {
				double coord1 = Double.parseDouble(centroidcoordinates[i]);
				double coord2 = Double.parseDouble(coordinates[i]);
				dist += Math.pow((coord1 - coord2), 2);
			}
			dist = Math.sqrt(dist);
			if (dist < minDist) {
				minDist = dist;
				minDistCentroid = currentCentroid;
			}
		}
		output.collect(new IntWritable(clusterMap.get(minDistCentroid)),
				datapoint);
	}
}
