import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;

public class KMeansReducer implements
		Reducer<IntWritable, Text, IntWritable, Text> {
	long counter;

	@Override
	public void configure(JobConf conf) {
	}

	@Override
	public void close() throws IOException {

	}

	@Override
	public void reduce(IntWritable clusterID, Iterator<Text> dataPoints,
			OutputCollector<IntWritable, Text> output, Reporter reporter)
			throws IOException {
		double count = 0;
		double[] clusterCentroid = null;
		while (dataPoints.hasNext()) {
			count++;
			String dataPoint = dataPoints.next().toString();
			String[] dpCoordinates = dataPoint.split(",");
			if (clusterCentroid == null) {
				clusterCentroid = new double[dpCoordinates.length];
			}
			for (int i = 0; i < dpCoordinates.length; i++) {
				double coordinate = Double.parseDouble(dpCoordinates[i]);
				clusterCentroid[i] = clusterCentroid[i] + coordinate;
			}
		}
		System.out.println(clusterID);
		System.out.println(count);
		String centroidCoordinates = "";
		for (int i = 0; i < clusterCentroid.length; i++) {
			clusterCentroid[i] = clusterCentroid[i] / count;
			centroidCoordinates = centroidCoordinates + clusterCentroid[i];
			if (i != clusterCentroid.length - 1) {
				centroidCoordinates += ",";
			}
		}
		output.collect(clusterID, new Text(centroidCoordinates));

	}
}
