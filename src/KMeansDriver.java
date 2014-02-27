import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class KMeansDriver extends Configured implements Tool {

	@Override
	public int run(String[] args) throws Exception {
		Options options = new Options();
		options.addOption("it", true, "Number of Iterations");
		options.addOption("i", true, "Input Directory Path");
		options.addOption("o", true, "Output Directory Path");
		options.addOption("s", true, "Seed Path");
		options.addOption("m", true, "Number of Map tasks");
		options.addOption("r", true, "Number of Reduce Tasks");
		int maxIterations = 0;
		CommandLineParser parser = new BasicParser();
		CommandLine cmd = parser.parse(options, args);
		if (!cmd.hasOption("it")) {
			System.out
					.println("Number of iterations not specified. Initializing prgram to run default 5 iterations");
			maxIterations = 5;
		}
		if (!cmd.hasOption("i")) {
			System.out
					.println("Please specify the input path using the -i option");
			return 1;
		}
		if (!cmd.hasOption("o")) {
			System.out
					.println("Please specify the output path using the -o option");
			return 1;
		}
		if (!cmd.hasOption("s")) {
			System.out
					.println("Please specify the random seed path using the -s option");
			return 1;
		}
		if (maxIterations == 0) {
			maxIterations = Integer.parseInt(cmd.getOptionValue("it"));
		}
		for (int i = 0; i < maxIterations; i++) {
			JobConf conf = new JobConf(getConf(), KMeansDriver.class);
			conf.setJobName("KMeans");
			conf.setOutputKeyClass(IntWritable.class);
			conf.setOutputValueClass(Text.class);
			conf.setJarByClass(KMeansDriver.class);
			conf.setMapperClass(KMeansMapper.class);
			conf.setReducerClass(KMeansReducer.class);
			if (cmd.hasOption("m")) {
				conf.setNumMapTasks(Integer.parseInt(cmd.getOptionValue("m")));
			}
			if (cmd.hasOption("r")) {
				conf.setNumMapTasks(Integer.parseInt(cmd.getOptionValue("r")));
			}
			FileInputFormat.addInputPath(conf,
					new Path(cmd.getOptionValue("i")));
			FileOutputFormat.setOutputPath(conf,
					new Path(cmd.getOptionValue("o") + i));
			if (i == 0) {
				conf.set("ClusterPath", cmd.getOptionValue("s"));
			} else {
				conf.set("ClusterPath", cmd.getOptionValue("o") + (i - 1)
						+ "/part-00000");
			}
			conf.set("maxIterations", cmd.getOptionValue("it"));
			JobClient.runJob(conf);
		}
		return 0;
	}

	public static void main(String[] args) throws Exception {
		int res = ToolRunner.run(new Configuration(), new KMeansDriver(), args);
		System.exit(res);
	}
}
