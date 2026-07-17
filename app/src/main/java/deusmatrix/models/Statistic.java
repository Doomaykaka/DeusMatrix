package deusmatrix.models;

import java.util.Date;
import java.util.Objects;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "statistics")
public class Statistic {
    @Id
    @TableGenerator(
            name = "statistic_gen",
            table = "statistic_gen",
            pkColumnName = "gen_name",
            valueColumnName = "gen_val",
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "statistic_gen")
    private Long id;

    @NotNull @Column(nullable = false)
    private Date lastPlayDate;

    @NotNull @Column(nullable = false)
    private Long daysInGame;

    @NotNull @Column(nullable = false)
    private Long easyWins;

    @NotNull @Column(nullable = false)
    private Long middleWins;

    @NotNull @Column(nullable = false)
    private Long hardWins;

    @NotNull @Column(nullable = false)
    private Long easyBestTime;

    @NotNull @Column(nullable = false)
    private Long middleBestTime;

    @NotNull @Column(nullable = false)
    private Long hardBestTime;

    @NotNull @Column(nullable = false)
    private Long easyLose;

    @NotNull @Column(nullable = false)
    private Long middleLose;

    @NotNull @Column(nullable = false)
    private Long hardLose;

    public Statistic() {}

    public Statistic(
            Date lastPlayDate,
            Long daysInGame,
            Long easyWins,
            Long middleWins,
            Long hardWins,
            Long easyBestTime,
            Long middleBestTime,
            Long hardBestTime,
            Long easyLose,
            Long middleLose,
            Long hardLose) {
        final Long DEFAULT_ID = null;

        this.id = DEFAULT_ID;
        this.lastPlayDate = lastPlayDate;
        this.daysInGame = daysInGame;
        this.easyWins = easyWins;
        this.middleWins = middleWins;
        this.hardWins = hardWins;
        this.easyBestTime = easyBestTime;
        this.middleBestTime = middleBestTime;
        this.hardBestTime = hardBestTime;
        this.easyLose = easyLose;
        this.middleLose = middleLose;
        this.hardLose = hardLose;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getLastPlayDate() {
        return lastPlayDate;
    }

    public void setLastPlayDate(Date lastPlayDate) {
        this.lastPlayDate = lastPlayDate;
    }

    public Long getDaysInGame() {
        return daysInGame;
    }

    public void setDaysInGame(Long daysInGame) {
        this.daysInGame = daysInGame;
    }

    public Long getEasyWins() {
        return easyWins;
    }

    public void setEasyWins(Long easyWins) {
        this.easyWins = easyWins;
    }

    public Long getMiddleWins() {
        return middleWins;
    }

    public void setMiddleWins(Long middleWins) {
        this.middleWins = middleWins;
    }

    public Long getHardWins() {
        return hardWins;
    }

    public void setHardWins(Long hardWins) {
        this.hardWins = hardWins;
    }

    public Long getEasyBestTime() {
        return easyBestTime;
    }

    public void setEasyBestTime(Long easyBestTime) {
        this.easyBestTime = easyBestTime;
    }

    public Long getMiddleBestTime() {
        return middleBestTime;
    }

    public void setMiddleBestTime(Long middleBestTime) {
        this.middleBestTime = middleBestTime;
    }

    public Long getHardBestTime() {
        return hardBestTime;
    }

    public void setHardBestTime(Long hardBestTime) {
        this.hardBestTime = hardBestTime;
    }

    public Long getEasyLose() {
        return easyLose;
    }

    public void setEasyLose(Long easyLose) {
        this.easyLose = easyLose;
    }

    public Long getMiddleLose() {
        return middleLose;
    }

    public void setMiddleLose(Long middleLose) {
        this.middleLose = middleLose;
    }

    public Long getHardLose() {
        return hardLose;
    }

    public void setHardLose(Long hardLose) {
        this.hardLose = hardLose;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Statistic statistic = (Statistic) o;
        return Objects.equals(id, statistic.id)
                && Objects.equals(lastPlayDate, statistic.lastPlayDate)
                && Objects.equals(daysInGame, statistic.daysInGame)
                && Objects.equals(easyWins, statistic.easyWins)
                && Objects.equals(middleWins, statistic.middleWins)
                && Objects.equals(hardWins, statistic.hardWins)
                && Objects.equals(easyBestTime, statistic.easyBestTime)
                && Objects.equals(middleBestTime, statistic.middleBestTime)
                && Objects.equals(hardBestTime, statistic.hardBestTime)
                && Objects.equals(easyLose, statistic.easyLose)
                && Objects.equals(middleLose, statistic.middleLose)
                && Objects.equals(hardLose, statistic.hardLose);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                id,
                lastPlayDate,
                daysInGame,
                easyWins,
                middleWins,
                hardWins,
                easyBestTime,
                middleBestTime,
                hardBestTime,
                easyLose,
                middleLose,
                hardLose);
    }
}
