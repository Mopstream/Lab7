package com.mopstream.server.utility;


import java.time.LocalDateTime;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

import com.mopstream.common.data.Discipline;
import com.mopstream.common.data.LabWork;
import com.mopstream.common.exceptions.CollectionIsEmptyException;
import com.mopstream.common.exceptions.DatabaseHandlingException;
import com.mopstream.common.utility.Outputer;

/**
 * Operates the collection itself.
 */
public class CollectionManager {
    private PriorityQueue<LabWork> labsCollection;
    private LocalDateTime lastInitTime;
    private DatabaseCollectionManager databaseCollectionManager;

    public CollectionManager(DatabaseCollectionManager databaseCollectionManager) {
        this.databaseCollectionManager = databaseCollectionManager;

        loadCollection();
    }

    /**
     * @return Last initialization time or null if there wasn't initialization.
     */
    public LocalDateTime getLastInitTime() {
        return lastInitTime;
    }


    /**
     * @return The collecton itself.
     */
    public PriorityQueue<LabWork> getCollection() {
        return labsCollection;
    }

    /**
     * @return Name of the collection's type.
     */
    public String collectionType() {
        return labsCollection.getClass().getName();
    }

    /**
     * @return Size of the collection.
     */
    public int collectionSize() {
        return labsCollection.size();
    }

    /**
     * @param id ID of the lab.
     * @return A lab by its ID or null if lab isn't found.
     */
    public LabWork getById(Long id) {
        return labsCollection.stream().filter(x -> x.getId().equals(id)).findFirst().orElse(null);
    }

    /**
     * @param labWorkToFind A lab which's value will be found.
     * @return A lab by its value or null if lab isn't found.
     */
    public LabWork getByValue(LabWork labWorkToFind) {
        return labsCollection.stream().filter(x -> x.equals(labWorkToFind)).findFirst().orElse(null);
    }

    /**
     * Adds a new lab to collection.
     *
     * @param labWork A lab to add.
     */
    public void addToCollection(LabWork labWork) {
        labsCollection.add(labWork);
    }

    /**
     * @param disciplineToFilter Discipline to filter by.
     * @return Information about valid labs or empty string, if there's no such labs.
     */
    public String disciplineFilteredInfo(Discipline disciplineToFilter) {
        return labsCollection.stream().filter(x -> x.getDiscipline().equals(disciplineToFilter))
                .map(LabWork::toString)
                .collect(Collectors.joining("\n\n"));
    }

    /**
     * Removes a lab from collection.\
     *
     * @param labWork that needs to be deleted
     */
    public void removeFromCollection(LabWork labWork) {
        labsCollection.remove(labWork);
    }

    /**
     * Prints the elements of the collection in ascending order.
     */
    public String printAskending() {
        return labsCollection.stream().sorted().map(LabWork::toString).collect(Collectors.joining("\n\n"));
    }

    /**
     * @param minimalPoint Minimal Point to filter by
     * @return info of the elements whose minimum Point field value is less than the specified one
     * @throws CollectionIsEmptyException if the collection is empty
     */
    public String filterByMinimalPoint(long minimalPoint) throws CollectionIsEmptyException {
        if (labsCollection.isEmpty()) throw new CollectionIsEmptyException();
        return labsCollection.stream().filter(x -> x.getMinimalPoint() < minimalPoint).reduce("", (sum, m) -> sum += m + "\n\n", (sum1, sum2) -> sum1 + sum2).trim();
    }

    /**
     * Removes all items smaller than the specified one from the collection
     *
     * @param labWorkToCompare lab to filter by
     */
    public PriorityQueue<LabWork> getLower(LabWork labWorkToCompare) {
        return labsCollection.stream().filter(labWork -> labWork.compareTo(labWorkToCompare) < 0).collect(PriorityQueue::new, PriorityQueue::add, PriorityQueue::addAll);
    }

    /**
     *
     */
    public LabWork getHead() {
        return labsCollection.peek();
    }

    /**
     * Clears the collection
     */
    public void clearCollection() {
        labsCollection.clear();
    }


    /**
     * Loads the collection from file
     */
    private void loadCollection() {
        try {
            labsCollection = databaseCollectionManager.getCollection();
            lastInitTime = LocalDateTime.now();
            Outputer.println("Коллекция загружена.");
        } catch (DatabaseHandlingException exception) {
            labsCollection = new PriorityQueue<>();
            Outputer.printerror("Коллекция не может быть загружена!");
        }
    }

    public String showCollection() {
        if (labsCollection.isEmpty()) return "Коллекция пуста!";
        return labsCollection.stream().reduce("", (sum, m) -> sum += m + "\n\n", (sum1, sum2) -> sum1 + sum2).trim();
    }

}
