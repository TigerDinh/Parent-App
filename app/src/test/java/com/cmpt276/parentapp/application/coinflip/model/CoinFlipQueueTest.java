package com.cmpt276.parentapp.application.coinflip.model;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

import com.cmpt276.parentapp.application.children.model.Child;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class CoinFlipQueueTest {

    CoinFlipQueue queue;

    @Before
    public void Setup() {
        Child alice = new Child("Alice", null, null);
        Child bob = new Child("Bob",null, null);
        queue = new CoinFlipQueue(Arrays.asList(alice, bob));
    }

    @Test
    public void getCandidate() {
        assertEquals(new Child("Alice",null, null), queue.getCandidate());
    }

    @Test
    public void getCandidate_RemoveUntilEmpty_CandidateUpdatedCorrectly() {
        assertEquals(new Child("Alice",null, null), queue.getCandidate());
        queue.remove(new Child("Alice",null, null));
        assertEquals(new Child("Bob",null, null), queue.getCandidate());
        queue.remove(new Child("Bob",null, null));
        assertEquals(queue.ANONYMOUS, queue.getCandidate());
    }

    @Test
    public void getCandidate_CandidateNobodyThenAddChild_CandidateNobody() {
        queue.cleanCandidate();
        assertEquals(queue.ANONYMOUS, queue.getCandidate());
        queue.add(new Child("David",null, null));
        assertEquals(queue.ANONYMOUS, queue.getCandidate());
    }

    @Test
    public void getCandidate_CandidateNobodyThenRemoveChild_CandidateNobody() {
        queue.cleanCandidate();
        assertEquals(queue.ANONYMOUS, queue.getCandidate());
        queue.remove(new Child("Alice",null, null));
        assertEquals(queue.ANONYMOUS, queue.getCandidate());
    }

    @Test
    public void setCandidate() {
        queue.setCandidate(1);
        assertEquals(new Child("Bob",null, null), queue.getCandidate());
        List<String> names = Arrays.asList("Alice", "Bob");
        assertIterableEquals(names, queue.view());
    }

    @Test
    public void pushBack_TheFrontToTheEnd() {
        assertEquals(new Child("Alice",null, null), queue.getCandidate());
        queue.pushBack(new Child("Alice",null, null));
        assertEquals(new Child("Bob",null, null), queue.getCandidate());
        List<String> names = Arrays.asList("Bob", "Alice");
        assertIterableEquals(names, queue.view());
    }

    @Test
    public void pushBack_TheMiddleToTheEnd() {
        queue.add(new Child("David",null, null));
        List<String> names = Arrays.asList("Alice", "Bob", "David");
        assertIterableEquals(names, queue.view());
        queue.setCandidate(1);
        queue.pushBack(new Child("Bob",null, null));
        names = Arrays.asList("Alice", "David", "Bob");
        assertIterableEquals(names, queue.view());
    }

    @Test
    public void view() {
        List<String> names = Arrays.asList("Alice", "Bob");
        assertIterableEquals(names, queue.view());
    }

    @Test
    public void add() {
        queue.add(new Child("Caleb",null, null));
        List<String> names = Arrays.asList("Alice", "Bob", "Caleb");
        assertIterableEquals(names, queue.view());
    }

    @Test
    public void remove_TopCandidate_CandidateShiftToNext() {
        assertEquals(new Child("Alice",null, null), queue.getCandidate());
        queue.remove(new Child("Alice",null, null));
        List<String> names = Arrays.asList("Bob");
        assertIterableEquals(names, queue.view());
        assertEquals(new Child("Bob",null, null), queue.getCandidate());
    }

    @Test
    public void replace_TopCandidate_CandidateReplaced() {
        queue.replace(new Child("Alice",null, null), new Child("Eddy",null, null));
        List<String> names = Arrays.asList("Eddy", "Bob");
        assertIterableEquals(names, queue.view());
        assertEquals(new Child("Eddy",null, null), queue.getCandidate());
    }
}