package com.mopstream.common.interaction;


import com.mopstream.common.data.Coordinates;
import com.mopstream.common.data.Difficulty;
import com.mopstream.common.data.Discipline;
import com.mopstream.common.data.LabWork;

import java.io.Serializable;

public class NewLab implements Serializable {
    private String name;
    private Coordinates coordinates;
    private long minimalPoint;
    private Difficulty difficulty;
    private Discipline discipline;

    public NewLab(String name, Coordinates coordinates,
                  long minimalPoint, Difficulty difficulty, Discipline discipline) {
        this.name = name;
        this.coordinates = coordinates;
        this.minimalPoint = minimalPoint;
        this.difficulty = difficulty;
        this.discipline = discipline;
    }

    /**
     * @return Name of the labwork.
     */
    public String getName() {
        return name;
    }


    /**
     * @return Coordinates of the labwork.
     */
    public Coordinates getCoordinates() {
        return coordinates;
    }

    /**
     * @return Minimal Point of the labwork.
     */
    public Long getMinimalPoint() {
        return minimalPoint;
    }

    /**
     * @return Difficulty of the labwork.
     */
    public Difficulty getDifficulty() {
        return difficulty;
    }

    /**
     * @return Discipline of the labwork.
     */
    public Discipline getDiscipline() {
        return discipline;
    }

    @Override
    public String toString() {
        String info = "";
        info += "Лаба, которая будет добавлена";
        info += "\n Название: " + name;
        info += "\n Координаты: " + coordinates;
        info += "\n Минимальный балл: " + minimalPoint;
        info += "\n Сложность: " + difficulty;
        info += "\n Предмет: " + discipline;
        return info;
    }

    @Override
    public int hashCode() {
        return name.hashCode() + coordinates.hashCode() + difficulty.hashCode() + discipline.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof LabWork) {
            LabWork labWorkObj = (LabWork) obj;
            return name.equals(labWorkObj.getName()) && coordinates.equals(labWorkObj.getCoordinates())
                    && (minimalPoint == labWorkObj.getMinimalPoint()) && (difficulty == labWorkObj.getDifficulty())
                    && (discipline.equals(labWorkObj.getDiscipline()));
        }
        return false;
    }
}
