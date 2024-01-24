package fr.vannes.recordstore.BO;

import java.util.List;

/**
 * This class represents a collection of music albums.
 * It contains the following information:
 * - id: the unique identifier of the collection
 * - list of records: the list of records of the collection
 */
public class Collection {

    private String id;
    private List<Record> records;

    public Collection() {
    }

    public Collection(String id, List<Record> records) {
        this.id = id;
        this.records = records;
    }


    /**
     * Add a record to the collection
     * @param record the record to add
     */
    public void addRecord(Record record) {
        if (record != null) {
            this.records.add(record);
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Record> getRecords() {
        return records;
    }

    public void setRecords(List<Record> records) {
        this.records = records;
    }


}
