package assignment3.cqlapi;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.UUID;

import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;

/**
 * Hello world!
 *
 */
public class App {
	public static final int BATCH_SIZE = 100;

	public static void main(String[] args) throws FileNotFoundException,
			IOException {

		Cluster cluster = null;
		try {
			cluster = Cluster.builder() // (1)
					.addContactPoint("127.0.0.1").build();
			Session session = cluster.connect(); // (2)
			//
			// ResultSet rs = session
			// .execute("select * from pranjal_keyspace.user_profiles"); // (3)
			// for (Row element : rs.all()) {
			//
			// System.out.println(element);
			// }
			session.execute("CREATE KEYSPACE pranjal_keyspace WITH replication "
					+ "= {'class':'SimpleStrategy', 'replication_factor':1};");

			// readBookRatingCSVFile(session);
			//readBooksCSVFile(session);
			readUsersCSVFile(session);

			// (4)
		} finally {
			if (cluster != null)
				cluster.close(); // (5)
		}

	}

	public static void readBookRatingCSVFile(Session session)
			throws FileNotFoundException, IOException {

		session.execute("CREATE TABLE pranjal_keyspace.ratings ("
				+ "id uuid PRIMARY KEY," + "user_id text," + "isbn text,"
				+ "book_rating text," + ");");

		int count = 0;

		String csvFile = "/Users/pranjal/Desktop/cassandra-basics/BX-CSV-Dump/BX-Book-Ratings.csv";
		String line = "";
		String cvsSplitBy = ";";

		try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

			BatchStatement batchStatement = new BatchStatement();
			while ((line = br.readLine()) != null) {
				count++;

				String[] tags = line.split(cvsSplitBy);

				PreparedStatement preparedStatement = session
						.prepare("INSERT INTO pranjal_keyspace.ratings"
								+ " (id,user_id , isbn , book_rating) VALUES (?,?, ?, ?)");
				BoundStatement boundStatement = new BoundStatement(
						preparedStatement);
				boundStatement.bind(UUID.randomUUID(),
						tags[0].replaceAll("\"", ""),
						tags[1].replaceAll("\"", ""),
						tags[2].replaceAll("\"", ""));
				batchStatement.add(boundStatement);

				if (count > 300) {

					count = 0;
					System.out.println("before" + batchStatement.size());
					session.execute(batchStatement);

					batchStatement.clear();
					System.out.println("After" + batchStatement.size());

				}
			}
		}
	}

	public static void readBooksCSVFile(Session session)
			throws FileNotFoundException, IOException {
		// "ISBN";"Book-Title";"Book-Author";"Year-Of-Publication";"Publisher";"Image-URL-S";"Image-URL-M";"Image-URL-L"
		session.execute("CREATE TABLE pranjal_keyspace.books ("
				+ "id uuid PRIMARY KEY," + "isbn text," + "book_title text,"
				+ "book_author text," + "year_of_pub text," + "publisher text,"
				+ "image_url_s text," + "image_url_m text,"
				+ "image_url_l text," + ");");

		int count = 0;

		String csvFile = "/Users/pranjal/Desktop/cassandra-basics/BX-CSV-Dump/BX-Books.csv";
		String line = "";
		String cvsSplitBy = ";";

		try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

			BatchStatement batchStatement = new BatchStatement();
			while ((line = br.readLine()) != null) {
				count++;

				String[] tags = line.split(cvsSplitBy);

				PreparedStatement preparedStatement = session
						.prepare("INSERT INTO pranjal_keyspace.books"
								+ " (id,isbn , book_title , book_author,year_of_pub,publisher,image_url_s,image_url_m,image_url_l) VALUES (?,?,?,?,?,?,?,?,?)");
				BoundStatement boundStatement = new BoundStatement(
						preparedStatement);
				// for(String s:tags){
				// System.out.println(s);
				// }

				boundStatement.bind(UUID.randomUUID(),
						tags[0].replaceAll("\"", ""),
						tags[1].replaceAll("\"", ""),
						tags[2].replaceAll("\"", ""),
						tags[3].replaceAll("\"", ""),
						tags[4].replaceAll("\"", ""),
						tags[5].replaceAll("\"", ""),
						tags[6].replaceAll("\"", ""),

						tags[7].replaceAll("\"", ""));
				batchStatement.add(boundStatement);

				if (count > BATCH_SIZE) {

					count = 0;
					System.out.println("before" + batchStatement.size());
					session.execute(batchStatement);

					batchStatement.clear();
					System.out.println("After" + batchStatement.size());

				}
			}
		}
	}

	public static void readUsersCSVFile(Session session)
			throws FileNotFoundException, IOException {

		session.execute("CREATE TABLE pranjal_keyspace.users("
				+ "id uuid PRIMARY KEY," + "user_id text," + "location text,"
				+ "age text," + ");");

		int count = 0;

		String csvFile = "/Users/pranjal/Desktop/cassandra-basics/BX-CSV-Dump/BX-Users.csv";
		String line = "";
		String cvsSplitBy = ";";

		try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

			BatchStatement batchStatement = new BatchStatement();
			while ((line = br.readLine()) != null) {
				count++;

				String[] tags = line.split(cvsSplitBy);

				PreparedStatement preparedStatement = session
						.prepare("INSERT INTO pranjal_keyspace.users"
								+ " (id,user_id , location , age) VALUES (?,?, ?, ?)");
				BoundStatement boundStatement = new BoundStatement(
						preparedStatement);
				boundStatement.bind(UUID.randomUUID(),
						tags[0].replaceAll("\"", ""),
						tags[1].replaceAll("\"", ""),
						tags[2].replaceAll("\"", ""));
				batchStatement.add(boundStatement);

				if (count > BATCH_SIZE) {

					count = 0;
					System.out.println("before" + batchStatement.size());
					session.execute(batchStatement);

					batchStatement.clear();
					System.out.println("After" + batchStatement.size());

				}
			}
		}
	}
}