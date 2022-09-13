package com.mopstream.server.utility;

import com.mopstream.common.data.*;
import com.mopstream.common.exceptions.DatabaseHandlingException;
import com.mopstream.common.interaction.NewLab;
import com.mopstream.common.interaction.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.PriorityQueue;

/**
 * Operates the database collection itself.
 */
public class DatabaseCollectionManager {
    // MARINE_TABLE
    private final String SELECT_ALL_LABS = "SELECT * FROM " + DatabaseHandler.LAB_TABLE;
    private final String SELECT_LAB_BY_ID = SELECT_ALL_LABS + " WHERE " +
            DatabaseHandler.LAB_TABLE_ID_COLUMN + " = ?";
    private final String SELECT_LAB_BY_ID_AND_USER_ID = SELECT_LAB_BY_ID + " AND " +
            DatabaseHandler.LAB_TABLE_USER_ID_COLUMN + " = ?";
    private final String INSERT_LAB = "INSERT INTO " +
            DatabaseHandler.LAB_TABLE + " (" +
            DatabaseHandler.LAB_TABLE_NAME_COLUMN + ", " +
            DatabaseHandler.LAB_TABLE_CREATION_DATE_COLUMN + ", " +
            DatabaseHandler.LAB_TABLE_MINIMIMAL_POINT_COLUMN + ", " +
            DatabaseHandler.LAB_TABLE_DIFFICULTY_COLUMN + ", " +
            DatabaseHandler.LAB_TABLE_USER_ID_COLUMN + ") VALUES (?, ?, ?, ?, ?)";

    private final String DELETE_LAB_BY_ID = "DELETE FROM " + DatabaseHandler.LAB_TABLE +
            " WHERE " + DatabaseHandler.LAB_TABLE_ID_COLUMN + " = ?";
    private final String UPDATE_LAB_NAME_BY_ID = "UPDATE " + DatabaseHandler.LAB_TABLE + " SET " +
            DatabaseHandler.LAB_TABLE_NAME_COLUMN + " = ?" + " WHERE " +
            DatabaseHandler.LAB_TABLE_ID_COLUMN + " = ?";
    private final String UPDATE_LAB_MINIMIMAL_POINT_BY_ID = "UPDATE " + DatabaseHandler.LAB_TABLE + " SET " +
            DatabaseHandler.LAB_TABLE_MINIMIMAL_POINT_COLUMN + " = ?" + " WHERE " +
            DatabaseHandler.LAB_TABLE_ID_COLUMN + " = ?";
    private final String UPDATE_LAB_DIFFICULTY_BY_ID = "UPDATE " + DatabaseHandler.LAB_TABLE + " SET " +
            DatabaseHandler.LAB_TABLE_DIFFICULTY_COLUMN + " = ?" + " WHERE " +
            DatabaseHandler.LAB_TABLE_ID_COLUMN + " = ?";
    // COORDINATES_TABLE
    private final String SELECT_ALL_COORDINATES = "SELECT * FROM " + DatabaseHandler.COORDINATES_TABLE;
    private final String SELECT_COORDINATES_BY_LAB_ID = SELECT_ALL_COORDINATES +
            " WHERE " + DatabaseHandler.COORDINATES_TABLE_LAB_ID_COLUMN + " = ?";
    private final String INSERT_COORDINATES = "INSERT INTO " +
            DatabaseHandler.COORDINATES_TABLE + " (" +
            DatabaseHandler.COORDINATES_TABLE_LAB_ID_COLUMN + ", " +
            DatabaseHandler.COORDINATES_TABLE_X_COLUMN + ", " +
            DatabaseHandler.COORDINATES_TABLE_Y_COLUMN + ") VALUES (?, ?, ?)";
    private final String DELETE_COORDINATES_BY_LAB_ID = "DELETE FROM " + DatabaseHandler.COORDINATES_TABLE +
            " WHERE " + DatabaseHandler.COORDINATES_TABLE_LAB_ID_COLUMN + " = ?";
    private final String UPDATE_COORDINATES_BY_LAB_ID = "UPDATE " + DatabaseHandler.COORDINATES_TABLE + " SET " +
            DatabaseHandler.COORDINATES_TABLE_X_COLUMN + " = ?, " +
            DatabaseHandler.COORDINATES_TABLE_Y_COLUMN + " = ?" + " WHERE " +
            DatabaseHandler.COORDINATES_TABLE_LAB_ID_COLUMN + " = ?";
    // DISCIPLINE_TABLE
    private final String SELECT_ALL_DISCIPLINE = "SELECT * FROM " + DatabaseHandler.DISCIPLINE_TABLE;
    private final String SELECT_DISCIPLINE_BY_LAB_ID = SELECT_ALL_DISCIPLINE +
            " WHERE " + DatabaseHandler.DISCIPLINE_TABLE_LAB_ID_COLUMN + " = ?";
    private final String INSERT_DISCIPLINE = "INSERT INTO " +
            DatabaseHandler.DISCIPLINE_TABLE + " (" +
            DatabaseHandler.DISCIPLINE_TABLE_LAB_ID_COLUMN + ", " +
            DatabaseHandler.DISCIPLINE_TABLE_NAME_COLUMN + ", " +
            DatabaseHandler.DISCIPLINE_TABLE_PRACTICE_HOURS_COLUMN + ") VALUES (?, ?, ?)";
    private final String UPDATE_DISCIPLINE_BY_LAB_ID = "UPDATE " + DatabaseHandler.DISCIPLINE_TABLE + " SET " +
            DatabaseHandler.DISCIPLINE_TABLE_NAME_COLUMN + " = ?, " +
            DatabaseHandler.DISCIPLINE_TABLE_PRACTICE_HOURS_COLUMN + " = ?" + " WHERE " +
            DatabaseHandler.DISCIPLINE_TABLE_LAB_ID_COLUMN + " = ?";
    private final String DELETE_DISCIPLINE_BY_LAB_ID = "DELETE FROM " + DatabaseHandler.DISCIPLINE_TABLE +
            " WHERE " + DatabaseHandler.DISCIPLINE_TABLE_LAB_ID_COLUMN + " = ?";
    private DatabaseHandler databaseHandler;
    private DatabaseUserManager databaseUserManager;

    public DatabaseCollectionManager(DatabaseHandler databaseHandler, DatabaseUserManager databaseUserManager) {
        this.databaseHandler = databaseHandler;
        this.databaseUserManager = databaseUserManager;
    }

    /**
     * Create Lab.
     *
     * @param resultSet Result set parametres of Lab.
     * @return New Lab.
     * @throws SQLException When there's exception inside.
     */
    private LabWork createLab(ResultSet resultSet) throws SQLException {
        long id = resultSet.getLong(DatabaseHandler.LAB_TABLE_ID_COLUMN);
        String name = resultSet.getString(DatabaseHandler.LAB_TABLE_NAME_COLUMN);
        Coordinates coordinates = getCoordinatesByLabId(id);
        LocalDateTime creationDate = resultSet.getTimestamp(DatabaseHandler.LAB_TABLE_CREATION_DATE_COLUMN).toLocalDateTime();
        long minimalPoint = resultSet.getLong(DatabaseHandler.LAB_TABLE_MINIMIMAL_POINT_COLUMN);
        Difficulty difficulty = Difficulty.valueOf(resultSet.getString(DatabaseHandler.LAB_TABLE_DIFFICULTY_COLUMN));
        Discipline discipline = getDisciplineByLabId(id);
        User owner = databaseUserManager.getUserById(resultSet.getLong(DatabaseHandler.LAB_TABLE_USER_ID_COLUMN));
        return new LabWork(
                id,
                name,
                coordinates,
                creationDate,
                minimalPoint,
                difficulty,
                discipline,
                owner
        );
    }

    /**
     * @return List of Labs.
     * @throws DatabaseHandlingException When there's exception inside.
     */
    public PriorityQueue<LabWork> getCollection() throws DatabaseHandlingException {
        PriorityQueue<LabWork> labList = new PriorityQueue<>();
        PreparedStatement preparedSelectAllStatement = null;
        try {
            preparedSelectAllStatement = databaseHandler.getPreparedStatement(SELECT_ALL_LABS, false);
            ResultSet resultSet = preparedSelectAllStatement.executeQuery();
            while (resultSet.next()) {
                labList.add(createLab(resultSet));
            }
        } catch (SQLException exception) {
            throw new DatabaseHandlingException();
        } finally {
            databaseHandler.closePreparedStatement(preparedSelectAllStatement);
        }
        return labList;
    }

    /**
     * @param labId Id of Lab.
     * @return coordinates.
     * @throws SQLException When there's exception inside.
     */
    private Coordinates getCoordinatesByLabId(long labId) throws SQLException {
        Coordinates coordinates;
        PreparedStatement preparedSelectCoordinatesByMarineIdStatement = null;
        try {
            preparedSelectCoordinatesByMarineIdStatement =
                    databaseHandler.getPreparedStatement(SELECT_COORDINATES_BY_LAB_ID, false);
            preparedSelectCoordinatesByMarineIdStatement.setLong(1, labId);
            ResultSet resultSet = preparedSelectCoordinatesByMarineIdStatement.executeQuery();
            if (resultSet.next()) {
                coordinates = new Coordinates(
                        resultSet.getLong(DatabaseHandler.COORDINATES_TABLE_X_COLUMN),
                        resultSet.getDouble(DatabaseHandler.COORDINATES_TABLE_Y_COLUMN)
                );
            } else throw new SQLException();
        } catch (SQLException exception) {
            throw new SQLException(exception);
        } finally {
            databaseHandler.closePreparedStatement(preparedSelectCoordinatesByMarineIdStatement);
        }
        return coordinates;
    }

    /**
     * @param labId Id of Lab.
     * @return discipline.
     * @throws SQLException When there's exception inside.
     */
    private Discipline getDisciplineByLabId(long labId) throws SQLException {
        Discipline discipline;
        PreparedStatement preparedSelectChapterByLabIdStatement = null;
        try {
            preparedSelectChapterByLabIdStatement =
                    databaseHandler.getPreparedStatement(SELECT_DISCIPLINE_BY_LAB_ID, false);
            preparedSelectChapterByLabIdStatement.setLong(1, labId);
            ResultSet resultSet = preparedSelectChapterByLabIdStatement.executeQuery();
            if (resultSet.next()) {
                discipline = new Discipline(
                        resultSet.getString(DatabaseHandler.DISCIPLINE_TABLE_NAME_COLUMN),
                        resultSet.getInt(DatabaseHandler.DISCIPLINE_TABLE_PRACTICE_HOURS_COLUMN)
                );
            } else throw new SQLException();
        } catch (SQLException exception) {
            throw new SQLException(exception);
        } finally {
            databaseHandler.closePreparedStatement(preparedSelectChapterByLabIdStatement);
        }
        return discipline;
    }

    /**
     * @param newLab Lab raw.
     * @param user   User.
     * @return Lab.
     * @throws DatabaseHandlingException When there's exception inside.
     */
    public LabWork insertLab(NewLab newLab, User user) throws DatabaseHandlingException {
        LabWork labWork;
        PreparedStatement preparedInsertLabStatement = null;
        PreparedStatement preparedInsertCoordinatesStatement = null;
        PreparedStatement preparedInsertDisciplineStatement = null;
        try {
            databaseHandler.setCommitMode();
            databaseHandler.setSavepoint();

            LocalDateTime creationTime = LocalDateTime.now();

            preparedInsertLabStatement = databaseHandler.getPreparedStatement(INSERT_LAB, true);
            preparedInsertCoordinatesStatement = databaseHandler.getPreparedStatement(INSERT_COORDINATES, false);
            preparedInsertDisciplineStatement = databaseHandler.getPreparedStatement(INSERT_DISCIPLINE, false);

            preparedInsertLabStatement.setString(1, newLab.getName());
            preparedInsertLabStatement.setTimestamp(2, Timestamp.valueOf(creationTime));
            preparedInsertLabStatement.setLong(3, newLab.getMinimalPoint());
            preparedInsertLabStatement.setString(4, newLab.getDifficulty().toString().toUpperCase());
            preparedInsertLabStatement.setLong(5, databaseUserManager.getUserIdByUser(user));
            if (preparedInsertLabStatement.executeUpdate() == 0) throw new SQLException();
            ResultSet generatedLabKeys = preparedInsertLabStatement.getGeneratedKeys();
            long labId;
            if (generatedLabKeys.next()) {
                labId = generatedLabKeys.getLong(1);
            } else throw new SQLException();

            preparedInsertCoordinatesStatement.setLong(1, labId);
            preparedInsertCoordinatesStatement.setLong(2, newLab.getCoordinates().getX());
            preparedInsertCoordinatesStatement.setDouble(3, newLab.getCoordinates().getY());
            if (preparedInsertCoordinatesStatement.executeUpdate() == 0) throw new SQLException();

            preparedInsertDisciplineStatement.setLong(1, labId);
            preparedInsertDisciplineStatement.setString(2, newLab.getDiscipline().getName());
            preparedInsertDisciplineStatement.setInt(3, newLab.getDiscipline().getPracticeHours());
            if (preparedInsertDisciplineStatement.executeUpdate() == 0) throw new SQLException();

            labWork = new LabWork(
                    labId,
                    newLab.getName(),
                    newLab.getCoordinates(),
                    creationTime,
                    newLab.getMinimalPoint(),
                    newLab.getDifficulty(),
                    newLab.getDiscipline(),
                    user
            );

            databaseHandler.commit();
            return labWork;
        } catch (SQLException exception) {
            databaseHandler.rollback();
            throw new DatabaseHandlingException();
        } finally {
            databaseHandler.closePreparedStatement(preparedInsertLabStatement);
            databaseHandler.closePreparedStatement(preparedInsertCoordinatesStatement);
            databaseHandler.closePreparedStatement(preparedInsertDisciplineStatement);
            databaseHandler.setNormalMode();
        }
    }

    /**
     * @param newLabWork Lab raw.
     * @param labId      Id of Lab.
     * @throws DatabaseHandlingException When there's exception inside.
     */
    public void updateLabById(long labId, NewLab newLabWork) throws DatabaseHandlingException {
        PreparedStatement preparedUpdateLabNameByIdStatement = null;
        PreparedStatement preparedUpdateLabMinimalPointByIdStatement = null;
        PreparedStatement preparedUpdateLabDifficultyByIdStatement = null;
        PreparedStatement preparedUpdateCoordinatesByLabIdStatement = null;
        PreparedStatement preparedUpdateDisciplineByLabIdStatement = null;
        try {
            databaseHandler.setCommitMode();
            databaseHandler.setSavepoint();

            preparedUpdateLabNameByIdStatement = databaseHandler.getPreparedStatement(UPDATE_LAB_NAME_BY_ID, false);
            preparedUpdateLabMinimalPointByIdStatement = databaseHandler.getPreparedStatement(UPDATE_LAB_MINIMIMAL_POINT_BY_ID, false);
            preparedUpdateLabDifficultyByIdStatement = databaseHandler.getPreparedStatement(UPDATE_LAB_DIFFICULTY_BY_ID, false);
            preparedUpdateCoordinatesByLabIdStatement = databaseHandler.getPreparedStatement(UPDATE_COORDINATES_BY_LAB_ID, false);
            preparedUpdateDisciplineByLabIdStatement = databaseHandler.getPreparedStatement(UPDATE_DISCIPLINE_BY_LAB_ID, false);

            if (newLabWork.getName() != null) {
                preparedUpdateLabNameByIdStatement.setString(1, newLabWork.getName());
                preparedUpdateLabNameByIdStatement.setLong(2, labId);
                if (preparedUpdateLabNameByIdStatement.executeUpdate() == 0) throw new SQLException();
            }
            if (newLabWork.getMinimalPoint() != -1) {
                preparedUpdateLabMinimalPointByIdStatement.setLong(1, newLabWork.getMinimalPoint());
                preparedUpdateLabMinimalPointByIdStatement.setLong(2, labId);
                if (preparedUpdateLabMinimalPointByIdStatement.executeUpdate() == 0) throw new SQLException();
            }
            if (newLabWork.getDifficulty() != null) {
                preparedUpdateLabDifficultyByIdStatement.setString(1, newLabWork.getDifficulty().toString());
                preparedUpdateLabDifficultyByIdStatement.setLong(2, labId);
                if (preparedUpdateLabDifficultyByIdStatement.executeUpdate() == 0) throw new SQLException();
            }
            if (newLabWork.getCoordinates() != null) {
                preparedUpdateCoordinatesByLabIdStatement.setLong(1, newLabWork.getCoordinates().getX());
                preparedUpdateCoordinatesByLabIdStatement.setDouble(2, newLabWork.getCoordinates().getY());
                preparedUpdateCoordinatesByLabIdStatement.setLong(3, labId);
                if (preparedUpdateCoordinatesByLabIdStatement.executeUpdate() == 0) throw new SQLException();
            }
            if (newLabWork.getDiscipline() != null) {
                preparedUpdateDisciplineByLabIdStatement.setString(1, newLabWork.getDiscipline().getName());
                preparedUpdateDisciplineByLabIdStatement.setLong(2, newLabWork.getDiscipline().getPracticeHours());
                preparedUpdateDisciplineByLabIdStatement.setLong(3, labId);
                if (preparedUpdateDisciplineByLabIdStatement.executeUpdate() == 0) throw new SQLException();
            }

            databaseHandler.commit();
        } catch (SQLException exception) {
            databaseHandler.rollback();
            throw new DatabaseHandlingException();
        } finally {
            databaseHandler.closePreparedStatement(preparedUpdateLabNameByIdStatement);
            databaseHandler.closePreparedStatement(preparedUpdateLabMinimalPointByIdStatement);
            databaseHandler.closePreparedStatement(preparedUpdateLabDifficultyByIdStatement);
            databaseHandler.closePreparedStatement(preparedUpdateCoordinatesByLabIdStatement);
            databaseHandler.closePreparedStatement(preparedUpdateDisciplineByLabIdStatement);
            databaseHandler.setNormalMode();
        }
    }

    /**
     * Delete Lab by id.
     *
     * @param labId Id of Lab.
     * @throws DatabaseHandlingException When there's exception inside.
     */
    public void deleteLabById(long labId) throws DatabaseHandlingException {
        PreparedStatement preparedDeleteLabByIdStatement = null;
        PreparedStatement preparedDeleteCoordinatesByLabIdStatement = null;
        PreparedStatement preparedDeleteDisciplineByLabIdStatement = null;
        try {
            databaseHandler.setCommitMode();
            databaseHandler.setSavepoint();

            preparedDeleteCoordinatesByLabIdStatement = databaseHandler.getPreparedStatement(DELETE_COORDINATES_BY_LAB_ID, false);
            preparedDeleteCoordinatesByLabIdStatement.setLong(1, labId);
            if (preparedDeleteCoordinatesByLabIdStatement.executeUpdate() == 0) throw new SQLException();

            preparedDeleteDisciplineByLabIdStatement = databaseHandler.getPreparedStatement(DELETE_DISCIPLINE_BY_LAB_ID, false);
            preparedDeleteDisciplineByLabIdStatement.setLong(1, labId);
            if (preparedDeleteDisciplineByLabIdStatement.executeUpdate() == 0) throw new SQLException();

            preparedDeleteLabByIdStatement = databaseHandler.getPreparedStatement(DELETE_LAB_BY_ID, false);
            preparedDeleteLabByIdStatement.setLong(1, labId);
            if (preparedDeleteLabByIdStatement.executeUpdate() == 0) throw new SQLException();

            databaseHandler.commit();
        } catch (SQLException exception) {
            databaseHandler.rollback();
            throw new DatabaseHandlingException();
        } finally {
            databaseHandler.closePreparedStatement(preparedDeleteLabByIdStatement);
            databaseHandler.closePreparedStatement(preparedDeleteCoordinatesByLabIdStatement);
            databaseHandler.closePreparedStatement(preparedDeleteDisciplineByLabIdStatement);
            databaseHandler.setNormalMode();
        }
    }

    /**
     * Checks Lab user id.
     *
     * @param LabId Id of Lab.
     * @param user  Owner of marine.
     * @return Is everything ok.
     * @throws DatabaseHandlingException When there's exception inside.
     */
    public boolean checkLabUserId(long LabId, User user) throws DatabaseHandlingException {
        PreparedStatement preparedSelectLabByIdAndUserIdStatement = null;
        try {
            preparedSelectLabByIdAndUserIdStatement = databaseHandler.getPreparedStatement(SELECT_LAB_BY_ID_AND_USER_ID, false);
            preparedSelectLabByIdAndUserIdStatement.setLong(1, LabId);
            preparedSelectLabByIdAndUserIdStatement.setLong(2, databaseUserManager.getUserIdByUser(user));
            ResultSet resultSet = preparedSelectLabByIdAndUserIdStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException exception) {
            throw new DatabaseHandlingException();
        } finally {
            databaseHandler.closePreparedStatement(preparedSelectLabByIdAndUserIdStatement);
        }
    }

    /**
     * Clear the collection.
     *
     * @throws DatabaseHandlingException When there's exception inside.
     */
    public void clearCollection() throws DatabaseHandlingException {
        PriorityQueue<LabWork> labList = getCollection();
        for (LabWork labWork : labList) {
            deleteLabById(labWork.getId());
        }
    }
}