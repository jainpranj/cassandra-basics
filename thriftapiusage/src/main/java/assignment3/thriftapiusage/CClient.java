package assignment3.thriftapiusage;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.UUID;

import org.apache.cassandra.thrift.Cassandra;
import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.apache.cassandra.thrift.ColumnParent;
import org.apache.cassandra.thrift.ColumnPath;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.InvalidRequestException;
import org.apache.cassandra.thrift.NotFoundException;
import org.apache.cassandra.thrift.SlicePredicate;
import org.apache.cassandra.thrift.SliceRange;
import org.apache.cassandra.thrift.TimedOutException;
import org.apache.cassandra.thrift.UnavailableException;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;

public class CClient {
	public static void main(String[] args) throws TException,
			InvalidRequestException, UnavailableException,
			NotFoundException, TimedOutException, FileNotFoundException, IOException {
		TTransport tr = new TFramedTransport(new TSocket("localhost", 9160));
		TProtocol proto = new TBinaryProtocol(tr);
		Cassandra.Client client = new Cassandra.Client(proto);
		tr.open();

		client.set_keyspace("pranjal_keyspace");
		ColumnParent parent = new ColumnParent("ratings");
		readBookRatingCSVFile(parent, client);
		tr.close();
	}

	public static ByteBuffer toByteBuffer(String value)
			throws UnsupportedEncodingException {
		return ByteBuffer.wrap(value.getBytes("UTF-8"));
	}

	public static String toString(ByteBuffer buffer)
			throws UnsupportedEncodingException {
		byte[] bytes = new byte[buffer.remaining()];
		buffer.get(bytes);
		return new String(bytes, "UTF-8");
	}

	public static void readBookRatingCSVFile(ColumnParent parent,
			Cassandra.Client client) throws FileNotFoundException, IOException,
			InvalidRequestException, NotFoundException, UnavailableException,
			TimedOutException, TException {

		// session.execute("CREATE TABLE pranjal_keyspace.ratings ("
		// + "id uuid PRIMARY KEY," + "user_id text," + "isbn text,"
		// + "book_rating text," + ");");

		String csvFile = "/Users/pranjal/Desktop/cassandra-basics/BX-CSV-Dump/BX-Book-Ratings.csv";
		String line = "";
		String cvsSplitBy = ";";

		try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
			while ((line = br.readLine()) != null) {

				String[] tags = line.split(cvsSplitBy);
				String key_user_id = String.valueOf(UUID.randomUUID());
				// insert data
				long timestamp = System.currentTimeMillis();

				Column nameColumn = new Column(toByteBuffer("user_id"));
				nameColumn.setValue(toByteBuffer(tags[0].replaceAll("\"", "")));
				nameColumn.setTimestamp(timestamp);
				client.insert(toByteBuffer(key_user_id), parent, nameColumn,
						ConsistencyLevel.ONE);

				Column ageColumn = new Column(toByteBuffer("isbn"));
				ageColumn.setValue(toByteBuffer(tags[1].replaceAll("\"", "")));
				ageColumn.setTimestamp(timestamp);
				client.insert(toByteBuffer(key_user_id), parent, ageColumn,
						ConsistencyLevel.ONE);
				Column book_rating = new Column(toByteBuffer("book_rating"));
				book_rating.setValue(toByteBuffer(tags[2].replaceAll("\"", "")));
				book_rating.setTimestamp(timestamp);
				client.insert(toByteBuffer(key_user_id), parent, book_rating,
						ConsistencyLevel.ONE);

				ColumnPath path = new ColumnPath("ratings");

				// read single column
				path.setColumn(toByteBuffer("user_id"));
				System.out.println(client.get(toByteBuffer(key_user_id), path,
						ConsistencyLevel.ONE));
				// read entire row
				SlicePredicate predicate = new SlicePredicate();
				SliceRange sliceRange = new SliceRange(toByteBuffer(""),
						toByteBuffer(""), false, 10);
				predicate.setSlice_range(sliceRange);

				List<ColumnOrSuperColumn> results = client.get_slice(
						toByteBuffer(key_user_id), parent, predicate,
						ConsistencyLevel.ONE);
				for (ColumnOrSuperColumn result : results) {
					Column column = result.column;
					System.out.println(toString(column.name) + " -> "
							+ toString(column.value));
				}
			}

		}

	}
}