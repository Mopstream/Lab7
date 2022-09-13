package com.mopstream.common.data;

import java.io.Serializable;

/**
 * Discipline of the labwork.
 */
public class Discipline implements Serializable {
    private final String name;
    private final Integer practiceHours;

    public Discipline(String name, Integer practiceHours) {
        this.name = name;
        this.practiceHours = practiceHours;
    }

    /**
     * @return Name of the discipline.
     */
    public String getName() {
        return name;
    }

    /**
     * @return Practise hours of the discipline.
     */
    public Integer getPracticeHours() {
        return practiceHours;
    }

    @Override
    public String toString() {
        return "Дисциплина " + name + " " + practiceHours;
    }

    @Override
    public int hashCode() {
        return name.hashCode() + (int) practiceHours;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Discipline) {
            Discipline disciplineObj = (Discipline) obj;
            return name.equals(disciplineObj.getName()) && (practiceHours == disciplineObj.getPracticeHours());
        }
        return false;
    }
}
