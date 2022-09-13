package com.mopstream.client.utility;


import com.mopstream.client.AppClient;
import com.mopstream.common.data.*;
import com.mopstream.common.exceptions.IncorrectInputInScriptException;
import com.mopstream.common.exceptions.MustBeNotEmptyException;
import com.mopstream.common.exceptions.NotInDeclaredLimitsException;
import com.mopstream.common.utility.Outputer;

import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Asks a user a lab's value.
 */
public class LabWorkAsker {

    private Scanner userReader;
    private boolean fileMode;

    public LabWorkAsker(Scanner userReader) {
        this.userReader = userReader;
        fileMode = false;
    }

    /**
     * Sets labasker mode to 'File Mode'.
     */
    public void setFileMode() {
        fileMode = true;
    }

    /**
     * Sets labasker mode to 'User Mode'.
     */
    public void setUserMode() {
        fileMode = false;
    }

    /**
     * Asks a user the lab's name.
     *
     * @return Lab's name.
     * @throws IncorrectInputInScriptException If script is running and something goes wrong.
     */
    public String askName() throws IncorrectInputInScriptException {
        String name;
        try{
            while (true) {
                try {
                    Outputer.println("Введите имя:");
                    Outputer.print(AppClient.PS2);
                    name = userReader.nextLine().trim();
                    if (fileMode)  Outputer.println(name);
                    if (name.equals("")) throw new MustBeNotEmptyException();
                    break;
                } catch (MustBeNotEmptyException exception) {
                    Outputer.printerror("Имя не может быть пустым!");
                    if (fileMode) throw new IncorrectInputInScriptException();
                } catch (IllegalStateException exception) {
                    Outputer.printerror("Непредвиденная ошибка!");
                    System.exit(0);
                }
            }
        }
        catch (NoSuchElementException exception){
            name = null;
        }
        return name;
    }


    /**
     * Asks a user the labs's X coordinate.
     *
     * @return Lab's X coordinate.
     * @throws IncorrectInputInScriptException If script is running and something goes wrong.
     */
    public Long askX() throws IncorrectInputInScriptException {
        String strX;
        Long x;
        try{
            while (true) {
                try {
                    Outputer.println("Введите координату X:");
                    Outputer.print(AppClient.PS2);
                    strX = userReader.nextLine().trim();
                    if (fileMode) Outputer.println(strX);
                    x = Long.parseLong(strX);
                    break;
                } catch (NumberFormatException exception) {
                    Outputer.printerror("Координата X должна быть представлена числом!");
                    if (fileMode) throw new IncorrectInputInScriptException();
                } catch (IllegalStateException exception) {
                    Outputer.printerror("Непредвиденная ошибка!");
                    System.exit(0);
                }
            }
        }
        catch (NoSuchElementException exception){
            x = null;
        }
        return x;
    }

    /**
     * Asks a user the lab's Y coordinate.
     *
     * @return Lab's Y coordinate.
     * @throws IncorrectInputInScriptException If script is running and something goes wrong.
     */
    public double askY() throws IncorrectInputInScriptException {
        String strY;
        double y;
        try{
            while (true) {
                double MAX_Y = 334d;
                try {
                    Outputer.println("Введите координату Y <= " + MAX_Y + ":");
                    Outputer.print(AppClient.PS2);
                    strY = userReader.nextLine().trim();
                    if (fileMode) Outputer.println(strY);
                    y = Double.parseDouble(strY);
                    if (y > MAX_Y) throw new NotInDeclaredLimitsException();
                    break;
                } catch (NotInDeclaredLimitsException exception) {
                    Outputer.printerror("Координата Y не может превышать " + MAX_Y + "!");
                    if (fileMode) throw new IncorrectInputInScriptException();
                } catch (NumberFormatException exception) {
                    Outputer.printerror("Координата Y должна быть представлена числом!");
                    if (fileMode) throw new IncorrectInputInScriptException();
                } catch (IllegalStateException exception) {
                    Outputer.printerror("Непредвиденная ошибка!");
                    System.exit(0);
                }
            }
        }
        catch (NoSuchElementException exception){
            y = 335d;
        }
        return y;
    }

    /**
     * Asks a user the lab's coordinates.
     *
     * @return Lab's coordinates.
     * @throws IncorrectInputInScriptException If script is running and something goes wrong.
     */
    public Coordinates askCoordinates() throws IncorrectInputInScriptException {
        Long x;
        double y;
        x = askX();
        if (x==null) return new Coordinates(x, 335d);
        y = askY();
        return new Coordinates(x, y);
    }

    /**
     * Asks a user the lab's minimal point.
     *
     * @return Lab's minimal point.
     * @throws IncorrectInputInScriptException If script is running and something goes wrong.
     */
    public Long askMinimalPoint() throws IncorrectInputInScriptException {
        String strMinPoint;
        Long minPoint;
        try{
            while (true) {
                try {
                    Outputer.println("Введите минимальный балл:");
                    Outputer.print(AppClient.PS2);
                    strMinPoint = userReader.nextLine().trim();
                    if (fileMode) Outputer.println(strMinPoint);
                    minPoint = Long.parseLong(strMinPoint);
                    long MIN_MIN_POINT = 0;
                    if (minPoint <= MIN_MIN_POINT) throw new NotInDeclaredLimitsException();
                    break;
                } catch (NotInDeclaredLimitsException exception) {
                    Outputer.printerror("Минимальный балл долен быть больше нуля!");
                    if (fileMode) throw new IncorrectInputInScriptException();
                } catch (NumberFormatException exception) {
                    Outputer.printerror("Минимальный балл должен быть представлен числом!");
                    if (fileMode) throw new IncorrectInputInScriptException();
                } catch (IllegalStateException exception) {
                    Outputer.printerror("Непредвиденная ошибка!");
                    System.exit(0);
                }
            }
        }
        catch (NoSuchElementException exception){
            minPoint = null;
        }
        return minPoint;
    }

    /**
     * Asks a user the lab's difficulty.
     *
     * @return Lab's difficulty.
     * @throws IncorrectInputInScriptException If script is running and something goes wrong.
     */
    public Difficulty askDifficulty() throws IncorrectInputInScriptException {
        String strDifficulty;
        Difficulty difficulty;
        try{
            while (true) {
                try {
                    Outputer.println("Сложности - " + Difficulty.nameList());
                    Outputer.println("Введите сложность:");
                    Outputer.print(AppClient.PS2);
                    strDifficulty = userReader.nextLine().trim();
                    if (fileMode) Outputer.println(strDifficulty);
                    difficulty = Difficulty.valueOf(strDifficulty.toUpperCase());
                    break;
                } catch (IllegalArgumentException exception) {
                    Outputer.printerror("Сложности нет в списке!");
                    if (fileMode) throw new IncorrectInputInScriptException();
                } catch (IllegalStateException exception) {
                    Outputer.printerror("Непредвиденная ошибка!");
                    System.exit(0);
                }
            }
        }
        catch (NoSuchElementException exception){
            difficulty = null;
        }
        return difficulty;
    }

    /**
     * Asks a user the lab's discipline name.
     *
     * @return Lab's discipline name.
     * @throws IncorrectInputInScriptException If script is running and something goes wrong.
     */
    public String askDisciplineName() throws IncorrectInputInScriptException {
        String nameDiscipline;
        try{
            while (true) {
                try {
                    Outputer.println("Введите название дисциплины:");
                    Outputer.print(AppClient.PS2);
                    nameDiscipline = userReader.nextLine().trim();
                    if (fileMode) Outputer.println(nameDiscipline);
                    if (nameDiscipline.equals("")) throw new MustBeNotEmptyException();
                    break;
                } catch (MustBeNotEmptyException exception) {
                    Outputer.printerror("Название дисциплины не может быть пустым!");
                    if (fileMode) throw new IncorrectInputInScriptException();
                } catch (IllegalStateException exception) {
                    Outputer.printerror("Непредвиденная ошибка!");
                    System.exit(0);
                }
            }
        }
        catch (NoSuchElementException exception){
            nameDiscipline = null;
        }
        return nameDiscipline;
    }

    /**
     * Asks a user the lab's discipline practicehours.
     *
     * @return Lab's discipline practicehours.
     * @throws IncorrectInputInScriptException If script is running and something goes wrong.
     */
    public Integer askDisciplinePractiseHours() throws IncorrectInputInScriptException {
        String strpracticeHours;
        Integer practiceHours;
        try{
            while (true) {
                try {
                    Outputer.println("Введите часы практики:");
                    Outputer.print(AppClient.PS2);
                    strpracticeHours = userReader.nextLine().trim();
                    if (fileMode) Outputer.println(strpracticeHours);
                    practiceHours = Integer.parseInt(strpracticeHours);
                    break;
                } catch (NumberFormatException exception) {
                    Outputer.printerror("Часы практики должны быть представлены числом!");
                    if (fileMode) throw new IncorrectInputInScriptException();
                } catch (IllegalStateException exception) {
                    Outputer.printerror("Непредвиденная ошибка!");
                    System.exit(0);
                }
            }
        }
        catch (NoSuchElementException exception){
            practiceHours = null;
        }
        return practiceHours;
    }

    /**
     * Asks a user the lab's discipline.
     *
     * @return Lab's discipline.
     * @throws IncorrectInputInScriptException If script is running and something goes wrong.
     */
    public Discipline askDiscipline() throws IncorrectInputInScriptException {
        String name;
        Integer practiceHours;
        name = askDisciplineName();
        if (name.equals("ERROR")) return new Discipline(name, -1);
        practiceHours = askDisciplinePractiseHours();
        return new Discipline(name, practiceHours);
    }

    /**
     * Asks a user a question.
     *
     * @param question A question.
     * @return Answer (true/false).
     * @throws IncorrectInputInScriptException If script is running and something goes wrong.
     */
    public boolean askQuestion(String question) throws IncorrectInputInScriptException {
        String finalQuestion = question + " (+/-):";
        String answer;
        while (true) {
            try {
                Outputer.println(finalQuestion);
                Outputer.print(AppClient.PS2);
                answer = userReader.nextLine().trim();
                if (fileMode) Outputer.println(answer);
                if (!answer.equals("+") && !answer.equals("-")) throw new NotInDeclaredLimitsException();
                break;
            } catch (NoSuchElementException exception) {
                Outputer.printerror("Ответ не распознан!");
                if (fileMode) throw new IncorrectInputInScriptException();
            } catch (NotInDeclaredLimitsException exception) {
                Outputer.printerror("Ответ должен быть представлен знаками '+' или '-'!");
                if (fileMode) throw new IncorrectInputInScriptException();
            } catch (IllegalStateException exception) {
                Outputer.printerror("Непредвиденная ошибка!");
                System.exit(0);
            }
        }
        return answer.equals("+");
    }

    @Override
    public String toString() {
        return "LabWorkAsker (вспомогательный класс для запросов пользователю)";
    }
}