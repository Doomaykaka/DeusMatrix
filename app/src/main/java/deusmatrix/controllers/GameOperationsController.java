package deusmatrix.controllers;

import deusmatrix.dao.StatisticsDAO;
import deusmatrix.dao.UsersDAO;
import deusmatrix.models.Statistic;
import deusmatrix.models.User;
import deusmatrix.utils.Constants;
import deusmatrix.utils.Logger;
import deusmatrix.utils.SupportFunctions;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class GameOperationsController {
    private final UsersDAO usersDAO;
    private final StatisticsDAO statisticsDAO;
    private final EntityManagerFactory entityManagerFactory;

    public GameOperationsController(UsersDAO usersDAO, StatisticsDAO statisticsDAO) {
        this.usersDAO = usersDAO;
        this.statisticsDAO = statisticsDAO;
        this.entityManagerFactory = usersDAO.getEntityManagerFactory();
    }

    public List<User> getAllUsers() {
        Logger.getInstance().info("Get all users");
        List<User> users = usersDAO.getAll();
        return users == null ? Collections.emptyList() : users;
    }

    public boolean saveUser(User user) {
        Logger.getInstance().info("Save user");
        return executeTransaction(manager -> manager.persist(user));
    }

    public boolean updateUser(User user) {
        Logger.getInstance().info("Update user");
        return executeTransaction(manager -> manager.merge(user));
    }

    public User createNewUser(String name) {
        User newUser = SupportFunctions.createEmptyUser(name);
        Logger.getInstance().info("Create user");

        return saveUser(newUser) ? newUser : null;
    }

    public boolean removeUser(Long id) {
        Logger.getInstance().info("Remove user");
        return executeTransaction(manager -> {
            User user = manager.find(User.class, id);
            if (user == null) {
                throw new IllegalArgumentException("User does not exist");
            }
            manager.remove(user);
        });
    }

    public boolean exportUser(User user, File fileToSave) {
        if (user == null || fileToSave == null || user.getStatistic() == null) {
            return false;
        }

        Logger.getInstance().info("Export user");
        JSONObject userObject = new JSONObject();
        userObject.put("formatVersion", 1L);
        userObject.put("name", user.getName());
        userObject.put("creationDate", user.getCreationDate().toInstant().getEpochSecond());
        userObject.put("level", user.getLevel());
        userObject.put("experience", user.getExperience());
        userObject.put("experienceToNextLevel", user.getExperienceToNextLevel());
        userObject.put("statistic", statisticToJSON(user.getStatistic()));

        try {
            StringWriter writer = new StringWriter();
            userObject.writeJSONString(writer);
            File absoluteFile = fileToSave.getAbsoluteFile();
            return SupportFunctions.writeContentInNewFile(
                    absoluteFile.getParentFile(), absoluteFile.getName(), List.of(writer.toString()));
        } catch (IOException | RuntimeException e) {
            Logger.getInstance().warning("Can't export user: " + e.getMessage());
            return false;
        }
    }

    private JSONObject statisticToJSON(Statistic statistic) {
        JSONObject statisticObject = new JSONObject();
        statisticObject.put(
                "lastPlayDate", statistic.getLastPlayDate().toInstant().getEpochSecond());
        statisticObject.put("daysInGame", statistic.getDaysInGame());
        statisticObject.put("easyWins", statistic.getEasyWins());
        statisticObject.put("middleWins", statistic.getMiddleWins());
        statisticObject.put("hardWins", statistic.getHardWins());
        statisticObject.put("easyBestTime", statistic.getEasyBestTime());
        statisticObject.put("middleBestTime", statistic.getMiddleBestTime());
        statisticObject.put("hardBestTime", statistic.getHardBestTime());
        statisticObject.put("easyLose", statistic.getEasyLose());
        statisticObject.put("middleLose", statistic.getMiddleLose());
        statisticObject.put("hardLose", statistic.getHardLose());
        return statisticObject;
    }

    public User importUser(File fileToLoad) {
        if (fileToLoad == null) {
            return null;
        }

        Logger.getInstance().info("Import user");
        try {
            String userJson = Files.readString(fileToLoad.toPath(), StandardCharsets.UTF_8);
            Object parsed = new JSONParser().parse(userJson);
            if (!(parsed instanceof JSONObject userObject)) {
                throw new IllegalArgumentException("Save must contain a JSON object");
            }

            String name = requiredString(userObject, "name").trim();
            if (name.isEmpty()) {
                throw new IllegalArgumentException("User name must not be empty");
            }

            Date creationDate = dateFromSeconds(requiredLong(userObject, "creationDate"));
            long level = nonNegativeLong(userObject, "level");
            long experience = nonNegativeLong(userObject, "experience");
            long experienceToNextLevel = nonNegativeLong(userObject, "experienceToNextLevel");
            Statistic statistic = statisticFromJSON(userObject.get("statistic"));

            return new User(name, creationDate, level, experience, experienceToNextLevel, statistic);
        } catch (IOException | ParseException | RuntimeException e) {
            Logger.getInstance().warning("Can't import user: " + e.getMessage());
            return null;
        }
    }

    private Statistic statisticFromJSON(Object value) {
        if (!(value instanceof JSONObject statisticObject)) {
            throw new IllegalArgumentException("Statistic object is missing");
        }

        Date lastPlayDate = dateFromSeconds(requiredLong(statisticObject, "lastPlayDate"));
        return new Statistic(
                lastPlayDate,
                nonNegativeLong(statisticObject, "daysInGame"),
                nonNegativeLong(statisticObject, "easyWins"),
                nonNegativeLong(statisticObject, "middleWins"),
                nonNegativeLong(statisticObject, "hardWins"),
                nonNegativeLong(statisticObject, "easyBestTime"),
                nonNegativeLong(statisticObject, "middleBestTime"),
                nonNegativeLong(statisticObject, "hardBestTime"),
                nonNegativeLong(statisticObject, "easyLose"),
                nonNegativeLong(statisticObject, "middleLose"),
                nonNegativeLong(statisticObject, "hardLose"));
    }

    private String requiredString(JSONObject object, String key) {
        Object value = object.get(key);
        if (!(value instanceof String string)) {
            throw new IllegalArgumentException("Field '" + key + "' must be a string");
        }
        return string;
    }

    private long requiredLong(JSONObject object, String key) {
        Object value = object.get(key);
        if (!(value instanceof Number number)) {
            throw new IllegalArgumentException("Field '" + key + "' must be numeric");
        }
        return number.longValue();
    }

    private long nonNegativeLong(JSONObject object, String key) {
        long value = requiredLong(object, key);
        if (value < 0) {
            throw new IllegalArgumentException("Field '" + key + "' must not be negative");
        }
        return value;
    }

    private Date dateFromSeconds(long seconds) {
        return new Date(Math.multiplyExact(seconds, Constants.SECONDS_TO_MILLIS_MULTIPLIER));
    }

    private boolean executeTransaction(Consumer<EntityManager> operation) {
        if (entityManagerFactory == null) {
            Logger.getInstance().warning("Entity manager factory is not configured");
            return false;
        }

        EntityManager manager = null;
        EntityTransaction transaction = null;
        try {
            manager = entityManagerFactory.createEntityManager();
            transaction = manager.getTransaction();
            transaction.begin();
            operation.accept(manager);
            transaction.commit();
            return true;
        } catch (PersistenceException | IllegalArgumentException e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            Logger.getInstance().warning("Can't execute user transaction: " + e.getMessage());
            return false;
        } finally {
            if (manager != null && manager.isOpen()) {
                manager.close();
            }
        }
    }
}
