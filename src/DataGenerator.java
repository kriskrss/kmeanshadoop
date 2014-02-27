import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class DataGenerator {
	static ArrayList<String> points = new ArrayList<String>();
	static ArrayList<String> seed = new ArrayList<String>();

	public static void main(String[] args) {
		int dimensions = Integer.parseInt(args[0]);
		int numPoints = Integer.parseInt(args[1]);
		int numClusters = Integer.parseInt(args[2]);
		generatePoints(dimensions, numPoints);
		generateRandomSeed(numClusters, numPoints);

		try {
			writeToFiles();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void writeToFiles() throws IOException {
		File f1 = new File("input/points.txt");
		File f2 = new File("input/seed.txt");
		FileWriter fw1 = new FileWriter(f1);
		BufferedWriter bw1 = new BufferedWriter(fw1);
		FileWriter fw2 = new FileWriter(f2);
		BufferedWriter bw2 = new BufferedWriter(fw2);
		Iterator<String> it1 = points.iterator();
		Iterator<String> it2 = seed.iterator();
		while (it1.hasNext()) {
			bw1.write(it1.next());
			if (it1.hasNext()) {
				bw1.newLine();
			}
		}
		while (it2.hasNext()) {
			bw2.write(it2.next());
			if (it2.hasNext()) {
				bw2.newLine();
			}
		}
		bw1.close();
		bw2.close();
	}

	private static void generateRandomSeed(int numClusters, int numPoints) {
		Random random = new Random();
		for (int i = 0; i < numClusters; i++) {
			int rand = random.nextInt(numPoints);
			seed.add(points.get(rand));
		}
		System.out.println(seed.size());
		System.out.println(seed);
	}

	private static void generatePoints(int dimensions, int numPoints) {
		Random random = new Random();
		for (int i = 0; i < numPoints; i++) {
			System.out.println(i);
			String coordinates = "";
			for (int j = 0; j < dimensions; j++) {
				double coordinate = random.nextDouble();
				coordinates = coordinates + coordinate;
				if (j != dimensions - 1) {
					coordinates = coordinates + ",";
				}
			}
			points.add(coordinates);

		}
		System.out.println(points.size());
	}
}
