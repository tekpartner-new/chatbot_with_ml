package net.tekpartner.chatbot.dataimporter;

import com.google.cloud.Timestamp;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.StructuredQuery.OrderBy;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

/**
 * A simple Topic List application demonstrating how to connect to Cloud Datastore, create, modify,
 * delete, and query entities.
 */
public class ImporterUtil {

  // [START datastore_build_service]
  // Create an authorized Datastore service using Application Default Credentials.
  private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

  // Create a Key factory to construct keys associated with this project.
  private final KeyFactory keyFactory = datastore.newKeyFactory().setKind("Topic");
  // [END datastore_build_service]

  // [START datastore_add_entity]

  public ImporterUtil() {

  }

  /**
   * Converts a list of topic entities to a list of formatted topic strings.
   *
   * @param topics An iterator over topic entities
   * @return A list of topics strings, one per entity
   */
  static List<String> formatTopics(Iterator<Entity> topics) {
    List<String> strings = new ArrayList<>();
    while (topics.hasNext()) {
      Entity topic = topics.next();
      if (topic.getBoolean("done")) {
        strings.add(
            String
                .format("%d : %s (done)", topic.getKey().getId(), topic.getString("description")));
      } else {
        strings.add(String.format("%d : %s (created %s)", topic.getKey().getId(),
            topic.getString("description"), topic.getTimestamp("created")));
      }
    }
    return strings;
  }
  // [END datastore_add_entity]

  // [START datastore_update_entity]

  /**
   * Exercises the methods defined in this class.
   *
   * <p>Assumes that you are authenticated using the Google Cloud SDK (using
   * {@code gcloud auth application-default login}).
   */
  public static void main(String[] args) throws Exception {
//    ImporterUtil importerUtil = new ImporterUtil();

//    importerUtil.readFile();

    /*
    System.out.println("Cloud Datastore Topic List");
    System.out.println();
    printUsage();
    while (true) {
      String commandLine = System.console().readLine("> ");
      if (commandLine.trim().isEmpty()) {
        break;
      }
      try {
        ImporterUtil.handleCommandLine(commandLine);
      } catch (IllegalArgumentException e) {
        System.out.println(e.getMessage());
        printUsage();
      }
    }
    */
    System.out.println("exiting");
    System.exit(0);
  }
  // [END datastore_update_entity]

  // [START datastore_retrieve_entities]

  private static void printUsage() {
    System.out.println("Usage:");
    System.out.println();
    System.out.println("  new <description>  Adds a topic with a description <description>");
    System.out.println("  done <topic-id>     Marks a topic as done");
    System.out.println("  list               Lists all topics by creation time");
    System.out.println("  delete <topic-id>   Deletes a topic");
    System.out.println();
  }
  // [END datastore_retrieve_entities]

  void readFile() throws IOException {

    ClassLoader classLoader = getClass().getClassLoader();
    File file = new File(classLoader.getResource("TEKPartner_HR_Manual.txt").getFile());

    BufferedReader br = new BufferedReader(new FileReader(file));

    String st;
    String topic = StringUtils.EMPTY;
    String topicText = StringUtils.EMPTY;

    while ((st = br.readLine()) != null) {
      System.out.println(st);
      if (numberOfWords(st) <= 5) {
        if (StringUtils.isNotEmpty(topic)) {
          this.addTopic(topic, topicText);
        }
        topic = st;
        topicText = StringUtils.EMPTY;
      } else {
        topicText = topicText.concat(st);
      }
    }

    if (StringUtils.isNotEmpty(topic) && StringUtils.isNotEmpty(topicText)) {
      this.addTopic(topic, topicText);
    }
  }

  private int numberOfWords(String line) {
    int count = 0;

    char ch[] = new char[line.length()];
    for (int i = 0; i < line.length(); i++) {
      ch[i] = line.charAt(i);
      if (((i > 0) && (ch[i] != ' ') && (ch[i - 1] == ' ')) || ((ch[0] != ' ') && (i == 0))) {
        count++;
      }
    }
    return count;
  }

  // [START datastore_delete_entity]

  /**
   * Adds a topic entity to the Datastore.
   *
   * @param topicName The Topic Name
   * @param description The Topic Description
   * @return The {@link Key} of the entity
   */
  Key addTopic(String topicName, String description) {
    Key key = datastore.allocateId(keyFactory.newKey());
    Entity topic = Entity.newBuilder(key)
        .set("topic", topicName)
        .set("description", description)
        .set("created", Timestamp.now())
        .build();
    datastore.put(topic);
    return key;
  }
  // [END datastore_delete_entity]

  /**
   * Returns a list of all topic entities in ascending order of creation time.
   */
  Iterator<Entity> listTopics() {
    Query<Entity> query =
        Query.newEntityQueryBuilder().setKind("Topic").setOrderBy(OrderBy.asc("created")).build();
    return datastore.run(query);
  }
}
