package main.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BlockTest {

   private Block testBlock;

    @BeforeEach
    void createBlock() {

        ArrayList<Task> tasks = new ArrayList<>();

        for(int i = 0; i < 3; i++) {

            ArrayList<Answer> answer = new ArrayList<>();
            answer.add(new Answer("antwort 1", true));
            answer.add(new Answer("antwort 2", false));
            answer.add(new Answer("antwort 3", false));

            tasks.add(new Task("Frage " + i, answer ));

        }

        testBlock = new Block("TaskBlock", tasks);

    }

    @Test
    void setCurrTask() {

        testBlock.setCurrTask(0);
        assertEquals(testBlock.getCurrTask(), testBlock.getTasks().get(0));

    }

    @Test
    void switchToNextTask1() {

        testBlock.setCurrTask(0);
        boolean wasSwitched = testBlock.switchToNextTask();

        assertEquals(testBlock.getCurrTask(), testBlock.getTasks().get(1));
        assertTrue(wasSwitched);
    }

    @Test
    void switchToNextTask2() {

        testBlock.setCurrTask(2);
        boolean wasSwitched = testBlock.switchToNextTask();

        assertEquals(testBlock.getCurrTask(), testBlock.getTasks().get(2));
        assertFalse(wasSwitched);

    }


    @Test
    void switchToPrevTask1() {

        testBlock.setCurrTask(2);
        boolean wasSwitched = testBlock.switchToPrevTask();

        assertEquals(testBlock.getCurrTask(), testBlock.getTasks().get(1));
        assertTrue(wasSwitched);

    }

    @Test
    void switchToPrevTask2() {

        testBlock.setCurrTask(0);
        boolean wasSwitched = testBlock.switchToPrevTask();

        assertEquals(testBlock.getCurrTask(), testBlock.getTasks().get(0));
        assertFalse(wasSwitched);

    }


    @Test
    void isLastTask1() {

        testBlock.setCurrTask(2);
        assertTrue(testBlock.isLastTask());

    }

    @Test
    void isLastTask2() {

        testBlock.setCurrTask(0);
        assertFalse(testBlock.isLastTask());

    }

    @Test
    void isFirstTask1() {

        testBlock.setCurrTask(0);
        assertTrue(testBlock.isFirstTask());

    }

    @Test
    void isFirstTask2() {

        testBlock.setCurrTask(2);
        assertFalse(testBlock.isFirstTask());

    }

}